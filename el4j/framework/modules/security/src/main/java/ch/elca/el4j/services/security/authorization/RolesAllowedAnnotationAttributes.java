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
package ch.elca.el4j.services.security.authorization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

import org.acegisecurity.SecurityConfig;

/**
 * 
 * This class is a Java 5 Annotation <code>Attributes</code> metadata 
 * implementation used for secure method interception.
 * <p>This <code>Attributes</code> implementation will return security 
 * configuration for classes described using the <code>RolesAllowed</code>
 * Java 5 annotation.</p>
 *
 * This class is the equivalent of Acegi Security's 
 * <code>SecurityAnnotationAttributes</code> class for the 
 * <code>RolesAllowed</code> annotation. 
 *
 * @see org.acegisecurity.annotation.SecurityAnnotationAttributes
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class RolesAllowedAnnotationAttributes {
    /**
     * Get the <code>Secured</code> attributes for a given target class.
     *
     * @param target The target method
     *
     * @return Collection of <code>SecurityConfig</code>
     *
     * @see Attributes#getAttributes
     */
    public Collection getAttributes(Class target) {
        Set<SecurityConfig> attributes = new HashSet<SecurityConfig>();

        for (Annotation annotation : target.getAnnotations()) {
            // check for Secured annotations
            if (annotation instanceof RolesAllowed) {
                RolesAllowed attr = (RolesAllowed) annotation;

                for (String auth : attr.value()) {
                    attributes.add(new SecurityConfig(auth));
                }

                break;
            }
        }

        return attributes;
    }

    public Collection getAttributes(Class clazz, Class filter) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    /**
     * Get the <code>RolesAllowed</code> attributes for a given target method.
     *
     * @param method The target method
     *
     * @return Collection of <code>SecurityConfig</code>
     *
     * @see Attributes#getAttributes
     */
    public Collection getAttributes(Method method) {
        Set<SecurityConfig> attributes = new HashSet<SecurityConfig>();

        for (Annotation annotation : method.getAnnotations()) {
            // check for RolesAllowed annotations
            if (annotation instanceof RolesAllowed) {
                RolesAllowed attr = (RolesAllowed) annotation;

                for (String auth : attr.value()) {
                    attributes.add(new SecurityConfig(auth));
                }

                break;
            }
        }

        return attributes;
    }

    public Collection getAttributes(Method method, Class clazz) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    public Collection getAttributes(Field field) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    public Collection getAttributes(Field field, Class clazz) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
}
