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
package ch.elca.el4j.tests.util.metadata.annotations.definitions;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 * Example Annotation containing the name of the annotation as
 * default value. The interceptor will delegete this value to
 * the test method. The method will the compare the given names
 * with the expected names.
 * 
 * 
 * This class is ... TODO ADH
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Häfeli (ADH)
 */
@Target( { TYPE, METHOD } )
@Retention( RetentionPolicy.RUNTIME )
public @interface ExampleClassAnnotationOne {

    String value() default "ExampleClassAnnotationOne";

}


 