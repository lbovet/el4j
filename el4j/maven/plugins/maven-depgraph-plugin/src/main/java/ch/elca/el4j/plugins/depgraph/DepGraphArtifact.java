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
package ch.elca.el4j.plugins.depgraph;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 
 * This class represents an artifact.
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
public class DepGraphArtifact {
    /**
     * The artifacts groupId.
     */
    private String m_groupId;
    
    /**
     * The artifacts Id.
     */
    private String m_artifactId;
    
    /**
     * The artifacts version.
     */
    private String m_version;
    
    /**
     * All dependencies of this artifact.
     */
    private Collection<DepGraphArtifact> m_artifacts 
        = new LinkedList<DepGraphArtifact>();
    
    /**
     * Create a new DepGraphArtifact.
     * 
     * @param artifactId The id to set
     * @param groupId The group to set
     * @param version The version to set
     */
    public DepGraphArtifact(String artifactId, String groupId, String version) {
        if (artifactId == null) {
            throw new NullPointerException("ArtifactId null");
        }
        
        if (groupId == null) {
            throw new NullPointerException("GroupId null");
        }
        
        if (version == null) {
            throw new NullPointerException("Version null");
        }
        
        m_artifactId = artifactId;
        m_groupId = groupId;
        m_version = version;
    }
    
    /**
     * Get this artifacts id.
     * @return The id
     */
    public String getArtifactId() {
        return m_artifactId;
    }
    
    /**
     * Get this artifacts group.
     * @return The group id
     */
    public String getGroupId() {
        return m_groupId;
    }
    
    /**
     * Get this artifacts version.
     * @return The version
     */
    public String getVersion() {
        return m_version;
    }
    
    /**
     * Get a qualified name of this artifact.
     * @return groupId:version:artifactId
     */
    public String getQualifiedName() {
        return m_groupId + ":" + m_version + ":" + m_artifactId;
    }
    
    /**
     * Add a new dependency.
     * @param dependency The dependency to add
     */
    public void addDependencie(DepGraphArtifact dependency) {
        if (dependency == null) {
            throw new NullPointerException("Dependency null");
        }
        
        if (m_artifacts.contains(dependency)) {
            throw new IllegalArgumentException(
                "Artifact already depends on " + dependency.getQualifiedName());
        }
        
        m_artifacts.add(dependency);
    }
    
    /**
     * Get this artifacts dependencies.
     * @return The dependencies
     */
    public Collection<DepGraphArtifact> getDependencies() {
        return new LinkedList<DepGraphArtifact>(m_artifacts);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof DepGraphArtifact) {
            DepGraphArtifact d = (DepGraphArtifact) obj;
            return m_artifactId.equals(d.getArtifactId())
                && m_groupId.equals(d.getGroupId())
                && m_version.equals(d.getVersion());
        } else {
            return super.equals(obj);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public int hashCode() {
        return getQualifiedName().hashCode();
    }

}
