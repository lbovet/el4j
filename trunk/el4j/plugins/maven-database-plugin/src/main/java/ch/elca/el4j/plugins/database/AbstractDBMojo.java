/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * This class holds all fields and methods commmon to all database mojos, namely
 * the properties from the pom file, the Maven project and repository as well as
 * the DataHolder, which encapsulates all Database specific properties.
 * 
 * It provides methods to its sublcasses to start the derby NetworkServer and to
 * execute an action, i.e. extract and execute SQL statements from a given 
 * source path.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 *
 * @requiresProject true
 * @requiresDependencyResolution test
 */
public abstract class AbstractDBMojo extends AbstractMojo {
    
    // Checkstyle: MemberName off
    
    /**
     * Decides whether to wait after the container is started or to return 
     * the execution flow to the user.
     * 
     * @parameter expression="${db.wait}" default-value = "true"
     * @required
     */
    protected boolean wait;
    
    /**
     * Path to properties file where connection properties (username, password 
     * and url)can be found.
     * 
     * For this property, no prefix <code>classpath:*</code> is needed. 
     * Moreover it can include a generic <code>{db.name}</code> if a
     * <code>env.properties</code> file is provided (in the project dir).
     * 
     * @parameter expression="${db.connectionPropertiesSource}"
     */
    private String connectionPropertiesSource;
    
    /**
     * Path to properties file where JDBC driver name can be found.
     * 
     * For this property, no prefix <code>classpath:*</code> is needed. 
     * Moreover it can include a generic <code>{db.name}</code> if a
     * <code>env.properties</code> file is provided (in the project dir).
     * 
     * @parameter expression="${db.driverPropertiesSource}" default-value=
     *  "scenarios/db/raw/common-database-override-{db.name}.properties"
     */
    private String driverPropertiesSource;
    
    /**
     * Name of database to use (either db2 or oracle).
     * 
     * @parameter expression="${db.dbName}"
     * 
     */
    private String dbName;
    
    /**
     * Separator for string lists.
     *
     * @parameter expression="${separator}" default-value=","
     */
    private String separator;
    
    /**
     * Directory of external-tools in el4j project.
     * 
     * @parameter expression="${el4j.project.external-tools}"
     * @required
     */
    private String externalToolsPath;

    /**
     * The maven project from where this plugin is called.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * SQL Source Directories, i.e. directories where to find the .sql files.
     * 
     * @parameter expression="${db.sqlSourceDir}"
     */
    private String sqlSourceDir;
   
    /**
     * Local maven repository.
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository repository;

    
    /**
     * Database Data Holder of this project.
     */
    private DatabaseDataHolder dataHolder;

    // Checkstyle: MemberName on
   
    /**
     * Execute a given goal.
     * Looks for matching sql resources in <code>sqlSourceDir</code>, extracts
     * sql statements and executes them.
     * 
     * @param goal Goal to execute
     * @throws Exception
     */
    protected void executeAction(String goal) throws Exception {
        // If no SQL Directories or properties given, skip goal
        if (sqlSourceDir != null && connectionPropertiesSource != null) {
            processResources(getResources(getSqlSourcesPath(goal)), goal);
        }
    }

