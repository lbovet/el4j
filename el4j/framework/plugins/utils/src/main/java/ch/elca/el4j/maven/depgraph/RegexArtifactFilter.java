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
package ch.elca.el4j.maven.depgraph;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * This class filters artifacts according to their name, group and version.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philippe Jacot (PJA)
 */
public class RegexArtifactFilter implements ArtifactFilter {
	/**
	 * Filter for the artifactId.
	 */
	private Pattern m_artifactFilter;

	/**
	 * Filter for the groupId.
	 */
	private Pattern m_groupFilter;

	/**
	 * Filter for the version.
	 */
	private Pattern m_versionFilter;

	/**
	 * Artifacts to ignore.
	 */
	private List<Artifact> m_ignoreList = new LinkedList<Artifact>();

	/**
	 * Create a new RegexArtifactFilter with given constraints.
	 *
	 * @param artifactFilter
	 *            Filter the artifactid has to match
	 * @param groupFilter
	 *            Filter the groupid has to match
	 * @param versionFilter
	 *            Filter the version has to match
	 */
	public RegexArtifactFilter(String artifactFilter, String groupFilter,
		String versionFilter) {

		if (StringUtils.isNotEmpty(artifactFilter)) {
			m_artifactFilter = Pattern.compile(artifactFilter);
		}

		if (StringUtils.isNotEmpty(groupFilter)) {
			m_groupFilter = Pattern.compile(groupFilter);
		}

		if (StringUtils.isNotEmpty(versionFilter)) {
			m_versionFilter = Pattern.compile(versionFilter);
		}
	}

	/**
	 * Get the list of artifacts to ignore.
	 *
	 * @return List of ignored artifacts
	 */
	public List<Artifact> getIgnoreList() {
		return m_ignoreList;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean include(Artifact artifact) {
		boolean artifactMatch = true;
		boolean groupMatch = true;
		boolean versionMatch = true;

		if (m_ignoreList.contains(artifact)) {
			return true;
		}

		if (m_artifactFilter != null) {
			artifactMatch = m_artifactFilter.matcher(artifact.getArtifactId())
				.find();

		}

		if (m_groupFilter != null) {
			groupMatch = m_groupFilter.matcher(artifact.getGroupId()).find();
		}

		if (m_versionFilter != null) {
			versionMatch = m_versionFilter.matcher(artifact.getVersion())
				.find();
		}
		return artifactMatch && groupMatch && versionMatch;
	}
}