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
import ch.elca.el4j.maven.plugins.database.util.DbController;
import ch.elca.el4j.maven.plugins.database.util.DbControllerFactory;
import ch.elca.el4j.util.codingsupport.annotations.FindBugsSuppressWarnings;

/**
 * This class holds all fields and methods commmon to all database mojos, namely
 * the properties from the pom file, the Maven project and repository as well as
 * the DataHolder, which encapsulates all Database specific properties.
 *
 * It provides methods to its sublcasses to start the the DB server and to
 * execute an action, i.e. extract and execute SQL statements from a given
 * source path.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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
	
	// Checkstyle: MemberName on

	/**
	 * The Data Holder.
	 */
	private DatabaseNameHolder m_holder;
	
	/**
	 * The DB controller to start and stop the DB.
	 */
	private DbController m_dbController;
	
	
	/** {@inheritDoc} */
	public final void execute() throws MojoExecutionException,
		MojoFailureException {
		
		if (skip) {
			getLog().info("Skipping database plugin due to configuration");
		} else if ("pom".equals(this.getProject().getPackaging())) {
			throw new MojoExecutionException("You are trying to run the db plugin on a pom-artifact. Try it in a jar-artifact instead.");
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
	 * @return    the DB controller to start and stop the DB.
	 */
	@FindBugsSuppressWarnings(value = "UWF_UNWRITTEN_FIELD",
								justification = "Field toolsPath is injected by maven.")	
	protected DbController getDbController() {
		if (m_dbController == null) {
			try {
				String db = getDbNameHolder().getDbName();
				m_dbController = DbControllerFactory.create(db);
			} catch (Exception e) {
				getLog().error("Error getting DbName: " + e.getMessage());
				return null;
			}
		}
		
		m_dbController.setHomeDir(toolsPath + "/" + m_dbController.getDbName() + "/"
			+ m_dbController.getDbName() + "-databases");
		return m_dbController;
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
			m_holder = new DatabaseNameHolder(getResourceLoader(true, true), getProject());
		}
		return m_holder;
	}
}