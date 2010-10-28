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
package ch.elca.el4j.maven.plugins.manifestdecorator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import ch.elca.el4j.maven.plugins.AbstractSlf4jEnabledMojo;


/**
 * Prepares data for the special config section inside the manifest.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 *
 * @goal manifest-prepare-config-section
 * @phase package
 * @requiresProject true
 */
public class ManifestAddConfigSectionMojo extends AbstractSlf4jEnabledMojo {
	// Checkstyle: MemberName off
	
	/**
	 * Comma separated includes for the files list.
	 *
	 * @parameter expression="${fileListIncludes}" default-value="**\/*"
	 * @required
	 */
	protected String fileListIncludes;

	/**
	 * Comma separated excludes for the files list.
	 *
	 * @parameter expression="${fileListExcludes}" default-value="**\/*.class"
	 * @required
	 */
	protected String fileListExcludes;
	
	/**
	 * The prefix of each property which will be set.
	 *
	 * @parameter expression="${propertyNamePrefix}" default-value="el4j-config"
	 * @required
	 */
	protected String propertyNamePrefix;
	
	/**
	 * Separator for string lists.
	 *
	 * @parameter expression="${separator}" default-value=","
	 * @required
	 */
	protected String separator;

	/**
	 * Packaging name for normal jars.
	 *
	 * @parameter expression="${packagingNameJar}" default-value="jar"
	 * @required
	 */
	protected String packagingNameJar;
	
	/**
	 * Packaging name for test jars.
	 *
	 * @parameter expression="${packagingNameTestJar}" default-value="test-jar"
	 * @required
	 */
	protected String packagingNameTestJar;
	
	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	/**
	 * The maven session.
	 * 
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	protected MavenSession session;
	
	/**
	 * Runtime Information used to check the Maven version.
	 * @since 2.0
	 * @component role="org.apache.maven.execution.RuntimeInformation"
	 */
	protected RuntimeInformation rti;

	//Checkstyle: MemberName on
	
	/**
	 * The list of runtime dependencies of this project.
	 */
	private List<Dependency> deps;
	
