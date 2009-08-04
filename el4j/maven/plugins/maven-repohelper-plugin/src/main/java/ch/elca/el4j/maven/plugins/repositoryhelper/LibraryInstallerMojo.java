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
package ch.elca.el4j.maven.plugins.repositoryhelper;

import org.codehaus.plexus.util.cli.Commandline;
import org.springframework.util.StringUtils;


/**
 * Maven mojo to install multiple libraries (jars and sources) just in the local
 * repository.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 *
 * @goal install-libraries
 * @requiresProject false
 */
public class LibraryInstallerMojo extends AbstractLibraryAdderMojo {
	/**
	 * {@inheritDoc}
	 */
	protected void modifyCommandLine(MavenDependency dependency, Commandline cmd) {
		cmd.createArg().setValue("install:install-file");
		cmd.createArg().setValue("-DgroupId=" + dependency.getGroupId());
		cmd.createArg().setValue("-DartifactId=" + dependency.getArtifactId());
		cmd.createArg().setValue("-Dversion=" + dependency.getVersion());
		cmd.createArg().setValue("-Dfile=" + dependency.getLibraryPath());
		
		// Set these values only if it is not a pom
		if (!dependency.isPomOnly()) {
			cmd.createArg().setValue("-Dpackaging=jar");
			String classifier = dependency.getClassifier();
			if (StringUtils.hasText(classifier)) {
				cmd.createArg().setValue("-Dclassifier=" + classifier);
			}
		}
		
		// Use existing pom or let the plugin generate one
		if (dependency.getPomPath() != null) {
			cmd.createArg().setValue("-DpomFile=" + dependency.getPomPath());
			cmd.createArg().setValue("-DgeneratePom=false");
		} else {
			cmd.createArg().setValue("-DgeneratePom=true");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String getActionVerb() {
		return "install";
	}
}
