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
package ch.elca.el4j.tests.tcpforwarder;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.tcpforwarder.TcpForwarder;

import net.sourceforge.jwebunit.TestingEngineRegistry;
import net.sourceforge.jwebunit.WebTestCase;

/**
 * This class tests the tcp redirector to connect to a web page.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/tests/tcp_forwarder/src/test/java/ch/elca/el4j/tests/tcpforwarder/WebpageTest.java $",
 *    "$Revision:1114 $",
 *    "$Date:2006-09-08 09:39:24 +0000 (Fr, 08 Sep 2006) $",
 *    "$Author:swisswheel $"
 * );</script>
 *
 * @author Florian Suess (FLS)
 * @author Alex Mathey (AMA)
 */
public class WebpageTest extends WebTestCase {
 
    /**
     * Delay between the single test steps (in milliseconds).
     */
    static final int DELAY = 2000;
    
    /**
     * New input port -> Forwarder between INPUT_PORT and DEST_PORT.
     */
    static final int INPUT_PORT = 9273;
    
    /**
     * Original URL of the application to test (optional).
     */
    static final String DEST_URL = "www.elca.ch";
    
    /**
     * Original port of the application to test.
     */
    static final int DEST_PORT = 80;
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(WebpageTest.class);
    
    /**
     * Help variable to test if an exception occured.
     */
    int m_gotException = 0;

    /**
     * set up function for WebTests using JWebUnit.
     */
    public void setUp() {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        getTestContext().setBaseUrl("http://localhost:" + INPUT_PORT);    
    }
    
    /**
     * 
     * This test uses a tcp forwarder to connect to a web page and verifies that
     * this connection has been established, then cuts the connection to the
     * web page and verifies that there is no connectivity to the page any
     * more, then reconnects to the web page again and tests whether the
     * connection has been re-established.
     * 
     * Module web-tests is used to perform these tests.
     * 
     * @throws Exception
     */
    public void testForwarder() throws Exception {
        TcpForwarder tf = null;
        try {
            InetSocketAddress target 
                = new InetSocketAddress(Inet4Address.getByName(DEST_URL), 
                        DEST_PORT);
            tf = new TcpForwarder(INPUT_PORT, target);
    
            Thread.sleep(DELAY);
            s_logger.debug("testing if '" + DEST_URL + "' is up...");
            Thread.sleep(DELAY);
            try {
                beginAt("/"); 
            } catch (RuntimeException e) {
                fail("Page not reachable -> Test FAILED");
            }
            
            assertLinkPresentWithText("Newsletter");
            s_logger.debug("TEST OK");
            Thread.sleep(DELAY);
    
            s_logger.debug("Cutting Link to '" + DEST_URL + "'");
            tf.unplug();
            Thread.sleep(DELAY);
    
            // Check if Runtime Exception occurs.  
            try {
                beginAt("/");
            } catch (RuntimeException e) {
                s_logger.debug("Page not reachable");
                m_gotException = 1;
            }
            
            if (m_gotException != 1) {
                fail("Test FAILED, connection still up!");
            }
    
            s_logger.debug("TEST OK");
            Thread.sleep(DELAY);
    
            s_logger.debug("Restoring Link");
            tf.plug();
    
            Thread.sleep(DELAY);
            s_logger.debug("testing if '" + DEST_URL + "' is up again");
            Thread.sleep(DELAY);
    
            try {
                beginAt("/");
            } catch (RuntimeException e) {
                fail("Page not reachable -> Test FAILED");
            }
    
            assertLinkPresentWithText("Newsletter");
            
            // Unplugging again
            tf.unplug();
            tf = null;
            s_logger.debug("TEST OK");
        } finally {
            if (tf != null) {
                try {
                    tf.unplug();
                } catch (RuntimeException e) {
                    s_logger.debug("Swallowed exception in finally block.", e);
                }
            }
        }
    }
}

