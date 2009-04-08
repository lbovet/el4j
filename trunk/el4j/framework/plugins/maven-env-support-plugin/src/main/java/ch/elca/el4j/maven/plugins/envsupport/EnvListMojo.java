/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.envsupport;

import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.io.Resource;

/**
 * This class is ...
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @goal list
 * @requiresProject true
 * @requiresDependencyResolution compile
 *
 * @author Stefan Wismer (SWI)
 */
public class EnvListMojo extends EnvSupportMojo {

	/** {@inheritDoc} */
	public void execute() throws MojoExecutionException, MojoFailureException {
		initializeFiltering();
		
		showEnvPropertiesFiles("env-placeholder.properties");
		showMergedProperties("env-placeholder.properties");
		
		showEnvPropertiesFiles("env-bean-property.properties");
		showMergedProperties("env-bean-property.properties");

	}

	/**
	 * Print the resulting (filtered) properties.
	 * 
	 * @param envPropertiesFilename    the env property filename
	 */
	private void showMergedProperties(String envPropertiesFilename) throws MojoExecutionException {
		try {
			Resource[] resources = getResourceLoader(true).getDependenciesResources(
				"classpath*:" + envPropertiesFilename);
			
			getLog().info("");
			getLog().info("Resulting merged and evaluated " + envPropertiesFilename + ":");
			
			Properties properties = new Properties();
			for (Resource resource : resources) {
				try {
					properties.load(resource.getInputStream());
					getLog().info(" (Including " + getArtifactNameFromResource(resource) + ")");
				} catch (IOException e) {
					throw new MojoExecutionException(
						"Cannot load resource '" + resource.toString() + "'");
				}
			}
			
			properties.putAll(getFilteredOverwriteProperties(envPropertiesFilename));
			
			
			for (Object keyObj : properties.keySet()) {
				String key = (String) keyObj;
				getLog().info("  " + key + "=" + properties.getProperty(key));
			}
		} catch (IOException e) {
			throw new MojoExecutionException(
				"Cannot collect env files for '" + envPropertiesFilename + "'");
		}
	}
	
	/**
	 * Print each property file taken for property resolution.
	 * 
	 * @param envPropertiesFilename    the env property filename
	 */
	protected void showEnvPropertiesFiles(String envPropertiesFilename) throws MojoExecutionException {
		Resource[] resources = getAllUnfilteredResources(envPropertiesFilename);
		
		// print most specific resource first
		//ArrayUtils.reverse(resources);
		
		getLog().info("");
		getLog().info("Properties stored in " + envPropertiesFilename + ":");
		
		for (Resource resource : resources) {
			getLog().info("  Properties of " + getArtifactNameFromResource(resource) + ":");
			
			Properties props = new Properties();
			try {
				props.load(resource.getInputStream());
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Cannot load resource '" + resource.toString() + "'");
			}
			for (Object keyObj : props.keySet()) {
				String key = (String) keyObj;
				getLog().info("    " + key + "=" + props.getProperty(key));
			}
		}
		
		getLog().info("");
		getLog().info("Checking properties...");
		
		// make list 'most specific resource last'
		//ArrayUtils.reverse(resources);
		checkProperties(resources);
	}
}
