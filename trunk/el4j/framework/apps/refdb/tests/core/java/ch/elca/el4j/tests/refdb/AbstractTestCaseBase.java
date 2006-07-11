/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.refdb;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.apps.refdb.dao.ReferenceDao;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This class is a base class for tests in module-refdb-core.
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
     * String array to point to the used configuration files.
     */
    /*
    private String[] m_includeConfigLocations = {
        "classpath:optional/interception/methodTracing.xml",
        "classpath*:mandatory/*.xml",
        "classpath*:scenarios/db/raw/*.xml",
        "classpath*:scenarios/dataaccess/ibatis/*.xml",
        "classpath:optional/interception/transactionCommonsAttributes.xml" };
    */

    /**
     * String array to declare files which should not be used.
     */
    /*
    private String[] m_excludeConfigLocations = null;
    */

    /**
     * Application context to load beans.
     */
    private ApplicationContext m_applicationContext;

    /**
     * Data source. Created by application context.
     */
    private DataSource m_dataSource;

    /**
     * Reference dao from refdb. Created by application context.
     */
    private ReferenceDao m_referenceDao;

    /**
     * Reference dao from refdb. Created by application context.
     */
    private ReferenceService m_referenceService;

    /**
     * Default constructor. Loads the application context.
     */
    /*protected AbstractTestCaseBase() {
        m_applicationContext = new ModuleApplicationContext(
                m_includeConfigLocations, m_excludeConfigLocations, false,
                (ApplicationContext) null);
    }*/
    
    /**
     * Hide default constructor.
     */
    protected AbstractTestCaseBase() { }

    /**
     * @return Returns the applicationContext.
     */
    protected synchronized ApplicationContext getApplicationContext() {
        //return m_applicationContext;
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
            con.createStatement().execute(
                "DELETE FROM REFERENCEKEYWORDRELATIONSHIPS");
            con.createStatement().execute(
                "DELETE FROM FILES");
            con.createStatement().execute(
                "DELETE FROM ANNOTATIONS");
            con.createStatement().execute(
                "DELETE FROM LINKS");
            con.createStatement().execute(
                "DELETE FROM BOOKS");
            con.createStatement().execute(
                "DELETE FROM FORMALPUBLICATIONS");
            con.createStatement().execute(
                "DELETE FROM REFERENCESTABLE");
            con.createStatement().execute(
                "DELETE FROM KEYWORDS");
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

    /**
     * Method to add a fake reference to database.
     * 
     * @param name
     *            Is the name the fake reference must have.
     * @return Returns the key of the created reference.
     */
    protected int addFakeReference(String name) {
        LinkDto link = new LinkDto();
        link.setName(name);
        ReferenceDao dao = getReferenceDao();
        link = dao.saveLink(link);
        return link.getKey();
    }
    
    /**
     * Method to add a default fake reference to database.
     * 
     * @return Returns the primary key of the fake reference.
     */
    protected int addDefaultFakeReference() {
        return addFakeReference("Fake reference");
    }
    
    /**
     * @return Returns the referenceDao.
     */
    protected ReferenceDao getReferenceDao() {
        if (m_referenceDao == null) {
            m_referenceDao 
                = (ReferenceDao) getApplicationContext().getBean(
                    "referenceDao");
        }
        return m_referenceDao;
    }
    
    /**
     * @return Returns the reference service.
     */
    protected ReferenceService getReferenceService() {
        if (m_referenceService == null) {
            m_referenceService 
                = (ReferenceService) getApplicationContext().getBean(
                    "referenceService");
        }
        return m_referenceService;
    }
}
