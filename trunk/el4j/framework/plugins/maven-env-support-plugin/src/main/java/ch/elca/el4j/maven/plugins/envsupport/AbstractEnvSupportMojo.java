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
package ch.elca.el4j.maven.plugins.envsupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.ReflectionProperties;
import org.apache.maven.shared.filtering.PropertyUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import ch.elca.el4j.env.xml.ResolverUtils;
import ch.elca.el4j.maven.ResourceLoader;
import ch.elca.el4j.maven.plugins.envsupport.handlers.VariablesAndFinalPropertiesHandler;

/**
 * Abstract environment support plugin. Filters the resources of given env dir
 * and saves the generate resources in a special dir.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractEnvSupportMojo extends AbstractDependencyAwareMojo {
	/**
	 * An empty string array.
	 */
	public static final String[] EMPTY_STRING_ARRAY = {};
	
	/**
	 * Default includes string array.
	 */
	public static final String[] DEFAULT_INCLUDES = {"**/**"};
	
	/**
	 * The character encoding scheme to be applied.
	 *
	 * @parameter
	 */
	protected String encoding;
	
	/**
	 * The list of additional key-value pairs aside from that of the System, and
	 * that of the project, which would be used for the filtering.
	 *
	 * @parameter expression="${project.build.filters}"
	 */
	protected List<String> filters;
	
	/**
	 * Properties used for filtering.
	 */
	protected ErrorTracingProperties m_filterProperties;

	/**
	 * Copies the resources filtered.
	 *
	 * @param resourceDir Is the source dir.
	 * @param outputDir Is the dest dir.
	 * @param resourceType Is the type of resources.
	 * @return Returns <code>true</code> if copying was successful.
	 * @throws MojoExecutionException On problem.
	 */
	protected boolean copyResourcesFiltered(File resourceDir, File outputDir,
		String resourceType)
		throws MojoExecutionException {
		
		if (!resourceDir.exists() || !resourceDir.isDirectory()) {
			getLog().debug("Env " + resourceType + " directory '"
				+ resourceDir.getAbsolutePath() + "' does not exist.");
			return false;
		} else {
			getLog().debug("Will process env " + resourceType + " directory '"
				+ resourceDir.getAbsolutePath() + "'.");
		}
	
		if (!outputDir.exists() && !outputDir.mkdirs()) {
			throw new MojoExecutionException(
				"Cannot create env " + resourceType + " output directory: "
				+ outputDir);
		}
		
		if (StringUtils.hasText(encoding)) {
			getLog().debug("Using encoding: '" + encoding
					+ "' to copy filtered " + resourceType + ".");
		} else {
			getLog().debug("Using default encoding to copy filtered "
				+ resourceType + ".");
		}
	
		DirectoryScanner scanner = new DirectoryScanner();
	
		scanner.setBasedir(resourceDir);
		scanner.setIncludes(DEFAULT_INCLUDES);
		scanner.addDefaultExcludes();
		scanner.scan();
		String[] includedFiles = scanner.getIncludedFiles();
		
		for (String fileName : includedFiles) {
			File sourceFile = new File(resourceDir, fileName);
			File destinationFile = new File(outputDir, fileName);
			
			if (!destinationFile.getParentFile().exists()) {
				destinationFile.getParentFile().mkdirs();
			}
	
			try {
				if (fileName.equals("env.xml")) {
					FileUtils.copyFile(sourceFile, destinationFile);
				} else {
					copyFileFiltered(sourceFile, destinationFile);
				}
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Error copying filtered env " + resourceType + ".", e);
			}
		}
		
		return true;
	}

	/**
	 * Initializes the properties used for filtering.
	 *
	 * @throws MojoExecutionException On problem.
	 */
	protected void initializeFiltering() throws MojoExecutionException {
		m_filterProperties = new ErrorTracingProperties();
	
		// Project properties
		m_filterProperties.putAll(getProject().getProperties());
		
		// Properties from filter property files
		for (String filterFile : filters) {
			try {
				Properties properties = PropertyUtils.loadPropertyFile(
					new File(filterFile), true, true);
				m_filterProperties.putAll(properties);
			} catch (IOException e) {
				throw new MojoExecutionException("Error loading property "
					+ "filter file '" + filterFile + "'", e);
			}
		}
		
		// CHANGE BY MZE ELJ-103
		// System properties applied as last
		// Due to issue http://jira.codehaus.org/browse/MNG-1992 system properties
		// do now override properties defined in settings or pom files.
		m_filterProperties.putAll(System.getProperties());
		
		// CHANGE BY MZE ELJ-103
		// For backward compatibility reason we leave the lines below...
		// Now, apply overridden system properties (on the command line).
		// Copied from our patched resources-plugin.
		final String prefix = "override.";
		Properties overrides = System.getProperties();
		for (Object currentKeyObj :  overrides.keySet()) {
			String currentKey = (String) currentKeyObj;
			if (currentKey.startsWith(prefix)) {
				String trueKey = currentKey.substring(prefix.length());
				m_filterProperties.put(trueKey, overrides.get(currentKey));
				getLog().info(
					"DEPRECATED: Use of 'override.' no more needed since EL4J 1.7: Property "
					+ trueKey + " overridden, is now: " + overrides.get(currentKey));
			}
		}
		
		if (getLog().isDebugEnabled()) {
			// Print all available properties for debug reason
			List<String> messageLines = new ArrayList<String>();
			for (Map.Entry<Object, Object> entry : m_filterProperties.entrySet()) {
				messageLines.add("    " + entry.getKey() + "=" + entry.getValue());
			}
			Collections.sort(messageLines);
			getLog().debug("Used filter properties:");
			for (String messageLine : messageLines) {
				getLog().debug(messageLine);
			}
		}
	}

	/**
	 * Copies the a env resource file filtered.
	 *
	 * @param from
	 *            Is the source file.
	 * @param to
	 *            Is the dest file.
	 * @throws IOException
	 *             On copy problem.
	 */
	protected void copyFileFiltered(File from, File to) throws IOException {
		Reader fileReader = null;
		Writer fileWriter = null;
		try {
			if (!StringUtils.hasText(encoding)) {
				fileReader = new BufferedReader(new FileReader(from));
				fileWriter = new FileWriter(to);
			} else {
				FileInputStream instream = new FileInputStream(from);
				FileOutputStream outstream = new FileOutputStream(to);
				fileReader = new BufferedReader(
					new InputStreamReader(instream, encoding));
				fileWriter = new OutputStreamWriter(outstream, encoding);
			}
			
			boolean isPropertiesFile = false;
			
			if (to.isFile() && to.getName().endsWith(".properties")) {
				isPropertiesFile = true;
			}
			
			Reader reader = createFilteredReader(fileReader, isPropertiesFile);
	
			IOUtil.copy(reader, fileWriter);
		} finally {
			IOUtil.close(fileReader);
			IOUtil.close(fileWriter);
		}
	}
	
	/**
	 * Create a filtering file reader on top of a normal reader.
	 * 
	 * @param fileReader          the file reader to make filtering
	 * @param isPropertiesFile    is this a reader for properties files?
	 * @return                    the filtering file reader
	 */
	protected Reader createFilteredReader(Reader fileReader, boolean isPropertiesFile) {
		// support ${token}
		Reader reader = new InterpolationFilterReader(fileReader,
			m_filterProperties, "${", "}");

		// support @token@
		reader = new InterpolationFilterReader(reader,
				m_filterProperties, "@", "@");
		
		reader = new InterpolationFilterReader(reader,
				new ReflectionProperties(
					getProject(), isPropertiesFile), "${", "}");
		
		return reader;
	}
	
	/**
	 * Create the properties file containing the variable -> value mapping.
	 * @param envXmlFilename       the env.xml filename
	 * @param outputDir            the output directory
	 * @param envValuesFilename    the filename of the properties file to write
	 */
	protected void createEnvValuesFile(String envXmlFilename, File outputDir, String envValuesFilename) {
		Resource[] resources = getAllEnvXmls(envXmlFilename);
		InputStream inputStream = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser saxParser = factory.newSAXParser();
			
			VariablesAndFinalPropertiesHandler finalEntriesHandler = new VariablesAndFinalPropertiesHandler();
			
			for (Resource resource : resources) {
				inputStream = resource.getInputStream();
				saxParser.parse(inputStream, finalEntriesHandler);
				inputStream.close();
			}
				
			// write variables to file
			m_filterProperties.clearSuccesses();
			ResolverUtils.resolve(finalEntriesHandler.getConcatenatedValues(), m_filterProperties);
			Set<String> successes = m_filterProperties.getSuccesses();
			
			Properties envValues = new Properties();
			for (String prop : successes) {
				envValues.put(prop, m_filterProperties.get(prop));
			}
			
			if (envValues.size() > 0) {
				File envVariablesFile = new File(outputDir, envValuesFilename);
				outputDir.mkdirs();
				envValues.store(new FileOutputStream(envVariablesFile), null);
			}
		} catch (Exception e) {
			getLog().error(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					getLog().warn(e1);
					// ignore
				}
			}
		}
	}

	/**
	 * Create the properties file containing the final properties.
	 * @param resourceDir             the directory to look for the env.xml file
	 * @param envXmlFilename          the env.xml filename
	 * @param outputDir               the output directory
	 * @param envConstantsFilename    the filename of the properties file to write
	 */
	protected void createEnvConstantsFile(File resourceDir, String envXmlFilename, File outputDir,
		String envConstantsFilename) {
		FileInputStream envXmlStream = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			SAXParser saxParser = factory.newSAXParser();
			
			VariablesAndFinalPropertiesHandler finalEntriesHandler = new VariablesAndFinalPropertiesHandler();
			
			File envXml = new File(resourceDir, envXmlFilename);
			
			if (envXml.exists()) {
				envXmlStream = new FileInputStream(envXml);
				saxParser.parse(envXmlStream, finalEntriesHandler);
				envXmlStream.close();
				
				Properties finalProperties = finalEntriesHandler.getFinalProperties();
				
				// filter values of final properties
				for (Object objectKey : finalProperties.keySet()) {
					String key = (String) objectKey;
					String value = finalProperties.getProperty(key);
					String resolvedValue = ResolverUtils.resolve(value, m_filterProperties);
					// add property to resolved properties map
					m_filterProperties.setProperty(key, resolvedValue);
					
					finalProperties.setProperty(key, resolvedValue);
				}
				
				// write final properties to file
				if (finalProperties.size() > 0) {
					File envConstantsFile = new File(outputDir, envConstantsFilename);
					outputDir.mkdirs();
					finalProperties.store(new FileOutputStream(envConstantsFile), null);
				}
			}
		} catch (Exception e) {
			getLog().error(e);
		} finally {
			if (envXmlStream != null) {
				try {
					envXmlStream.close();
				} catch (IOException e1) {
					getLog().warn(e1);
					// ignore
				}
			}
		}
	}
	
	/**
	 * @param envXmlFilename    the env.xml filename
	 * @return                  all env.xml resources on the classpath
	 */
	protected Resource[] getAllEnvXmls(String envXmlFilename) {
		Resource[] resources = null;
		try {
			Resource[] depResources = getResourceLoader().getDependenciesResources(
				"classpath*:" + envXmlFilename);
			
			Resource[] projectEnvFile = getProjectEnvFiles(envXmlFilename);
			
			resources = (Resource[]) ArrayUtils.addAll(depResources, projectEnvFile);
		} catch (IOException e) {
			resources = null;
		}
		return resources;
	}
	
	/**
	 * @param resource    a resource
	 * @return            the name of the artifact containing the given resource
	 */
	public String getArtifactNameFromResource(Resource resource) {
		// is resource from current project (source)?
		String localResourceName = null;
		if (resource instanceof FileSystemResource) {
			localResourceName = getArtifactNameFromLocalResource((FileSystemResource) resource);
		} else {
			// is resource from current project (target)?
			try {
				boolean islocalResource = resource.getFile().getPath().startsWith(getProject().getBasedir().getPath());
				if (islocalResource) {
					localResourceName = "this artifact (" + getProject().getArtifact().getArtifactId() + ")";
				}
			} catch (IOException e) {
				// continue
			}
		}
		if (localResourceName != null) {
			return localResourceName;
		}
		
		final String repoURL = "jar:file:/" + getRepository().getBasedir() + "/";
		// is resource from project dependencies?
		for (Object artifactObj : getProject().getArtifacts()) {
			Artifact artifact = (Artifact) artifactObj;
			try {
				if (resource.getURL().toString().startsWith(repoURL + getRepository().pathOf(artifact))) {
					String suffix = "";
					if (artifact.getType().equals("test-jar")) {
						suffix = ":test";
					}
					return artifact.getGroupId() + ":" + artifact.getArtifactId() + suffix;
				}
			} catch (Exception e) {
				getLog().warn(e);
				// continue
			}
		}
		
		return resource.toString();
	}
	
	/**
	 * @param resource    a resource located in the current project
	 * @return            the name of the artifact containing the given resource (the main question: test or not?)
	 *                    or <code>null</code> if resource cannot be associated with the current project.
	 */
	protected abstract String getArtifactNameFromLocalResource(FileSystemResource resource);
	
	/**
	 * @param envPropertiesFilename    the env property file name
	 * @return                         an array of resources (is never <code>null</code>)
	 */
	protected abstract Resource[] getProjectEnvFiles(String envPropertiesFilename);
	
	/**
	 * @return    the resource loader to use
	 */
	protected abstract ResourceLoader getResourceLoader();
}