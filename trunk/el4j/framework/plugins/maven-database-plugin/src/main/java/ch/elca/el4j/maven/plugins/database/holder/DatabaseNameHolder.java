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

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

import ch.elca.el4j.maven.ResourceLoader;

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
public class DatabaseNameHolder {

	/**
	 * Logger.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(DatabaseNameHolder.class);
	
	/**
	 * Placeholder for database name.
	 */
	private static final String PLACEHOLDER = "{db.name}";
	
	/**
	 * The resource loader.
	 */
	protected ResourceLoader m_resourceLoader;
	
	/**
	 * Database name.
	 */
	private String m_dbName;
	
	/**
	 * Constructor.
	 * @param resourceLoader    the resource loader
	 * @param project           the Maven project
	 */
	public DatabaseNameHolder(ResourceLoader resourceLoader, MavenProject project) {
		m_resourceLoader = resourceLoader;
		loadDBName(project);
	}
	
	/**
	 * Util method for replacing occurrences in String, because
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
	 * Get resources from the classloader.
	 * @param path Path of the resources to get
	 * @return Array of resources
	 */
	public Resource[] getResources(String path) {
		try {
			return m_resourceLoader.getResources(path);
		} catch (IOException e) {
			throw new DatabaseHolderException(e);
		}
	}
	
	/**
	 * Checks if resource array contains only one element and if so, returns
	 * properties of given resource (properties) file. Array as parameter is
	 * needed due to (Spring) PathResolver we use.
	 *
	 * @param resources
	 *            Array of resources. Most specific resource must be last.
	 * @return Properties object of file given
	 * @throws IOException
	 * @throws IllegalAccessException
	 */
	protected Properties getProperties(Resource... resources)
		throws IOException {
		if (resources.length == 0) {
			throw new IllegalArgumentException(
				"Path doesn't contain resources");
		}
		
		Properties properties = new Properties();
		for (Resource resource : resources) {
			try {
				properties.load(resource.getInputStream());
			} catch (IOException e) {
				throw new DatabaseHolderException("Cannot load resource '" + resource.toString() + "'", e);
			}
		}
		
		return properties;
	}
	
	/**
	 * Get name of database (either db2 or oracle) from project's
	 * env-values.properties or env.properties file or configuration tag.
	 *
	 * At first, system property db.name is checked.
	 * If not set, classpath is searched for .env files containing
	 * db.name .
	 *
	 * @param project The current MavenProject (to get system properties)
	 *
	 */
	private void loadDBName(MavenProject project) {

		// Check if DB Name was set with configuration tag or should be
		// read from project's env-values.properties or env.properties
		
		String dbFromConfig = project.getProperties().getProperty("db.name");
		
		if (dbFromConfig == null) {
			
			try {
				// Check if project contains env-values.properties
				Resource[] resources = getResources("classpath*:env-values.properties");
				
				if (resources.length == 0) {
					// Check if project contains old env.properties
					resources = getResources("classpath*:env/env.properties");
				}
				
				Properties properties = getProperties(resources);
				m_dbName = properties.getProperty("db.name");
				s_logger.info("DB name set from env(-values).properties to: " + getDbName());
			} catch (Exception e) {
				throw new DatabaseHolderException(e);
			}
		} else {
			m_dbName = dbFromConfig;
			
			s_logger.info("DB name set from system property to: "
				+ getDbName());
		}
	}
}
