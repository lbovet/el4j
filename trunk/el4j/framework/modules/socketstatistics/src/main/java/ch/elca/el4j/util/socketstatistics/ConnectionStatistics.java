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

import java.net.InetAddress;
import java.util.Date;

import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogFactory;
import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogger;

/**
 * Keeps statistics of one (open / closed) socket connection.
 * 
 * @author Jonas Hauenstein (JHN)
 */

public class ConnectionStatistics implements Comparable<ConnectionStatistics> {

	/**
	 * Generic m_logger.
	 */
	private final GenericLogger m_logger = GenericLogFactory.getLogger(SocketStatistics.class);

	/**
	 * Date when socket was created.
	 */
	private Date m_created;

	/**
	 * Date when socket was destroyed / closed.
	 */
	private Date m_destroyed;

	/**
	 * The total number of bytes sent on socket.
	 */
	private long m_totalBytesSent;

	/**
	 * The total number of bytes received on socket.
	 */
	private long m_totalBytesRecveived;

	/**
	 * The unique id of the socket.
	 */
	private long m_socketId;

	/**
	 * The remote address of the socket.
	 */
	private InetAddress m_remoteAddress;

	/**
	 * The remoteport of the socket.
	 */
	private int m_remotePort;

	/**
	 * The local port of the socket.
	 */
	private int m_localPort;

	/**
	 * Constructor.
	 * 
	 * @param socketID
	 *            (unique) id to be assigned to socket
	 */
	public ConnectionStatistics(long socketID) {
		this.m_socketId = socketID;
		this.m_created = new Date();
		this.m_destroyed = null;
		this.m_totalBytesSent = 0;
		this.m_totalBytesRecveived = 0;
		this.m_remoteAddress = null;
		this.m_remotePort = 0;
		this.m_localPort = 0;
		m_logger.log("info", "Socket opened with Socket-ID " + m_socketId);
	}

	/**
	 * Add a certain amounts of sent bytes to socket stats.
	 * 
	 * @param nrofbytes
	 *            number of sent bytes
	 */
	public synchronized void addbytessent(int nrofbytes) {
		m_totalBytesSent += nrofbytes;
	}

	/**
	 * Add a certain amounts of received bytes to socket stats.
	 * 
	 * @param nrofbytes
	 *            number of received bytes
	 */
	public synchronized void addbytesrecv(int nrofbytes) {
		m_totalBytesRecveived += nrofbytes;
	}

	/**
	 * Getter for number of received bytes.
	 * 
	 * @return total number of bytes received on socket
	 */
	public long getBytesReceived() {
		return m_totalBytesRecveived;
	}

	/**
	 * Getter for number of sent bytes.
	 * 
	 * @return total number of bytes sent on socket
	 */
	public long getBytesSent() {
		return m_totalBytesSent;
	}

	/**
	 * Setter for sockets remote address.
	 * 
	 * @param ra
	 *            sockets remote address
	 */
	public synchronized void setRemoteAdress(InetAddress ra) {
		this.m_remoteAddress = ra;
	}

	/**
	 * Getter for sockets remote address. Using string to guarantee compatibility to MXBeans
	 * 
	 * @return sockets remote address
	 */
	public String getRemoteAdress() {
		return this.m_remoteAddress == null ? "" : this.m_remoteAddress.toString();
	}

	/**
	 * Setter for sockets remote port.
	 * 
	 * @param rp
	 *            sockets remote port
	 */
	public synchronized void setRemotePort(int rp) {
		this.m_remotePort = rp;
	}

	/**
	 * Getter for sockets remote port.
	 * 
	 * @return sockets remote port
	 */
	public int getRemotePort() {
		return this.m_remotePort;
	}

	/**
	 * Setter for sockets local port.
	 * 
	 * @param lp
	 *            sockets local port
	 */
	public synchronized void setLocalPort(int lp) {
		this.m_localPort = lp;
	}

	/**
	 * Getter for sockets local port.
	 * 
	 * @return sockets local port
	 */
	public int getLocalPort() {
		return this.m_localPort;
	}

	/**
	 * Mark monitored socket as closed / destroyed.
	 */
	public synchronized void setDestroyed() {
		m_logger.log("info", "Socket closed with Socket-ID " + m_socketId);
		SocketStatistics.setConnectionDestroyed(this);
	}

