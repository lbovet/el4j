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
package ch.elca.helloworld.tests;

import ch.elca.helloworld.services.Calculator;

import junit.framework.TestCase;

/**
 * Sample JUnit testcase for the Hello World project.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class CalculatorTest extends TestCase {
    /**
     * Tests the division method of calculator.
     */
    public void testCalculatorDivision() {
        Calculator c = new Calculator();
        // Checkstyle: MagicNumber off
        double dividend = 3;
        double divisor = 7;
        double delta = 0.000001;
        // Checkstyle: MagicNumber on
        double result = c.div(dividend, divisor);
        assertEquals(dividend / divisor, result, delta);
        
        divisor = 0;
        // Checkstyle: EmptyBlock off
        try {
            c.div(dividend, divisor);
            fail("No exception on division by zero!");
        } catch (RuntimeException e) { }
        // Checkstyle: EmptyBlock on
    }
}
