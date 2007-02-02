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

public class VersionResult {
    
    private List<ArtifactVersion> m_versions = new LinkedList<ArtifactVersion>();
    
    private Artifact m_artifact;
    
    public VersionResult(Artifact artifact, List<ArtifactVersion> versions) {
        if (artifact == null || versions == null) {
            throw new NullPointerException("Arguments must not be null");
        }
        
        Collections.<ArtifactVersion>sort(versions);
        this.m_artifact = artifact;
        this.m_versions = versions;
    }
    
    /**
     * Get the available versions
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
    
    public boolean isNewerVersionAvailable() {
        ArtifactVersion currentVersion 
            = new DefaultArtifactVersion(m_artifact.getVersion());
        
        for (ArtifactVersion version : m_versions) {
            if (version.compareTo(currentVersion)==1) {
                return true;
            }
        }
        return false;
    }
    
    public List<ArtifactVersion> getNewerVersions() {
        List<ArtifactVersion> newerVersions = new LinkedList<ArtifactVersion>();
        ArtifactVersion currentVersion 
            = new DefaultArtifactVersion(m_artifact.getVersion());
    
        for (ArtifactVersion version : m_versions) {
            if (version.compareTo(currentVersion)==1) {
                newerVersions.add(version);
            }
        }
        
        // Sort the result
        Collections.<ArtifactVersion>sort(newerVersions);
        return newerVersions;
    }
}
