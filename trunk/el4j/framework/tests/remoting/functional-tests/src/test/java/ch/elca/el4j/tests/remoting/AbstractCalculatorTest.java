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
package ch.elca.el4j.tests.remoting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorOperation;

/**
 * This class is a test for the calculator.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Waraich Rashid (RWA)
 */

public abstract class AbstractCalculatorTest {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractCalculatorTest.class);

    /**
     * Instance of the calculator proxy.
     */
    private Calculator m_calc;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {
        ApplicationContext appContext = getContext();
        m_calc = (Calculator) appContext.getBean("calculator");
    }

    /**
     * Gives back the Context of the test.
     * @return Returns the mApplicationContext
     */    
    abstract ApplicationContext getContext();
   
    /**
     * This test tests the area calculation method.
     */
    @Test
    public void testAreaCalculation() {
        final double a = 2.3;
        final double b = 5.7;
        final double delta = 0.00000001;
        double result = m_calc.getArea(a, b);
        assertEquals("The area is not correctly calculated.", result, 
            a * b, delta);
    }
    
    /**
     * This test tests the exception handling.
     * @throws Exception
     */
    @Test
    public void testExceptionBehaviour() {
        try {
            m_calc.throwMeAnException();
            fail("No exception was thrown.");
        } catch (CalculatorException e) {
            s_logger.debug("Expected exception caught.", e);
        }
    }
    
    /**
     * Tests if the protocol is able to handle enumerations.
     */
    @Test
    public void testAbilityToHandleEnumerations() {
        // Checkstyle: MagicNumber off
        double result
            = m_calc.calculate(1.2, 2.5, CalculatorOperation.ADDITION);
        assertEquals(3.7, result, 0.1);
        result
            = m_calc.calculate(1.2, 2.5, CalculatorOperation.SUBTRACTION);
        assertEquals(-1.3, result, 0.1);
        //Checkstyle: MagicNumber on
    }
}