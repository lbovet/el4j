/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

/**
 * Print a list of all test env properties with their resolved values.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @goal listTest
 * @requiresProject true
 * @requiresDependencyResolution test
 *
 * @author Stefan Wismer (SWI)
 */
public class TestEnvListMojo extends EnvListMojo {
	// Checkstyle: MemberName off
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
	
	/** {@inheritDoc} */
	public void execute() throws MojoExecutionException, MojoFailureException {
		initializeFiltering();
		
		showEnvPropertiesFiles("env-placeholder.properties");
		showMergedProperties("env-placeholder.properties");
		
		showEnvPropertiesFiles("env-bean-property.properties");
		showMergedProperties("env-bean-property.properties");

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
