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
package ch.elca.el4j.services.remoting.socketfactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * This class is a custom {@link RMISocketFactory} allowing to define a SocketTimeout
 * for the RMI connection.
 * The value of this timeout can be set in the spring configuration file
 * rmi-timeout.xml (in main/resources/mandatory).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Dominik Zindel (DZI)
 */
public class RMITimeoutSocketFactory extends RMISocketFactory implements
	Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 953865798975586465L;

	/**
	 * The SocketTimeout.
	 */
	private int m_timeout = 60000;

	/**
	 * The constructor with the desired timeout as parameter.
	 * 
	 * @param timeout
	 *            The SocketTimeout.
	 */
	public RMITimeoutSocketFactory(int timeout) {
		super();
		m_timeout = timeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		ServerSocket socket = new ServerSocket(port);
		socket.setSoTimeout(m_timeout);
		return socket;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		Socket socket = new Socket(host, port);
		socket.setSoTimeout(m_timeout);
		return socket;
	}

}
