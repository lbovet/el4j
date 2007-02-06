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
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;

/**
 * This class is ... <script type="text/javascript">printFileStatus ("$URL$",
 * "$Revision$", "$Date$", "$Author$" );</script>
 * 
 * @author Philippe Jacot (PJA)
 */
public abstract class AbstractVersionMojo extends AbstractMojo {

    /**
     * The local Repository
     * 
     * @parameter expression="${localRepository}
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * The artifact factory
     * 
     * @component
     * @required
     */
    protected ArtifactFactory factory;

    /**
     * Metadata Manager
     * 
     * @component
     */
    private RepositoryMetadataManager metadataManager;

    /**
     * Metadata Source
     * 
     * @component
     */
    private ArtifactMetadataSource metadataSource;

    protected VersionResult getAvailableVersions(Artifact a,
        List<ArtifactRepository> remoteRepositories) {
        List<ArtifactVersion> result = null;

        try {
            RepositoryMetadata snapshotMetadata = new SnapshotArtifactRepositoryMetadata(
                a);
            metadataManager.resolve(snapshotMetadata, remoteRepositories,
                localRepository);

            result = (List<ArtifactVersion>) metadataSource
                .retrieveAvailableVersions(a, localRepository,
                    remoteRepositories);
            // To surely not get an Abstract list that does not support insertion
            result = new LinkedList<ArtifactVersion>(result);

            // For snapshots add the used snapshot as well
            if (snapshotMetadata.isSnapshot()) {
                result.add(new DefaultArtifactVersion(snapshotMetadata
                    .getMetadata().getVersion()));
            }

        } catch (ArtifactMetadataRetrievalException e) {
            getLog().warn(
                "Unable to retrieve Artifact Metadata for " + a.toString());
            result = Collections.emptyList();
        } catch (RepositoryMetadataResolutionException e) {
            getLog().warn("Unable to resolve Repository Data");
            result = Collections.emptyList();
        }

        if (result == null) {
            result = new LinkedList<ArtifactVersion>();
        }

        return new VersionResult(a, result);
    }

    protected void printArtifactVersionComparison(Artifact a,
        List<ArtifactVersion> versions, boolean listAllVersions) {
        Restriction restriction = new Restriction(new DefaultArtifactVersion(a
            .getVersion()), false, null, false);
        List<ArtifactVersion> higherVersions = new LinkedList<ArtifactVersion>();

        Collections.sort(versions);

        for (ArtifactVersion version : versions) {
            if (restriction.containsVersion(version)) {
                higherVersions.add(version);
            }
        }

        if (listAllVersions || !higherVersions.isEmpty()) {
            Log log = getLog();
            log.info(a.getGroupId() + ":" + a.getArtifactId());
            log.info("\tVersion: " + a.getVersion());

            if (listAllVersions) {
                log.info("\tAvailable Versions:");
                for (ArtifactVersion version : versions)
                    log.info("\t\t" + version);
            }

            log.info("\tHigher Versions:");
            if (!higherVersions.isEmpty()) {
                for (ArtifactVersion version : higherVersions) {
                    log.info("\t\t" + version);
                }
            } else {
                log.info("\t\tHighest Version is in use");
            }

        }
    }

    protected void printArtifacts(List<VersionResult> versions, boolean printAll) {
        Log log = getLog();
        for (VersionResult v : versions) {
            if (printAll || v.isNewerVersionAvailable()) {
                log.info("ArtifactID:\t" + v.getArtifact().getArtifactId());
                log.info("GroupID:\t" + v.getArtifact().getGroupId());
                log.info("Version:\t" + v.getArtifact().getVersion());
                
                if (printAll) {
                    log.info("\tAll available versions:");
                    for (ArtifactVersion artifactVersion : v.getVersions()) {
                        log.info("\t\t" + artifactVersion.toString());
                    }
                } else {
                    log.info("\tAll newer versions:");
                    for (ArtifactVersion artifactVersion : v.getNewerVersions()) {
                        log.info("\t\t" + artifactVersion.toString());
                    }
                }
                log.info("");

            }
        }
    }
}