	/**
	 * The list of testing dependencies of this project.
	 */
	private List<Dependency> testDeps;
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException {
		String projectPackaging = project.getPackaging();
		if (!StringUtils.hasText(projectPackaging)
			|| projectPackaging.contains("pom")) {
			getLog().info("No manifest config section property creation for "
				+ "pom project.");
			return;
		}
		
		/**
		 * Create the id for the current module.
		 */
		String manifestModule = project.getGroupId() + ":"
			+ project.getArtifactId() + ":" + packagingNameJar;
		String manifestTestModule = project.getGroupId() + ":"
			+ project.getArtifactId() + ":" + packagingNameTestJar;
		
		findDependencies();
		
		String manifestDependencies = getDependencyList(deps, false);
		String manifestTestDependencies = getDependencyList(testDeps, false);
		logDependencies(deps, testDeps);
		
		// Prepend dependency to own (main) module jar.
		manifestTestDependencies
			= manifestModule + separator + manifestTestDependencies;
		
		/**
		 * Get resource file list for manifest.
		 */
		Build build = project.getBuild();
		String outputDirectoryString = null;
		String testOutputDirectoryString = null;
		if (build != null) {
			outputDirectoryString = build.getOutputDirectory();
			testOutputDirectoryString = build.getTestOutputDirectory();
		}
		String manifestFiles
			= findFileResources(outputDirectoryString, "output");
		String manifestTestFiles
			= findFileResources(testOutputDirectoryString, "testOutput");
		
		/**
		 * Write the generated properties into project properties.
		 * maven has problems with empty Strings: it writes "null" into the manifest instead of "".
		 */
		Properties projectProperties = project.getProperties();
		projectProperties.setProperty(
			propertyNamePrefix + ".module", manifestModule);
		projectProperties.setProperty(
			propertyNamePrefix + ".testmodule", manifestTestModule);
		projectProperties.setProperty(
			propertyNamePrefix + ".files", ensureNotEmpty(manifestFiles));
		projectProperties.setProperty(
			propertyNamePrefix + ".testfiles", ensureNotEmpty(manifestTestFiles));
		projectProperties.setProperty(
			propertyNamePrefix + ".dependencies", ensureNotEmpty(manifestDependencies));
		projectProperties.setProperty(
			propertyNamePrefix + ".testdependencies", ensureNotEmpty(manifestTestDependencies));
	}
	/**
	 * Get runtime dependency list for manifest.
	 * 
	 * This got broken in maven 3, we use two different strategies to
	 * get the dependencies.
	 */
	@SuppressWarnings("deprecation")
	private void findDependencies() throws MojoExecutionException {
		ArtifactVersion detectedMavenVersion = rti.getApplicationVersion();
		VersionRange vr;
		try {
			vr = VersionRange.createFromVersionSpec("[3.0,)");
		} catch (InvalidVersionSpecificationException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}
		
		if (containsVersion(vr, detectedMavenVersion)) {
			// In maven 3, use the new API.
			
			// We're fetching this component dynamically. When annotating a field with
			// @component, maven 2 chokes on it because this class didn't
			// exist yet.
			ProjectDependenciesResolver resolver;
			try {
				resolver = (ProjectDependenciesResolver)
					session.lookup(ProjectDependenciesResolver.class.getName());
			} catch (ComponentLookupException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
			
			Collection<String> scopes = new ArrayList<String>(1);
			scopes.add(Artifact.SCOPE_COMPILE);
			scopes.add(Artifact.SCOPE_RUNTIME);
			Set<Artifact> depsArtifacts;
			try {
				depsArtifacts = resolver.resolve(project, scopes, session);
			} catch (ArtifactResolutionException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			} catch (ArtifactNotFoundException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
			
			scopes.add(Artifact.SCOPE_TEST);
			Set<Artifact> testDepsArtifacts;
			try {
				testDepsArtifacts = resolver.resolve(project, scopes, session);
			} catch (ArtifactResolutionException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			} catch (ArtifactNotFoundException e) {
				throw new MojoExecutionException(e.getLocalizedMessage(), e);
			}
			
			// Convert the Artifacts to Dependencies so we can reuse the rest
			// of the code in this plugin.
			deps = new ArrayList<Dependency>(depsArtifacts.size());
			for (Artifact a : depsArtifacts) {
				deps.add(convertToDependency(a));
			}
			
			testDeps = new ArrayList<Dependency>(testDepsArtifacts.size());
			for (Artifact a : testDepsArtifacts) {
				testDeps.add(convertToDependency(a));
			}
		} else {
			// In maven 2, use the old API.
			deps = project.getRuntimeDependencies();
			testDeps = project.getTestDependencies();
		}
	}
	
	/**
	 * Convert an Artifact to a Dependecy.
	 * This code is copied from MavenProject.
	 * @param a The artifact to convert.
	 * @return The corresponding dependency.
	 */
	private Dependency convertToDependency(Artifact a) {
		Dependency dependency = new Dependency();
		dependency.setArtifactId(a.getArtifactId());
		dependency.setGroupId(a.getGroupId());
		dependency.setVersion(a.getVersion());
		dependency.setScope(a.getScope());
		dependency.setType(a.getType());
		dependency.setClassifier(a.getClassifier());
		
		return dependency;
	}

	/**
	 * Log dependencies more intelligently.
	 * 
	 * @param deps	the dependencies for normal executions
	 * @param testDeps  the dependencies for tests
	 */
	private void logDependencies(List<Dependency> deps, List<Dependency> testDeps) {
		
		List<Dependency> workingDeps = deps == null ? new ArrayList<Dependency>() : deps;
		List<Dependency> workingTestDeps = testDeps == null ? new ArrayList<Dependency>() : testDeps;
		
		
		List<Dependency> onlyInNormal = calculateDependencyOnlyInFirstList(
			workingDeps, workingTestDeps);
		
		List<Dependency> onlyInTests = calculateDependencyOnlyInFirstList(
			workingTestDeps, workingDeps);
		
		String manifestDependencies = getDependencyList(workingDeps, true);
		
		getLog().info("Project " + project.getGroupId() + ":"
			+ project.getArtifactId() + " has the following "
			+ workingDeps.size() + " runtime dependencies: "
			+ manifestDependencies);
		
		getLog().info("Delta for tests:    only in tests: " + getDependencyList(onlyInTests, true)
			+ "  | only in normal execution: " + getDependencyList(onlyInNormal, true));
		
	}

	private List<Dependency> calculateDependencyOnlyInFirstList(
			List<Dependency> deps, List<Dependency> testDeps) {
		List<Dependency> onlyInNormal = new ArrayList<Dependency>();
		for (Dependency d : deps) {
			boolean found = false;
			for (Dependency tDep : testDeps) {
				if ((d.getArtifactId().equals(tDep.getArtifactId()))
					&& (d.getGroupId().equals(tDep.getGroupId()))
					&& (d.getVersion().equals(tDep.getVersion()))
					&& (d.getType().equals(tDep.getType()))) {
					
					found = true;
					break;
				}
			}
			
			if (!found) {
				onlyInNormal.add(d);
			}
		}
		return onlyInNormal;
	}

	/**
	 * @param deps Are the dependencies to fit into a single string.
	 * @param showVersion shall we add the version for each dependency
	 *                     (used for debugging)
	 * @return Returns the dependencies as a string.
	 */
	protected String getDependencyList(List<Dependency> deps, boolean showVersion) {
		Assert.notNull(deps);
		String manifestDependencies = "";
		if (!CollectionUtils.isEmpty(deps)) {
			StringBuffer sb = new StringBuffer();
			int manifestDependencyCount = 0;
			for (Dependency dependency : deps) {
				String artifactId = dependency.getArtifactId();
				String groupId = dependency.getGroupId();
				String type = dependency.getType();
				String dependencyString
					= groupId + ":" + artifactId + ":" + type;
				
				if (showVersion) {
					dependencyString += ":" + dependency.getVersion();
				}
				
				if (manifestDependencyCount > 0) {
					sb.append(separator);
				}
				sb.append(dependencyString);
				manifestDependencyCount++;
				
			}
			manifestDependencies = sb.toString();
			
		}
		return manifestDependencies;
	}

	/**
	 * @param fileResourceDirectoryString Is the resource directory as string.
	 * @param directoryName Is the name of the given directory.
	 * @return Returns a list of file resources as string.
	 */
	protected String findFileResources(String fileResourceDirectoryString,
		String directoryName) {
		Assert.hasText(directoryName);
		String resourceFiles = "";
		if (StringUtils.hasText(fileResourceDirectoryString)) {
			File fileResourceDirectory = new File(fileResourceDirectoryString);
			if (fileResourceDirectory.exists()
				&& fileResourceDirectory.isDirectory()
				&& fileResourceDirectory.canRead()) {
				try {
					@SuppressWarnings("unchecked")
					List<String> resourceFileList = FileUtils.getFileNames(
						fileResourceDirectory, fileListIncludes,
						fileListExcludes, false, true);
					int resourceFileCount = 0;
					StringBuffer sb = new StringBuffer();
					for (String resource : resourceFileList) {
						if (resourceFileCount > 0) {
							sb.append(separator);
						}
						resource = resource.replace("\\", "/");
						sb.append(resource);
						resourceFileCount++;
					}
					resourceFiles = sb.toString();
					getLog().info("Project " + project.getGroupId() + ":"
						+ project.getArtifactId()
						+ " has following resource files in " + directoryName
						+ " directory '"
						+ fileResourceDirectoryString + "': " + resourceFiles);
				} catch (IOException e) {
					getLog().info("Project " + project.getGroupId() + ":"
						+ project.getArtifactId()
						+ " made trouble while reading " + directoryName
						+ " directory '"
						+ fileResourceDirectoryString + "'.", e);
				}
				
			} else {
				getLog().info("Project " + project.getGroupId() + ":"
					+ project.getArtifactId()
					+ " has no readable " + directoryName + " directory '"
					+ fileResourceDirectoryString + "'.");
			}
		} else {
			getLog().info("Project " + project.getGroupId() + ":"
				+ project.getArtifactId() + " has no " + directoryName
				+ " directory.");
		}
		return resourceFiles;
	}
	
	/**
	 * @param string    the String that must not be empty
	 * @return          " " if string was empty, otherwise the string itself
	 */
	private String ensureNotEmpty(String string) {
		return string.length() > 0 ? string : " ";
	}
	
	/**
	 * Copied from Artifact.VersionRange. This is tweaked to handle singular ranges properly. Currently the default
	 * containsVersion method assumes a singular version means allow everything. This method assumes that "2.0.4" ==
	 * "[2.0.4,)"
	 *
	 * @param allowedRange range of allowed versions.
	 * @param theVersion the version to be checked.
	 * @return true if the version is contained by the range.
	 */
	@SuppressWarnings("unchecked")
	public static boolean containsVersion(VersionRange allowedRange, ArtifactVersion theVersion) {
		boolean matched = false;
		ArtifactVersion recommendedVersion = allowedRange.getRecommendedVersion();
		if (recommendedVersion == null) {
			for (Iterator i = allowedRange.getRestrictions().iterator(); i.hasNext() && !matched;) {
				Restriction restriction = (Restriction) i.next();
				if (restriction.containsVersion(theVersion)) {
					matched = true;
				}
			}
		} else {
			// only singular versions ever have a recommendedVersion
			int compareTo = recommendedVersion.compareTo(theVersion);
			matched = (compareTo <= 0);
		}
		return matched;
	}

}
