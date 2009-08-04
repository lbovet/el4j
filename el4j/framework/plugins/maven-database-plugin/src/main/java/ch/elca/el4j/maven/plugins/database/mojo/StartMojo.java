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
package ch.elca.el4j.maven.plugins.database.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.maven.plugins.database.AbstractDBMojo;

/**
 * This class is a database mojo for the 'start' statement.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @goal start
 * @author David Stefan (DST)
 */
public class StartMojo extends AbstractDBMojo {
	
	/**
	 * Delay ensures that "Press ..." is last line on console.
	 */
	private static final int DELAY = 500;
	
	// Checkstyle: MemberName off
	/**
	 * The port to run the DB.
	 *
	 * @parameter expression="${db.internal.port}"  default-value="-1"
	 */
	private int dbPort;
	
	/**
	 * The user name required to access the DB.
	 *
	 * @parameter expression="${db.username}"  default-value=""
	 */
	private String dbUsername;
	
	/**
	 * The password required to access the DB.
	 *
	 * @parameter expression="${db.password}"  default-value=""
	 */
	private String dbPassword;
	// Checkstyle: MemberName on
	
	/**
	 * {@inheritDoc}
	 */
	public void executeInternal() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Starting database (StartMojo)...");
			getDbController().setPort(dbPort);
			getDbController().setUsername(dbUsername);
			getDbController().setPassword(dbPassword);
			getDbController().start();
			
			if (hasToWait()) {
				Thread.sleep(DELAY);
				getLog().info("Press Ctrl-C to stop Server");
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					getLog().error("Error during wait", e);
				}
			}
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}
}