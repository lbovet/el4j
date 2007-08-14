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
package ch.elca.el4j.plugins.database.holder;

import java.sql.Driver;
import java.util.Properties;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

import ch.elca.el4j.plugins.database.DepGraphWalker;

/**
 * 
 * This class holds the data needed to connect to a database, namely the 
 * URL, the username, the password and the JDBC driver.
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
public class ConnectionPropertiesHolder extends DatabaseNameHolder {

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
     * JDBC Driver for database connection.
     */
    private String m_driverName;
    
    /**
     * Constructor.
     * @param repository Maven repository
     * @param project Maven project
     * @param dbName Name of database from maven parameter
     * @param connectionSource Path to properties file of connection properties
     * @param driverSource Path to properties file for Driver Property
     * @param walker The Dependency Graph walker
     */
    public ConnectionPropertiesHolder(ArtifactRepository repository,
        MavenProject project, DepGraphWalker walker, String dbName, 
        String connectionSource, String driverSource) {
        super(repository, project, walker, dbName);
        loadDriverName(driverSource);
        loadConnectionProperties(connectionSource);
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
     */
    public Driver getDriver() {
        try {
            return (Driver) getClassloader()
                .loadClass(m_driverName).newInstance();
        } catch (Exception e) {
            throw new DatabaseHolderException(e);
        } 
    }
    
    /**
     * Get driver from specified properties file.
     * 
     * @param sourceDir
     *            Path where to find properties file
     */
    private void loadDriverName(String sourceDir) {
        try {
            String source = replaceDbName(sourceDir);
            Resource[] resources = getResources("classpath*:" + source);
            Properties properties = getProperties(resources);
            m_driverName = properties.getProperty("dataSource.driverClassName");
        } catch (Exception e) {
            throw new DatabaseHolderException(
                "Error reading Driver Name properties", e);
        }
    }
    
    /**
     * Load connection properties from specified properties file.
     * @param sourceDir
     *          Path where to find properties file
     */
    private void loadConnectionProperties(String sourceDir) {
        try {
            String source = replaceDbName(sourceDir);
            Resource[] resources = getResources("classpath*:" + source);
            Properties properties = getProperties(resources);
            m_url = properties.getProperty("dataSource.url");
            m_username = properties.getProperty("dataSource.username");
            m_password = properties.getProperty("dataSource.password");
        } catch (Exception e) {
            throw new DatabaseHolderException(
                "Error reading connection properties", e);
        }
        
    }
}
