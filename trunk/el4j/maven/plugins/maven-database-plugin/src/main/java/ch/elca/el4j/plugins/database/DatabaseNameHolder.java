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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * This class holds the DatabaseName.
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
public class DatabaseNameHolder extends AbstractDatabaseHolder {

    /**
     * Logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(DatabaseNameHolder.class);
    
    /**
     * Placeholder for database name.
     */
    private static final String PLACEHOLDER = "{db.name}";

    /**
     * Database name.
     */
    private String m_dbName;
    
    /**
     * Constructor.
     * @param repository Maven repository
     * @param project Maven project
     * @param dbName Name of database from maven parameter
     */
    public DatabaseNameHolder(ArtifactRepository repository, 
        MavenProject project, String dbName) {
        super(repository, project);
        loadDBName(getProjectURLs(), dbName);
    }
    
    /**
     * Util method for replacing occurences in String, because 
     *  replace method of String (and StringUtils) class doesn't work for this.
     * 
     * @param input String where we want to replace oldExpr
     * @return New string
     */
    public String replaceDbName(String input) {
        int old = PLACEHOLDER.length();
        String result = input;
        for (int i = 0; i + old < result.length(); i = i + 1) {
            if (result.substring(i, i + old).equalsIgnoreCase(PLACEHOLDER)) {
                String before = result.substring(0, i);
                String after = result.substring(i + old, result.length());
                result = before + m_dbName + after;
            }
        }
        return result;   
    }
    
    /**
     * @return The database name
     */
    public String getDbName() {
        return m_dbName;
    }
    
    /**
     * Checks if resource array contains only one element and if so, returns
     * properties of given resource (properties) file. Array as parameter is
     * needed due to (Spring) PathResolver we use.
     * 
     * @param resources
     *            Array of resources. Should only be one properties file
     * @return Properties object of file given
     * @throws IOException
     */
    protected Properties getProperties(Resource[] resources) 
        throws IOException, IllegalAccessException {
        if (resources.length == 0) {
            throw new IllegalArgumentException(
                "Path doesn't contain resources");
        }
        if (resources.length > 1) {
            s_logger.warn("Source path is ambigous");
        }
        Resource resource = resources[0];
        Properties properties = new Properties();
        properties.load(resource.getURL().openStream());
        return properties;
        
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
     * @throws IOException
     */
    private void loadDBName(List<URL> urls, String dbName) {

        // Check if DB Name was set with configuration tag or should be
        // read from project's env.properties
        if ((dbName == null)
            || (!dbName.equalsIgnoreCase("db2") && !dbName
                .equalsIgnoreCase("oracle"))) {
            try {
                // Create own classloader and resovler for env.properties,
                // because we only want the file from the project we're
                // working on
                URLClassLoader projectClasspath = URLClassLoader.newInstance(
                    urls.toArray(new URL[1]), Thread.currentThread()
                        .getContextClassLoader());
                PathMatchingResourcePatternResolver projectResolver 
                    = new PathMatchingResourcePatternResolver(projectClasspath);

                // Check if project contains env.properties
                Resource[] resources = projectResolver
                    .getResources("classpath*:env/env.properties");
                try {
                    Properties properties = getProperties(resources);
                    m_dbName = properties.getProperty("db.name");
                } catch (IllegalAccessException e) {
                    // Didn't find a env.properties file in the project
                    // Therefore, start looking in the dependencies
                    resources = getResolver().getResources(
                        "classpath*:env/env.properties");
                    try {
                        m_dbName 
                            = getProperties(resources).getProperty("db.name");
                    } catch (IllegalAccessException e1) {
                        throw new DatabaseHolderException(e1);
                    }
                }
            } catch (IOException e) {
                throw new DatabaseHolderException(e);

            }
        } else {
            m_dbName = dbName;
        }
    }
}
