/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.repositoryhelper;

/**
 * Represents a maven dependency.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class MavenDependency {
	/**
	 * Path to the library this dependency is made for.
	 */
	private String m_libraryPath;
	
	/**
	 * Path to the optional pom.xml file.
	 */
	private String m_pomPath;
	
	/**
	 * Group id.
	 */
	private String m_groupId;

	/**
	 * Artifact id.
	 */
	private String m_artifactId;
	
	/**
	 * Version.
	 */
	private String m_version;
	
	/**
	 * Classifier.
	 */
	private String m_classifier;
	
	/**
	 * Flag if this dep is only a pom. Default is <code>false</code>.
	 */
	private boolean m_pomOnly = false;

	/**
	 * @return Returns the libraryPath.
	 */
	public final String getLibraryPath() {
		return m_libraryPath;
	}

	/**
	 * @param libraryPath Is the libraryPath to set.
	 */
	public final void setLibraryPath(String libraryPath) {
		m_libraryPath = libraryPath;
	}
	
	/**
	 * @return Returns the pomPath.
	 */
	public String getPomPath() {
		return m_pomPath;
	}

	/**
	 * @param pomPath Is the pomPath to set.
	 */
	public void setPomPath(String pomPath) {
		m_pomPath = pomPath;
	}

	/**
	 * @return Returns the artifactId.
	 */
	public final String getArtifactId() {
		return m_artifactId;
	}

	/**
	 * @param artifactId Is the artifactId to set.
	 */
	public final void setArtifactId(String artifactId) {
		m_artifactId = artifactId;
	}

	/**
	 * @return Returns the classifier.
	 */
	public final String getClassifier() {
		return m_classifier;
	}

	/**
	 * @param classifier Is the classifier to set.
	 */
	public final void setClassifier(String classifier) {
		m_classifier = classifier;
	}

	/**
	 * @return Returns the groupId.
	 */
	public final String getGroupId() {
		return m_groupId;
	}

	/**
	 * @param groupId Is the groupId to set.
	 */
	public final void setGroupId(String groupId) {
		m_groupId = groupId;
	}

	/**
	 * @return Returns the version.
	 */
	public final String getVersion() {
		return m_version;
	}

	/**
	 * @param version Is the version to set.
	 */
	public final void setVersion(String version) {
		m_version = version;
	}
	
	/**
	 * @return Returns the <code>true</code> if this dep is only a pom.
	 */
	public final boolean isPomOnly() {
		return m_pomOnly;
	}

	/**
	 * @param pomOnly Must be set to <code>true</code> if this dep is only a pom.
	 */
	public final void setPomOnly(boolean pomOnly) {
		m_pomOnly = pomOnly;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "MavenDependency: " + getGroupId() + ":" + getArtifactId()
			+ ":" + getVersion() + ":" + getClassifier();
	}
}
