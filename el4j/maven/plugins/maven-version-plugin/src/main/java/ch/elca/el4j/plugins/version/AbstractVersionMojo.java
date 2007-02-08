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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.SnapshotArtifactRepositoryMetadata;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;

/**
 * This class is the starting point for analyzing the versions of artifacts 
 * and provides some general tools.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
public abstract class AbstractVersionMojo extends AbstractMojo {
    /**
     * The local Repository.
     * 
     * @parameter expression="${localRepository}
     * @required
     * @readonly
     */
    private ArtifactRepository m_localRepository;

    /**
     * The artifact factory.
     * 
     * @component
     * @required
     */
    private ArtifactFactory m_factory;

    /**
     * Metadata Manager.
     * 
     * @component
     */
    private RepositoryMetadataManager m_metadataManager;

    /**
     * Metadata Source.
     * 
     * @component
     */
    private ArtifactMetadataSource m_metadataSource;
    
    /**
     * Get all available versions for an artifact.
     * @param artifact Artifact to get versions of
     * @param remoteRepositories Remote Repositories to look in
     * @return A list of versions
     */
    @SuppressWarnings("unchecked")
    protected VersionResult getAvailableVersions(Artifact artifact,
        List<ArtifactRepository> remoteRepositories) {
        List<ArtifactVersion> result = null;

        try {
            RepositoryMetadata snapshotMetadata 
                = new SnapshotArtifactRepositoryMetadata(artifact);
            m_metadataManager.resolve(snapshotMetadata, remoteRepositories,
                m_localRepository);

            result = (List<ArtifactVersion>) m_metadataSource
                .retrieveAvailableVersions(artifact, m_localRepository,
                    remoteRepositories);
            
            // To surely not get an Abstract list 
            // that does not support insertion
            result = new LinkedList<ArtifactVersion>(result);

            // For snapshots add the used snapshot as well
            if (snapshotMetadata.isSnapshot()) {
                result.add(new DefaultArtifactVersion(snapshotMetadata
                    .getMetadata().getVersion()));
            }

        } catch (ArtifactMetadataRetrievalException e) {
            getLog().warn("Unable to retrieve Artifact Metadata for " 
                + artifact.toString());
            result = Collections.emptyList();
        } catch (RepositoryMetadataResolutionException e) {
            getLog().warn("Unable to resolve Repository Data");
            result = Collections.emptyList();
        }

        if (result == null) {
            result = new LinkedList<ArtifactVersion>();
        }

        return new VersionResult(artifact, result);
    }   

    /**
     * Create an artifact for the given data.
     * 
     * @param artifactId The artifact ID
     * @param groupId The group ID
     * @param version The version
     * @param scope The scope
     * @param type The type
     * @return An artifact
     */
    protected Artifact getArtifact(
        String artifactId, 
        String groupId, 
        String version, 
        String scope, 
        String type) {
        return m_factory.createArtifact(
            groupId, artifactId, version, scope, type);
    }
}