    /**
     * Returns true if database needs to be started.
     * 
     * @return Return whether database need to be started.
     */
    protected boolean needStartup() throws Exception {
        /*
         * HACK: Because we only support derby and oracle, we can find out if we
         * have to start database by checking the head of the URL.
         * 
         * In case of an exception, start NetworkServer just to be on the
         * safe side.
         */
        try {
            String db = getDataHolder().getDbName();
            return (db == null || db.equalsIgnoreCase("db2"));
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Returns directory, where NetworkServer should be started. 
     * This will be the location, where the NetworkServer looks for databases.
     * 
     * @return Home Directory of NetworkServer
     */
    protected String getDerbyLocation() {
        return externalToolsPath + "/derby/derby-databases";
    }
    
    /**
     * @return The instance of the DatabaseDataHolder.
     * @throws IllegalPropertyException 
     */
    private DatabaseDataHolder getDataHolder() throws Exception {
        if (dataHolder == null) {
            dataHolder = new DatabaseDataHolder();
            dataHolder.prepareHolder(repository, project);
            // Let Data Holder load data from properties files.
            getDataHolder().loadData(dbName, connectionPropertiesSource,
                driverPropertiesSource);
        }
        return dataHolder;
    }
    
   
       
    /**
     * Establish a database connection.
     * 
     * @return the connection established
     * @throws Exception
     */
    private Connection getConnection() throws Exception {
        Properties prop = new Properties();
        prop.put("user", getDataHolder().getUsername());
        prop.put("password", getDataHolder().getPassword());
        Driver driver = getDataHolder().getDriver();
        return driver.connect(getDataHolder().getUrl(), prop);
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
    private List<String> extractStmtsFromFile(URL fileURL) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        String part;
        String stmt = "";
        int index;

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
        return result;
    }
    
    /**
     * Seperate source paths in sourceDir and return them as an array.
     * 
     * @return Array of source paths
     */
    private List<String> seperateSourcePath() {
        int index;
        List<String> result = new ArrayList<String>();
        // Add seperator at end due to following algorithm
        if (!sqlSourceDir.endsWith(separator)) {
            sqlSourceDir = sqlSourceDir + separator;
        }
        while ((index = sqlSourceDir.indexOf(separator)) != -1) {           
            result.add(sqlSourceDir.substring(0, index).trim());
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
     * Iterates through array and executes sql statements of resources.
     * If goal is create or update, resources are processed in reversed order.
     * If goal is delete or drop, resources are processed sequentially. This
     * ensures that dependency files are processed in the right order.
     * 
     * @param resources
     *            Array of resources
     * @param goal The goal to execute.
     * @throws Exception 
     * @throws Exception
     */
    private void processResources(List<Resource> resources, String goal) 
        throws Exception {
        List<SQLException> sqlExceptions = new ArrayList<SQLException>();
        Connection connection = getConnection();
        Statement stmt;
        if (goal.equalsIgnoreCase("create") 
            || goal.equalsIgnoreCase("update")) {
            // Process resources from back to beginning to beginn with
            // Resources of uppermost dependency first
            for (int i = (resources.size() - 1); i >= 0; i--) {
                getLog().info(
                    "Processing resource: " + resources.get(i).getFilename());
                List<String> sqlStmts = extractStmtsFromFile(resources.get(i)
                    .getURL());
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
        } else {
            for (int i = 0; i < resources.size(); i++) {
                getLog().info(
                    "Processing resource: " + resources.get(i).getFilename());
                List<String> sqlStmts = extractStmtsFromFile(resources.get(i)
                    .getURL());
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
        }

        if (!sqlExceptions.isEmpty()) {
            for (SQLException e : sqlExceptions) {
                getLog()
                    .error(
                        "Encountered error during "
                            + "execution of sql statements", e);
            }
            throw new Exception("Error during sql statement execution");
        }
    }
    
    /**
     * Returns array of source paths of sql files.
     * 
     * @param action
     *            mojo is implementing
     * @return array of sourcepaths
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
     * Process all source paths and return resources (without duplicates).
     * 
     * @param sourcePaths
     *            of sql files
     * @return Array of resources
     * @throws Exception 
     * @throws Exception 
     * @throws Exception
     */
    private List<Resource> getResources(List<String> sourcePaths) 
        throws Exception {
        HashMap<URL, Resource> resourcesMap = new HashMap<URL, Resource>();
        Resource[] resources;
        List<Resource> result = new ArrayList<Resource>();
        for (String source : sourcePaths) {
            resources = getDataHolder().getPathResolver().getResources(source);
            for (Resource resource : resources) {
                if (!resourcesMap.containsKey(resource.getURL())) {
                    result.add(resource);
                    resourcesMap.put(resource.getURL(), resource);
                }
            }
        }
        return result;
    }

   
}