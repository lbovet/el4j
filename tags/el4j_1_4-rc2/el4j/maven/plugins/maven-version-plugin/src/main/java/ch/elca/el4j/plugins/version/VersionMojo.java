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

import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;


/**
 * Lists for the current project all Dependencies, Managed Dependencies, Plugins
 * and managed Plugins if a newer version is available.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 * 
 * @goal list
 * @requiresDependencyResolution compile|test
 */
public class VersionMojo extends AbstractProjectVersionMojo {

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject m_project;
                             
    /** 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
            
        for (Artifact artifact : (Collection<Artifact>) 
            m_project.getDependencyArtifacts()) {
            createOverview(artifact).addOccurence(
                new ArtifactOccurence(m_project, ReferenceType.DEPENDENCY));
        }
        
        for (Dependency dependency : (Collection<Dependency>)
            m_project.getDependencyManagement().getDependencies()) {
            createOverview(toArtifact(dependency))
                .addOccurence(new ArtifactOccurence(
                    m_project, ReferenceType.DEPENDENCY_MANAGEMENT));
        }

        for (Artifact artifact : (Collection<Artifact>)
            m_project.getPluginArtifacts()) {
            createOverview(artifact).addOccurence(
                new ArtifactOccurence(m_project, ReferenceType.PLUGIN));  
        }

        for (Plugin plugin : (Collection<Plugin>) 
            m_project.getPluginManagement().getPlugins()) {
            createOverview(toArtifact(plugin))
                .addOccurence(new ArtifactOccurence(
                    m_project, ReferenceType.PLUGIN_MANAGEMENT));
        } 
        
        printArtifactOverviews();
    }
}
