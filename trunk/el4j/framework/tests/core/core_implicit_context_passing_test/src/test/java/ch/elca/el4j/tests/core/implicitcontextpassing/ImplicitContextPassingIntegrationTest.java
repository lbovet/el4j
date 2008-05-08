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

package ch.elca.el4j.tests.core.implicitcontextpassing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.tests.remoting.service.Calculator;

// Checkstyle: MagicNumber off
// Checkstyle: EmptyBlock off

/**
 * This integration test checks correctness of the Implicit context passer.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author David Stefan (DST)
 */

public class ImplicitContextPassingIntegrationTest {

    /**
     * Location where config files can be found.
     * The order of the config files is crucial. If the client is started before
     * the server, it obviously won't work.
     */
    private static final String[] CONFIG_LOCATION 
        = new String[]{
            "classpath:scenarios/implicitcontextpassing/rmi-server-config.xml",
            "classpath:scenarios/implicitcontextpassing/rmi-client-config.xml"
        };

    /**
     * ApplicationContext object.
     */
    private ApplicationContext m_appContext;

    /**
     * ImplicitContextPassingRegistry for client.
     */
    private ImplicitContextPassingRegistry m_clientRegistry;

    /**
     * ImplicitContextPassingRegistry for server.
     */
    private ImplicitContextPassingRegistry m_serverRegistry;
    
    /**
     * Implicit context passer for client.
     */
    private ImplicitContextPassTester m_clientPasserA;

    /**
     * Implicit context passer for server.
     */
    private ImplicitContextPassTester m_serverPasserA; 
    
    /**
     * Inteface of service provided by server.
     */
    private Calculator m_calc;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {

        m_appContext = new ModuleApplicationContext(CONFIG_LOCATION, true);
        m_clientRegistry = (ImplicitContextPassingRegistry) m_appContext
            .getBean("clientImplicitContextPassingRegistry");

        m_serverRegistry = (ImplicitContextPassingRegistry) m_appContext
            .getBean("serverImplicitContextPassingRegistry");
        
        m_serverPasserA = (ImplicitContextPassTester) m_appContext
            .getBean("serverPasserA");
        
        m_clientPasserA = (ImplicitContextPassTester) m_appContext
            .getBean("clientPasserA");
        
        m_calc = (Calculator) m_appContext.getBean("calculator");
    }

    /**
     * This test simulates registration with contextPasser of client beeing
     * null.
     */
    @Test
    public void testClientContextPasserIsNull() {
        try {
            m_clientRegistry.registerImplicitContextPasser(null);
            fail("Passing null should raise an exception");
        } catch (NullPointerException e) {
            // should raise a nullpointer exception
        }
    }

    /**
     * This test simulates registration with contextPasser of server beeing
     * null.
     */
    @Test
    public void testServerContextPasserIsNull() {
        try {
            m_serverRegistry.registerImplicitContextPasser(null);
            fail("Passing null should raise an exception");
        } catch (NullPointerException e) {
            // should raise a nullpointer exception
        }
    }

    /**
     * This test simulates assignment to passer of a registry beeing null .
     */
    @Test
    public void testRegistryIsNull() {
        ImplicitContextPasserImplA passerA = new ImplicitContextPasserImplA();
        try {
            passerA.setImplicitContextPassingRegistry(null);
            fail("Passing null should raise an exception");
        } catch (RuntimeException e) {
            // should raise a runtime exception
        }
    }

    /**
     * This tests simulates context passing with one passer on each side, 
     * passing an integer.
     */ 
    @Test
    public void testOnePasserIntegerData() {
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.INT_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), 
            m_serverPasserA.getReceivedData());
        // Reset data for next test
        m_serverPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);
    }
    
    /**
     * This tests simulates context passing with one passer on each side, 
     * passing a float.
     */ 
    @Test
    public void testOnePasserFloatData() {
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.FLOAT_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), 
            m_serverPasserA.getReceivedData());
        // Reset data for next test
        m_serverPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);

    }
    
    /**
     * This tests simulates context passing with one passer on each side, 
     * passing an double.
     */
    @Test
    public void testOnePasserDoubleData() {
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.DOUBLE_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), 
            m_serverPasserA.getReceivedData());
        // Reset data for next test
        m_serverPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);

    }
    
    /**
     * This tests simulates context passing with one passer on each side, 
     * passing a list.
     */
    @Test
    public void testOnePasserListData() {
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.LIST_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), 
            m_serverPasserA.getReceivedData());
        // Reset data for next test
        m_serverPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);
    }

   /**
     * This tests simulates context passing with one passer on each side, 
     * passing an empty list.
     */ 
    @Test
    public void testOnePasserEmptyListData() {
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.NULL_LIST_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), 
            m_serverPasserA.getReceivedData());
        // Reset data for next test
        m_serverPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);
    }    
    
   /**
    * This test checks if context is NOT passed if service method isn't called.
    */
    @Test
    public void testServiceNotCalled() {
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.DOUBLE_TEST);
        assertNull(m_serverPasserA.getReceivedData());
        // Reset data for next test
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.RESET);
    }
    
    /**
     * This tests simulates context passing with two passers on each side, 
     * passing a double and a list.
     */
    @Test
    public void testTwoPasser() {
        
        ImplicitContextPasserImplB serverPasserB = (ImplicitContextPasserImplB) 
            m_appContext.getBean("serverPasserB");
    
        ImplicitContextPasserImplB clientPasserB = (ImplicitContextPasserImplB) 
            m_appContext.getBean("clientPasserB");
        
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.DOUBLE_TEST);
        clientPasserB.setDataToUse(ImplicitContextPassTester.LIST_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), m_serverPasserA
            .getReceivedData());
        assertEquals(clientPasserB.getTestData(), serverPasserB
            .getReceivedData());
    }
    
    /**
     * This tests simulates context passing with two passers on client side 
     * and one on server side, passing a double and a list.
     */
    @Test
    public void testTwoAndOnePasser() {
        
        ImplicitContextPasserImplB clientPasserB = (ImplicitContextPasserImplB) 
            m_appContext.getBean("clientPasserB");
        
        m_clientPasserA.setDataToUse(ImplicitContextPasserImplA.DOUBLE_TEST);
        clientPasserB.setDataToUse(ImplicitContextPassTester.DOUBLE_TEST);
        m_calc.getArea(0.65, 0.78);
        assertEquals(m_clientPasserA.getTestData(), m_serverPasserA
            .getReceivedData());
        assertNotSame(clientPasserB.getTestData(), m_serverPasserA
            .getReceivedData());
    }
    
}
// Checkstyle: EmptyBlock on
// Checkstyle: MagicNumber on
