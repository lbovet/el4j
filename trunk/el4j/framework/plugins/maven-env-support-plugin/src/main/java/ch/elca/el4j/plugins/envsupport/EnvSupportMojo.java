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
package ch.elca.el4j.plugins.envsupport;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Environment support plugin. Filters the resources of given env dir and saves
 * the generate resources in a special dir.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 *
 * @goal resources
 * @phase generate-resources
 * @requiresProject true
 */
public class EnvSupportMojo extends AbstractEnvSupportMojo {
	// Checkstyle: MemberName off
	/**
	 * The output directory into which to copy the env resources.
	 *
	 * @parameter expression="${envsupport.outputDirectory}"
	 *            default-value="${project.build.directory}/env"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * The resource directory where to transfer files.
	 *
	 * @parameter expression="${envsupport.resourceDirectory}"
	 *            default-value="src/main/env"
	 * @required
	 */
	private File resourceDirectory;
	
	/**
	 * The global resource directory where to transfer files.
	 *
	 * @parameter expression="${envsupport.globalResourceDirectory}"
	 */
	private File globalResourceDirectory;
	
	/**
	 * Flag to indicate if the global resource dir should be used.
	 *
	 * @parameter expression="${envsupport.useGlobalResourceDirectory}"
	 *            default-value="false"
	 */
	private boolean useGlobalResourceDirectory;
	// Checkstyle: MemberName on
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ("pom".equals(getProject().getPackaging())) {
			return;
		}
		initializeFiltering();
		
		if (useGlobalResourceDirectory) {
			copyResourcesFiltered(globalResourceDirectory, outputDirectory, "globalResources");
			processEnvPropertiesFiles(outputDirectory, "globalResources", "env-placeholder.properties");
			processEnvPropertiesFiles(outputDirectory, "globalResources", "env-bean-property.properties");
		} else {
			copyResourcesFiltered(resourceDirectory, outputDirectory, "resources");
			processEnvPropertiesFiles(outputDirectory, "resources", "env-placeholder.properties");
			processEnvPropertiesFiles(outputDirectory, "resources", "env-bean-property.properties");
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected Resource[] getProjectEnvFiles(String envPropertiesFilename) {
		File envFile = new File(useGlobalResourceDirectory ? globalResourceDirectory : resourceDirectory,
			envPropertiesFilename);
		
		if (envFile.exists()) {
			return (Resource[]) new Resource[] {new FileSystemResource(envFile)};
		} else {
			return new Resource[0];
		}
	}
}
