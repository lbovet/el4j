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
package ch.elca.el4j.util.codingsupport.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotated API element is preliminary and subject to major change. The
 * idea for this type was shamelessly stolen from 
 * 
 * <p>
 *<a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/annotations.html">
 * http://java.sun.com/j2se/1.5.0/docs/guide/language/annotations.html</a>
 * 
 * 
 * <p> The String argument should detail why the annotated element is 
 * preliminary. This annotation itself is preliminary (prototype stage).
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
@Documented
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface Preliminary { 
    /** why this is preliminary. */
    @Preliminary("lack of known use cases")
    String value() default "";
}
