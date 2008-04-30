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
package ch.elca.el4j.tests.refdb;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dao.BookDao;
import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.apps.refdb.dao.LinkDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;

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
     * Application context to load beans.
     */
    private ConfigurableApplicationContext m_applicationContext;

    /**
     * Data source. Created by application context.
     */
    private DataSource m_dataSource;

    /**
     * Link DAO. Created by application context.
     */
    private LinkDao m_linkDao;
    
    
    /**
     * FormalPublication DAO. Created by application context.
     */
    private FormalPublicationDao m_formalPublicationDao;

    /**
     * Book DAO. Created by application context.
     */
    private BookDao m_bookDao;

    
    /**
     * Annotation DAO. Created by application context.
     */
    private AnnotationDao m_annotationDao;
    
    /**
     * File DAO. Created by application context.
     */
    private FileDao m_fileDao;
    
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

    /**
     * Method to add a fake reference to database.
     * 
     * @param name
     *            Is the name the fake reference must have.
     * @return Returns the key of the created reference.
     */
    protected int addFakeReference(String name) {
        Link link = new Link();
        link.setName(name);
        LinkDao dao = getLinkDao();
        link = dao.saveOrUpdate(link);
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
     * @return Returns the link DAO.
     */
    protected LinkDao getLinkDao() {
        if (m_linkDao == null) {
            DefaultDaoRegistry daoRegistry 
                = (DefaultDaoRegistry) getApplicationContext()
                    .getBean("daoRegistry");
            m_linkDao = (LinkDao) daoRegistry.getFor(Link.class);
        }
        return m_linkDao;
    }
    
    /**
     * @return Returns the formalPublication DAO.
     */
    protected FormalPublicationDao getFormalPublicationDao() {
        if (m_formalPublicationDao == null) {
            DefaultDaoRegistry daoRegistry 
                = (DefaultDaoRegistry) getApplicationContext()
                    .getBean("daoRegistry");
            m_formalPublicationDao 
                   = (FormalPublicationDao) daoRegistry.getFor(
                    FormalPublication.class);
        }
        return m_formalPublicationDao;
    }
    
    
    /**
    * @return Returns the book DAO.
    */
   protected BookDao getBookDao() {
       if (m_bookDao == null) {
           DefaultDaoRegistry daoRegistry 
               = (DefaultDaoRegistry) getApplicationContext()
                   .getBean("daoRegistry");
           m_bookDao 
                  = (BookDao) daoRegistry.getFor(
                   Book.class);
       }
       return m_bookDao;
   }
    
    
    /**
     * @return Returns the annotation DAO.
     */
    protected AnnotationDao getAnnotationDao() {
        if (m_annotationDao == null) {
            DefaultDaoRegistry daoRegistry 
                = (DefaultDaoRegistry) getApplicationContext()
                    .getBean("daoRegistry");
            m_annotationDao = (AnnotationDao) daoRegistry
                .getFor(Annotation.class);
        }
        return m_annotationDao;
    }
    
    /**
     * @return Returns the file DAO.
     */
    protected FileDao getFileDao() {
        if (m_fileDao == null) {
            DefaultDaoRegistry daoRegistry 
                = (DefaultDaoRegistry) getApplicationContext()
                    .getBean("daoRegistry");
            m_fileDao = (FileDao) daoRegistry
                .getFor(File.class);
        }
        return m_fileDao;
    }
    
}
