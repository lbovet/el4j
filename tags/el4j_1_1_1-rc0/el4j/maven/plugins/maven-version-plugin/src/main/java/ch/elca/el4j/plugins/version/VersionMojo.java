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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.transform.ArtifactTransformationManager;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;


/**
 * Mojo to config files in the .settings directory.
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


public class VersionMojo extends AbstractVersionMojo {
	
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * Should the plugin list all versions?
     * 
     * @parameter expression="${version.listall}"
     */
    private boolean listAllVersions = false;
                         
    /** 
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<VersionResult> dependencies = new LinkedList<VersionResult>();
        List<VersionResult> plugins = new LinkedList<VersionResult>();
        List<VersionResult> managedDependencies = new LinkedList<VersionResult>();
        List<VersionResult> managedPlugins = new LinkedList<VersionResult>();        
        
        List<ArtifactRepository> remoteRepositories = 
            (List<ArtifactRepository>) project.getRemoteArtifactRepositories();

		for (Dependency d:(List<Dependency>) project.getDependencies()) {
			Artifact a = convertDependencyToArtifact(d);
            dependencies.add(getAvailableVersions(a, remoteRepositories));
        }
        
        for (Dependency d:(List<Dependency>) project.getDependencyManagement().getDependencies()) {
            Artifact a = convertDependencyToArtifact(d);
            managedDependencies.add(getAvailableVersions(a, remoteRepositories));
        }        		

		for(Artifact a:(Set<Artifact>) project.getPluginArtifacts()) {
            plugins.add(getAvailableVersions(a, remoteRepositories));      
        }

        for (Plugin p:(List<Plugin>) project.getPluginManagement().getPlugins()) {
            Artifact a = convertPluginToArtifact(p);
            managedPlugins.add(getAvailableVersions(a, remoteRepositories));
        } 
        
        getLog().info("Dependencies:");
        printArtifacts(plugins, listAllVersions);

        getLog().info("Plugins:");
        printArtifacts(plugins, listAllVersions);
        
        getLog().info("Managed Dependencies:");
        printArtifacts(managedDependencies, listAllVersions);
        
        getLog().info("Managed Plugins:");
        printArtifacts(managedPlugins, listAllVersions);
    }
	

    
    private Artifact convertDependencyToArtifact(Dependency dependency){
        Artifact a = factory.createArtifact(
            dependency.getGroupId(),
            dependency.getArtifactId(),
            dependency.getVersion(),
            dependency.getScope(),
            dependency.getType());
        return a;
    }
    
    private Artifact convertPluginToArtifact(Plugin plugin){
        Artifact a = factory.createArtifact(
            plugin.getGroupId(),
            plugin.getArtifactId(),
            plugin.getVersion(),
            Artifact.SCOPE_RUNTIME,
            "maven-plugin");
        return a;
    }

	
	
	

}
