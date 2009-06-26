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
package ch.elca.el4j.maven.plugins.beans;


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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The mojo to extract beans from a project.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 *
 * @goal beans
 * @requiresDependencyResolution runtime
 */
public class BeansMojo extends AbstractMojo {

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
	 * @required
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

		File file = new File(sourceFile);

		if (!file.isAbsolute()) {
			file = new File(m_project.getBasedir(), sourceFile);
		}

		ConfigurationExtractor ex = new ConfigurationExtractor(file);

		BeanPathResolver resolver = new BeanPathResolver();
		resolver.setLogger(new LogCallback() {

			/** {@inheritDoc} */
			public boolean isActive() {
				return getLog().isDebugEnabled();
			}

			/** {@inheritDoc} */
			public void log(String str) {
				getLog().info(str);
			}
		});

		// reads out all inclusive configuration files and all exclusive configuration files
		String[] files = resolver.resolve(ex.getInclusive(), ex.getExclusive(), classpath, m_project.getBasedir()
			.toString());

		/*
		 * truncate to get full path of form: d:/....
		 */
		for (int j = 0; j < files.length; j++) {

			files[j] = files[j].substring(files[j].indexOf("file:/") + 6);

			// cut away classpath where necessary
			if (files[j].startsWith(m_project.getBasedir().getAbsolutePath().replace("\\", "/"))) {
				files[j] = files[j].substring(m_project.getBasedir().getAbsolutePath().length());

			} else if (files[j].contains(m_localRepository.getBasedir().replace("\\", "/"))) {
				// if files is not in classpath, then maybe it is in the m2repository
				files[j] = "external:/M2_REPO" + files[j].substring(m_localRepository.getBasedir().length());

			} else {
				/* if it isn't in classpath nor in m2repo, well then just cut of everything before the first ":"
				 * because format is file:/ .. 
				 */
				files[j] = "external:/" + files[j].substring("file:/".length());
			}
		}

		/* if we have found config files then force spring nature... and write .springBeans file */
		if (files.length > 0) {
			SpringNatureForcer forcer = new SpringNatureForcer(m_project.getBasedir());
			forcer.setLogger(new LogCallback() {

				/** {@inheritDoc} */
				public boolean isActive() {
					return getLog().isInfoEnabled();
				}

				/** {@inheritDoc} */
				public void log(String str) {
					getLog().info(str);
				}
			});
			forcer.forceSpringNature();

			writespringBeansFile(files);
		}

	}

	/**
	 * Writes the paths of the springIDE configuration files into .springBeans.
	 * 
	 * @param files
	 *            are the paths of the used (in the Module Application Context) configuration files.
	 */

	private void writespringBeansFile(String[] files) throws MojoExecutionException, MojoFailureException {
		Map context = new HashMap();
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
	 * 
	 * @param context is a HashMap holding the configuration details
	 * @param out is the file we want to write to
	 * @param template is the name of the templates file
	 * @throws MojoExecutionException
	 */
	
	protected void applyTemplate(Map context, File out, String template) throws MojoExecutionException {

		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(getClass(), "");

		out.getParentFile().mkdirs();
		try {
			Writer configWriter = new FileWriter(out);
			Template tpl = cfg.getTemplate(template);
			tpl.process(context, configWriter);
			configWriter.flush();
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

	/**
	 * Interface to allow other classes to use the mojo logger.
	 */
	interface LogCallback {
		/**
		 * Log a debug message.
		 * 
		 * @param str
		 *            The mesasge to log.
		 */
		void log(String str);

		/**
		 * Whether to do logging (saves time if we skip the statements completely otherwise). Calls
		 * Logger.isDebugEnabled().
		 * 
		 * @return Whether logging is active.
		 */
		boolean isActive();
	}

}
