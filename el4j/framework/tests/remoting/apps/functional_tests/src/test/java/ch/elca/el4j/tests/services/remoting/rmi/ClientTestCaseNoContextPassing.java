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
package ch.elca.el4j.tests.services.remoting.rmi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.services.remoting.loadbalancing.common.BusinessObject;

/**
 * This class tests the idempotent invocation interceptor that handles retrials
 * by itself. <script type="text/javascript">printFileStatus ("$URL$",
 * "$Revision$", "$Date$", "$Author$" );</script>
 * 
 * @author Stefan Pleisch (SPL)
 */
public class ClientTestCaseNoContextPassing extends TestCase {

    public ClientTestCaseNoContextPassing() {
        super();
        m_applicationContext = new ModuleApplicationContext(
            getInclusiveConfigLocations(), 
            (String[]) null, 
            false, 
            null);
    } // <init>

    /**
     * Tests whether the server/DB properly abort the transaction upon reception
     * of a special flag.
     * 
     * @see #THROW_IDEMPOTENTINVOCATION_EXCEPTION
     */
    public void testNextProtocol() {
        getLog().debug("Starting test 'testIdempotence'....");
        try {
            s_logger.debug("-- Calling with hello 1") ;
            String result = getTestObj().call("hello1") ;
            assertEquals("Server 1 should have executed this code.", 
                         "localhost:8089",
                         result);
            s_logger.debug("-- Calling with hello 2") ;
            result = getTestObj().call("hello2") ;
            assertEquals("Server 1 should have executed this code.", 
                "localhost:8089",
                result);
            s_logger.debug("-- Calling with hello 3") ;
            result = getTestObj().call("hello3") ;
            assertEquals("Server 1 should have executed this code.", 
                "localhost:8089",
                result);
            s_logger.debug("-- Calling with hello 4") ;
            result = getTestObj().call("hello4") ;
            assertEquals("Server 1 should have executed this code.", 
                         "localhost:8089",
                         result);
            s_logger.debug("-- Calling with hello 5") ;
            result = getTestObj().call("hello5") ;
            assertEquals("Server 1 should have executed this code.", 
                "localhost:8089",
                result);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        } // catch
    } // testSecondAccess()

    /** {@inheritDoc} */
    protected void runTest() {
        testNextProtocol();
    } // runTest()

    /** {@inheritDoc} */
    protected String[] getInclusiveConfigLocations() {
        return new String[] {
            "classpath*:mandatory/*.xml",
            "classpath:rmi/rmi-nocontext-protocol-config.xml",
           "classpath:rmi/startup-client.xml"};
     } // getInclusiveConfigLocations()

    protected Log getLog() {
        return s_logger;
    } // getLog()

    protected BusinessObject getTestObj() {
        if (m_obj == null) {
            BeanFactory factory = (BeanFactory) m_applicationContext;

            m_obj = (BusinessObject) factory.getBean("rmiBusinessObj");

        } // if
        return m_obj;
    } // getTestObj

    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
        .getLog(ClientTestCaseNoContextPassing.class);

    private ApplicationContext m_applicationContext ;
    
    private BusinessObject m_obj ;
    
} // CLASS ClientMultipleServerTestCase
