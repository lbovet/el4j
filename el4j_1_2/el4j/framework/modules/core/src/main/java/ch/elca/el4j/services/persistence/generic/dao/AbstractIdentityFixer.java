/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.generic.dao;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.IntroductionInfoSupport;

import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.NewEntityState;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoChangeNotifier;
import ch.elca.el4j.util.metadata.MetaDataCollector;

/**
 * Fixes object identities mangled by loosing ORM context or by remoting.
 * 
 * <h4>Motivation</h4>
 * 
 * Object Identity is an important concept in OOP, but is not always 
 * guaranteed or maintained. For instance, sending an object back and
 * forth over the wire (using any standard remoting protocal) will create new
 * objects, causing changes applied to the object not to propagate properly -
 * even within a single VM. Similarly, loosing an OR-Mapper's context between 
 * load invocations typically results in creating multiplie proxies to the same 
 * persisted object. Accepting that loss of identity complicates the programming
 * model, and contradicts OO methodology. 
 * 
 * <p> Given a definition of logical identity and a means to recognize 
 * immutable value types, an instance of this class fixes the identities of the 
 * objects passing through it while propagating state updates to the logical
 * object's unique representative.
 * 
 * <h4>Terminology</h4>
 * A <i>representative</i> is an object that has a logical identity. A given
 * identity fixer will always return the same representative for every logical 
 * identity, different identity fixers may return different ones. Usually,
 * you will therefore use a singleton identity fixer, but you may allocate as
 * many as you please ;-)
 * 
 * <h4>Configuration</h4>
 * To get a working identity fixer, you must write a subclass and override the
 * two abstract methods, {@link #id(Object)} and {@link #immutableValue(Object)}
 * . 
 * 
 * <h4>Use</h4>
 * All objects received from an identity-mangling source should pass through an
 * identity fixer. {@link GenericInterceptor} provides a generic Spring AOP 
 * interceptor to be wrapped around the identity-mangling objects. As an
 * alternative, manual
 * access to indentity translation is granted by {@link #merge(Object, Object)}.
 * 
 * <h4>Guarantees</h4>
 * During its lifetime, an identity fixer will always return the same object
 * for every logical identity. It will update the shared instance with the state
 * of the new copies, and notify registered observers about every such update.
 * These guarantees extend to objects (directly or indirecly) referenced by the
 * translated object unless they are recognized as immutable values by
 * {@link #immutableValue(Object)}.
 * 
 * <h4>Requirements</h4>
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
    /**
     * Id for objects of anynomous types (= value types).
     */
    protected static final Object ANONYMOUS = new Object();
    
    /** Cache for {@link #fields(Class)}. */
    static Map<Class<?>, List<Field>> s_cachedFields 
        = new HashMap<Class<?>, List<Field>>();

    /** The logger. */
    static Log s_logger = LogFactory.getLog(AbstractIdentityFixer.class);
    
    /** ID generator for logging. */
    static ObjectIdentifier s_oi = new ObjectIdentifier();    
    
    
    /** Indendation for tracing. */
    int m_traceIndentation = 0;
    
    /** The notifier for broadcasting changes. */
    DaoChangeNotifier m_changeNotifier;
    
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
        this(new DefaultDaoChangeNotifier());
    }
    
    /**
     * Constructor.
     * @param changeNotifier The notifier for broadcasting changes.
     */
    @SuppressWarnings("unchecked")
    public AbstractIdentityFixer(DaoChangeNotifier changeNotifier) {
        m_changeNotifier = changeNotifier;
        m_representatives = new ReferenceMap(
            AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK);
    }
    
    /**
     * Returns the notifier used to announce changes. Chiefly useful because
     * you can subscribe to change messages there.
     * @return see above
     */
    public DaoChangeNotifier getChangeNotifier() {
        return m_changeNotifier;
    }
    
    
    /**
     * Returns a list of all non-static fields of class {@code c}.
     * @param c The concerned class
     * @return A list of all non-static fields of the given class 
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
     * Returns a list of all non-static AccessibleObjects
     * (fields and methods, no constructors) of class {@code c}.
     * Also searches in superclasses.
     * 
     * @param c the concerned class
     * @return
     */    protected static List<AccessibleObject> instanceAccessibleObjects(Class<?> c) {
        List<AccessibleObject> fs = new ArrayList<AccessibleObject>();
        
        
      
        //TODO: also search interfaces?
        for (Class<?> sc = c; sc != null; sc = sc.getSuperclass()) {
           
            //first, get fields
            for (Field f : sc.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    fs.add(f);
                }
            }
            
            //next, get methods
            for (Method m : sc.getDeclaredMethods()) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    fs.add(m);
                }
            }
        }
        return fs;
    }
    
    
    
    /**
     * Returns the Fields corresponding to fields in class {@code c}. The fields
     * can be read from / written into (regardless of access modifiers).
     * @param c the class whose fields are desired
     * @return see above.
     */
    private static List<Field> fields(Class<?> c) {
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
     * Actually performs the merge.
     *  
     * @param anchor
     *              the (presumed) representative, or null
     * @param updated 
     *              the new version of the object
     * @param isIdentical 
     *              whether anchor is known to be transitively identical with 
     *              updated.
     * @param reached 
     *              the set of objects in the updated object graph that have
     *              been (or are beeing) merged. Used to avoid merging an 
     *              object more than once. 
     * @return 
     *              the representative.
     */
    @SuppressWarnings("unchecked")
    protected <T> T merge(T anchor, T updated, 
                          boolean isIdentical,
                          IdentityHashMap<Object, Object> reached) {
        if (immutableValue(updated)) {
            trace("", updated, " is an immutable value");
            return updated;
        }
                
        T attached = (T) reached.get(updated);
        if (attached != null) {
            trace("",  updated, " was already merged to ", attached);
            return attached;
        }

        // choose representative
        //s_logger.debug(m_representatives);
        Object id = id(updated);
        if (isIdentical) {
            attached = anchor;
            assert id(anchor) == null
                || id(anchor) == ANONYMOUS
                || id(anchor).equals(id);
        } else {
            if (id == ANONYMOUS) {
                attached = anchor;
            } else {
                assert id != null;
                attached = (T) m_representatives.get(id);
            }
            
            if (attached == null) {
                attached = updated;
            }
        }
        assert attached != null;
        if (id != ANONYMOUS && id != null) {
            m_representatives.put(id, attached);
        }
        
        // register representative
        reached.put(updated, attached);
        
        trace("merging ", updated, " to ", attached);
        m_traceIndentation++;
        if (updated.getClass().isArray()) {
            
            int l = Array.getLength(updated);
            assert Array.getLength(attached) == Array.getLength(updated);
            for (int i = 0; i < l; i++) {
                Array.set(
                    attached, i,
                    merge(
                        anchor != null ? Array.get(anchor, i) : null,
                        Array.get(updated, i),
                        isIdentical,
                        reached
                    )
                );
            }            
        } else {
            for (Field f : fields(updated.getClass())) {
                try {
                    f.set(
                        attached,
                        merge(
                            anchor != null ? f.get(anchor) : null,
                            f.get(updated),
                            isIdentical,
                            reached
                        )
                    );
                } catch (IllegalAccessException e) { assert false : e; }
            }
        }
        m_traceIndentation--;

        // notify state change
        NewEntityState e = new NewEntityState();
        e.setChangee(attached);
        m_changeNotifier.announce(e);
        
        return attached;
    }
    
    /**
     * Updates the unique representative by duplicating the state in
     * {@code updated}. If no representative exists so far, one is created.
     * 
     * <p>For every potentially modified entity, {@link NewEntityState} 
     * notification are sent using the configured change notifier.
     * 
     * @param anchor 
     *              the representative known to be transitively identical 
     *              with {@code updated}, or null, if the representative's  
     *              logical identity is already defined.
     * @param updated
     *              The object holding the new state.
     * @return The representative.
     */
    public <T> T merge(T anchor, T updated) {
        return merge(
            anchor, 
            updated, 
            anchor != null, 
            new IdentityHashMap<Object, Object>()
        ); 
    }
    
    /**
     * Returns the globally unique, logical id for the provided object, or
     * {@code null}, if it has no id (yet), or {@link #ANONYMOUS} is this
     * object is of value type. {@code o} may be null, point to an ordinary
     * object or to an array.
     * <p>
     * The ID objects returned by this method must be value-comparable using
     * {@code equals} (which implies that hashCode must be overridden as well).
     * To permit garbage-collection, ids refering to the object they identify
     * should do so with weak references.
     * 
     * @param o
     *            The object for which a globally unique, logical id will be
     *            returned
     * @return A globally unique, logical ID object for the given object
     */
    // Keys become eligible for collection shortly after the object they 
    // identify becomes is weakly reachable (because the latter triggers removal
    // from m_representatives.
    protected abstract Object id(Object o);
    
    /**
     * Returns whether the given reference represents an immutable value, either
     * because it really is a value ({@code null}) or because the referenced
     * object's identity is not accessed and its state is not modified.
     * {@code o} may be null, point to an ordinary object or to an array.
     * 
     * @param o
     *            The concerned object
     * @return <code>True</code> if the given object represents an immutable
     *         value, <code>false</code> otherwise
     */
    protected abstract boolean immutableValue(Object o);
    
    /** 
     * A generic "around advice" (as defined in AOP terminology) for remote
     * objects. This interceptor works "out of the box" unless incoming objects
     * have unknown logical identity. If this occurs, it attempts to infer
     * logical identity from {@link ReturnsUnchangedParameter} annotations.
     */
    public class GenericInterceptor
            extends IntroductionInfoSupport 
            implements IntroductionInterceptor {

        /** 
         * Constructor.
         * @param fixedInterface The marker-interface to be "introduced".
         */
        @SuppressWarnings("unchecked")
        public GenericInterceptor(Class<?> fixedInterface) {
            publishedInterfaces.add(fixedInterface);
        }
        
        /** {@inheritDoc} */
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Object result = invocation.proceed();
            ReturnsUnchangedParameter rp 
                = invocation.getMethod().getAnnotation(
                    ReturnsUnchangedParameter.class
                );
            return (rp != null) 
                 ? merge(invocation.getArguments()[rp.value()], result) 
                 : merge(null, result);
        }

        /**
         * Convenience method returning a proxy to the supplied object
         * that implements this "advice".
         * @param o .
         * @return .
         */
        @SuppressWarnings("unchecked")
        public Object decorate(Object o) {
            ProxyFactory pf = new ProxyFactory(o);
            pf.setProxyTargetClass(false);
            pf.addAdvice(this);                
            return pf.getProxy();
        }
    }
    
    
    ////////////
    // Tracing
    ////////////
    
    /** 
     * Logs a trace message. The arguments are assembled on a single line. It
     * is assumed that arguments with even index are strings to be printed, and
     * arguments with odd index are objects whose class and identity should be
     * inserted.
     * @param os .
     */
    void trace(Object... os) {
        if (s_logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < m_traceIndentation; i++) {
                sb.append("    ");
            }
            boolean isLiteral = false;
            for (Object o : os) {
                isLiteral = o instanceof String && !isLiteral;
                if (isLiteral) {
                    sb.append(o);
                } else {
                    s_oi.format(o, sb);                    
                }
            }
            s_logger.debug(sb);
        }
    }

    /**
     * For tracing. Assigns names to traced objects to identify them in
     * trace output.
     */
    private static class ObjectIdentifier {
        /** The next id to be assigned. */
        int m_maxid = 0;
        
        /** Maps every already encountered object to its id. */
        IdentityHashMap<Object, Integer> m_seen 
            = new IdentityHashMap<Object, Integer>();
        
        /**
         * Prints {code o}'s class and id to {@code toAppendTo}.
         * 
         * @param obj
         *            The concerned object
         * @param toAppendTo
         *            The StringBuilder to which the object's class and id will
         *            be printed
         * @return {@code toAppendTo} (for call chaining)
         */
        public StringBuilder format(Object obj, StringBuilder toAppendTo) {
            if (obj == null) {
                return toAppendTo.append("null");
            } else {
                Integer id = m_seen.get(obj);
                if (id == null) {
                    id = m_maxid++;
                    m_seen.put(obj, id);
                }
                return toAppendTo.append(obj.getClass().getSimpleName())
                                 .append(id.toString());
            }
        }
    }   
}