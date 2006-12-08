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
package ch.elca.el4j.plugins.database;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


/**
 * 
 * This class holds all the Database specific data like URL, username, driver,
 * etc. It extracts these informations from properties files from the project
 * itself and dependencies of the project.
 * Moreover, it provides an enriched classloader with all classes and resources
 * reachable by the project. 
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
public class DatabaseDataHolder {

    /**
     * Logger.
     */
    private static Log s_logger = LogFactory.getLog(DatabaseDataHolder.class);

    /**
     * Classloader with all project dependend classes.
     */
    private URLClassLoader m_classloader;

    /**
     * Path matcher to find sql files.
     */
    private PathMatchingResourcePatternResolver m_resolver;

    /**
     * JDBC Driver for database connection.
     */
    private String m_driverName;

    /**
     * Database name.
     */
    private String m_dbName;
    
    /**
     * URL of database.
     */
    private String m_url;

    /**
     * Username for database connection.
     */
    private String m_username;

    /**
     * Password for database connection.
     */
    private String m_password;
    
    /**
     * URLs of dependency artifacts.
     */
    private List<URL> m_dependencyURLs;
    
    /**
     * URLs of project artifacts.
     */
    private List<URL> m_projectURLs;
    
    /**
     * Prepares the Holder, i.e. create the enriched classloader.
     * 
     * @param repository Artifact Repository.
     * @param project Project this plugin is called upon
     * @throws IOException 
     */   
    public void prepareHolder(ArtifactRepository repository, 
        MavenProject project) throws IOException {
        
        m_dependencyURLs = getDependencyURLs(repository, project);
        m_projectURLs = getProjectUrls(repository, project);
        createEnrichedClassloader(m_dependencyURLs, m_projectURLs);
    }
    
    /**
     * Process properties files and extract necessary information.
     * 
     * @param dbName dbName from configuration in pom file
     * @param connectionSource Directory of Connection properties.
     * @param driverSource Directory of Driver properties
     * @throws Exception
     */
    public void loadData(String dbName, String connectionSource, 
            String driverSource) throws Exception {
        
        loadDBName(m_projectURLs, dbName);
        loadConnectionProperties(connectionSource);
        loadDriverName(driverSource);
    }
    
    /**
     * @return Returns the PathResolver that works on the enriched classloader.
     */
    public PathMatchingResourcePatternResolver getPathResolver() {
        return m_resolver;
    }

    /**
     * @return Returns the URL of the database.
     */
    public String getUrl() {
        return m_url;
    }

    /**
     * @return Returns the username of the database.
     */
    public String getUsername() {
        return m_username;
    }

    /**
     * @return Returns the password of the database.
     */
    public String getPassword() {
        return m_password;
    }
    
    /**
     * @return Returns the JDBC driver.
     * @throws Exception
     */
    public Driver getDriver() throws Exception {
        return (Driver) m_classloader.loadClass(m_driverName).newInstance();
    }
    
    /**
     * @return Database Name.
     */
    public String getDbName() {
        return m_dbName;
    }
    
    /**
     * Add all project dependencies as well as project specific resources
     * to actual classpath and generate PathResolver.
     * 
     * @param urls Urls from dependencies to include into classpath.
     * @param projectURLs URLs from project to include.
     * @throws IOException
     */
    private void createEnrichedClassloader(List<URL> urls, 
        List<URL> projectURLs) throws IOException {
        
        urls.addAll(projectURLs);
        // Set thread's classloader as parent classloader
        m_classloader = URLClassLoader.newInstance(urls.toArray(new URL[1]),
            Thread.currentThread().getContextClassLoader());
        m_resolver = new PathMatchingResourcePatternResolver(m_classloader);
    }
    
    /**
     * Get name of database (either db2 or oracle) from project's 
     * env.properties file or configuration tag. 
     * 
     * If neither of both is set, goal might be to start NetworkServer
     * and therefore database name isn't needed.
     * 
     * @param urls The Project URLs (to build classpath)
     * @param dbName dbName from configuration tag in pom file
     * @throws IOException
     */
    private void loadDBName(List<URL> urls, String dbName) throws Exception {
        // Check if DB Name was set with configuration tag or should be
        // read from project's env.properties
        if (dbName == null || dbName.equalsIgnoreCase("db2")
            || dbName.equalsIgnoreCase("oracle")) {

            // Create own classloader and resovler for env.properties, because
            // we only want the file from the project we're working on
            URLClassLoader projectClasspath = URLClassLoader.newInstance(urls
                .toArray(new URL[1]), Thread.currentThread()
                     .getContextClassLoader());
            PathMatchingResourcePatternResolver projectResolver 
                = new PathMatchingResourcePatternResolver(projectClasspath);

            // Check if project contains env.properties
            Resource[] resources = projectResolver
                .getResources("classpath*:env/env.properties");
            Properties properties = getProperties(resources);
            m_dbName = properties.getProperty("db.name");
        } else {
            m_dbName = dbName;
        }
       
    }
    
    /**
     * Get driver from specified properties file.
     * 
     * @param sourceDir
     *            Path where to find properties file
     * @throws IOException
     */
    private void loadDriverName(String sourceDir) throws Exception  {
        String source = replace(sourceDir, "{db.name}", m_dbName);
        Resource[] resources = m_resolver.getResources("classpath*:" + source);
        Properties properties = getProperties(resources);
        m_driverName = properties.getProperty("dataSource.driverClassName");
    }

    
    /**
     * Load connection properties from specified properties file.
     * @param sourceDir
     *          Path where to find properties file
     * @throws IOException
     */
    private void loadConnectionProperties(String sourceDir) throws Exception {

        String source = replace(sourceDir, "{db.name}", m_dbName);
        Resource[] resources = m_resolver.getResources("classpath*:" + source);
        Properties properties = getProperties(resources);
        m_url = properties.getProperty("dataSource.url");
        m_username = properties.getProperty("dataSource.username");
        m_password = properties.getProperty("dataSource.password");
    }

    
    /**
     * Collects and returns list of project urls. This includes the normal jar
     * as well as the {project-name}-tests.jar file in case we work on a test
     * project.
     * 
     * @param repo The artifact repository.
     * @param project The projects we're working on.
     * @return List of project's jar URLs
     * @throws MalformedURLException
     */
    private ArrayList<URL> getProjectUrls(ArtifactRepository repo, 
            MavenProject project) throws IOException {
        String path;
        ArrayList<URL> urls = new ArrayList<URL>();

        // Construct URL for /target directory of project (where we will find
        // the jar files taken for the test phase).
        path = "/" + project.getBasedir().getAbsolutePath() + "/" + "target";
        URL url = new URL("file", "", path + "/");

        // Create own classloader for /target directory
        URLClassLoader projectClasspath = URLClassLoader.newInstance(
            new URL[] {url}, Thread.currentThread().getContextClassLoader());
        PathMatchingResourcePatternResolver projectResolver 
            = new PathMatchingResourcePatternResolver(projectClasspath);

        // Look for .jar files in target directory and add them to 
        // projectUrls
        Resource[] res = projectResolver.getResources("classpath*:*.jar");
        for (Resource r : res) {
            urls.add(r.getURL());
        }
        return urls;
    }
    
    /**
     * Collects and returns list of urls of all dependencies of this project. 
     * 
     * @param repo The artifact repository.
     * @param project The projects we're working on.
     * @return List of dependencies jar URLs
     * @throws MalformedURLException
     */
    private List<URL> getDependencyURLs(ArtifactRepository repo, 
            MavenProject project) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        // Iterate through test dependencies, because it contains more resources
        for (Object obj : project.getTestArtifacts()) {
            Artifact artifact = (Artifact) obj;
            urls.add(constructURL(repo.getBasedir(), repo.pathOf(artifact)));
        }
        return urls;
    }
    
    /**
     * Constructs a URL from the given Base directory and the file path.
     * 
     * @param baseDir Base directory of maven repository.
     * @param filePath Path of file we want URL for
     * @return URL of this file
     * @throws MalformedURLException
     */
    private URL constructURL(String baseDir, String filePath) 
        throws MalformedURLException {
        String path = "file:/" + baseDir + "/" + filePath;
        return  new URL("jar", "", path + "!/");
    }
    
    /**
     * Util method for replacing occurences in String, because 
     *  replace method of String (and StringUtils) class doesn't work for this.
     * 
     * @param input String where we want to replace oldExpr
     * @param oldExpr Expression to replace
     * @param newExpr New expression
     * @return New string
     */
    private String replace(String input, String oldExpr, String newExpr) {
        int old = oldExpr.length();
        String result = input;
        for (int i = 0; i + old < result.length(); i = i + 1) {
            if (result.substring(i, i + old).equalsIgnoreCase(oldExpr)) {
                String before = result.substring(0, i);
                String after = result.substring(i + old, result.length());
                result = before + newExpr + after;
            }
        }
        return result;   
    }
    
    
    /**
     * Checks if resource array contains only one element and if so, returns 
     * properties of given resource (properties) file. 
     * 
     * Array as parameter is needed due to (Spring) PathResolver we use.
     * 
     * @param resources Array of resources. Should only be one properties file
     * @return Properties object of file given
     * @throws IOException
     */
    private Properties getProperties(Resource[] resources) throws Exception {
        if (resources.length != 1) {
            s_logger
                .warn("Source path is ambigious or doesn't contain resources");
        }
        Resource resource = resources[0];
        Properties properties = new Properties();
        properties.load(resource.getURL().openStream());
        return properties;
        
    } 
}
