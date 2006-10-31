/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.tests.util.metadata.attributes;


/**
 * This is the interface of class <code>FooImpl</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface Foo {
    /**
     * This method will be intercepted by the defined attribute.
     * 
     * @param number
     *            The number to multiply with the base variable.
     * @return The product of base and number.
     * 
     * @@ch.elca.el4j.tests.util.metadata.attributes.ExampleAttributeOne(16)
     * @@ch.elca.el4j.tests.util.metadata.attributes.ExampleAttributeTwo(478)
     */
    public int test(int number);

    /**
     * This method will be intercepted by the defined attribute.
     * 
     * @param number
     *            The number to multiply with the base variable.
     * @param innerClass
     *            The inner class.
     * @return The product of base and number.
     * 
     * @@ch.elca.el4j.tests.util.metadata.attributes.ExampleAttributeOne(25)
     * @@ch.elca.el4j.tests.util.metadata.attributes.ExampleAttributeTwo(998)
     */
    public int test(int number, FooImpl.Bar innerClass);
}