/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.generic.repo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;

import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.NewEntityState;
import ch.elca.el4j.services.persistence.generic.repo.impl.DefaultRepositoryChangeNotifier;


/**
 * Fixes object identities mangled by loosing ORM context or by remoting.
 * 
 * <p>We call objects in the database logical objects, and their fully 
 * materialized local proxies representatives. 
 * 
 * <p> An IdentityFixer ensures uniqueness of representatives, i.e. for every 
 * logical object, an IdentityFixer will always return the same representative. 
 * 
 * <p> An IdentityFixer can be shown a new version of a logical object to update
 * the representative's state.
 * 
 * <p> If the client refrains from 
 * checking the dynamic type or the identity of non-materialized proxies, every
 * proxy to a logical object behaves exactly like this object unless 
 * non-materialized properties are accessed.
 * 
 * <p> This class needs {@link java.lang.reflect.ReflectPermission} 
 * "suppressAccessChecks" if a security manager is present and an object
 * requiring fixing has non-public fields.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public abstract class AbstractIdentityFixer {
    /** Cache for {@link #fields(Class)}. */
    private static Map<Class<?>, List<Field>> s_cachedFields 
        = new HashMap<Class<?>, List<Field>>();
        
    /** The notifier for broadcasting changes. */
    RepositoryChangeNotifier m_changeNotifier;
    
    /** 
     * The representatives, keyed by their id.
     * @see #id(Object)
     */
    Map<Object, Object> m_representatives; 
    
    /**
     * Constructs a new IdentityFixer. You'd never have guessed that, would you?
     * ;-)
     */
    public AbstractIdentityFixer() {
        this(new DefaultRepositoryChangeNotifier());
    }
    
    /**
     * Constructor.
     * @param changeNotifier The notifier for broadcasting changes.
     */
    @SuppressWarnings("unchecked")
    public AbstractIdentityFixer(RepositoryChangeNotifier changeNotifier) {
        m_changeNotifier = changeNotifier;
        
        int weak = AbstractReferenceMap.WEAK;
        m_representatives = new ReferenceMap(weak, weak);
    }
    
    /**
     * Returns the Fields corresponding to fields in class {@code c}. The fields
     * can be read from / written into (regardless of access modifiers).
     * @param c the class whose fields are desired
     * @return see above.
     */
    private List<Field> fields(Class<?> c) {
        List<Field> fields = s_cachedFields.get(c);
        if (fields == null) {
            fields = instanceFields(c);
            s_cachedFields.put(c, fields);
            
            for (Field f : fields) {
                if (!Modifier.isPublic(f.getModifiers())) {
                    final Field[] FIELDS 
                        = fields.toArray(new Field[fields.size()]);
                    AccessController.doPrivileged(
                        new PrivilegedAction<Object>() {
                            public Object run() {
                                Field.setAccessible(FIELDS, true);
                                return null;
                            }
                        }
                    );
                }
            }
        }
        return fields;
    }
    
    /**
     * Returns a list of all non-static fields of class {@code c}.
     */
    protected static List<Field> instanceFields(Class<?> c) {
        List<Field> fs = new ArrayList<Field>();
        for (Class<?> sc = c; sc != null; sc = sc.getSuperclass()) {
            for (Field f : sc.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    fs.add(f);
                }
            }
        }
        return fs;
    }
    
    /**
     * Returns the notifier used to announce changes. Chiefly useful because
     * you can subscribe to change messages there.
     * @return see above
     */
    public RepositoryChangeNotifier getChangeNotifier() {
        return m_changeNotifier;
    }
    
    /**
     * Actually performs the merge. 
     * @see #merge(Object, Object)
     * @param anchor see {@link #merge(Object, Object)}
     * @param updated see {@link #merge(Object, Object)}
     * @param reached the set of referenced objects that were already discovered
     *                (the call discovering  an object is responsible for
     *                updating it)
     * @return the representative.
     */
    @SuppressWarnings("unchecked")
    protected <T> T doMerge(T anchor, T updated, 
                            IdentityHashMap<Object, Object> reached) {
        
        assert needsFixing(updated) : updated;
        assert id(updated) != null;
        
        // choose representative
        T attached;
        if (anchor == null) {
            attached = (T) m_representatives.get(id(updated));
            if (attached == null) {
                attached = updated;
            }
        } else {
            assert id(anchor) == null 
                || id(anchor).equals(id(updated));
            attached = anchor;
            m_representatives.put(id(attached), attached);
        }
        
        // merge state if not already done
        if (!reached.containsKey(attached)) {
            reached.put(attached, null);
            for (Field f : fields(updated.getClass())) {
                f.setAccessible(true);
                // remove access protection from f
                
                try {
                    f.set(
                        attached,
                        mergeIfNeeded(
                            anchor != null ? f.get(anchor) : null,
                            f.get(updated)
                        )
                    );
                } catch (IllegalAccessException e) { assert false : e; }
            }

            NewEntityState e = new NewEntityState();
            e.changee = attached;
            m_changeNotifier.announce(e);
        }
               
        return attached;
    }
    
    
    /**
     * Updates the unique representitive by duplicating the state in
     * {@code updated}. If no representative exists so far, one is created.
     * 
     * @param anchor the representative known to be identical with updated,
     *               or null, if the representative's logical identity is 
     *               already defined.
     * @return the representative
     */
    @SuppressWarnings("unchecked")
    public <T> T merge(T anchor, T updated) {
        return doMerge(anchor, updated, new IdentityHashMap<Object, Object>());
    }

    /**
     * Merges if updated is materialized, immediately returns {@code updated} 
     * otherwise.
     * @see #merge(Object, Object)
     */
    public Object mergeIfNeeded(Object anchor, Object updated) {
        return needsFixing(updated)
             ? merge(anchor, updated)
             : updated;
    }
    
    /** 
     * Returns the globally unique, logical id for the provided object, or 
     * {@code null}, if it has no id (yet). 
     * 
     * The ID objects returned by this method 
     * must be value-comparable using {@code equals} 
     * (which implies that hashCode must be overridden as well).
     */
    protected abstract Object id(Object o);
    
    /**
     * Returns whether the supplied object needs identity fixing, i.e. if its
     * identity matters 
     * (i.e. not a conceptional value type like {@code String}).
     * and it is materialized. 
     */
    protected abstract boolean needsFixing(Object o);
}