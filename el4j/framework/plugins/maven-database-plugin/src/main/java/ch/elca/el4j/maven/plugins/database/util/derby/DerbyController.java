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

import ch.elca.el4j.maven.plugins.database.util.DbController;

/**
 * This class starts the Derby NetworkServer.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * @author David Stefan (DST)
 */
public final class DerbyController implements DbController {

	/**
	 * Network server Control object.
	 */
	private NetworkServerControl m_server;

	/**
	 * Derby Database directory.
	 */
	private String m_derbyDir;
	
	/**
	 * Derby Database port.
	 */
	private int m_port;
	
	/**
	 * Derby Database user name.
	 */
	private String m_username;
	
	/**
	 * Derby Database password.
	 */
	private String m_password;

	
	/**
	 * Set the property "derby.system.home" so that database and log file will
	 * be placed in the right directory.
	 */
	private void setWorkingDir() {
		System.setProperty("derby.system.home", m_derbyDir);
	}

	/**
	 * Create the network server control.
	 *
	 * @throws Exception
	 */
	private void createNetworkServer() throws Exception {
		// check if homeDir was set.
		assert (m_derbyDir != null);
		setWorkingDir();
		if (StringUtils.isNotBlank(m_username) && StringUtils.isNotBlank(m_password)) {
			m_server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"), m_port, m_username, m_password);
		} else {
			m_server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"), m_port);
		}
	}
	
	/** {@inheritDoc} */
	public void setHomeDir(String dir) {
		m_derbyDir = dir;
	}
	
	/** {@inheritDoc} */
	public void setPort(int port) {
		m_port = port > 0 ? port : NetworkServerControl.DEFAULT_PORTNUMBER;
	}
	
	/** {@inheritDoc} */
	public void setUsername(String username) {
		m_username = username;
	}
	
	/** {@inheritDoc} */
	public void setPassword(String password) {
		m_password = password;
	}

	
	/** {@inheritDoc} */
	public void start() throws Exception {
		if (m_server == null) {
			createNetworkServer();
		}
		// Test if there's already a Network Server running on this port.
		// If so, do nothing, else start server.
		try {
			m_server.ping();
		} catch (Exception e) {
			m_server.start(new PrintWriter(System.out));
		}
	}

	/** {@inheritDoc} */
	public void stop() throws Exception {
		if (m_server == null) {
			createNetworkServer();
		}
		m_server.ping();
		m_server.shutdown();
	}
	
	/** {@inheritDoc} */
	public String getDbName() {
		return "derby";
	}
	
}