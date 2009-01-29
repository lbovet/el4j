/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.plugins.database.mojo;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.core.io.Resource;

/**
 * This class represents an SQL update script updating a schema from one version to another.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
final class UpdateScript implements Comparable<UpdateScript> {
	
	/**
	 * The version separator.
	 */
	static final String VersionSeparator = "-to-";
	
	/**
	 * The SQL file resource.
	 */
	private Resource m_resource;
	
	/**
	 * The identifier (taken from <code>update-[identifier]-[versionFrom]-to-[versionTo].sql</code>).
	 */
	private String m_identifier;
	
	/**
	 * The source version.
	 */
	private ArtifactVersion m_versionFrom;
	
	/**
	 * The target version.
	 */
	private ArtifactVersion m_versionTo;
	
	/**
	 * @param resource    the SQL file
	 */
	private UpdateScript(Resource resource) {
		m_resource = resource;
		String filename = resource.getFilename();
		
		
		if (filename.contains(VersionSeparator)) {
			try {
				String withoutSuffix = filename.substring(0, filename.length() - ".sql".length());
				int beforeTo = withoutSuffix.lastIndexOf(VersionSeparator);
				String versionTo = withoutSuffix.substring(beforeTo + VersionSeparator.length());
				String identifierAndFrom = withoutSuffix.substring(0, beforeTo);
				int beforeFrom = identifierAndFrom.lastIndexOf("-");
				String versionFrom = identifierAndFrom.substring(beforeFrom + 1);
				m_identifier = identifierAndFrom.substring("update-".length(), beforeFrom);
				
				// make version numbers nicer
				m_versionFrom = new DefaultArtifactVersion(versionFrom.replace("_", "."));
				m_versionTo = new DefaultArtifactVersion(versionTo.replace("_", "."));
			} catch (Exception e) {
				// error in processing
				m_resource = null;
			}
		} else {
			m_resource = null;
		}
	}
	
	/**
	 * @param resource    the SQL file
	 * @return            an UpdateScript or <code>null</code> if error occurred
	 */
	public static UpdateScript parse(Resource resource) {
		UpdateScript newScript = new UpdateScript(resource);
		if (newScript.getResource() == null) {
			return null;
		} else {
			return newScript;
		}
	}
	
	/**
	 * @return    the identifier update script
	 */
	public String getIdentifier() {
		return m_identifier;
	}
	
	/**
	 * @return    the source version of the update script
	 */
	public ArtifactVersion getVersionFrom() {
		return m_versionFrom;
	}
	
	/**
	 * @return    the target version of the update script
	 */
	public ArtifactVersion getVersionTo() {
		return m_versionTo;
	}
	
	/**
	 * @return    the associated SQL resource
	 */
	public Resource getResource() {
		return m_resource;
	}
	
	/**
	 * @param from    the start of the version range
	 * @param to      the end of the version range
	 * @return        <code>true</code> if script lies inside given version range
	 */
	@SuppressWarnings("unchecked")
	public boolean versionRangeIsBetween(ArtifactVersion from, ArtifactVersion to) {
		int direction = (int) Math.signum(m_versionFrom.compareTo(m_versionTo));
		if (Math.signum(from.compareTo(to)) != direction) {
			// update script goes in the other direction
			return false;
		}
		if (Math.signum(from.compareTo(m_versionFrom)) == -direction
			|| Math.signum(to.compareTo(m_versionTo)) == direction) {
			// not in range
			return false;
		}
		return true;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public int compareTo(UpdateScript o) {
		// compare identifier
		if (getIdentifier().compareTo(o.getIdentifier()) != 0) {
			return getIdentifier().compareTo(o.getIdentifier());
		} else {
			// compare version if identifier is equal
			return getVersionFrom().compareTo(o.getVersionFrom());
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return m_resource != null ? getResource().toString() : "(invalid)";
	}
}