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
package ch.elca.el4j.maven.plugins.coberturaruntime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Goal to collect java source files of instrumented class files.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * 
 * @goal sourcecollector
 * @phase test-compile
 * @requiresProject true
 */
public class CoberturaSourceCollector extends AbstractCoberturaMojo {
	// Checkstyle: MemberName off
	
	/**
	 * Comma separated includes for the files list.
	 *
	 * @parameter expression="${cobertura-runtime.sourceFileListIncludes}" default-value="**\/*.java"
	 * @required
	 */
	protected String sourceFileListIncludes;

	/**
	 * Comma separated excludes for the files list.
	 *
	 * @parameter expression="${cobertura-runtime.sourceFileListExcludes}" default-value="**\/*Test.java"
	 * @required
	 */
	protected String sourceFileListExcludes;
	
	//Checkstyle: MemberName on
	
	/**
	 * Executes the plugin.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		String projectPackaging = project.getPackaging();
		if (!StringUtils.hasText(projectPackaging)
			|| projectPackaging.contains("pom")) {
			getLog().info("No source code collection for pom project.");
			return;
		}
		
		// Build the path to the java source dir
		if (coberturaDataDirectory.exists()) {
			if (!coberturaDataDirectory.isDirectory() || !coberturaDataDirectory.canWrite()) {
				new MojoExecutionException("The given cobertura data directory '" + coberturaDataDirectory.getName()
					+ "' exists but is not a directory or it can not be written into it.");
			}
		} else {
			coberturaDataDirectory.mkdirs();
		}
		File coberturaSourceDirectory = new File(coberturaDataDirectory, sourceCollectorDirectoryName);
		if (!coberturaSourceDirectory.exists()) {
			coberturaSourceDirectory.mkdirs();
		}
		String coberturaSourceDirectoryString = coberturaSourceDirectory.getAbsolutePath();
		
		// Create the list of source files
		Build build = project.getBuild();
		String sourceDirectoryString = null;
		String testSourceDirectoryString = null;
		if (build != null) {
			sourceDirectoryString = build.getSourceDirectory();
			testSourceDirectoryString = build.getTestSourceDirectory();
		}
		List<String> relativeSourceFilePaths = findSourceFiles(sourceDirectoryString, "source");
		
		if (!relativeSourceFilePaths.isEmpty()) {
			// Collect main source files
			File sourceDirectory = new File(sourceDirectoryString);
			for (String relativeSourceFilePath : relativeSourceFilePaths) {
				File source = new File(sourceDirectory, relativeSourceFilePath);
				File destination = new File(coberturaSourceDirectory, relativeSourceFilePath);
				try {
					FileUtils.copyFile(source, destination);
				} catch (IOException e) {
					getLog().warn("Could not copy source file '" + relativeSourceFilePath + "' from '" 
						+ sourceDirectoryString + "' to '" + coberturaSourceDirectoryString + "'!");
				}
			}
		}
		
		// Do the same for the test files, if needed
		if (includeTestFiles) {
			List<String> relativeSourceTestFilePaths = findSourceFiles(testSourceDirectoryString, "testSource");
			
			if (!relativeSourceTestFilePaths.isEmpty()) {
				// Collect test source files
				File testSourceDirectory = new File(testSourceDirectoryString);
				for (String relativeTestSourceFilePath : relativeSourceTestFilePaths) {
					File source = new File(testSourceDirectory, relativeTestSourceFilePath);
					File destination = new File(coberturaSourceDirectory, relativeTestSourceFilePath);
					try {
						FileUtils.copyFile(source, destination);
					} catch (IOException e) {
						getLog().warn("Could not copy test source file '" + relativeTestSourceFilePath + "' from '" 
							+ testSourceDirectoryString + "' to '" + coberturaSourceDirectoryString + "'!");
					}
				}
			}
		}
	}

	/**
	 * @param sourceDirectoryString Is the output directory as string.
	 * @param directoryName Is the nice name of the given directory (used for logging).
	 * @return Returns the list of found files.
	 */
	@SuppressWarnings("unchecked")
	protected List<String> findSourceFiles(String sourceDirectoryString, String directoryName) {
		Assert.hasText(directoryName);
		List<String> sourceFiles = new ArrayList<String>();
		if (StringUtils.hasText(sourceDirectoryString)) {
			File sourceDirectory = new File(sourceDirectoryString);
			if (sourceDirectory.exists() && sourceDirectory.isDirectory() && sourceDirectory.canRead()) {
				try {
					List<String> fileList = FileUtils.getFileNames(
						sourceDirectory, sourceFileListIncludes,
						sourceFileListExcludes, false, true);
					sourceFiles.addAll(fileList);
					getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId()
						+ " has " + sourceFiles.size() + " file(s) in " + directoryName
						+ " directory '" + sourceDirectoryString + "'.");
				} catch (IOException e) {
					getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId()
						+ " made trouble while reading " + directoryName + " directory '" + sourceDirectoryString
						+ "'.", e);
				}
			} else {
				getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId() + " has no readable "
					+ directoryName + " directory '" + sourceDirectoryString + "'.");
			}
		} else {
			getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId() + " has no "
				+ directoryName + " directory.");
		}
		return sourceFiles;
	}
}