	/**
	 * External getter for sockets destroyed / close date. Using string to guarantee compatibility to MXBeans
	 * 
	 * @return string of date when socket was destroyed / closed
	 */
	public String getDestroyedDate() {
		return m_destroyed == null ? "" : m_destroyed.toString();
	}

	/**
	 * Internal getter for sockets destroyed / close date. Returns date object
	 * 
	 * @return date when socket was destroyed / closed
	 */
	protected Date getDestroyedDateInt() {
		return m_destroyed;
	}

	/**
	 * Sets socket as destroyed / closed with date = now.
	 */
	protected synchronized void setDestroyedDateInt() {
		m_destroyed = new Date();
	}

	/**
	 * External getter for sockets creation date. Using string to guarantee compatibility to MXBeans
	 * 
	 * @return string of date when socket was created
	 */
	public String getCreatedDate() {
		return m_created == null ? "" : m_created.toString();
	}

	/**
	 * Internal getter for sockets creation date. Returns date object
	 * 
	 * @return date when socket was created
	 */
	protected Date getCreatedDateInt() {
		return m_created;
	}

	/**
	 * Getter for sockets id.
	 * 
	 * @return sockets unique id
	 */
	public long getSocketID() {
		return m_socketId;
	}

	/**
	 * Return stats of socket as string.
	 * 
	 * @return stats of socket
	 */
	protected String getStatistics() {
		StringBuilder sb = new StringBuilder("Socket ID: " + m_socketId);
		sb.append("\n  [ created on " + getCreatedDate() + " ]");
		if (m_destroyed != null) { sb.append("\n  [ closed on " + getDestroyedDate() + " ]"); }
		sb.append("\n  [ Remote address / port: " + m_remoteAddress + " / " + m_remotePort + " ]");
		sb.append("\n  [ Local port: " + m_localPort + " ]");
		sb.append("\n  [ Total bytes received on socket: " + m_totalBytesRecveived + " ]");
		sb.append("\n  [ Total bytes sent on socket: " + m_totalBytesSent + " ]");
		return sb.toString();
	}

	/**
	 * Return stats of socket as string in csv format.
	 * 
	 * @return stats of socket
	 */
	protected String getStatisticsCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append(m_socketId);
		sb.append(";");
		sb.append(getCreatedDate());
		sb.append(";");
		sb.append(getDestroyedDate());
		sb.append(";");
		sb.append(getRemoteAdress());
		sb.append(";");
		sb.append(m_remotePort);
		sb.append(";");
		sb.append(m_localPort);
		sb.append(";");
		sb.append(m_totalBytesRecveived);
		sb.append(";");
		sb.append(m_totalBytesSent);
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Compare using destroyed / closed date of socket / ConnectionStatics. First hand sort order is destruction
	 * ascending date. Open sockets are sorted among each other by creation date. {@inheritDoc}
	 */
	@Override
	public int compareTo(ConnectionStatistics o) {
		// check if they are the same
		if (this.m_socketId == o.getSocketID()) {
			return 0;
		}
		// sort by destroyed date asc
		// put open / not yet closed sockets on bottom of list ordered by creation date
		if (this.m_destroyed == null && o.getDestroyedDateInt() != null) {
			return 1;
		}
		if (this.m_destroyed != null && o.getDestroyedDateInt() == null) {
			return -1;
		}
		if (this.m_destroyed == null && o.getDestroyedDateInt() == null) {
			if (this.m_created.getTime() < o.getCreatedDateInt().getTime()) {
				return -1;
			} else if (this.m_created.getTime() == o.getCreatedDateInt().getTime()) {
				return this.m_socketId < o.getSocketID() ? -1 : 1;
			} else {
				return 1;
			}
		}
		if (this.m_destroyed.getTime() < o.getDestroyedDateInt().getTime()) {
			return -1;
		} else if (this.m_destroyed.getTime() == o.getDestroyedDateInt().getTime()) {
			return this.m_socketId < o.getSocketID() ? -1 : 1;
		} else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		return (int) m_socketId;
	}

	@Override
	public boolean equals(Object o) {
		return ((ConnectionStatistics) o).getSocketID() == this.m_socketId;
	}
}
