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

import ch.elca.el4j.maven.plugins.database.AbstractDBExecutionMojo;


/**
 * This class is a convenience mojo that includes the 'start',
 * 'silentDrop' and 'create' mojo.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @goal prepare
 * @author David Stefan (DST)
 */
public class PrepareMojo extends AbstractDBExecutionMojo {
	// Checkstyle: MemberName off
	/**
	 * Delay to wait for the DB Server.
	 */
	private static final int DELAY = 500;
	
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
			getLog().info("Starting database (PrepareMojo)...");
			getDbController().setPort(dbPort);
			getDbController().setUsername(dbUsername);
			getDbController().setPassword(dbPassword);
			getDbController().start();
			
			long startTime = System.currentTimeMillis();
			long remainingTime = DELAY;
			do {
				try {
					Thread.sleep(remainingTime);
				} catch (InterruptedException ie) {
					getLog().debug("executeInternal: sleep interrupted.");
				}
				remainingTime = DELAY - (System.currentTimeMillis() - startTime);
			} while (remainingTime > 0);
			
			getLog().info("Executing silent drop");
			// Execute a silent drop
			try {
				executeAction("drop", true, true);
				// Checkstyle: EmptyBlock off
			} catch (Exception e) {
				// Skip Exception
			}
			// Checkstyle: EmptyBlock on
			getLog().info("Executing create");
			// Create tables
			executeAction("create", false, false);
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}
}