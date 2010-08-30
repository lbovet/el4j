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

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.maven.ResourceLoader;

/**
 * Print a list of all env properties with their resolved values.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @goal list
 * @requiresProject true
 * @requiresDependencyResolution compile
 *
 * @author Stefan Wismer (SWI)
 */
public class EnvListMojo extends AbstractEnvListMojo {
	// Checkstyle: MemberName off
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
	
	
	/** {@inheritDoc} */
	@Override
	protected ResourceLoader getResourceLoader() {
		return getResourceLoader(true, false);
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
	
	/** {@inheritDoc} */
	@Override
	protected String getArtifactNameFromLocalResource(FileSystemResource resource) {
		if (resource.getFile().getPath().startsWith(resourceDirectory.getPath())) {
			return "this artifact (" + getProject().getArtifact().getArtifactId() + ")";
		}
		return null;
	}
}
