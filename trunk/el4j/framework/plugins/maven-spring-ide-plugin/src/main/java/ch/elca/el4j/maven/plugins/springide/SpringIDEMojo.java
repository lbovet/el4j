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
package ch.elca.el4j.maven.plugins.springide;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import ch.elca.el4j.maven.plugins.AbstractSlf4jEnabledMojo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A mojo to generate the .springIDE file and to force SpringNature on Eclipse by reading the files in the Module Application Context.
 * The sourcefile to be used to get the Module Application Context can either be specified in the
 * sourceFile parameter or, if unspecified, will be searched by the mojo.
 * Rules that this mojo adheres to for searching the source file that specify the module application context: 
 * 1. If there is a .xml file that contains <!-- $$ BEANS INCLUDE $$ --> the mojo will assume a webproject and take this file.
 * 2. Else it will look through alphabetically through all files in the base directory and take the first .java
 * file that contains // $$ BEANS INCLUDE $$

 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD) 
 * @author Daniel Thomas (DTH)
 *
 * @goal spring-ide
 * @requiresDependencyResolution runtime
 */
public class SpringIDEMojo extends AbstractSlf4jEnabledMojo {

	/**
	 * The maven project - used for runtime classpath resolution.
	 * 
	 * @parameter expression="${project}"
	 */
	private MavenProject m_project;

	/**
	 * Local maven repository.
	 * 
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository m_localRepository;

	// Checkstyle: MemberName off

	/**
	 * The file to read configuration information from.
	 * 
	 * @parameter
	 */
	private String sourceFile;

	/**
	 * @parameter default-value="xml"
	 * @alias configExtensions
	 */
	private String configSuffixes;

	/**
	 * @parameter default-value="true"
	 */
	private boolean allowBeanDefinitionOverriding;

	/**
	 * @parameter default-value="true"
	 */
	private boolean enableImports;

	/**
	 * @parameter default-value="true"
	 */
	private boolean incomplete;
	/**
	 * @parameter default-value="${project.artifactId}"
	 */
	private String name;

	// Checkstyle: MemberName on

	/** {@inheritDoc} */
	public void execute() throws MojoExecutionException, MojoFailureException {

		URL[] classpath = constructClasspath();

		File source = null;

		/* for a web project just take the web.xml file */
		
		
		if (m_project.getPackaging().equals("war")) {
			/* sourceFile should be null, if it's not, tell user that we are ignoring configuration */
			if (sourceFile != null) {
				getLog().info("Ignoring configuration, trying to use web.xml");
			}
			sourceFile = m_project.getBasedir() + File.separator + "src" + File.separator + "main" + File.separator
				+ "webapp" + File.separator + "WEB-INF" + File.separator + "web.xml";
			source = new File(sourceFile);
			
			
		
			

			/* check if we really have the right file */
			if (!(source.exists())) {
				getLog().info("Could't find web.xml");
			}

			/* for a jar project without specified sourcefile, find a adequate sourceFile. */
		} else if ((sourceFile == null) && (m_project.getPackaging().equals("jar"))) {

			source = SourceResolver.getSourceFile(m_project.getBasedir());

			/* if we didn't find a source File, inform user */
			if (source == null) {
				getLog().info("No definition of Module Application Context found");
				getLog().info("Insert // $$ BEANS INCLUDE before definition of Module Application Context");
			}

			/*
			 * in this case a source file with the definition of the Module Application Context was given, and we have
			 * the packaging of a jar
			 */

		} else if ((sourceFile != null) && (m_project.getPackaging().equals("jar"))) {

			/*
			 * make a path to the .java file out of the Fully Qualified Class Name, we get something like
			 * ch/elca/el4j/demos/gui/myClass.java ps. have to copy it into a new String
			 */

			sourceFile = sourceFile.replace(".", File.separator);
			sourceFile = sourceFile.concat(".java");

			/*
			 * now get the full path by getting the compileSource Roots and appending the sourceFile to them
			 */
			List<String> sourceRoots = m_project.getCompileSourceRoots();
			for (String root : sourceRoots) {
				File temp = new File(root + File.separator + sourceFile);
				if (temp.exists()) {
					source = temp;
				}
			}
			/* if we just didn't find the file through the fully qualified class name */
			if (!(source.exists())) {
				getLog().info("Could't find sourceFile denoted by Fully Quallified Class Name:");
				getLog().info(sourceFile);
			}

		} else {
			// in this case the packaging is wrong..., and we don't do anything
			source = null;
		}

		if (source != null) {

			getLog().info(
				"Full Path of file used for extracting Module Application Context:" + source.getAbsolutePath());
			ConfigurationExtractor ex = new ConfigurationExtractor(source);

			BeanPathResolver resolver = new BeanPathResolver();
			
			/* small test to make sure that we really have found includes */
			if (ex.getInclusive() == null) {
				getLog().error("Couldn't find any included configuration files");
				getLog().error("Make sure that comments needed for execution of this plugin exist and are at the right place");
				return;
			}

			// reads out all inclusive configuration files and all exclusive configuration files
			String[] files = resolver.resolve(ex.getInclusive(), ex.getExclusive(), classpath, m_project.getBasedir()
				.toString());

			/*
			 * truncate to get full path of form: d:/....
			 */
			for (int j = 0; j < files.length; j++) {

				files[j] = files[j].substring(files[j].indexOf("file:/") + "file:/".length());

				// cut away classpath where necessary
				if (files[j].startsWith(m_project.getBasedir().getAbsolutePath().replace("\\", "/"))) {
					files[j] = files[j].substring(m_project.getBasedir().getAbsolutePath().length());

				} else if (files[j].contains(m_localRepository.getBasedir().replace("\\", "/"))) {
					// if files is not in classpath, then maybe it is in the m2repository
					files[j] = "external:/M2_REPO" + files[j].substring(m_localRepository.getBasedir().length());

				} else {
					/*
					 * if it isn't in classpath nor in m2repo, well then just cut of everything before the first ":"
					 * because format is file:/ ..
					 */
					files[j] = "external:/" + files[j].substring("file:/".length());
				}
			}

			/* if we have found config files then force spring nature... and write .springBeans file */
			if (files.length > 0) {
				SpringNatureForcer.forceSpringNature(m_project.getBasedir());

				writespringBeansFile(files);
			}
		}
	}

