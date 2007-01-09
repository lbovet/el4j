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
package ch.elca.el4j.plugins.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * 
 * This class is ...
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
public abstract class AbstractDBExecutionMojo extends AbstractDBMojo {

    // Checkstyle: MemberName off
    
    /**
     * Path to properties file where connection properties (username, password 
     * and url)can be found.
     * 
     * For this property, no prefix <code>classpath*:</code> is needed. 
     * Moreover it can include a generic <code>{db.name}</code> if a
     * <code>env.properties</code> file is provided (in the project dir).
     * 
     * @parameter expression="${db.connectionPropertiesSource}"
     */
    private String connectionPropertiesSource;
    
    /**
     * Path to properties file where JDBC driver name can be found.
     * 
     * For this property, no prefix <code>classpath*:</code> is needed. 
     * Moreover it can include a generic <code>{db.name}</code> if a
     * <code>env.properties</code> file is provided (in the project dir).
     * 
     * @parameter expression="${db.driverPropertiesSource}" default-value=
     *  "scenarios/db/raw/common-database-override-{db.name}.properties"
     */
    private String driverPropertiesSource;
    
    /**
     * Separator for string lists.
     *
     * @parameter expression="${separator}" default-value=","
     */
    private String separator;
    
    /**
     * SQL Source Directories, i.e. directories where to find the .sql files.
     * 
     * By convention these are <code>classpath*:/etc/sql/general/</code> for 
     * sql files used by both database types and 
     * <code>classpath*:/etc/sql/{db.name}/</code> for those sql files that are
     * specific.
     * 
     * Note: if you use a non-default separator in your project you have to 
     * state this parameter expclicetly as well as it uses the default separator
     * in its default value.
     * 
     * @parameter expression="${db.sqlSourceDir}" default-value=
     *  "/etc/sql/general/, /etc/sql/{db.name}/"
     */
    private String sqlSourceDir;
    
    // Checkstyle: MemberName on
    
    /**
     * Execute a given goal.
     * Looks for matching sql resources in <code>sqlSourceDir</code>, extracts
     * sql statements and executes them.
     * 
     * @param goal Goal to execute
     * @param reversed Should be statements processed in reverse order?
     * @throws Exception
     */
    protected void executeAction(String goal, boolean reversed) {
        // If no connection properties are given, skip goal
        if (connectionPropertiesSource != null) {
            List<Resource> resources = getResources(getSqlSourcesPath(goal));
            if (reversed) {
                Collections.reverse(resources);
            }
            processResources(resources, goal);
        }
    }
    
