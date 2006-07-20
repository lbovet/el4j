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

import ch.elca.el4j.services.persistence.generic.repo.AbstractIdentityFixer;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;

/**
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
        private Class<?> m_clazz;
        
        /** The object's hibernate id. */
        private Object m_hibernateId;
        
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ID) {
                ID other = (ID) obj;
                return m_clazz      .equals(other.m_clazz      )
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
            assert idf != null;
            
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
        try {
            Object hid = idField(o.getClass()).get(o);
            if (hid != null) {
                ID uid = new ID();
                uid.m_clazz = o.getClass();
                uid.m_hibernateId = hid;
                return uid;
            } else {
                return null;
            }
        } catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean needsFixing(Object o) {
        return o != null && o.getClass().getAnnotation(Entity.class) != null;
    }
}
