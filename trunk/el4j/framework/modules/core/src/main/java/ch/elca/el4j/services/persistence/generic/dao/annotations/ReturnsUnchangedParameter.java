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
package ch.elca.el4j.services.persistence.generic.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated method returns a parameter without changing it. The 
 * annotations' value contains the index of the parameter returned; it defaults
 * to 0, i.e. the first parameter.
 * <p>
 * More precisely, this annotation states that the method's return value is 
 * <i>transitively logically identical</i> to the value passed. 
 * <i>Logically identical</i> means that the
 * returned object has the same logical identity, <i>transitive</i> means that
 * this must hold for all references reachable through it as
 * well. Note that objects without logical identity are trivially logically 
 * identical, ending the recursion. Logical identity is defined by the subclass
 * of AbstractIdentityFixer in use.
 * 
 *<p>For instance, <pre>
 *    &#64;ReturnsUnchangedParameter
 *    T saveOrUpdate(T entity);
 *</pre>
 *means that saving an object returns the saved object, which is logically 
 *identical to its former version. 
 *
 *<p> As of JDK 1.5, it is impossible to inherit method annotations when
 *overriding/implementing a method; such annotations must therefore be provided
 *manually.
 *
 * @see ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer
 *
 *<script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */ 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReturnsUnchangedParameter {
    /** The index of the parameter returned. The first parameter has index 0. */
    int value() default 0;
}
