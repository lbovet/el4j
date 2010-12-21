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
package ch.elca.el4j.maven.plugins.database.mojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.io.Resource;

import ch.elca.el4j.maven.plugins.database.AbstractDBExecutionMojo;
import ch.elca.el4j.util.codingsupport.annotations.FindBugsSuppressWarnings;



/**
 * This class is a database mojo for the 'update' statement.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @goal update
 * @author David Stefan (DST)
 */
public class UpdateMojo extends AbstractDBExecutionMojo {

	/**
	 * Action this mojo is implementing and identifier sql files have to start
	 * with.
	 */
	private static final String ACTION = "update";
	
	// Checkstyle: MemberName off
	
	/**
	 * The current database schema version.
	 *
	 * @parameter expression="${db.currentVersion}"  default-value=""
	 */
	private String currentVersion;
	
	/**
	 * The target database schema version.
	 *
	 * @parameter expression="${db.targetVersion}"  default-value=""
	 */
	private String targetVersion;
	
	// Checkstyle: MemberName on

	/**
	 * {@inheritDoc}
	 */
	public void executeInternal() throws MojoExecutionException, MojoFailureException {
		try {
			executeAction(ACTION, false, false);
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}
	
	/** {@inheritDoc} */
	@Override
	@FindBugsSuppressWarnings(value = "UWF_UNWRITTEN_FIELD",
			justification = "Fields currentVersion and targetVersion are injected by maven.")	
	protected List<Resource> preProcessResources(List<Resource> resources) throws MojoFailureException {
		super.preProcessResources(resources);
		
		if (StringUtils.isEmpty(currentVersion) || StringUtils.isEmpty(targetVersion)) {
			return getAllUnversionedResources(resources);
		} else {
			return getSuitableVersionedResources(resources);
		}
	}
	
	/**
	 * @param resources    all available SQL resources
	 * @return             a list of all SQL resources not having version information
	 */
	protected List<Resource> getAllUnversionedResources(List<Resource> resources) {
		List<Resource> result = new ArrayList<Resource>();
		
		getLog().info("No version specified. Processing all non-versioned files.");
		for (Resource resource : resources) {
			if (!resource.getFilename().contains(UpdateScript.VersionSeparator)) {
				result.add(resource);
			}
		}
		return result;
	}
	
	/**
	 * @param resources    all available SQL resources
	 * @return             a list of all SQL resources having to be processed to migrate the SQL schema
	 *                     to another version
	 */
	@SuppressWarnings("unchecked")
	protected List<Resource> getSuitableVersionedResources(List<Resource> resources) throws MojoFailureException {
		List<Resource> result = new ArrayList<Resource>();
		
		ArtifactVersion from = new DefaultArtifactVersion(currentVersion);
		ArtifactVersion to = new DefaultArtifactVersion(targetVersion);
		
		List<UpdateScript> suitableScripts = new ArrayList<UpdateScript>();
		for (Resource resource : resources) {
			UpdateScript script = UpdateScript.parse(resource);
			
			if (script == null) {
				getLog().warn("Could not parse filename '" + resource.getFilename() + "'. Filename must be of the form "
					+ "'update-<sometext>-<versionFrom>" + UpdateScript.VersionSeparator
					+ "<versionTo>.sql'. Update script is ignored.");
			} else {
				// take all scripts that lie in version range and correct direction
				if (script.versionRangeIsBetween(from, to)) {
					suitableScripts.add(script);
				}
			}
		}
		
		Collections.sort(suitableScripts, new Comparator<UpdateScript>() {
			@Override
			public int compare(UpdateScript u1, UpdateScript u2) {
				// compare identifier
				if (u1.getIdentifier().compareTo(u2.getIdentifier()) != 0) {
					return u1.getIdentifier().compareTo(u2.getIdentifier());
				} else {
					// compare version if identifier is equal
					return u1.getVersionFrom().compareTo(u2.getVersionFrom());
				}
			}
		});
		
		// downgrade schema?
		if (from.compareTo(to) > 0) {
			Collections.reverse(suitableScripts);
		}
		
		// convert list
		for (UpdateScript updateScript : suitableScripts) {
			result.add(updateScript.getResource());
		}
		
		return result;
	}
}
