/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.keyword;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This class is a base class for tests in module <code>keyword-core</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractTestCaseBase extends TestCase {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractTestCaseBase.class);

    /**
     * Application context to load beans.
     */
    private ConfigurableApplicationContext m_applicationContext;

    /**
     * Data source. Created by application context.
     */
    private DataSource m_dataSource;

    /**
     * Hide default constructor.
     */
    protected AbstractTestCaseBase() { }

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
        return true;
    }

    /**
     * @return Returns the string array with exclude locations.
     */
    protected abstract String[] getExcludeConfigLocations();

    /**
     * @return Returns the string array with include locations.
     */
    protected abstract String[] getIncludeConfigLocations();

    /**
     * @return Returns the dataSource.
     */
    protected DataSource getDataSource() {
        if (m_dataSource == null) {
            m_dataSource 
                = (DataSource) getApplicationContext().getBean("dataSource");
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
            con.commit();
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
    
    @Override
    protected void tearDown() throws Exception {
        if (m_applicationContext != null) {
            m_applicationContext.close();
        }
        super.tearDown();
    }
}