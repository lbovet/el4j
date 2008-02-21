/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ${groupId};

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * Sample JUnit testcase for the Hello World project.
 *
 * @author Martin Zeltner (MZE)
 */
public class CalculatorTest extends TestCase {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(CalculatorTest.class);

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
        try {
            c.div(dividend, divisor);
            fail("No exception on division by zero!");
        } catch (RuntimeException e) {
            s_logger.debug("Expected exception caught.", e);
        }
    }
}
