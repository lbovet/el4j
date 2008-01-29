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
package ch.elca.el4j.tests.seam;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.jboss.seam.contexts.ServletLifecycle;

import org.jboss.seam.mock.SeamTest;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import ch.elca.el4j.web.context.ModuleWebApplicationContext;

import org.testng.annotations.*;

/**
 * This class is a base class for tests in module <code>seam-war</code> using
 * TestNG.
 * It contains code to set up and merge a additional Spring application context.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Frank Bitzer (FBI)
 */
public abstract class BaseEntityManagerTest extends SeamTest{
    
    
    /**
     * the Spring application context containing El4j DAO objects etc.
     */
    ModuleWebApplicationContext m_springApplicationContext;
    

    
    /**
     * Set up Spring application context and call method to clean database
     * before tests are executed.
     * 
     * {@inheritDoc}
     */
    @Override
    @BeforeClass
    public void init() throws Exception {
        
        super.init();
               
        
        ServletContext servletContext = ServletLifecycle.getServletContext();
        
        m_springApplicationContext = new ModuleWebApplicationContext(
            getIncludeConfigLocations(), 
            getExcludeConfigLocations(),
            this.isBeanOverridingAllowed(),
            servletContext,
            true);
        
        servletContext.setAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, 
            m_springApplicationContext);
        
        startupContextLoader(m_springApplicationContext);
        
        
        
        //clean database
        cleanDatabase();
           
        
    }
    
    
    
    /**
     * Simply tests wether session is valid.
     * 
     * @throws Exception
     */
    @Test
    public void unitTestStartOver() throws Exception {
       
       assert !this.isSessionInvalid();
       
    }
    
  
    /**
     * Returns Spring application context that was set up in 
     * <code>init()</code>.
     * 
     * @return
     */
    protected ModuleWebApplicationContext getSpringApplicationContext() {
        return m_springApplicationContext;
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
    protected String[] getExcludeConfigLocations(){
        return new String[] {
          "classpath*:mandatory/keyword/keyword-core-config.xml"};
    }

    /**
     * @return Returns the string array with include locations.
     */
    protected  String[] getIncludeConfigLocations(){
        return new String[] {
          "classpath*:mandatory/*.xml",
          "classpath*:mandatory/generic/*.xml",
          "classpath*:scenarios/db/raw/*.xml",
          "classpath*:scenarios/dataaccess/hibernate/*.xml",
          "classpath*:optional/interception/transactionJava5Annotations.xml"};
        
    }

    /**
     * Starts context loader
     * @param webApplicationContext
     */
    protected void startupContextLoader(WebApplicationContext webApplicationContext)
    {
        if(webApplicationContext instanceof ConfigurableWebApplicationContext)
            ((ConfigurableWebApplicationContext)webApplicationContext).refresh();
    }
   
   
    /**
     * Deletes all entries from Client table in database.
     * 
     * @throws Exception
     */
    protected void cleanDatabase() throws Exception {
        Connection con = null;
        
        try {
            con = getDataSource().getConnection();
            
            try {
                con.createStatement().execute(
                    "DELETE FROM Client");
            } catch (SQLException e) {
                Logger.getAnonymousLogger().info(
                    "There was a problem while deleting rows of "
                    + "table 'Client'. Maybe the table "
                    + "does not exist.");
            }
            
            con.commit();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.getAnonymousLogger().info(
                        "Connection could not be closed.");
                }
            }
        }
        
        
    }
    
    /**
     * @return Returns the dataSource.
     */
    protected DataSource getDataSource() {
        
      return (DataSource) getSpringApplicationContext().getBean("dataSource");
      
    }
    
}