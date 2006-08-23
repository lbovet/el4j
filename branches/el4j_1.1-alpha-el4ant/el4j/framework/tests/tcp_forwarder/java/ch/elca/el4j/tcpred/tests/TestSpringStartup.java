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
package ch.elca.el4j.tcpred.tests;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tcpred.TcpForwarder;
import ch.elca.el4j.util.env.EnvPropertiesUtils;

import junit.framework.TestCase;

/**
 * 
 * This class tests whether Spring can start if there is no database connection.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class TestSpringStartup extends TestCase {
    
    
    
    /**
     * Delay between the single test steps (in milliseconds).
     */
    static final int DELAY = 2000;
    
    /**
     * New input port -> Forwarder between INPUT_PORT and target port resp.
     * target server.
     */
    static final int INPUT_PORT = 6789;
    
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
        = LogFactory.getLog(TestSpringStartup.class);
    
    /**
     * Application context to load beans.
     */
    private ApplicationContext m_applicationContext;
    
    /**
     * @return Returns the applicationContext.
     */
    protected synchronized ApplicationContext getApplicationContext() {
        if (m_applicationContext == null) {
            m_applicationContext = new ModuleApplicationContext(
                getIncludeConfigLocations(), getExcludeConfigLocations(), 
                isBeanOverridingAllowed(), (ApplicationContext) null);
        }
        return m_applicationContext;
    }

    /**
     * @return Returns <code>true</code> if bean definition overriding should
     *         be allowed.
     */
    protected boolean isBeanOverridingAllowed() {
        return false;
    }
    
    /**
     * @return Returns the string array with exclude locations.
     */
    protected String[] getExcludeConfigLocations() {
        return new String[] { 
            "classpath*:scenarios/dataaccess/hibernate/keyword-core-repository-hibernate-config.xml"
        };
    }

    /**
     * @return Returns the string array with include locations.
     */
    protected String[] getIncludeConfigLocations() {
        return new String[] {
            "classpath:optional/interception/methodTracing.xml",
            "classpath*:mandatory/*.xml",
            "classpath*:scenarios/db/raw/*.xml",
            "classpath*:scenarios/dataaccess/ibatis/*.xml",
            //"classpath*:scenarios/dataaccess/hibernate/*.xml",
            "classpath*:optional/interception/transactionJava5Annotations.xml"};
    }
    
    /**
     * This test tries to start Spring when there is no connection to a
     * database.
     */
    public void testSpringWithoutDBConnection() throws Exception {
        
        String dbName = EnvPropertiesUtils.getEnvProperties().getProperty(
            "db.name");

        boolean db2 = dbName.equals("db2");

        TcpForwarder ti;
        if (db2) {
            ti = new TcpForwarder(INPUT_PORT, DERBY_DEST_PORT);
        } else {
            SocketAddress target = new InetSocketAddress(Inet4Address
                .getByName(ORACLE_SERVER_NAME), ORACLE_DEST_PORT);
            ti = new TcpForwarder(INPUT_PORT, target);
        }

        Thread.sleep(DELAY);
        
        // Cutting the connection to the database
        ti.unplug();
        
        KeywordService service = null;
        
        try {
            service = (KeywordService) getApplicationContext()
                .getBean("keywordService");
            s_logger.debug(service.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Spring failed to start up...");
        }
        
        // Establishing the connection to the database
        ti.plug();
        
        List<KeywordDto> keywordsList = service.getAllKeywords();
        for (KeywordDto k : keywordsList) {
            service.removeKeyword(k.getKey());
        }
        KeywordDto newKeyword = new KeywordDto();
        newKeyword.setName("NewKeyword");
        newKeyword.setDescription("NewKeyword description");
        
        service.saveKeyword(newKeyword);
        
        KeywordDto newKeyword2 = new KeywordDto();
        newKeyword2.setName("NewKeyword");
        newKeyword.setDescription("NewKeyword 2 description");
        
        try {
            service.saveKeyword(newKeyword2);
        } catch (DataIntegrityViolationException e) {
            s_logger.debug("Expected exception catched.", e);
        } catch (Exception e) {
            fail("Exception translation has not been performed correctly.");
        }
    }
}
