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
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
     * Prepares the Holder, i.e. get the necessary information from 
     * the properties files given.
     * 
     * @param repository Artifact Repository.
     * @param project Project this plugin is called upon
     * @param driverSource Source path of driver properties file
     * @param connectionSource Source path of connection properties file
     * @throws IOException 
     */   
    public void prepareHolder(ArtifactRepository repository,
        MavenProject project, String driverSource, String connectionSource)
        throws IOException {
        
        List<URL> dependencyURLs = getDependencyURLs(repository, project);
        List<URL> projectURLs = getProjectUrls(repository, project);

        createEnrichedClassloader(dependencyURLs, projectURLs);
        loadDBName(projectURLs);
        loadConnectionProperties(connectionSource);
        loadDriver(driverSource);
    }
    
    /**
     * Get class of DerbyNetworkServerStarter from classloader.
     * 
     * This is done through reflection to avoid any derby specific libraries
     * in plugin.
     *
     * @param externalToolsPath Path of derby database.
     * @return Class of NetworkServerStarter
     * @throws Exception
     */
    public Class getDerbyNetworkServerStarter(String externalToolsPath) 
        throws Exception {
        Class starter = m_classloader
            .loadClass("ch.elca.el4j.util.derby.DerbyNetworkServerStarter");

        Class[] param = {String.class};
        Method setHomeDir = starter.getMethod("setHomeDir", param);
        Object[] args = {(Object) new String(externalToolsPath
            + "/derby/derby-databases")};
        setHomeDir.invoke(null, args);
        return starter;
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
     * env.properties file.
     * 
     * @param urls The Project URLs (to build classpath)
     * @throws IOException
     */
    private void loadDBName(List<URL> urls) throws IOException {
        // Create own classloader and resovler for env.properties, because
        // we only want the file from the project we're working on
        URLClassLoader projectClasspath = URLClassLoader.newInstance(urls
            .toArray(new URL[1]), 
            Thread.currentThread().getContextClassLoader());
        PathMatchingResourcePatternResolver projectResolver 
            = new PathMatchingResourcePatternResolver(projectClasspath);

        // Check if project contains env.properties
        Resource[] resources = projectResolver
            .getResources("classpath*:env/env.properties");
        Properties properties = getProperties(resources);
        m_dbName = properties.getProperty("db.name");
    }
    
    /**
     * Get driver from specified properties file.
     * 
     * @param sourceDir
     *            Path where to find properties file
     * @throws IOException
     */
    private void loadDriver(String sourceDir) 
        throws IOException  {
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
    private void loadConnectionProperties(String sourceDir) throws IOException {

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
            MavenProject project) throws MalformedURLException {
        String path;
        ArrayList<URL> urls = new ArrayList<URL>();

        // Add project's URL
        Artifact artifact = project.getArtifact();
        urls.add(constructURL(repo.getBasedir(), repo.pathOf(artifact)));

        // HACK: Add the URL for the test jar for the project artifact
        path = repo.pathOf(artifact);
        path = path.substring(0, path.lastIndexOf(".jar"));
        path = path.concat("-tests.jar");
        urls.add(constructURL(repo.getBasedir(), path));
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
    private Properties getProperties(Resource[] resources) throws IOException {
        if (resources.length != 1) {
            throw new RuntimeException(
                "Given resource path is ambigious or doesn't contain file");
        } else {
            Resource resource = resources[0];
            Properties properties = new Properties();
            properties.load(resource.getURL().openStream());
            return properties;
        }
    }
    
    
    

    
}
