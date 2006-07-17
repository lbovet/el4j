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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;


/**
 * This class is an example for using the tcp interceptor.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Florian Suess (FLS)
 */
public class TestDB extends TestCase {

    /**
     * Private logger.
     */
    private static Log s_logger 
       = LogFactory.getLog(TestDB.class);

    /**
     * Delay between the single test steps (in milliseconds).
     */
    static final int DELAY = 2000;
    
    /**
     * New input port -> Interceptor between INPUT_PORT and DEST_PORT.
     */
    static final int INPUT_PORT = 6789;
    
    /**
     * Original port of the application to test (Derby-DB: 1527).
     */
    static final int DEST_PORT = 1527;
    
    /**
     * Help variable to test if an exception occured.
     */
    int m_gotException = 0;

    /**
     * User interface to plug / unplug the described connection.
     * 
     * @throws Exception
     */
    public void testInterceptor() throws Exception {

        // Attempt to load database driver
        try {
            // Load Sun's jdbc-odbc driver
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
        } catch (ClassNotFoundException cnfe) {
            // driver not found
            s_logger.warn("Unable to load database driver");
            s_logger.warn("Details : " + cnfe);
            System.exit(0);
        }

        // Create a URL that identifies database
        String url = "jdbc:derby:net://localhost:1527/keyword;create=true;retrieveMessagesFromServerOnGetMessage=true";

        // Now attempt to create a database connection
        Connection dbConnection = DriverManager.getConnection(url,
            "keyword_user", "keyword_user");

        // Create a statement to send SQL
        Statement dbStatement = dbConnection.createStatement();

        // Create a simple table, which stores an employee ID and name
        dbStatement.execute("DROP TABLE linkstest");
        dbStatement
            .executeUpdate("CREATE TABLE linkstest (keyToReference INTEGER)");

        /**
         * Status of the connection (0: unplugged, 1: connected).
         */
        int status = 0;

        Date dt = new Date();

        System.out.println("Interceptor started...");

        status = 1;

        Thread.sleep(DELAY);

        //try {
        //    ti.unplug();
        //    }
        //catch (ConcurrentModificationException e) {
        //    System.out.println("Caught Exception:" + e);
        //}

        //System.out.println("Connection unplugged...");

        //Thread.sleep(DELAY);
        //ti.plug();

        //System.out.println("Connection restored...");
        //Thread.sleep(DELAY);

    }
}