	/**
	 * Writes the paths of the springIDE configuration files into .springBeans.
	 * 
	 * @param files
	 *            are the paths of the used (in the Module Application Context) configuration files.
	 */

	private void writespringBeansFile(String[] files) throws MojoExecutionException, MojoFailureException {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("configs", files);
		context.put("configSuffixes", configSuffixes.split(","));
		context.put("allowBeanDefinitionOverriding", String.valueOf(allowBeanDefinitionOverriding));
		context.put("incomplete", String.valueOf(incomplete));
		context.put("enableImports", String.valueOf(enableImports));
		context.put("name", name);

		getLog().info("create SpringIDE configuration for " + name);

		File dotSpringBeans = new File(m_project.getBasedir(), ".springBeans");
		applyTemplate(context, dotSpringBeans, "springBeans.fm");

		File prefs = new File(m_project.getBasedir(), ".settings/org.springframework.ide.eclipse.core.prefs");
		applyTemplate(context, prefs, "prefs.fm");

	}

	/**
	 * Uses a Template class (and template files) to automatically write the .springBeans and .pref files.
	 * 
	 * @param context
	 *            is a HashMap holding the configuration details
	 * @param out
	 *            is the file we want to write to
	 * @param template
	 *            is the name of the templates file
	 * @throws MojoExecutionException
	 */

	protected void applyTemplate(Map<String, Object> context, File out, String template) throws MojoExecutionException {

		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(getClass(), "");

		out.getParentFile().mkdirs();
		try {
			Writer configWriter = new FileWriter(out);
			Template tpl = cfg.getTemplate(template);
			tpl.process(context, configWriter);
			configWriter.close();
			getLog().info("Write SpringIDE configuration to: " + out.getAbsolutePath());
		} catch (IOException ioe) {
			throw new MojoExecutionException("Unable to write SpringIDE configuration file", ioe);
		} catch (TemplateException te) {
			throw new MojoExecutionException("Unable to merge freemarker template", te);
		}
	}

	/**
	 * Constructs an URL[] representing the runtime classpath.
	 * 
	 * @return The urls.
	 */
	private URL[] constructClasspath() {
		List<?> list;
		List<URL> classpath = new LinkedList<URL>();
		try {
			list = m_project.getRuntimeClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			// Can't happen - dependency resolution required.
			throw new RuntimeException(e);
		}
		for (Object o : list) {
			String s = o.toString();
			try {
				File f1 = new File(s);
				URL u1 = f1.toURL();
				classpath.add(u1);
			} catch (MalformedURLException e) {
				// Shouldn't really happen.
				throw new RuntimeException(e);
			}
		}
		return classpath.toArray(new URL[0]);
	}
}
