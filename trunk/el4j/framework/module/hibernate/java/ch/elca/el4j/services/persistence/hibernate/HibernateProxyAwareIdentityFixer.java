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
package ch.elca.el4j.services.persistence.hibernate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.Hibernate;

import ch.elca.el4j.services.persistence.generic.repo.AbstractIdentityFixer;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;

/**
 * An identity fixer for objects loaded by hibernate.
 * 
 * <p> Fully materialized hibernate-persisted objects (hereafter called 
 * representatives) bear the logical identity corresponding to their
 * identity in the database; the identity fixer therefore ensures that all
 * requests passing through this proxy yielding a representative for a given
 * persisted object always yield the same representative.
 * 
 * <p> This class considers all non-collection, non-entity types to be immutable
 * value types. Note that lazy loading proxies are not guaranteed a unique 
 * identity by 
 * this class. This is not possible, as they are not guaranteed the proper
 * dynamic type by hibernate.
 * 
 * <p> This class assumes that entities are annotated with 
 * {@link Entity}, and hibernate identities are stored in exactly one
 * property annotated with {@link Id}. Moreover, identity objects are required
 * to override equals and hashcode to provide a value comparison and must not be
 * identical to the object they identify.
 * 
 * <p> This class needs {@link java.lang.reflect.ReflectPermission} 
 * "suppressAccessChecks" if a security manager is present and an object
 * requiring fixing keeps its id in a non-public field. 
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
public class HibernateProxyAwareIdentityFixer 
        extends AbstractIdentityFixer {
    
    /**
     * A type holding an object's identity as required by 
     * {@link HibernateProxyAwareIdentityFixer#id(Object)}. 
     */
    @ImplementationAssumption(
        "A hibernate-persisted object never changes its dynamic type.")
    private static class ID {
        /** The object's dynamic type. */
        Class<?> m_clazz;
        
        /** The object's hibernate id. */
        Object m_hibernateId;
        
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ID) {
                ID other = (ID) obj;
                return m_clazz.      equals(other.m_clazz)
                    && m_hibernateId.equals(other.m_hibernateId);
            } else {
                return false;
            }
        }
        
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return m_clazz.hashCode() ^ m_hibernateId.hashCode();
        }
    }
    
    /** Cache for {@link #idField(Class)}. */
    private static Map<Class<?>, Field> s_cachedIdFields
        = new HashMap<Class<?>, Field>();
    
    /**
     * Returns the field holding the identifier in objects of class {@code c}.
     * @param c see above
     * @return see above
     */
    private static Field idField(Class<?> c) {
        Field idf = s_cachedIdFields.get(c);
        if (idf == null) {
            for (Field f : instanceFields(c)) {
                if (f.getAnnotation(Id.class) != null) {
                    if (idf == null) {
                        idf = f;
                    } else {
                        assert false 
                            : "Composite identifiers are not supported.";
                    }
                }
            }
            assert idf != null : c;
            
            if (!Modifier.isPublic(idf.getModifiers())) {
                final Field IDF = idf;
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        IDF.setAccessible(true);
                        return null;
                    }
                });                
            }
            
            s_cachedIdFields.put(c, idf);
        }
        return idf;
    }
    
    /** {@inheritDoc} */
    @Override
    protected Object id(Object o) {
        if (o.getClass().isAnnotationPresent(Entity.class)) {
            try {
                Field idf = idField(o.getClass());
                Object hid = idf.get(o);
                if (hid == null) {
                    return null;
                } else {
                    ID uid = new ID();
                    uid.m_clazz = o.getClass();
                    uid.m_hibernateId = hid;
                    return uid;
                }
            } catch (IllegalAccessException e) { 
                throw new RuntimeException(e);
            }
        } else {
            return ANONYMOUS;
        }
    }

    /** {@inheritDoc} */
    @Override
    @ImplementationAssumption(
        "no initialized proxies have the wrong dynamic type")
    // at the time of writing, the preceeding may be violated for entities
    // whose supertype is an entity type as well, that are first proxied and
    // subsequently initialized.
    // TODO: get rid of dependency to hibernate.jar (e.g. by duplicating the 
    // code in isInitialized while referring to types by name and reflection) 
    // and move this into a more appropriate module.
    protected boolean immutableValue(Object o) {
        boolean iv;
        if (o == null) {
            iv = true;
        } else if (!Hibernate.isInitialized(o)) {
            iv = true;
        } else if (o.getClass().isAnnotationPresent(Entity.class)) { 
            iv = false;
        } else if (o instanceof Iterable) {
            iv = false;
        } else if (o instanceof Map) {
            iv = false;
        } else if (o instanceof Map.Entry) {
            iv = false;
        } else if (o instanceof Object[]) {
            iv = false;
        } else {
            iv = true;
        }
        return iv;
    }
}