    /**
     * Iterates through array and executes sql statements of resources.
     * If goal is create or update, resources are processed in reversed order.
     * If goal is delete or drop, resources are processed sequentially. This
     * ensures that dependency files are processed in the right order.
     * 
     * @param resources
     *            Array of resources
     * @param goal The goal to execute.
     */
    private void processResources(List<Resource> resources, String goal) {
        List<SQLException> sqlExceptions = new ArrayList<SQLException>();
        Connection connection = getConnection();
        Statement stmt;
        try {
            // Process resources from back to beginning to beginn with
            // Resources of uppermost dependency first
            for (Resource resource : resources) {
                getLog().info("Processing resource: " + resource.getFilename());
                List<String> sqlStmts = extractStmtsFromFile(resource.getURL());
                // Execute statements extracted from file.
                // Collect exception and throw them afterwards to ensure that
                // all SQL Statements are processed.
                for (String sqlString : sqlStmts) {
                    try {
                        stmt = connection.createStatement();
                        stmt.execute(sqlString);
                    } catch (SQLException e) {
                        sqlExceptions.add(e);
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            sqlExceptions.add(e);
        } catch (IOException e2) {
            throw new DatabaseHolderException(e2);
        }
        if (!sqlExceptions.isEmpty()) {
            // If we encountered exceptions during execution,
            // throw new Exception and pass it first occured exception.
            throw new DatabaseHolderException(
                "Error during sql statement execution", sqlExceptions.get(0));
        }
    }
    
    /**
     * Process all source paths and return resources (without duplicates).
     * 
     * @param sourcePaths
     *            of sql files
     * @return Array of resources
     */
    private List<Resource> getResources(List<String> sourcePaths) {
        HashMap<URL, Resource> resourcesMap = new HashMap<URL, Resource>();
        Resource[] resources;
        DatabaseNameHolder holder = new DatabaseNameHolder(getRepository(),
            getProject(), getDbName());
        List<Resource> result = new ArrayList<Resource>();

        try {
            for (String source : sourcePaths) {
                resources = holder.getResolver().getResources(source);
                for (Resource resource : resources) {
                    if (!resourcesMap.containsKey(resource.getURL())) {
                        result.add(resource);
                        resourcesMap.put(resource.getURL(), resource);
                    }
                }
            }
        } catch (IOException e) {
            throw new DatabaseHolderException(e);
        }
        return result;
    }
    
    /**
     * Returns array of source paths of sql files.
     * 
     * @param action
     *            mojo is implementing
     * @return array of sourcepaths
     * @throws Exception 
     */
    private List<String> getSqlSourcesPath(String action) {
        List<String> result = new ArrayList<String>();
        List<String> resources = seperateSourcePath();
        for (String str : resources) {
            result.add("classpath*:" + str + action + "*.sql");
        }
        return result;
    }
    
    /**
     * Seperate source paths in sourceDir and return them as an array.
     * 
     * @return Array of source paths
     * @throws Exception 
     */
    private List<String> seperateSourcePath() {
        int index;
        String part;
        List<String> result = new ArrayList<String>();
        DatabaseNameHolder holder = new DatabaseNameHolder(
            getRepository(), getProject(), getDbName());
        // Add seperator at end due to following algorithm
        if (!sqlSourceDir.endsWith(separator)) {
            sqlSourceDir = sqlSourceDir + separator;
        }
        while ((index = sqlSourceDir.indexOf(separator)) != -1) {
            part = holder.replaceDbName(
                sqlSourceDir.substring(0, index).trim());
            result.add(part);
            // check if sourceDir has input after separator. If so, continue.
            int temp = sqlSourceDir.length();
            if (index < temp) {
                sqlSourceDir = sqlSourceDir.substring(index + 1, 
                    sqlSourceDir.length());
            } else {
                sqlSourceDir = "";
            }
        }
        return result;
    }
    
    /**
     * Extract sql statements from given file.
     * 
     * @param fileURL
     *            URL of the file
     * @return List of statements
     * @throws IOException 
     * @throws IOException
     */
    private List<String> extractStmtsFromFile(URL fileURL) {
        ArrayList<String> result = new ArrayList<String>();
        String part;
        String stmt = "";
        int index;
        
        try {
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(
                fileURL.openStream()));
            while ((part = buffRead.readLine()) != null) {
                // Filter out comments and blank lines
                if (StringUtils.hasText(part) && !part.startsWith("--")) {
                    // Sort statements by ';'
                    while ((index = part.indexOf(';')) != -1) {
                        // add statement to result array
                        result.add(stmt + part.substring(0, index));
                        // reset statement string
                        stmt = "";
                        // check if Part has input after ';'. If so, continue.
                        if (index < part.length()) {
                            part = part.substring(index + 1, part.length());
                        } else {
                            part = "";
                        }
                    }
                    stmt = stmt + part;
                }
            }
        } catch (IOException e) {
            throw new DatabaseHolderException(e);
        }
        return result;
    }
    
    /**
     * Establish a database connection.
     * 
     * @return the connection established
     * @throws Exception
     */
    private Connection getConnection() {
        ConnectionPropertiesHolder holder 
            = new ConnectionPropertiesHolder(
                getRepository(),
                getProject(), 
                getDbName(), 
                connectionPropertiesSource, 
                driverPropertiesSource);
        Properties prop = new Properties();
        prop.put("user", holder.getUsername());
        prop.put("password", holder.getPassword());
        Driver driver = holder.getDriver();
        try {
            return driver.connect(holder.getUrl(), prop);
        } catch (SQLException e) {
            throw new DatabaseHolderException(e);
        }   
    }
}
