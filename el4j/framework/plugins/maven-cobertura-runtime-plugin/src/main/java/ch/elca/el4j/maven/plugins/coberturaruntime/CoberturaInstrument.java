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

import net.sourceforge.cobertura.instrument.Main;

/**
 * Goal to instrument class files with cobertura stuff.
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
 * @goal instrument
 * @phase test-compile
 * @requiresProject true
 */
public class CoberturaInstrument extends AbstractCoberturaMojo {
	// Checkstyle: MemberName off
	
	/**
	 * Comma separated includes for the files list.
	 *
	 * @parameter expression="${cobertura-runtime.fileListIncludes}" default-value="**\/*.class"
	 * @required
	 */
	protected String fileListIncludes;

	/**
	 * Comma separated excludes for the files list.
	 *
	 * @parameter expression="${cobertura-runtime.fileListExcludes}" default-value="**\/*Test.class"
	 * @required
	 */
	protected String fileListExcludes;
	
	/**
	 * Comma separated list of methods to ignore for instrumentation. By default no method is ignored.
	 *
	 * @parameter expression="${cobertura-runtime.ignoredMethods}" default-value=""
	 */
	protected String ignoredMethods;
	
	//Checkstyle: MemberName on
	
	/**
	 * Executes the plugin.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		String projectPackaging = project.getPackaging();
		if (!StringUtils.hasText(projectPackaging)
			|| projectPackaging.contains("pom")) {
			getLog().info("No cobertura instrumentation for pom project.");
			return;
		}
		
		// Build the path to the cobertura data file
		if (coberturaDataDirectory.exists()) {
			if (!coberturaDataDirectory.isDirectory() || !coberturaDataDirectory.canWrite()) {
				new MojoExecutionException("The given cobertura data directory '" + coberturaDataDirectory.getName()
					+ "' exists but is not a directory or it can not be written into it.");
			}
		} else {
			coberturaDataDirectory.mkdirs();
		}
		File coberturaDataFile = new File(coberturaDataDirectory, coberturaDataFilename);
		String coberturaDataPath = null;
		try {
			coberturaDataPath = coberturaDataFile.getCanonicalPath();
		} catch (IOException e) {
			new MojoExecutionException("Could not create the canonical path of the cobertura data file with "
				+ "name '" + coberturaDataFilename + "' in directory '" + coberturaDataDirectory.getName() + "'!");
		}
		
		
		// Create the list of instrumentable files
		Build build = project.getBuild();
		String outputDirectoryString = null;
		String testOutputDirectoryString = null;
		if (build != null) {
			outputDirectoryString = build.getOutputDirectory();
			testOutputDirectoryString = build.getTestOutputDirectory();
		}
		List<File> instrumentableFiles = findInstrumentableFiles(outputDirectoryString, "output");
		
		if (!instrumentableFiles.isEmpty()) {
			// Instrument main classes
			List<String> argumentList = createArgumentList(
				coberturaDataPath, outputDirectoryString, instrumentableFiles);
			Main.main(argumentList.toArray(new String[argumentList.size()]));
		}
		
		
		// Do the same for the test files, if needed
		if (includeTestFiles) {
			List<File> instrumentableTestFiles
				= findInstrumentableFiles(testOutputDirectoryString, "testOutput");
			if (!instrumentableTestFiles.isEmpty()) {
				List<String> argumentTestList = createArgumentList(
					coberturaDataPath, testOutputDirectoryString, instrumentableTestFiles);
				Main.main(argumentTestList.toArray(new String[argumentTestList.size()]));
			}
		}
	}

	/**
	 * Creates the argument list.
	 * 
	 * @param coberturaDataPath Is the path to the cobertura reporting data file.
	 * @param outputDirectoryString Is the directory where the write the instrumented files.
	 * @param instrumentableFiles Are the files to instrument.
	 * @return Returns the argument list.
	 */
	private List<String> createArgumentList(String coberturaDataPath, String outputDirectoryString,
		List<File> instrumentableFiles) {
		// Create the argument list
		List<String> arguments = new ArrayList<String>();
		arguments.add("--datafile");
		arguments.add(coberturaDataPath);
		arguments.add("--destination");
		arguments.add(outputDirectoryString);
		
		// Append ignored methods
		if (StringUtils.hasText(ignoredMethods)) {
			String[] ignoredMethodsArray = ignoredMethods.split(",");
			for (String ignoredMethod : ignoredMethodsArray) {
				if (StringUtils.hasText(ignoredMethod)) {
					arguments.add("--ignoreMethod");
					arguments.add(ignoredMethod);
				}
			}
		}
		
		for (File instrumentableFile : instrumentableFiles) {
			try {
				String canonicalPath = instrumentableFile.getCanonicalPath();
				arguments.add(canonicalPath);
			} catch (IOException e) {
				new MojoExecutionException("Could not create the canonical path of the instrumentable file '"
					+ instrumentableFile.getName() + "'!");
			}
		}
		return arguments;
	}

	
	/**
	 * @param outputDirectoryString Is the output directory as string.
	 * @param directoryName Is the nice name of the given directory (used for logging).
	 * @return Returns the list of found files.
	 */
	@SuppressWarnings("unchecked")
	protected List<File> findInstrumentableFiles(String outputDirectoryString, String directoryName) {
		Assert.hasText(directoryName);
		List<File> instrumentableFiles = new ArrayList<File>();
		if (StringUtils.hasText(outputDirectoryString)) {
			File outputDirectory = new File(outputDirectoryString);
			if (outputDirectory.exists() && outputDirectory.isDirectory() && outputDirectory.canRead()) {
				try {
					List<File> fileList = FileUtils.getFiles(
						outputDirectory, fileListIncludes,
						fileListExcludes, true);
					instrumentableFiles.addAll(fileList);
					getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId()
						+ " has " + instrumentableFiles.size() + " file(s) in " + directoryName
						+ " directory '" + outputDirectoryString + "'.");
				} catch (IOException e) {
					getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId()
						+ " made trouble while reading " + directoryName + " directory '" + outputDirectoryString
						+ "'.", e);
				}
			} else {
				getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId() + " has no readable "
					+ directoryName + " directory '" + outputDirectoryString + "'.");
			}
		} else {
			getLog().info("Project " + project.getGroupId() + ":" + project.getArtifactId() + " has no "
				+ directoryName + " directory.");
		}
		return instrumentableFiles;
	}
}
