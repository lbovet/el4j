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
package ch.elca.el4j.tests.keyword;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This class is a base class for tests in module <code>keyword-core</code>.
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
public abstract class TestCaseBase extends TestCase {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(TestCaseBase.class);

    /**
     * String array to point to the used configuration files.
     */
    private String[] m_includeConfigLocations = {
        "classpath:optional/interception/methodTracing.xml",
        "classpath*:mandatory/*.xml",
        "classpath:scenarios/db/rawDatabase.xml",
        "classpath:scenarios/dataaccess/ibatisSqlMaps.xml",
        "classpath:optional/interception/transactionCommonsAttributes.xml" };

    /**
     * String array to declare files which should not be used.
     */
    private String[] m_excludeConfigLocations = null;

    /**
     * Application context to load beans.
     */
    private ApplicationContext m_applicationContext;

    /**
     * Data source. Created by application context.
     */
    private DataSource m_dataSource;

    /**
     * Default constructor. Loads the application context.
     */
    public TestCaseBase() {
        m_applicationContext = new ModuleApplicationContext(
            m_includeConfigLocations, m_excludeConfigLocations, false,
            (ApplicationContext) null);
    }

    /**
     * @return Returns the applicationContext.
     */
    protected ApplicationContext getApplicationContext() {
        return m_applicationContext;
    }

    /**
     * @return Returns the dataSource.
     */
    protected DataSource getDataSource() {
        if (m_dataSource == null) {
            m_dataSource 
                = (DataSource) m_applicationContext.getBean("dataSource");
        }
        return m_dataSource;
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        Connection con = null;
        try {
            con = getDataSource().getConnection();
            
            try {
                con.createStatement().execute(
                    "DELETE FROM REFERENCEKEYWORDRELATIONSHIPS");
            } catch (SQLException e) {
                s_logger.info("There was a problem while deleting rows of "
                    + "table 'REFERENCEKEYWORDRELATIONSHIPS'. Maybe the table "
                    + "does not exist.");
            }
            con.createStatement().execute("DELETE FROM KEYWORDS");
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    s_logger.info("Connection could not be closed.");
                }
            }
        }
    }
}