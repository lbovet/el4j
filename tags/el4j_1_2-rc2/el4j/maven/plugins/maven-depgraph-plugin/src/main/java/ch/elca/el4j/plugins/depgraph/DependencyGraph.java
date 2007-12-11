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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;

/**
 * 
 * This class is a set of artifacts that make up a dependency graph.
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
public class DependencyGraph {
    /**
     * All known artifacts
     * Non-Generic hasmap cause of a bug in a tool used for maven plugins.
     */
    // There is a qdox problem when using a HashMap
    // (Most likely all generics of the form <a,b>)
    // .. http://jira.codehaus.org/browse/QDOX-89
    private Map<String, DepGraphArtifact> m_artifacts 
        = new HashMap();
    
    /**
     * The name of the graph.
     */
    private String m_name;
    
    /**
     * Get the Artifact for the given attributes.
     * @param artifactId The artifact ID
     * @param groupId The group ID
     * @param version The version
     * @param scope The scope
     * @param type The type
     * @return A DepgraphArtifact
     */
    public DepGraphArtifact getArtifact(
        String artifactId, String groupId, 
        String version, String scope, String type) {
        DepGraphArtifact newArtifact 
            = new DepGraphArtifact(artifactId, groupId, version, scope, type);
        
        if (m_artifacts.containsKey(newArtifact.getQualifiedName())) {
            return m_artifacts.get(newArtifact.getQualifiedName());
        } else {
            m_artifacts.put(newArtifact.getQualifiedName(), newArtifact);
            return newArtifact;
        }
    }
    
    /**
     * Get a list of all registered artifacts.
     * @return A list of all artifacts
     */
    public List<DepGraphArtifact> getArtifacts() {
        return new LinkedList<DepGraphArtifact>(m_artifacts.values());
    }
    
    /**
     * Remove an artifact from the collection if it exists.
     * @param artifact The artifact to remove.
     */
    public void removeArtifact(DepGraphArtifact artifact) {
        if (m_artifacts.containsKey(artifact.getQualifiedName())) {
            m_artifacts.remove(artifact.getQualifiedName());
        }
        
    }
    
    /**
     * Get this graphs name.
     * @return The name
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * Set this graphs name.
     * @param name The name
     */
    public void setName(String name) {
        m_name = name;
    }
}
