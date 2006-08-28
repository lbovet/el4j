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
package ch.elca.el4j.plugins.repositoryhelper;

import org.codehaus.plexus.util.cli.Commandline;
import org.springframework.util.StringUtils;


/**
 * Maven mojo to install multiple libraries (jars and sources) just in the local
 * repository.
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
 * @goal install-libraries
 * @requiresProject false
 */
public class LibraryInstallerMojo extends AbstractLibraryAdderMojo {
    /**
     * {@inheritDoc}
     */
    protected void modifyCommandLine(MavenDependency dependency, 
        Commandline cmd) {
        cmd.createArgument().setValue("install:install-file");
        cmd.createArgument().setValue("-DgroupId=" + dependency.getGroupId());
        cmd.createArgument().setValue("-DartifactId=" 
            + dependency.getArtifactId());
        cmd.createArgument().setValue("-Dversion=" + dependency.getVersion());
        cmd.createArgument().setValue("-Dpackaging=jar");
        cmd.createArgument().setValue("-Dfile=" + dependency.getLibraryPath());
        
        String classifier = dependency.getClassifier();
        if (StringUtils.hasText(classifier)) {
            cmd.createArgument().setValue("-Dclassifier=" + classifier);
        }
        cmd.createArgument().setValue("-DgeneratePom=true");
    }
    
    /**
     * {@inheritDoc}
     */
    protected String getActionVerb() {
        return "install";
    }
}
