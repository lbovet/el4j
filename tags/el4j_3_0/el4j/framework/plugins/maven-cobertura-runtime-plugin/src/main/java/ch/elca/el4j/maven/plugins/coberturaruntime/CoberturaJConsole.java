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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import sun.tools.jconsole.JConsole;

/**
 * Goal to start JConsole for the cobertura-runtime JMX server.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * 
 * @goal jconsole
 * @requiresProject true
 */
public class CoberturaJConsole extends AbstractCoberturaMojo {
	// Checkstyle: MemberName off
	
	/**
	 * Is the url to the JMX service.
	 * 
	 * @parameter expression="${cobertura-runtime.jmxServiceUrl}"
	 * @required
	 * @readonly
	 */
	protected String jmxServiceUrl;
	
	// Checkstyle: MemberName on
	
	/**
	 * Executes the plugin.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		JConsole.main(new String[] {jmxServiceUrl});
		try {
			Thread.sleep(1000);
			getLog().info("Press Ctrl-C to continue...");
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			getLog().error("Error during wait", e);
		}
	}
}
