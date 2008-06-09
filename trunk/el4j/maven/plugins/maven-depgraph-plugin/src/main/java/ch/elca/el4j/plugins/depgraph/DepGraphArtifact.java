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
	 * The artifacts scope.
	 */
	private String m_scope;
	
	/**
	 * The artifacts type.
	 */
	private String m_type;
	
	/**
	 * The artifacts classifier.
	 */
	private String m_classifier;
	
	/**
	 * Whether this artifact has been omitted
	 */
	private boolean m_omitted;
	
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
	 * @param scope The scope to set
	 * @param type The type to set
	 */
	public DepGraphArtifact(String artifactId, String groupId, String version,
		String scope, String type, String classifier) {
		this(artifactId, groupId, version, scope, type, classifier, false);
	}
	
	/**
	 * Create a new DepGraphArtifact.
	 *
	 * @param artifactId The id to set
	 * @param groupId The group to set
	 * @param version The version to set
	 * @param scope The scope to set
	 * @param type The type to set
	 * @param omitted Whether this artifact is omitted
	 */
	public DepGraphArtifact(String artifactId, String groupId, String version,
		String scope, String type, String classifier, boolean omitted) {
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
		m_scope = scope;
		m_type = type;
		m_classifier = classifier != null ? classifier : "";
		m_omitted = omitted;
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
	 * Get this artifacts scope.
	 * @return The Scope
	 */
	public String getScope() {
		return m_scope;
	}
	
	/**
	 * Get this artifacts type.
	 * @return The type.
	 */
	public String getType() {
		return m_type;
	}
	
	/**
	 * Get this artifacts classifier.
	 * @return The classifier.
	 */
	public String getClassifier() {
		return m_classifier;
	}
	
	/**
	 * Get a qualified name of this artifact.
	 * @return groupId:version:classifier:artifactId:omitted
	 */
	public String getQualifiedName() {
		return m_groupId + ":" + m_version + ":" + m_classifier + ":"
			+ m_artifactId + ":" + m_omitted;
	}
	
	/**
	 * Add a new dependency.
	 * @param dependency The dependency to add
	 */
	public void addDependency(DepGraphArtifact dependency) {
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
				&& m_classifier.equals(d.getClassifier())
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

	/**
	 * @return whether this artifact has been omitted.
	 */
	public boolean isOmitted() {
		return m_omitted;
	}

	/**
	 * Set to true to omit this artifact.
	 *
	 * @param omitted
	 *            whether this artifact is to omit.
	 */
	public void setOmitted(boolean omitted) {
		this.m_omitted = omitted;
	}

}
