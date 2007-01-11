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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.tcpforwarder.TcpForwarder;
import ch.elca.el4j.tests.tcpforwarder.dom.Name;
import ch.elca.el4j.util.env.EnvPropertiesUtils;

import junit.framework.TestCase;

/**
 * 
 * This class is a TestSuite for the TCP Forwarder Module. 
 * It tests 
 * 1) Wheter a store call to a database (using hibernate) is forwarded 
 * to the right port.
 * 2) The Application Context can be created and the Dao retrieved with the
 * connection to the database cut. 
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
public class TcpForwarderTest extends TestCase {

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
     * Config locations.
     */
    private static final String[] CONFIG_LOCATIONS = {
        "classpath*:mandatory/*.xml",
        "classpath*:scenarios/db/raw/*.xml",
        "classpath*:scenarios/dataaccess/hibernate/*.xml",
        "classpath*:scenarios/dataaccess/hibernate/name/*.xml",
        "classpath*:optional/interception/transactionJava5Annotations.xml"};
    
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(TcpForwarderTest.class);
    
    /**
     * Application context to load beans.
     */
    private ConfigurableApplicationContext m_appContext;
    
    /**
     * Are we executing the tests on a DB2 database?
     */
    private boolean m_isDB2;
    
    /**
     * The TCP Forwarder.
     */
    private TcpForwarder m_forwarder;
    
    /**
     * Data Access Object.
     */
    private ConvenienceGenericDao<Name, Integer> m_dao;
    
    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        String dbName 
            = EnvPropertiesUtils.getEnvProperties().getProperty("db.name");
        m_isDB2 = dbName.equals("db2");
        if (m_isDB2) {
            m_forwarder = new TcpForwarder(INPUT_PORT, DERBY_DEST_PORT);
        } else {
            InetSocketAddress target = new InetSocketAddress(Inet4Address
                .getByName(ORACLE_SERVER_NAME), ORACLE_DEST_PORT);
            m_forwarder = new TcpForwarder(INPUT_PORT, target);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        m_forwarder.unplug();
        m_appContext.close();
        super.tearDown();
    }
    
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
        // Try if connection works
        executeFirstInsert();
        // Unplug and check, if database connection is down
        m_forwarder.unplug();
        Thread.sleep(DELAY);
        executeSecondInsert();
        Thread.sleep(DELAY);
        // Plug again and check if connection works again
        s_logger.debug("Restoring Link");
        m_forwarder.plug();
        Thread.sleep(DELAY);
        executeThirdInsert();
        Thread.sleep(DELAY);
        s_logger.debug("TEST OK");
    }
    
    /**
     * This test tries to start Spring when there is no connection to a
     * database.
     */
    public void testSpringWithoutDBConnection() throws Exception {
        // Cutting the connection to the database
        m_forwarder.unplug();

        try {
            getApplicationContext().getBean("nameDao");
            s_logger.debug("Spring context started up successfully.");
        } catch (Exception e) {
            fail("Spring failed to start up...");
        }

        // Establishing the connection to the database
        m_forwarder.plug();

        List<Name> nameList = getDao().findAll();
        for (Name k : nameList) {
            getDao().delete(k.getKey());
        }
        Name newName = new Name();
        newName.setName("NewName");

        getDao().saveOrUpdate(newName);

        Name newName2 = new Name();
        newName2.setName("NewName");

        try {
            getDao().saveOrUpdate(newName2);
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.");
        } catch (Exception e) {
            fail("Exception translation has not been performed correctly.");
        }
        s_logger.debug("TEST OK");
    }
    
    /**
     * @return Returns the applicationContext.
     */
    private ApplicationContext getApplicationContext() {
        if (m_appContext == null) {
            m_appContext 
                = new ModuleApplicationContext(CONFIG_LOCATIONS, false);
        }
        return m_appContext;
    }
    
    /**
     * @return The DAO for the inserts
     */
    @SuppressWarnings("unchecked")
    private ConvenienceGenericDao<Name, Integer> getDao() {
        if (m_dao == null) {
            m_dao = (ConvenienceGenericDao<Name, Integer>) 
                 getApplicationContext().getBean("nameDao");
        }
        return m_dao;
    }
    
    /**
     * Execute store of data object to test database.
     * 
     * @throws InterruptedException
     */
    private void executeFirstInsert() throws InterruptedException {
        if (m_isDB2) {
            s_logger.debug("testing if port '" + DERBY_DEST_PORT 
                + "' is up...");
        } else {
            s_logger.debug("testing if server '" + ORACLE_SERVER_NAME
                + "' is up...");
        }
        Thread.sleep(DELAY);
        try {
            Name name = new Name();
            name.setName("First");
            name = (Name) getDao().saveOrUpdate(name);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Database not reachable -> Test FAILED");
        }       
        s_logger.debug("TEST OK");
        Thread.sleep(DELAY);
        if (m_isDB2) {
            s_logger.debug("Cutting Link to port '" + DERBY_DEST_PORT + "'");
        } else {
            s_logger.debug("Cutting Link to server '" + ORACLE_SERVER_NAME
                + "'");
        }
    }
    /**
     * Execute the second store, which is supposed to fail, because
     * the TCP fowarder is unplugged.
     */
    private void executeSecondInsert() {
        try {
            Name name = new Name();
            name.setName("Second");
            name = (Name) getDao().saveOrUpdate(name);
            fail("Connection is still up");
        } catch (Exception e) {
            s_logger.debug("TEST OK");
        }
    }

    /**
     * Execute third store to check if database connection works after we 
     * have plugged it again.
     */
    private void executeThirdInsert() {
        if (m_isDB2) {
            s_logger.debug("testing if port '" + DERBY_DEST_PORT
                + "' is up again");
        } else {
            s_logger.debug("testing if server '" + ORACLE_SERVER_NAME
                + "' is up again");
        }
        try {
            Name name = new Name();
            name.setName("Third");
            name = (Name) getDao().saveOrUpdate(name);
        } catch (Exception e) {
            fail("Database not reachable -> Test FAILED");
        }
    }  
}
