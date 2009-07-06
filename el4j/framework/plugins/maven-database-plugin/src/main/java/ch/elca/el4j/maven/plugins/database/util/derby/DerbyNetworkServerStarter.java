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
package ch.elca.el4j.maven.plugins.database.util.derby;

import java.io.PrintWriter;
import java.net.InetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.derby.drda.NetworkServerControl;

/**
 * This class starts the Derby NetworkServer.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * @author David Stefan (DST)
 */
public final class DerbyNetworkServerStarter {

	/**
	 * Network server Control object.
	 */
	private static NetworkServerControl s_server;

	/**
	 * Derby Database directory.
	 */
	private static String s_derbyDir;
	
	/**
	 * Derby Database port.
	 */
	private static int s_port;
	
	/**
	 * Derby Database user name.
	 */
	private static String s_username;
	
	/**
	 * Derby Database password.
	 */
	private static String s_password;

	
	/** to hide constructor. */
	private DerbyNetworkServerStarter() { }
	
	/**
	 * Set the property "derby.system.home" so that database and log file will
	 * be placed in the right directory.
	 */
	private static void setWorkingDir() {
		System.setProperty("derby.system.home", s_derbyDir);
	}

	/**
	 * Create the network server control.
	 *
	 * @throws Exception
	 */
	private static void createNetworkServer() throws Exception {
		// check if homeDir was set.
		assert (s_derbyDir != null);
		setWorkingDir();
		if (StringUtils.isNotBlank(s_username) && StringUtils.isNotBlank(s_password)) {
			s_server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"), s_port, s_username, s_password);
		} else {
			s_server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"), s_port);
		}
	}
	
	/**
	 * Set the directory where databases and log files will be placed in.
	 *
	 * @param dir
	 *            directory
	 */
	public static void setHomeDir(String dir) {
		s_derbyDir = dir;
	}
	
	/**
	 * Set the port under which Derby should run.
	 * 
	 * @param port    the port number
	 */
	public static void setPort(int port) {
		s_port = port > 0 ? port : NetworkServerControl.DEFAULT_PORTNUMBER;
	}
	
	/**
	 * Set the user name required to access Derby.
	 * 
	 * @param username    the user name
	 */
	public static void setUsername(String username) {
		s_username = username;
	}
	
	/**
	 * Set the password required to access Derby.
	 * 
	 * @param password    the password
	 */
	public static void setPassword(String password) {
		s_password = password;
	}

	/**
	 * Starts the (static) network server.
	 *
	 * @throws Exception
	 */
	public static void startNetworkServer() throws Exception {
		if (s_server == null) {
			createNetworkServer();
		}
		// Test if there's already a Network Server running on this port.
		// If so, do nothing, else start server.
		try {
			s_server.ping();
		} catch (Exception e) {
			s_server.start(new PrintWriter(System.out));
		}
	}

	/**
	 * Terminates the (static) network server.
	 *
	 * @throws Exception
	 */
	public static void stopNetworkServer() throws Exception {
		if (s_server == null) {
			createNetworkServer();
		}
		s_server.ping();
		s_server.shutdown();
	}
	
}