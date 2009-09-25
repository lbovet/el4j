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
import java.io.OutputStream;

import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogFactory;
import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogger;

/**
 * Implementation of OutputStream with modification for logging.
 * 
 * @author Jonas Hauenstein (JHN)
 */

public class OutputStreamLogger extends OutputStream {

	/**
	 * Reference to original OutputStream.
	 */
	private OutputStream m_os;
	/**
	 * Reference to ConnectionStatistics for logging.
	 */
	private ConnectionStatistics m_cs;

	/**
	 * Generic Logger.
	 */
	private final GenericLogger m_logger = GenericLogFactory.getLogger(SocketStatistics.class);

	/**
	 * Constructor.
	 * 
	 * @param os
	 *            original OutputStream used for writing
	 * @param cs
	 *            ConnectionStatistics used for logging
	 */
	public OutputStreamLogger(OutputStream os, ConnectionStatistics cs) {
		this.m_os = os;
		this.m_cs = cs;
	}

	/**
	 * Modified version of write which also logs traffic to assigned ConnectionStatistics. {@inheritDoc}
	 */
	public void write(int b) throws IOException {
		m_os.write(b);
		m_cs.addbytessent(1);
		if (m_logger.isLogEnabled("debug")) {
			m_logger.log("debug", "SocketID: " + m_cs.getSocketID() + " writes " + (char) b);
		}
	}

	/**
	 * Modified version of write which also logs traffic to assigned ConnectionStatistics. {@inheritDoc}
	 */
	public void write(byte[] b, int off, int length) throws IOException {
		m_os.write(b, off, length);
		m_cs.addbytessent(length);
		if (m_logger.isLogEnabled("debug")) {
			// create String from response
			StringBuilder sb = new StringBuilder("SocketID: " + m_cs.getSocketID() + " reads ");
			for (int i = off; i < off + length; i++) {
				sb.append((char) b[i]);
			}
			m_logger.log("debug", sb.toString());
		}
	}

}
