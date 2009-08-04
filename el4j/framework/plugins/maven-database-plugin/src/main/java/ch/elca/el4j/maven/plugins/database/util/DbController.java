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
package ch.elca.el4j.maven.plugins.database.util;

/**
 * A DB controller that is able to start and stop the DB.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public interface DbController {
	/**
	 * @param homeDir    the home directory
	 */
	public void setHomeDir(String homeDir);
	
	/**
	 * Set the port under which the DB should run.
	 * 
	 * @param port    the port number
	 */
	public void setPort(int port);
	
	/**
	 * Set the user name required to access the DB.
	 * 
	 * @param username    the user name
	 */
	public void setUsername(String username);
	
	/**
	 * Set the password required to access the DB.
	 * 
	 * @param password    the password
	 */
	public void setPassword(String password);
	
	/**
	 * Start the DB if necessary.
	 */
	public void start() throws Exception;
	
	/**
	 * Stop the DB if necessary.
	 */
	public void stop() throws Exception;
	
	/**
	 * @return    the DB name
	 */
	public String getDbName();
}
