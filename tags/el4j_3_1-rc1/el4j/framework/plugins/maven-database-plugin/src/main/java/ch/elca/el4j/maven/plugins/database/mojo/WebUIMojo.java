/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.maven.plugins.database.AbstractDBMojo;
import ch.elca.el4j.maven.plugins.database.util.h2.H2Controller;

/**
 * Start the H2 web UI. Database driver dependencies have to be declared in
 * the <code>build/plugins/plugin/dependencies</code> section of the <code>pom.xml</code> file.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @goal webUI
 * @author Stefan Wismer (SWI)
 */
public class WebUIMojo extends AbstractDBMojo {
	// Checkstyle: MemberName off
	/**
	 * The port to run the DB.
	 *
	 * @parameter expression="${db.webui.port}"  default-value="8080"
	 */
	private int webUiPort;
	// Checkstyle: MemberName on
	
	/**
	 * {@inheritDoc}
	 */
	public void executeInternal() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Starting Web UI (WebUIMojo)...");
			getLog().info("Database driver dependencies have to be declared in the");
			getLog().info("build/plugins/plugin/dependencies section of the pom.xml file.");
			final H2Controller controller = new H2Controller();
			controller.startWebUI(webUiPort);
			
			getLog().info("Web UI is started on http://localhost:" + webUiPort);
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					getLog().info("Stopping Web UI (WebUIMojo)...");
					controller.stopWebUI();
				};
			});
			
			getLog().info("Press Ctrl-C to stop Server");
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				getLog().error("Error during wait", e);
			}
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}
}
