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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
 * @author Philippe Jacot (PJA)
 * @goal version
 */
public class ArtifactVersionMojo extends AbstractVersionMojo {
    //  Checkstyle: MemberName off
    /**
     * Artifact to find.
     * @parameter expression="${version.artifactid}"
     * @required
     */
    private String artifactId = "";
    
    /**
     * Group of the Artifact to find.
     * @parameter expression="${version.groupid}
     * @required
     */
    private String groupId = "";
    
    /**
     * Type of the Artifact to find.
     * @parameter expression="${version.type}
     */
    private String type = "jar";

    /**
     * Scope of the Artifact to find. 
     *
     * @parameter expression="${version.type}
     */
    private String scope = Artifact.SCOPE_RUNTIME;
    //  Checkstyle: MemberName on
    
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject m_project;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
        Artifact artifact = getArtifact(
            artifactId, groupId, Artifact.LATEST_VERSION, scope, type);
        
        List<ArtifactRepository> remoteRepositories;
        if (m_project == null) {
            // Use default remote repositories.
            getLog().info("Using default repositories");
            remoteRepositories = new LinkedList<ArtifactRepository>();
        } else {
            // Use remote repositories of the current project
            getLog().info("Using the currenct projects \"" 
                + m_project.getName() + "\" repositories.");
            remoteRepositories = m_project.getRemoteArtifactRepositories();
        }
        getLog().info("Used repositories:");
        for (ArtifactRepository repository : remoteRepositories) {
            getLog().info("\t" + repository.getId() + repository.getUrl());
        }
        
        VersionResult result 
            = getAvailableVersions(artifact, remoteRepositories);
        
        // List all found versions
        getLog().info("Artifact ID: " + artifactId);
        getLog().info("Group ID: " + groupId);
        getLog().info("Scope: " + scope);
        getLog().info("Type: " + type);
        for (ArtifactVersion version : result.getVersions()) {
            getLog().info("\t" + version);
        }
    }
}
