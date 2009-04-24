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
package ch.elca.el4j.maven.depgraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;

import org.apache.maven.artifact.Artifact;

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
	 * The name of the graph.
	 */
	private String m_name;
	
	/**
	 * Whether to draw artifact scope.
	 */
	private boolean m_drawScope = true;
	
	/**
	 * All known artifacts.
	 */
	private Map<String, DepGraphArtifact> m_artifacts
		= new HashMap<String, DepGraphArtifact>();
	
	/**
	 * Get the Artifact for the given attributes.
	 * @param artifact    The maven artifact
	 * @return A DepgraphArtifact
	 */
	public DepGraphArtifact getArtifact(Artifact artifact) {
		return getArtifact(artifact, false);
	}
	
	/**
	 * Get the Artifact for the given attributes.
	 * @param artifact    The maven artifact
	 * @param omitted Whether artifact is omitted
	 * @return A DepgraphArtifact
	 */
	public DepGraphArtifact getArtifact(Artifact artifact, boolean omitted) {
		DepGraphArtifact newArtifact
			= new DepGraphArtifact(artifact, omitted);
		
		if (m_artifacts.containsKey(newArtifact.getQualifiedName())) {
			return m_artifacts.get(newArtifact.getQualifiedName());
		} else {
			m_artifacts.put(newArtifact.getQualifiedName(), newArtifact);
			return newArtifact;
		}
	}
	
	/**
	 * @param qualifiedName    qualified name
	 * @return                 the corresponding DepgraphArtifact
	 */
	public DepGraphArtifact getArtifactByQualifiedName(String qualifiedName) {
		return m_artifacts.get(qualifiedName);
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

	/**
	 * @return whether to draw the artifact scope.
	 */
	public boolean drawScope() {
		return m_drawScope;
	}

	/**
	 * @param enable set to true to enable drawing of artifact scope.
	 */
	public void setDrawScope(boolean enable) {
		m_drawScope = enable;
	}
}
