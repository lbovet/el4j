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
package ch.elca.el4j.maven.plugins.database.util.h2;

import org.apache.commons.lang.StringUtils;
import org.h2.engine.Constants;
import org.h2.server.TcpServer;
import org.h2.tools.Server;

import ch.elca.el4j.maven.plugins.database.util.DbController;

/**
 * This class starts the H2 Server.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @author Stefan Wismer (SWI)
 */
public final class H2Controller implements DbController {

	/**
	 * Server Control object.
	 */
	private Server m_server;
	
	/**
	 * H2 Database port.
	 */
	private int m_port = Constants.DEFAULT_TCP_PORT;
	
	/**
	 * H2 Database user name.
	 */
	private String m_username;
	
	/**
	 * H2 Database password.
	 */
	private String m_password;

	/** {@inheritDoc} */
	public void setHomeDir(String homeDir) { }
	
	
	/** {@inheritDoc} */
	public void setPort(int port) {
		m_port = port > 0 ? port : Constants.DEFAULT_TCP_PORT;
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
			createServer();
		}
		// Test if there's already a Network Server running on this port.
		// If so, do nothing, else start server.
		if (!m_server.isRunning(false)) {
			m_server.start();
		}
	}

	/**
	 * Terminates the H2 server.
	 *
	 * @throws Exception
	 */
	public void stop() throws Exception {
		if (m_server == null) {
			createServer();
		}
		m_server.shutdown();
	}
	
	/** {@inheritDoc} */
	public String getDbName() {
		return "h2";
	}
	
	/**
	 * Create the network server control.
	 *
	 * @throws Exception
	 */
	private void createServer() throws Exception {
		if (StringUtils.isNotBlank(m_username) && StringUtils.isNotBlank(m_password)) {
			m_server = Server.createTcpServer(
				new String[] {"-tcpPort", Integer.toString(m_port), "-tcpPassword", m_password});
		} else {
			m_server = Server.createTcpServer(new String[] {"-tcpPort", Integer.toString(m_port)});
		}
	}
	
	/**
	 * Start the web UI.
	 * 
	 * @param port    the port where the web UI will be listening.
	 * @throws Exception
	 */
	public void startWebUI(int port) throws Exception {
		if (m_server == null) {
			m_server = Server.createWebServer(new String[] {"-webPort", Integer.toString(port)});
			m_server.start();
		}
	}
	
	/**
	 * Stop the web UI.
	 */
	public void stopWebUI() {
		if (m_server != null) {
			m_server.stop();
		}
	}
}