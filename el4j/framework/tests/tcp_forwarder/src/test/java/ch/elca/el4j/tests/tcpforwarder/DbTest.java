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
package ch.elca.el4j.tests.tcpforwarder;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.tcpforwarder.TcpForwarder;
import ch.elca.el4j.tests.keyword.dao.HibernateKeywordDaoTest;
import ch.elca.el4j.util.env.EnvPropertiesUtils;

import junit.framework.TestCase;
import junit.framework.TestResult;


/**
 * This class tests the connection to a database via a tcp forwarder.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/tests/tcp_forwarder/src/test/java/ch/elca/el4j/tests/tcpforwarder/DbTest.java $",
 *    "$Revision:1114 $",
 *    "$Date:2006-09-08 09:39:24 +0000 (Fr, 08 Sep 2006) $",
 *    "$Author:swisswheel $"
 * );</script>
 *
 * @author Florian Suess (FLS)
 * @author Alex Mathey (AMA)
 */
public class DbTest extends TestCase {

    /**
     * Delay between the single test steps (in milliseconds).
     */
    static final int DELAY = 2000;
    
    /**
     * New input port -> Forwarder between INPUT_PORT and target port resp.
     * target server.
     */
    static final int INPUT_PORT = 6786;
    
    /**
     * Port of the Derby database.
     */
    static final int DERBY_DEST_PORT = 1527;
    
    /**
     * Port of the Oracle database.
     */
    static final int ORACLE_DEST_PORT = 1521;

    /**
     * Original domain name of the Oracle database server.
     */
    static final String ORACLE_SERVER_NAME = "tulipe.elca.ch";
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(DbTest.class);
    
    /**
     * This test uses a tcp forwarder to connect to a databse and verifies that
     * this connection has been established, then cuts the connection to the
     * database and verifies that there is no connectivity to the database any
     * more, then reconnects to the database again and tests whether the
     * connection has been re-established.
     * 
     * @throws Exception
     */
    public void testForwarder() throws Exception {

        String dbName = EnvPropertiesUtils.getEnvProperties()
            .getProperty("db.name");
        
        boolean db2 = dbName.equals("db2");
        
        TcpForwarder ti;
        if (db2) {
            ti = new TcpForwarder(INPUT_PORT, DERBY_DEST_PORT);
        } else {
            SocketAddress target = new InetSocketAddress(Inet4Address
                .getByName(ORACLE_SERVER_NAME), ORACLE_DEST_PORT);
            ti = new TcpForwarder(INPUT_PORT, target);
        }
        
        HibernateKeywordDaoTest keywordTest = new HibernateKeywordDaoTest();
        keywordTest.setName("testInsertKeywords");
        TestResult testResult = null;
        
        Thread.sleep(DELAY);
        if (db2) {
            s_logger.debug("testing if port '" + DERBY_DEST_PORT 
                + "' is up...");
        } else {
            s_logger.debug("testing if server '" + ORACLE_SERVER_NAME
                + "' is up...");
        }
        Thread.sleep(DELAY);
        
        testResult = keywordTest.run();
        if (!testResult.wasSuccessful()) {
            fail("Database not reachable -> Test FAILED");
        }
                
        s_logger.debug("TEST OK");
        Thread.sleep(DELAY);
        
        if (db2) {
            s_logger.debug("Cutting Link to port '" + DERBY_DEST_PORT + "'");
        } else {
            s_logger.debug("Cutting Link to server '" + ORACLE_SERVER_NAME
                + "'");
        }
        ti.unplug();
        Thread.sleep(DELAY);
        
        testResult = null;
        keywordTest = null;
        keywordTest = new HibernateKeywordDaoTest();
        keywordTest.setName("testInsertKeywords");
        
        testResult = keywordTest.run();
        if (testResult.wasSuccessful()) {
            fail("Connection is still up");
        }
            
        s_logger.debug("TEST OK");
        Thread.sleep(DELAY);

        s_logger.debug("Restoring Link");
        ti.plug();
        
        Thread.sleep(DELAY);
        if (db2) {
            s_logger.debug("testing if port '" + DERBY_DEST_PORT
                + "' is up again");
        } else {
            s_logger.debug("testing if server '" + ORACLE_SERVER_NAME
                + "' is up again");
        }
        Thread.sleep(DELAY);
        
        testResult = null;
        keywordTest = null;
        keywordTest = new HibernateKeywordDaoTest();
        keywordTest.setName("testInsertKeywords");
        testResult = keywordTest.run();
        
        if (!testResult.wasSuccessful()) {
            fail("Database not reachable -> Test FAILED");
        }
            
        // Unplugging again
        ti.unplug();
        s_logger.debug("TEST OK");

    }
    
}
