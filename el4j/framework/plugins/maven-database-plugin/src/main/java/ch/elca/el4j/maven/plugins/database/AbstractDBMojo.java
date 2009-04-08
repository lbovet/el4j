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
package ch.elca.el4j.maven.plugins.database;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import ch.elca.el4j.maven.plugins.database.holder.DatabaseNameHolder;

/**
 * This class holds all fields and methods commmon to all database mojos, namely
 * the properties from the pom file, the Maven project and repository as well as
 * the DataHolder, which encapsulates all Database specific properties.
 *
 * It provides methods to its sublcasses to start the derby NetworkServer and to
 * execute an action, i.e. extract and execute SQL statements from a given
 * source path.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 *
 * @requiresProject true
 * @requiresDependencyResolution test
 */
public abstract class AbstractDBMojo extends AbstractDependencyAwareMojo {
	
	// Checkstyle: MemberName off
	
	/**
	 * Decides whether to wait after the container is started or to return
	 * the execution flow to the user.
	 *
	 * @parameter expression="${db.wait}" default-value = "true"
	 */
	private boolean wait;

	/**
	 * Directory of tools (such as application servers or local
	 * dbs) in the project.
	 *
	 * @parameter expression="${el4j.project.tools}"
	 * @required
	 */
	private String toolsPath;

	/**
	 * @parameter expression="${skip}" default-value = "false"
	 */
	private boolean skip;

	/**
	 * The Data Holder.
	 */
	private DatabaseNameHolder m_holder;
	
	// Checkstyle: MemberName on
	
	
	
	/** {@inheritDoc} */
	public final void execute() throws MojoExecutionException,
		MojoFailureException {
		
		if (skip) {
			getLog().info("Skipping database plugin due to configuration");
		} else {
			executeInternal();
		}
	}
	
	/**
	 * Execute mojo.
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected abstract void executeInternal() throws MojoExecutionException,
		MojoFailureException;
	
	/**
	 * Returns true if database needs to be started.
	 * That is, check database name.
	 * Currently, only db2 databases can be started.
	 *
	 * @return Return whether database need to be started.
	 */
	protected boolean needStartup() {
		
		try {
			String db = getDbNameHolder().getDbName();
			
			boolean result;
			if (db.equalsIgnoreCase("db2")) {
				result = true;
			} else {
				getLog().warn("Database " + db + " can not be started "
					+ "by this plugin.");
				result = false;
			}
			return result;
			
		} catch (Exception e) {
			getLog().error("Error getting DbName: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Returns directory, where NetworkServer should be started.
	 * This will be the location, where the NetworkServer looks for databases.
	 *
	 * @return Home Directory of NetworkServer
	 */
	protected String getDerbyLocation() {
		return toolsPath + "/derby/derby-databases";
	}

	/**
	 * @return If NetwerkServer is blocking or not
	 */
	protected boolean hasToWait() {
		return wait;
	}


	/**
	 * @return The holder
	 */
	protected DatabaseNameHolder getDbNameHolder() {
		if (m_holder == null) {
			m_holder = new DatabaseNameHolder(getResourceLoader(true), getProject());
		}
		return m_holder;
	}
}