/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.remoting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;

import junit.framework.TestCase;


/**
 * This class is a test for the calculator.
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
public class CalculatorRmiTest extends TestCase {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(CalculatorRmiTest.class);
    
    /**
     * Configuration locations.
     * Attention: Be aware that the server is loaded before the client!
     */
    private static final String[] CONFIG_LOCATIONS = {
        "classpath*:mandatory/*.xml",
        "server/app/remotingtests-rmi-server-config.xml",
        "client/remotingtests-rmi-client-config.xml"
    };

    /**
     * Instance of the calculator proxy.
     */
    private Calculator m_calc;

    /**
     * {@inheritDoc}
     */
    public void setUp() {
        ModuleApplicationContext appContext 
            = new ModuleApplicationContext(
                CONFIG_LOCATIONS, true);
        m_calc = (Calculator) appContext.getBean("calculator");
    }

    /**
     * This test tests the area calulation method.
     */
    public void testAreaCalculation() {
        final double VALUE_A = 2.3;
        final double VALUE_B = 5.7;
        final double FAULT_DELTA = 0.00000001;
        double result = m_calc.getArea(VALUE_A, VALUE_B);
        assertEquals("The area is not correctly calculated.", result, 
            VALUE_A * VALUE_B, FAULT_DELTA);
    }

    /**
     * This test tests the exception handling.
     * @throws Exception
     */
    public void testExceptionBehaviour() {
        try {
            m_calc.throwMeAnException();
            fail("No exception was thrown.");
        } catch (CalculatorException e) {
            s_logger.debug("Expected exception caught.", e);
        }
    }
}