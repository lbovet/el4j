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
package ch.elca.el4j.util.socketstatistics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

/**
 * SocketImpl for use with SocketStatistics. Generates and returns modified InputStreams / OutputStreams with support
 * logging. In other cases, it behaves just like the original java.net class
 * 
 * @author Jonas Hauenstein (JHN)
 */

public class SocketImplLogger extends SocketImpl {

	/**
	 * Delegator used for calls to java.net.SocksSocketImpl.
	 */
	private final ReflectiveDelegator m_delegator;

	/**
	 * Reference to the corresponding ConnectionStatistics.
	 */
	private final ConnectionStatistics m_constats;

	/**
	 * Constructor. creates a ReflectiveDelegator for delegation of method calls to java.net.SocksSocketImpl and a new
	 * ConnectionStatistics inside SocketStatistics
	 */
	public SocketImplLogger() {
		this.m_delegator = new ReflectiveDelegator(this, SocketImpl.class, "java.net.SocksSocketImpl");
		this.m_constats = SocketStatistics.addNewConStats();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void accept(SocketImpl s) throws IOException {
		m_delegator.invoke(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int available() throws IOException {
		Integer i = (Integer) m_delegator.invoke();
		return i.intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void bind(InetAddress host, int port) throws IOException {
		m_delegator.invoke(host, port);
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
	}

	/**
	 * Modified version of close(). Sets the the destroyed date in ConnectionStatistics {@inheritDoc}
	 */
	@Override
	protected void close() throws IOException {
		m_constats.setDestroyed();
		m_delegator.invoke();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void connect(String host, int port) throws IOException {
		try {
			m_delegator.invoke(host, port);
		} catch (DelegationException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void connect(InetAddress address, int port) throws IOException {
		try {
			m_delegator.invoke(address, port);
		} catch (DelegationException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void connect(SocketAddress address, int timeout) throws IOException {
		try {
			m_delegator.invoke(address, port);
		} catch (DelegationException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void create(boolean stream) throws IOException {
		m_delegator.invoke(stream);
	}

	/**
	 * Modified version of getInputStream(). Returns an InputStreamLogger for logging of socket traffic {@inheritDoc}
	 */
	@Override
	protected InputStream getInputStream() throws IOException {
		InputStream real = m_delegator.invoke();
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
		return new InputStreamLogger(real, m_constats);
	}

	/**
	 * Modified version of getOutputStream(). Returns an OutputStreamLogger for logging of socket traffic {@inheritDoc}
	 */
	@Override
	protected OutputStream getOutputStream() throws IOException {
		OutputStream real = m_delegator.invoke();
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
		return new OutputStreamLogger(real, m_constats);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void listen(int backlog) throws IOException {
		m_delegator.invoke(backlog);
		m_constats.setRemoteAdress((InetAddress) m_delegator.delegateTo("getInetAddress").invoke());
		m_constats.setRemotePort((Integer) m_delegator.delegateTo("getPort").invoke());
		m_constats.setLocalPort((Integer) m_delegator.delegateTo("getLocalPort").invoke());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void sendUrgentData(int data) throws IOException {
		m_delegator.invoke();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getOption(int optID) throws SocketException {
		return m_delegator.invoke(optID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOption(int optID, Object value) throws SocketException {
		m_delegator.invoke(optID, value);
	}

}
