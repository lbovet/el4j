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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.PropertyUtils;
import org.apache.maven.plugin.resources.ReflectionProperties;
import org.apache.maven.plugin.resources.util.InterpolationFilterReader;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import ch.elca.el4j.maven.ResourceLoader;

/**
 * Abstract environment support plugin. Filters the resources of given env dir
 * and saves the generate resources in a special dir.
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
	 * The property key prefix to make a property abstract.
	 */
	private static final String ABSTRACT_PROPERTY = "(abstract)";
	
	/**
	 * The property key prefix to make a property final.
	 */
	private static final String FINAL_PROPERTY = "(final)";
	
	// Checkstyle: MemberName off
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
	// Checkstyle: MemberName on
	
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
			File backupFile = new File(outputDir, fileName + ".orig");
	
			if (!destinationFile.getParentFile().exists()) {
				destinationFile.getParentFile().mkdirs();
			}
	
			try {
				copyFileFiltered(sourceFile, destinationFile);
				FileUtils.copyFile(sourceFile, backupFile);
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
	
		// System properties
		m_filterProperties.putAll(System.getProperties());
	
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
					"Property " + trueKey + " overridden, is now: " + overrides.get(currentKey));
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
	 * @param outputDir                The output directory into which to copy the env resources.
	 * @param resourceType             the resource type (used for log output only)
	 * @param envPropertiesFilename    the env property file
	 */
	protected void processEnvPropertiesFiles(File outputDir, String resourceType, String envPropertiesFilename)
		throws MojoExecutionException {
		
		if (!outputDir.exists() && !outputDir.mkdirs()) {
			throw new MojoExecutionException(
				"Cannot create env " + resourceType + " output directory: "
				+ outputDir);
		}
		
		FileOutputStream targetEnv = null;
		try {
			// check properties if they are valid (e.g. no final violations)
			checkProperties(getAllUnfilteredResources(envPropertiesFilename));
			
			Properties overwrittenProperties = getFilteredOverwriteProperties(envPropertiesFilename);
			
			if (!overwrittenProperties.isEmpty()) {
				File envFile = new File(outputDir, envPropertiesFilename);
				try {
					if (!envFile.exists() && !envFile.createNewFile()) {
						throw new MojoExecutionException(
							"Cannot create env file '" + envPropertiesFilename + "'");
					}
				} catch (IOException e) {
					throw new MojoExecutionException(
						"Cannot create env file '" + envPropertiesFilename + "'");
				}
				
				targetEnv = new FileOutputStream(envFile);
				overwrittenProperties.store(targetEnv, null);
			}
			
		} catch (IOException e) {
			IOUtil.close(targetEnv);
			throw new MojoExecutionException(
				"Cannot collect env files for '" + envPropertiesFilename + "'");
		} finally {
			IOUtil.close(targetEnv);
		}
	}

	/**
	 * Check if properties are valid (i.e. no final property gets overwritten) and print all abstract properties.
	 * 
	 * @param resources    the env properties files
	 */
	protected void checkProperties(Resource[] resources) throws MojoExecutionException {
		Map<String, Resource> finalProps = new HashMap<String, Resource>();
		Map<String, Resource> abstractProps = new HashMap<String, Resource>();
		
		for (Resource resource : resources) {
			Properties props = loadProperties(resource);
			// check that no final value gets overwritten
			for (Object keyObj : props.keySet()) {
				String key = (String) keyObj;
				
				boolean isAbstract = false;
				boolean isFinal = false;
				if (key.startsWith(ABSTRACT_PROPERTY)) {
					isAbstract = true;
					key = key.substring(ABSTRACT_PROPERTY.length());
				} else if (key.startsWith(FINAL_PROPERTY)) {
					isFinal = true;
					key = key.substring(FINAL_PROPERTY.length());
				}
				
				if (finalProps.containsKey(key)) {
					throw new MojoExecutionException(
						"It is not allowed to overwrite final property '" + key + "' in "
						+ getArtifactNameFromResource(resource).replace(".orig", "")
						+ ".\nAlternatively, you might want to recompile artifact containing '"
						+ getArtifactNameFromResource(finalProps.get(key)).replace(".orig", "")
						+ "' using the correct settings.");
				}
				if (!isAbstract && abstractProps.containsKey(key)) {
					abstractProps.remove(key);
				}
				
				if (isAbstract) {
					abstractProps.put(key, resource);
				} else if (isFinal) {
					finalProps.put(key, resource);
				}
			}
		}
		if (!abstractProps.isEmpty()) {
			getLog().info("The following abstract env properties are not set:");
			for (String key : abstractProps.keySet()) {
				getLog().info("    * " + key + " (in '" + getArtifactNameFromResource(abstractProps.get(key)) + "')");
			}
			getLog().info("They have to be set in order to make this artifact fully functional.");
		}
	}

	
	
	/**
	 * @param envPropertiesFilename    the env property file
	 * @return                         all filtered properties that are needed to be written in the env file
	 */
	protected Properties getFilteredOverwriteProperties(String envPropertiesFilename)
		throws IOException, MojoExecutionException {
		
		Resource[] resources = getResourceLoader().getDependenciesResources(
			"classpath*:" + envPropertiesFilename + ".orig");
		
		Properties unfilteredProperties = new Properties();
		
		// collect properties that are defined in other properties files but might be overridden now
		for (Resource resource : resources) {
			Properties props = loadProperties(resource);
			// do not collect constant, abstract or final values
			for (Object keyObj : props.keySet()) {
				String key = (String) keyObj;
				String value = props.getProperty(key) != null ? props.getProperty(key) : "";
				if (!key.startsWith(ABSTRACT_PROPERTY) && !key.startsWith(FINAL_PROPERTY)
					&& (value.contains("${") || value.contains("@"))) {
					unfilteredProperties.setProperty(key, value);
				}
			}
		}
		
		List<String> keysDefinedInThisArtifact = new ArrayList<String>();
		
		// add (non-abstract) properties defined in the current artifact
		resources = getProjectEnvFiles(envPropertiesFilename);
		for (Resource resource : resources) {
			Properties props = loadProperties(resource);
			// do not include abstract values
			for (Object keyObj : props.keySet()) {
				String key = (String) keyObj;
				String value = props.getProperty(key) != null ? props.getProperty(key) : "";
				if (key.startsWith(FINAL_PROPERTY)) {
					key = key.substring(FINAL_PROPERTY.length());
				}
				if (!key.startsWith(ABSTRACT_PROPERTY)) {
					unfilteredProperties.setProperty(key, value);
					keysDefinedInThisArtifact.add(key);
				}
			}
		}
		
		// load env files filtered by other artifacts to determine which expression have already been evaluated there
		Resource[] resourcesFilteredByOthers = getResourceLoader().getDependenciesResources(
			"classpath*:" + envPropertiesFilename);
		Properties propertiesFilteredByOthers = loadProperties(resourcesFilteredByOthers);
		
		m_filterProperties.clearErrors();
		Properties filteredProperties = filterProperties(unfilteredProperties);
		
		boolean unsetVariables = false;
		
		// only take properties that could have been evaluated (unless they are declared in current artifact)
		Properties overwriteProperties = new Properties();
		for (Object keyObj : unfilteredProperties.keySet()) {
			String key = (String) keyObj;
			
			boolean allExpressionsEvaluated = allExpressionsEvaluated(unfilteredProperties.getProperty(key));
			if (!allExpressionsEvaluated && !propertiesFilteredByOthers.containsKey(key)) {
				unsetVariables = true;
				getLog().warn("Could not evaluate expression '" + unfilteredProperties.getProperty(key)
					+ "' because some variables are not set.");
			}
			if (keysDefinedInThisArtifact.contains(key) || allExpressionsEvaluated) {
				overwriteProperties.setProperty(key, filteredProperties.getProperty(key));
			}
		}
		
		// warn if variables are not set
		if (unsetVariables) {
			getLog().warn("Use 'mvn envsupport:list' to get detailed information.");
		}
		
		return overwriteProperties;
	}
	
	/**
	 * @param unfilteredProperties    properties to filter
	 * @return                        the filtered properties
	 */
	protected Properties filterProperties(Properties unfilteredProperties) {
		Properties filteredProperties = new Properties();
		try {
			ByteArrayOutputStream unfilteredBuffer = new ByteArrayOutputStream();
			ByteArrayOutputStream filteredBuffer = new ByteArrayOutputStream();
			unfilteredProperties.store(unfilteredBuffer, null);
			
			Reader reader = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(unfilteredBuffer.toByteArray())));
			// filter output
			reader = createFilteredReader(reader, true);
			IOUtil.copy(reader, filteredBuffer);
			
			filteredProperties.load(new ByteArrayInputStream(filteredBuffer.toByteArray()));
		} catch (IOException e) {
			return null;
		}
		
		return filteredProperties;
	}
	
	/**
	 * @param envPropertiesFilename    the env property filename
	 * @return                         all resources matching the filename (project + dependencies)
	 */
	protected Resource[] getAllUnfilteredResources(String envPropertiesFilename) {
		Resource[] resources = null;
		try {
			Resource[] depResources = getResourceLoader().getDependenciesResources(
				"classpath*:" + envPropertiesFilename + ".orig");
			
			Resource[] projectEnvFile = getProjectEnvFiles(envPropertiesFilename);
			
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
	protected String getArtifactNameFromResource(Resource resource) {
		// is resource from current project?
		if (resource instanceof FileSystemResource) {
			String name = getArtifactNameFromLocalResource((FileSystemResource) resource);
			if (name != null) {
				return name;
			}
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
	 * @param resources    the resources to load the properties from (most specific resource has to be last)
	 * @return             the loaded properties
	 */
	private Properties loadProperties(Resource... resources) throws MojoExecutionException {
		Properties properties = new Properties();
		for (Resource resource : resources) {
			try {
				properties.load(resource.getInputStream());
			} catch (IOException e) {
				throw new MojoExecutionException(
					"Cannot load resource '" + resource.toString() + "'");
			}
		}
		return properties;
	}
	
	/**
	 * @param value    a String that can contain expressions
	 * @return         <code>true</code> if all expressions could be evaluated
	 */
	private boolean allExpressionsEvaluated(String value) {
		for (String errorKey : m_filterProperties.getErrors()) {
			if (value.contains("${" + errorKey + "}") || value.contains("@" + errorKey + "@")) {
				return false;
			}
		}
		return true;
	}
	
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