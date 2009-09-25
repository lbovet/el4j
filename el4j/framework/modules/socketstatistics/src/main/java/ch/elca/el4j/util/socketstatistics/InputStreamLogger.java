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

import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogFactory;
import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogger;


/**
 * Implementation of InputStream with modification for logging.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class InputStreamLogger extends InputStream {

	/**
	 * Reference to original OutputStream.
	 */
	private InputStream m_is;

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
	 * @param is
	 *            original InputStream used for reading
	 * @param cs
	 *            ConnectionStatistics used for logging
	 */
	public InputStreamLogger(InputStream is, ConnectionStatistics cs) {
		this.m_is = is;
		this.m_cs = cs;
	}

	/**
	 * Modified version of read which also logs traffic to assigned ConnectionStatistics. {@inheritDoc}
	 */
	public int read() throws IOException {
		int result = m_is.read();
		if (result != -1) {
			m_cs.addbytesrecv(1);
			if (m_logger.isLogEnabled("debug")) {
				m_logger.log("debug", "SocketID: " + m_cs.getSocketID() + " reads " + (char) result);
			}
		}
		return result;
	}

	/**
	 * Modified version of read which also logs traffic to assigned ConnectionStatistics. {@inheritDoc}
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		int length = m_is.read(b, off, len);
		if (length != -1) {
			m_cs.addbytesrecv(length);
			if (m_logger.isLogEnabled("debug")) {
				// create String from response
				StringBuilder sb = new StringBuilder("SocketID: " + m_cs.getSocketID() + " reads ");
				for (int i = off; i < off + length; i++) {
					sb.append((char) b[i]);
				}
				m_logger.log("debug", sb.toString());
			}
		}
		return length;
	}

}
