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

import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleClassAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleClassAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationFour;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleMethodAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationFive;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationFour;

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
@ExamplePackageAnnotationFour("ExamplePackageAnnotationFour: "
        + "Overwritten by Class FooImpl")
@ExampleInterfaceAnnotationThree("ExampleInterfaceAnnotationThree: "
        + "Overwritten by Class FooImpl")
@ExampleAbstractClassAnnotationTwo("ExampleAbstractClassAnnotationTwo: "
        + "Overwritten by Class FooImpl")
@ExampleClassAnnotationOne()
@ExampleClassAnnotationTwo()
public class FooImpl extends FooAbstract implements FooBase{

    @Override
    public void testInternalClass() {
        // TODO Auto-generated method stub

    }
    
    @ExampleMethodAnnotationOne()
    @ExamplePackageAnnotationFive("ExamplePackageAnnotationFive: "
            + "Overwritten by Method metaDataTester")
    @ExampleInterfaceAnnotationFour("ExampleInterfaceAnnotationFour: "
            + "Overwritten by Method metaDataTester")
    @ExampleAbstractClassAnnotationThree("ExampleAbstractClassAnnotationThree: "
            + "Overwritten by Method metaDataTester")
    @ExampleClassAnnotationTwo("ExampleClassAnnotationTwo: "
            + "Overwritten by Method metaDataTester")
    public Object[] metaDataClassInheritance(Object[] metaData) {
        return metaData;
    }
    
    @ExampleMethodAnnotationOne()
    @ExampleInterfaceAnnotationFour("ExampleInterfaceAnnotationFour: "
            + "Overwritten by Method metaDataMethodInheritance")
    public Object[] metaDataMethodInheritance(Object[] metaData) {
        return metaData;
    }

}
