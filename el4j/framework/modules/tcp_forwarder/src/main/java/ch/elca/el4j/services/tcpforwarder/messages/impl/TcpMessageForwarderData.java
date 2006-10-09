/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.tcpforwarder.messages.impl;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.tcpforwarder.messages.TcpMessageForwarder;

/**
 * Tcp message forwarder for data.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class TcpMessageForwarderData implements TcpMessageForwarder {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(TcpMessageForwarderData.class);

    /**
     * Holds the bytes contained in this message.
     */
    private ByteBuffer m_buffer;

    /**
     * Constructor.
     * 
     * @param b   Holds the bytes contained in this message, i.e. b.get()
     *            returns the first byte of data.
     */
    public TcpMessageForwarderData(ByteBuffer b) {
        m_buffer = b;
    }

    /**
     * {@inheritDoc}
     */
    public void forward(SocketChannel out) {
        try {
            out.write(m_buffer);
        } catch (Exception e) {
            s_logger.error("Error while forwarding data.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean last() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "TcpMessageForwarderData: " + m_buffer.limit() + " bytes";
    }
}
