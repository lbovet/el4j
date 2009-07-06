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
package ch.elca.el4j.maven.plugins.eclipsecs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.util.StringUtils;

/**
 * Mojo to create .checkstyle file.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 * @author Martin Zeltner (MZE)
 *
 * @goal eclipse-cs-config
 * @requiresProject true
 */
public class EclipseCsConfigMojo extends AbstractMojo {
	// Checkstyle: MemberName off
	
	/**
	 * Path to checkstyle file.
	 *
	 * @parameter expression="${checkstyle.checkstyleFilePath}"
	 * @required
	 */
	protected File checkstyleFile;
	
	/**
	 * Path to eclipse-cs config file.
	 *
	 * @parameter expression="${checkstyle.configFilePath}"
	 *            default-value=".checkstyle"
	 * @required
	 */
	protected File configFilePath;
	
	/**
	 * Flag to enable eclipse-cs.
	 *
	 * @parameter expression="${checkstyle.enableEclipseCs}"
	 *            default-value="true"
	 * @required
	 */
	protected boolean enableEclipseCs;
	
	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	// Checkstyle: MemberName on
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		String projectPackaging = project.getPackaging();
		if (!StringUtils.hasText(projectPackaging)
			|| projectPackaging.contains("pom")) {
			getLog().info("No eclipse-cs config for pom project.");
			return;
		}
		
		if (configFilePath.isDirectory()) {
			throw new MojoExecutionException("Given file path "
				+ configFilePath.getAbsolutePath()
				+ " is a directory!");
		}
		getLog().info("Writing file " + configFilePath.getAbsolutePath());
		
		VelocityContext context = new VelocityContext();
		
		context.put("name", project.getName());
		context.put("enabled", Boolean.toString(enableEclipseCs));
		context.put("location", checkstyleFile.getPath());
		
		try {
			Writer out = new FileWriter(configFilePath);
			Reader template = new InputStreamReader(getClass().getResourceAsStream("/etc/velocity/eclipse-cs.vm"));
			Velocity.evaluate(context, out, "elcaservice", template);
			IOUtil.close(template);
			IOUtil.close(out);
		} catch (IOException e) {
			getLog().error(e);
		}

	}
}
