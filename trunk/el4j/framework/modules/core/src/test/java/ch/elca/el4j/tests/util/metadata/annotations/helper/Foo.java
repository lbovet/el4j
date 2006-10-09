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
package ch.elca.el4j.tests.util.metadata.annotations.helper;

import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationFour;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationTwo;

/**
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
@ExamplePackageAnnotationTwo("ExamplePackageAnnotationTwo: Overwritten by Interface Foo")
@ExampleInterfaceAnnotationOne()
@ExampleInterfaceAnnotationTwo()
@ExampleInterfaceAnnotationThree()
@ExampleInterfaceAnnotationFour()
public interface Foo extends Base {
    
    public Object[] metaDataClassInheritance(Object[] metaData);
    
    @ExampleInterfaceAnnotationTwo("ExampleInterfaceAnnotationTwo: "
        + "Set from Method metaDataMethodInheritance in Interface Foo")
    @ExampleInterfaceAnnotationThree("ExampleInterfaceAnnotationThree: "
        + "Set from Method metaDataMethodInheritance in Interface Foo")
    @ExampleInterfaceAnnotationFour("ExampleInterfaceAnnotationFour: "
        + "Set from Method metaDataMethodInheritance in Interface Foo")
    public Object[] metaDataMethodInheritance(Object[] metaData);

}
