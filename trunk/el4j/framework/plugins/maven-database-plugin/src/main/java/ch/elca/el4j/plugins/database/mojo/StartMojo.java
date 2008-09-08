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
package ch.elca.el4j.plugins.database.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.plugins.database.AbstractDBMojo;
import ch.elca.el4j.plugins.database.util.derby.DerbyNetworkServerStarter;

/**
 * This class is a database mojo for the 'start' statement.
 * In case of using derby it starts the DerbyNetworkServer.
 * In case of using oracle, is doesn nothing (as we require
 * the oracle database to run when using this plugin).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @goal start
 * @author David Stefan (DST)
 */
public class StartMojo extends AbstractDBMojo {
	
	/**
	 * Delay ensures that "Press ..." is last line on console.
	 */
	private static final int DELAY = 500;
	
	/**
	 * The port to run derby.
	 *
	 * @parameter expression="${db.port}"  default-value="-1"
	 */
	private int dbPort;
	
	/**
	 * The user name required to access derby.
	 *
	 * @parameter expression="${db.username}"  default-value=""
	 */
	private String dbUsername;
	
	/**
	 * The password required to access derby.
	 *
	 * @parameter expression="${db.password}"  default-value=""
	 */
	private String dbPassword;
	
	/**
	 * {@inheritDoc}
	 */
	public void executeInternal() throws MojoExecutionException, MojoFailureException {
		try {
			if (needStartup()) {
				getLog().info("Starting database (StartMojo)...");
				
				DerbyNetworkServerStarter.setHomeDir(getDerbyLocation());
				DerbyNetworkServerStarter.setPort(dbPort);
				DerbyNetworkServerStarter.setUsername(dbUsername);
				DerbyNetworkServerStarter.setPassword(dbPassword);
				DerbyNetworkServerStarter.startNetworkServer();
				if (hasToWait()) {
					Thread.sleep(DELAY);
					getLog().info("Press Ctrl-C to stop Server");
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						getLog().error("Error during wait", e);
					}
				}
			}
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}
}