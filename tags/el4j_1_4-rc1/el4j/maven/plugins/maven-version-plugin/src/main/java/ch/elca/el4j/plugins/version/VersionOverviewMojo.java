/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.plugins.version;

import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Mojo to create an overview over all subfolder to get an idea of the used
 * artifacts and their versions.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Philippe Jacot (PJA)
 * @goal overview
 * @aggregator
 * @requiresDependencyResolution compile|test
 */
public class VersionOverviewMojo extends AbstractProjectVersionMojo {
    /**
     * All projects in the reactor.
     * 
     * @parameter default-value="${reactorProjects}"
     * @required
     * @readonly
     */
    private List<MavenProject> m_reactorProjects;
        
    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
        for (MavenProject project : m_reactorProjects) {
            // Dependencies
            for (Artifact dependency : (Set<Artifact>) project
                .getDependencyArtifacts()) {
                createOverview(dependency).addOccurence(
                    new ArtifactOccurence(project, ReferenceType.DEPENDENCY));
            }

            // Plugins
            for (Artifact plugin 
                : (Set<Artifact>) project.getPluginArtifacts()) {
                createOverview(plugin).addOccurence(
                    new ArtifactOccurence(project, ReferenceType.PLUGIN));
            }
        }

        
        // All projects have bee processed, print findings
        printArtifactOverviews();
    }



}
