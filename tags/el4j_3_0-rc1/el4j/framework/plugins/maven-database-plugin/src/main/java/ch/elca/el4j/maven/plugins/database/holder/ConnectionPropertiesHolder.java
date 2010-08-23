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
package ch.elca.el4j.maven.plugins.database.holder;

import java.sql.Driver;
import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

import ch.elca.el4j.maven.ResourceLoader;

/**
 *
 * This class holds the data needed to connect to a database, namely the
 * URL, the username, the password and the JDBC driver.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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
	 * @param resourceLoader    the resource loader
	 * @param project           the Maven project
	 * @param connectionSource  path to properties file of connection properties
	 * @param driverSource      path to properties file for Driver Property
	 */
	public ConnectionPropertiesHolder(ResourceLoader resourceLoader,
		MavenProject project, String connectionSource, String driverSource) {
		super(resourceLoader, project);
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
			return (Driver) m_resourceLoader.getClassLoader()
				.loadClass(m_driverName).newInstance();
		} catch (Exception e) {
			throw new DatabaseHolderException(e);
		}
	}
	
	/**
	 * Load connection properties from specified properties file.
	 * @param sourceDir
	 *          Path where to find properties file
	 */
	public void loadConnectionProperties(String sourceDir) {
		if (sourceDir != null) {
			String source = replaceDbName(sourceDir);
			if (!source.startsWith("file:/") && !source.startsWith("classpath:") && !source.startsWith("classpath*:")) {
				source = "classpath*:" + source;
			}
			try {
				Resource[] resources = m_resourceLoader.getResources(source);
				loadConnectionProperties(getProperties(resources));
			} catch (Exception e) {
				throw new DatabaseHolderException(
					"Error reading connection properties at " + source, e);
			}
		}
	}
	
	/**
	 * Load connection properties from specified properties file.
	 * @param properties    the bean-override properties
	 */
	public void loadConnectionProperties(Properties properties) {
		m_url = properties.getProperty("dataSource.jdbcUrl", "");
		if (m_url == null) {
			m_url = properties.getProperty("dataSource.url", "");
		}
		m_username = properties.getProperty("dataSource.user", "");
		if (m_username == null) {
			m_username = properties.getProperty("dataSource.username", "");
		}
		m_password = properties.getProperty("dataSource.password", "");
	}
	
	/**
	 * Get driver from specified properties file.
	 *
	 * @param sourceDir
	 *            Path where to find properties file
	 */
	private void loadDriverName(String sourceDir) {
		String source = replaceDbName(sourceDir);
		if (!source.startsWith("classpath:")
			&& !source.startsWith("classpath*:")) {
			source = "classpath*:" + source;
		}
		try {
			
			Resource[] resources = m_resourceLoader.getResources(source);
			Properties properties = getProperties(resources);
			
			m_driverName = properties.getProperty("dataSource.driverClass");
			if (m_driverName == null) {
				m_driverName = properties.getProperty("dataSource.driverClassName");
			}
		} catch (Exception e) {
			throw new DatabaseHolderException(
				"Error reading Driver Name properties at " + source, e);
		}
	}
	
	
}
