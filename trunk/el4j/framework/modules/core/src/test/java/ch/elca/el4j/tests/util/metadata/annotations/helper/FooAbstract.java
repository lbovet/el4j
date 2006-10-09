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

import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationThree;

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
@ExamplePackageAnnotationThree("ExamplePackageAnnotationThree: "
    + "Overwritten by Class FooAbstract")
@ExampleInterfaceAnnotationTwo("ExampleInterfaceAnnotationTwo: "
    + "Overwritten by Class FooAbstract")
@ExampleAbstractClassAnnotationOne()
@ExampleAbstractClassAnnotationTwo()
@ExampleAbstractClassAnnotationThree()
public abstract class FooAbstract implements Foo {

    public abstract void testInternalClass();

    @ExampleInterfaceAnnotationThree("ExampleInterfaceAnnotationThree: "
        + "Overwritten by Method metaDataMethodInheritance in "
        + "Class FooAbstract")
    public abstract Object[] metaDataMethodInheritance(Object[] metaData);

}
