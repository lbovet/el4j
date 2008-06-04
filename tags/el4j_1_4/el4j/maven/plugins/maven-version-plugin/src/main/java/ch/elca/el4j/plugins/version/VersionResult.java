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
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/**
 * 
 * This class holds the result of a query for available versions and provides
 * easy access to the newer versions available.
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
public class VersionResult {
    
    /**
     * List of all available versions.
     */
    private List<ArtifactVersion> m_versions 
        = new LinkedList<ArtifactVersion>();
    
    /**
     * The artifact this result was created with.
     */
    private Artifact m_artifact;
    
    /**
     * Create a new VersionResult for a given artifact and a list of available
     * versions.
     * @param artifact The artifact described
     * @param versions All available versions
     */
    @SuppressWarnings("unchecked")
    public VersionResult(Artifact artifact, List<ArtifactVersion> versions) {
        if (artifact == null || versions == null) {
            throw new NullPointerException("Arguments must not be null");
        }
        
        Collections.<ArtifactVersion>sort(versions);
        this.m_artifact = artifact;
        this.m_versions = versions;
    }
    
    /**
     * Get the available versions.
     * @return The available versions
     */
    public List<ArtifactVersion> getVersions() {
        return m_versions;
    }
    
    /**
     * Get the artifact.
     * @return The artifact
     */
    public Artifact getArtifact() {
        return m_artifact;
    }
    
    /**
     * Is a newer version of this artifact available?
     * @return Whether a new version is available
     */
    @SuppressWarnings("unchecked")
    public boolean isNewerVersionAvailable() {
        ArtifactVersion currentVersion 
            = new DefaultArtifactVersion(m_artifact.getVersion());
        
        for (ArtifactVersion version : m_versions) {
            if (version.compareTo(currentVersion) == 1) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all newer versions of the managed artifact.
     * @return All newer versions
     */
    @SuppressWarnings("unchecked")
    public List<ArtifactVersion> getNewerVersions() {
        List<ArtifactVersion> newerVersions = new LinkedList<ArtifactVersion>();
        ArtifactVersion currentVersion 
            = new DefaultArtifactVersion(m_artifact.getVersion());
    
        for (ArtifactVersion version : m_versions) {
            if (version.compareTo(currentVersion) == 1) {
                newerVersions.add(version);
            }
        }
        
        // Sort the result
        Collections.<ArtifactVersion>sort(newerVersions);
        return newerVersions;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!(obj instanceof VersionResult)) {
            return false;
        }
        
        VersionResult versionResult = (VersionResult) obj;
        
        if (!(versionResult.getArtifact().equals(getArtifact()))) {
            return false;
        }
        
        // This does not really work as ArtifactVersion does not 
        // implement equals
        if (!(versionResult.getVersions().equals(getVersions()))) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
