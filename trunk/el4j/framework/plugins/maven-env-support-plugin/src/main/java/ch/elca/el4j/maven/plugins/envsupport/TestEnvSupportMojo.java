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
package ch.elca.el4j.maven.plugins.envsupport;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.maven.ResourceLoader;

/**
 * Test environment support plugin. Filters the test resources of given env dir
 * and saves the generate test resources in a special dir.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 *
 * @goal testResources
 * @phase generate-test-resources
 * @requiresProject true
 */
public class TestEnvSupportMojo extends EnvSupportMojo {
	// Checkstyle: MemberName off
	/**
	 * The test output directory into which to copy the env resources.
	 *
	 * @parameter expression="${envsupport.testOutputDirectory}"
	 *            default-value="${project.build.directory}/env-test"
	 * @required
	 */
	private File testOutputDirectory;

	/**
	 * The test resource directory where to transfer files.
	 *
	 * @parameter expression="${envsupport.testResourceDirectory}"
	 *            default-value="src/test/env"
	 * @required
	 */
	private File testResourceDirectory;
	
	/**
	 * The global test resource directory where to transfer files.
	 *
	 * @parameter expression="${envsupport.globalTestResourceDirectory}"
	 */
	private File globalTestResourceDirectory;
	
	/**
	 * Flag to indicate if the global test resource dir should be used.
	 *
	 * @parameter expression="${envsupport.useGlobalTestResourceDirectory}"
	 *            default-value="false"
	 */
	private boolean useGlobalTestResourceDirectory;
	// Checkstyle: MemberName on
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		if ("pom".equals(getProject().getPackaging())) {
			return;
		}
		initializeFiltering();
		
		if (useGlobalTestResourceDirectory) {
			copyResourcesFiltered(globalTestResourceDirectory, testOutputDirectory, "globalTestResources");
			createEnvValuesFile("env.xml", testOutputDirectory, "env-values.properties");
			createEnvConstantsFile(globalTestResourceDirectory, "env.xml", testOutputDirectory,
				"env-constants.properties");
		} else {
			copyResourcesFiltered(testResourceDirectory, testOutputDirectory, "testResources");
			createEnvValuesFile("env.xml", testOutputDirectory, "env-values.properties");
			createEnvConstantsFile(testResourceDirectory, "env.xml", testOutputDirectory,
				"env-constants.properties");
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected ResourceLoader getResourceLoader() {
		return getResourceLoader(true, true);
	}
	
	/** {@inheritDoc} */
	@Override
	protected Resource[] getProjectEnvFiles(String envPropertiesFilename) {
		Resource[] nonTestResources = super.getProjectEnvFiles(envPropertiesFilename);
		
		File envFile = new File(useGlobalTestResourceDirectory ? globalTestResourceDirectory : testResourceDirectory,
			envPropertiesFilename);
		
		if (envFile.exists()) {
			return (Resource[]) ArrayUtils.add(nonTestResources, new FileSystemResource(envFile));
		} else {
			return nonTestResources;
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected String getArtifactNameFromLocalResource(FileSystemResource resource) {
		String nonTest = super.getArtifactNameFromLocalResource(resource);
		if (nonTest != null) {
			return nonTest;
		} else {
			if (resource.getFile().getPath().startsWith(testResourceDirectory.getPath())) {
				return "this artifact (" + getProject().getArtifact().getArtifactId() + ":test)";
			}
		}
		return null;
	}
}
