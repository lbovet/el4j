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


import org.apache.maven.artifact.Artifact;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import ch.elca.el4j.plugins.database.holder.ConnectionPropertiesHolder;
import ch.elca.el4j.plugins.database.holder.DatabaseHolderException;

/**
 * 
 * This class is the abstract class for all mojos, which are executing SQL
 * statements. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/
 *   maven/plugins/maven-database-plugin/src/main/java/ch/elca/el4j/plugins/
 *   database/AbstractDBExecutionMojo.java $",
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
     * Base path (in <code>classpath*:</code>)where properties files can be 
     * found.
     * 
     * @parameter expression="${db.connectionPropertiesDir}"  default-value=
     *  "scenarios/db/raw/"
     */
    private String connectionPropertiesDir;


    /**
     * Path to properties file where connection properties (username, password 
     * and url)can be found.
     * 
     * For this property, no prefix <code>classpath*:</code> is needed. 
     * Moreover it can include a generic <code>{db.name}</code> if a
     * <code>env.properties</code> file is provided (in the project dir).
     *  
     *  
     *  
     * @parameter expression="${db.connectionPropertiesSource}"
     */
    private String connectionPropertiesSource;



    /**
     * Template for filenames for .properties files used to read the
     * connection settings of the database.
     * You can use the variables {groupId}, {artifactId}, {version} and
     * {db.name} eg. {artifactId}-override-{db.name}.properties 
     * (this is the default-value)
     * 
     * 
     * @parameter expression="${db.connectionPropertiesSourceTemplate}" 
     * default-value="{artifactId}-override-{db.name}.properties"
     */
    private String connectionPropertiesSourceTemplate;

    /**
     * Base path (in <code>classpath*:</code>)where properties files can be 
     * found.
     * 
     * @parameter expression="${db.environmentBeanPropertyPropertiesPath}" 
     *            default-value="classpath:env-bean-property.properties"
     */
    private String environmentBeanPropertyPropertiesPath;

    /**
     * Path to properties file where JDBC driver name can be found.
     * 
     * For this property, no prefix <code>classpath*:</code> is needed. 
     * Moreover it can include a generic <code>{db.name}</code> if a
     * <code>env.properties</code> file is provided (in the project dir).
     * 
     * @parameter expression="${db.driverPropertiesSource}" default-value=
     *  "scenarios/db/raw/module-database-override-{db.name}.properties"
     */
    private String driverPropertiesSource;

    /**
     * Separator for string lists.
     *
     * @parameter expression="${separator}" default-value=","
     */
    private String separator;

    /**
     * Separator for sql statements.
     *
     * @parameter expression="${delimiter}" default-value=";"
     */
    private String delimiter;
    
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

    /**
     * The Data Holder.
     */
    private ConnectionPropertiesHolder m_holder;

    // Checkstyle: MemberName on

    /**
     * Execute a given goal.
     * Looks for matching sql resources in <code>sqlSourceDir</code>, extracts
     * sql statements and executes them.
     * 
     * Also looks for the properties-file to use, if none is specified
     * (per parameter oder pom.xml)
     * 
     * @param goal Goal to execute
     * @param reversed Should be statements processed in reverse order?
     * @param isSilent indicates whether we reduce log output
     */
    protected void executeAction(String goal, boolean reversed, 
        boolean isSilent) {
        
        //first make sure, that connectionPropertiesDir ends with a "/"
        //because this is needed for further operations
        if (!connectionPropertiesDir.endsWith("/")) {
            connectionPropertiesDir = connectionPropertiesDir + "/";
        }

        getLog().info("maven-database-plugin is working...");
        getLog().info("Current artifact: " + getProject().getArtifactId());

//      getLog().info("Dependency tree: ");
//      getLog().info("============");

//      List<Artifact> deps = getGraphWalker().
//      getDependencyArtifacts();
//      //add current artifact to search-list
//      if (deps != null) { deps.add(getProject().getArtifact()); }

//      for (Artifact dep : deps) {
//      getLog().info(dep.getArtifactId());
//      }

//      getLog().info("============");

//      getLog().info("Resources:");

//      Resource[] res = getHolder().getResources("classpath*:" 
//      + connectionPropertiesDir);
//      //res now holds all resources matching path-pattern

//      for (Resource r : res) {
//      try {
//      getLog().info("  " 
//      + r.getURL().getFile());
//      } catch (Exception e) {
//      getLog().info("Error getting URL of resource.");
//      }
//      }

        if (StringUtils.hasText(connectionPropertiesSource)) {
            getLog().info("Connection properties defined via pom parameter "
                + "'connectionPropertiesSource':" + connectionPropertiesSource);
        } else if (tryLoadingDatabasePropertiesViaEnvironment()) {
            getLog().info("Connection properties found via the environment: "
                + connectionPropertiesSource);
        } else {
            //no file for connection-properties was specified, so search it

            //then dbName must be provided, so that corrent 
            //properties file can be found
            //hint: since there is a default value for dbName, 
            //this case should never occur

            if (!StringUtils.hasText(getDbNameHolder().getDbName())) {
                getLog().error("Please provide a value for either "
                    + "the parameter 'db.connectionPropertiesSource' or "
                    + "'db.dbName'");
            } else {
                getLog().info(
                    "No .properties file was specified via POM or parameter.");
                getLog().info("Looking for .properties file...");

                connectionPropertiesSource = getPropertiesFile();


                //load the found properties (possible even if nothing found)
                getConnPropHolder().loadConnectionProperties(
                    connectionPropertiesSource);
            }
        }

        // If no connection properties are given, skip goal
        if (connectionPropertiesSource != null) {
            getLog().info("Using connectionPropertiesSource at '" 
                + getConnPropHolder().replaceDbName(connectionPropertiesSource)
                + "'");

            List<Resource> resources = getResources(getSqlSourcesPath(goal));
            if (reversed) {
                Collections.reverse(resources);
            }
            processResources(resources, goal, isSilent);
        } else {
            getLog().error("-----------------------------------------------");
            getLog().error("Missing parameter: connectionPropertiesSource!");
            getLog().error("Skipping goal...");
            getLog().error("-----------------------------------------------");
        }
    }
    
    /**
     * Tries to load the database connection properties via the environment.
     * 
     * @return Returns <code>true</code> if the properties could be successfully
     *         loaded.
     */
    private boolean tryLoadingDatabasePropertiesViaEnvironment() {
        boolean propertiesFound = false;
        if (StringUtils.hasText(environmentBeanPropertyPropertiesPath)) {
            List<String> pathList = new ArrayList<String>();
            pathList.add(environmentBeanPropertyPropertiesPath);
            List<Resource> resources = getResources(pathList);
            if (resources != null && resources.size() > 0) {
                Resource r = resources.get(0);
                String rUrlString;
                try {
                    rUrlString = r.getURL().toString();
                } catch (IOException e) {
                    rUrlString = "UNKNOWN_PATH/" + r.getFilename();
                }
                if (resources.size() > 1) {
                    getLog().warn("More than one environment file could be "
                        + "found! Only the first resource '"
                        + rUrlString + "' will be used!");
                }
                boolean necessaryPropertiesLoaded
                    = getConnPropHolder().loadConnectionProperties(r);
                if (necessaryPropertiesLoaded) {
                    connectionPropertiesSource
                        = environmentBeanPropertyPropertiesPath;
                    propertiesFound = true;
                } else {
                    getLog().warn("Environment file '" + rUrlString
                        + "' could not be used to load all necessary "
                        + "database properties.");
                }
            } else {
                getLog().warn("No environment file at '"
                    + environmentBeanPropertyPropertiesPath
                    + "' could be found. Database properties will be looked up "
                    + "in standard way.");
            }
        }
        return propertiesFound;
    }
    
    /**
     * Searches the resources for a .properties file containing connection 
     * properties for current dbName.
     * Looks in current artifact und from the leafs to the root
     * in all dependent artifacts
     * until properties file is found.
     * 
     * @author Frank Bitzer (FBI)
     * 
     * @return relative path of properties file (relative to classpath*:)
     */
    private String getPropertiesFile() {

        String result = null;

        //create list of dependencies
        List<Artifact>dependencies
            = getGraphWalker().getDependencyArtifacts();

        //add current artifact to search-list
        if (dependencies != null) { 
            dependencies.add(getProject().getArtifact());
        }


        //reverse to search bottom-up
        Collections.reverse(dependencies);

        String pattern;

        for (Artifact currentDependency : dependencies) {

//          pattern = "classpath*:" + connectionPropertiesDir
//          + currentDependency.getArtifactId()
//          + "-override-" + getDbName() + ".properties";

            getLog().info("Search artifact "
                + currentDependency.getArtifactId() + "...");


            //create pattern from template

            //at first, set variable artifactId
            pattern = this.connectionPropertiesSourceTemplate.replace
            ("{artifactId}", currentDependency.getArtifactId());

            //next, version
            pattern = pattern.replace
            ("{version}", currentDependency.getVersion());

            //then groupId
            pattern = pattern.replace
            ("{groupId}", currentDependency.getGroupId());

            //and at least, db.name using the function of the holder
            pattern = getConnPropHolder().replaceDbName(pattern);


            //search must be executed in classpath
            pattern = "classpath*:" + connectionPropertiesDir + pattern;

            getLog().info("Using pattern " + pattern);

            Resource[] res = getConnPropHolder().getResources(pattern);


            if (res.length == 0) {
                //no properties found for this artifact, so try parent artifact
                getLog().info("Artifact " + currentDependency.getArtifactId()
                    + " has no .properties file. Trying next dependency...");


            } else if (res.length == 1) {
                //exactly one .properties file was found, so return its path
                result = connectionPropertiesDir + res[0].getFilename();

                break;
            } else {
                //ambiguous properties files found 
                getLog().error("More then one .properties file found in "
                    + currentDependency.getArtifactId());

                result = null;

                break;

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
     * @param beSilent indicates whether we reduce log output
     */
    private void processResources(List<Resource> resources, String goal, 
        boolean beSilent) {
        List<SQLException> sqlExceptions = new ArrayList<SQLException>();
        List<String> failedSqlStatements = new ArrayList<String>();
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
                        failedSqlStatements.add(sqlString);
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            sqlExceptions.add(e);
            failedSqlStatements.add("<no stmt available>");
        } catch (IOException e2) {
            throw new DatabaseHolderException(e2);
        }
        if (!sqlExceptions.isEmpty()) {
            // If we encountered exceptions during execution,
            // throw new Exception and pass it first occured exception.
            if (!beSilent) {
                getLog().info("Exceptions during goal db:'" + goal + "'");
                for (int i = 0; i < sqlExceptions.size(); i++) {
                    getLog().info("failed stmt: " + failedSqlStatements.get(i));
                    getLog().info(sqlExceptions.get(i).toString());
                }
            }
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
        List<Resource> result = new ArrayList<Resource>();

        try {
            for (String source : sourcePaths) {
                resources = getConnPropHolder().getResources(source);
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
     */
    private List<String> getSqlSourcesPath(String action) {
        List<String> result = new ArrayList<String>();
        for (String str : seperateSourcePath()) {
            result.add("classpath*:" + str + action + "*.sql");
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
        String fullPath = sqlSourceDir;
        String part;
        List<String> result = new ArrayList<String>();
        // Add seperator at end due to following algorithm
        if (!fullPath.endsWith(separator)) {
            fullPath = fullPath + separator;
        }
        while ((index = fullPath.indexOf(separator)) != -1) {
            part = getConnPropHolder().replaceDbName(
                fullPath.substring(0, index).trim());
            result.add(part);
            // check if sourceDir has input after separator. If so, continue.
            int temp = fullPath.length();
            if (index < temp) {
                fullPath = fullPath.substring(index + 1, 
                    fullPath.length());
            } else {
                fullPath = "";
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
                    // Sort statements by delimiter, by default ';'
                    while ((index = part.indexOf(delimiter)) != -1) {
                        // add statement to result array
                        result.add(stmt + part.substring(0, index));
                        // reset statement string
                        stmt = "";
                        // check if Part has input after the delimiter.
                        // If so, continue.
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
     */
    private Connection getConnection() {
        Properties prop = new Properties();
        prop.put("user", getConnPropHolder().getUsername());
        prop.put("password", getConnPropHolder().getPassword());
        Driver driver = getConnPropHolder().getDriver();
        getLog().info("Trying to connect to db '" 
            + getConnPropHolder().getDbName() + "' at '" 
            + getConnPropHolder().getUrl() + "'");

        try {
            return driver.connect(getConnPropHolder().getUrl(), prop);
        } catch (SQLException e) {
            getLog().info("Error connecting to db " 
                + getConnPropHolder().getDbName() + " at " 
                + getConnPropHolder().getUrl() + "");
            throw new DatabaseHolderException(e);
        }   
    }

    /**
     * @return The Data Holder
     */
    private ConnectionPropertiesHolder getConnPropHolder() {


        if (m_holder == null) {
            m_holder = new ConnectionPropertiesHolder(
                getRepository(),
                getProject(), 
                getGraphWalker(),
                connectionPropertiesSource, 
                driverPropertiesSource);
        } 
        return m_holder;
    }
}
