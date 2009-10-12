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

import java.util.Collection;
import java.util.LinkedList;

import org.apache.maven.artifact.Artifact;

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
	 * The maven artifact.
	 */
	private Artifact m_artifact;
	
	/**
	 * Whether this artifact has been omitted.
	 */
	private boolean m_omitted;
	
	/**
	 * All dependencies of this artifact.
	 */
	private Collection<DepGraphArtifact> m_dependencies
		= new LinkedList<DepGraphArtifact>();

	/**
	 * Create a new DepGraphArtifact.
	 *
	 * @param artifact    The maven artifact
	 */
	public DepGraphArtifact(Artifact artifact) {
		this(artifact, false);
	}
	
	/**
	 * Create a new DepGraphArtifact.
	 *
	 * @param artifact    The maven artifact
	 * @param omitted Whether this artifact is omitted
	 */
	public DepGraphArtifact(Artifact artifact, boolean omitted) {
		m_artifact = artifact;
		m_omitted = omitted;
	}
	
	/**
	 * Get this artifacts id.
	 * @return The id
	 */
	public String getArtifactId() {
		return m_artifact.getArtifactId();
	}
	
	/**
	 * Get this artifacts group.
	 * @return The group id
	 */
	public String getGroupId() {
		return m_artifact.getGroupId();
	}
	
	/**
	 * Get this artifacts version.
	 * @return The version
	 */
	public String getVersion() {
		return m_artifact.getVersion();
	}
	
	/**
	 * Get this artifacts scope.
	 * @return The Scope
	 */
	public String getScope() {
		return m_artifact.getScope();
	}
	
	/**
	 * Get this artifacts type.
	 * @return The type.
	 */
	public String getType() {
		return m_artifact.getType();
	}
	
	/**
	 * Get this artifacts classifier.
	 * @return The classifier.
	 */
	public String getClassifier() {
		return m_artifact.getClassifier();
	}
	
	/**
	 * Get a qualified name of this artifact (ignore version as it might change during artifact resolution).
	 * @return groupId:classifier:artifactId:omitted
	 */
	public String getQualifiedName() {
		return getGroupId() + ":" + getClassifier() + ":"
			+ getArtifactId() + ":" + m_omitted;
	}
	
	/**
	 * @return    The maven artifact.
	 */
	public Artifact getMavenArtifact() {
		return m_artifact;
	}
	
	/**
	 * Add a new dependency.
	 * @param dependency The dependency to add
	 */
	public void addDependency(DepGraphArtifact dependency) {
		if (dependency == null) {
			throw new NullPointerException("Dependency null");
		}
		
		if (m_dependencies.contains(dependency)) {
			throw new IllegalArgumentException(
				"Artifact already depends on " + dependency.getQualifiedName());
		}
		
		m_dependencies.add(dependency);
	}
	
	/**
	 * Get this artifacts dependencies.
	 * @return The dependencies
	 */
	public Collection<DepGraphArtifact> getDependencies() {
		return new LinkedList<DepGraphArtifact>(m_dependencies);
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
	
	/**
	 *
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof DepGraphArtifact) {
			DepGraphArtifact d = (DepGraphArtifact) obj;
			return getQualifiedName().equals(d.getQualifiedName());
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
