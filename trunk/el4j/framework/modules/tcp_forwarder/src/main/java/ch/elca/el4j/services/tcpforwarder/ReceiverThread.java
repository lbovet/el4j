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
package ch.elca.el4j.services.tcpforwarder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.tcpforwarder.messages.TcpMessageForwarder;
import ch.elca.el4j.services.tcpforwarder.messages.impl.TcpMessageForwarderData;
import ch.elca.el4j.services.tcpforwarder.messages.impl.TcpMessageForwarderEnd;
import ch.elca.el4j.services.tcpforwarder.messages.impl.TcpMessageForwarderReset;

//Checkstyle: MagicNumber off
/**
 * Represents a connection receiver.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 * @author Alex Mathey (AMA)
 * @author Martin Zeltner (MZE)
 */
public class ReceiverThread extends Thread {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(ReceiverThread.class);

    /**
     * The socket to read from.
     */
    protected final SocketChannel m_in;
    
    /**
     * Buffer to read data into.
     */
    protected final ByteBuffer m_buf = ByteBuffer.allocate(8192);
    
    /**
     * Indicates whether reading is finished.
     */
    protected boolean m_done = false;

    /**
     * Constructor.
     * 
     * @param in
     *            The socket to read from
     */
    public ReceiverThread(SocketChannel in) {
        m_in = in;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        s_logger.debug(toString() + " started");
        while (!m_done) {
            TcpMessageForwarder msg;
            try {
                msg = receive();
            } catch (ClosedChannelException e) {
                m_done = true;
                break;
            }
            process(msg);
        }
        done();
        s_logger.debug(toString() + " terminated");
    }

    /**
     * Receives a connection.
     * 
     * @return The TCP message corresponding to the read data
     * @throws ClosedChannelException
     *             if the connection has been has been closed unexpectedly
     */
    protected TcpMessageForwarder receive() throws ClosedChannelException {
        TcpMessageForwarder result = null;
        try {
            m_buf.clear();
            int l = m_in.read(m_buf);
            if (l == -1) {
                result = TcpMessageForwarderEnd.INSTANCE;
            } else {
                m_buf.flip();
                result = new TcpMessageForwarderData(m_buf);
            }
        } catch (ClosedChannelException e) {
            throw e;
            // propagate exception
        } catch (IOException e) {
            // due to the sucky exception hierarchy, we have to parse 
            // the exception message to identify the kind of exception.
            // Hopefully they won't change too often :-/
            String m = e.getMessage();
            if (m != null) {
                if (m.equals("An established connection was aborted by the"
                    + " software in your host machine")) {
                    result = TcpMessageForwarderEnd.INSTANCE;
                }
                if (m.equals("An existing connection was forcibly closed "
                    + "by the remote host")) {
                    result = TcpMessageForwarderReset.INSTANCE;
                }
            }
            if (result != null) {
                s_logger.warn("unrecognized exception, assuming the channel "
                    + "was closed", e);
                result = TcpMessageForwarderEnd.INSTANCE;
            }
        }
        return result;
    }

    /** 
     * processes a received message.
     * 
     * @param msg Message to print.
     */
    protected void process(TcpMessageForwarder msg) {
        s_logger.debug(toString() + " " + msg.toString());
        m_done = msg.last();
    }

    /** 
     * this receiver is about to terminate.
     */
    protected void done() { }
}
//Checkstyle: MagicNumber on
