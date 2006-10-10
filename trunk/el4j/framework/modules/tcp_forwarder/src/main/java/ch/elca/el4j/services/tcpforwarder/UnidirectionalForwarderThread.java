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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Thread to forward got input to the output.
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
public class UnidirectionalForwarderThread extends Thread {
    /**
     * Is the forwarder buffer size.
     */
    public static final int BUFFER_SIZE = 4096;
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(UnidirectionalForwarderThread.class);
    
    /**
     * Is the link <code>this</code> belongs to.
     */
    protected final Link m_link;
    
    /**
     * Is the input source.
     */
    protected final Socket m_inputSocket;
    
    /**
     * Is the output drain.
     */
    protected final Socket m_outputSocket;
    
    /**
     * Flag to mark if the forwarding is done.
     */
    protected volatile boolean m_done = false;
    
    /**
     * Is the input stream of the input socket.
     */
    private InputStream m_in;
    
    /**
     * Is the output stream of the output socket.
     */
    private OutputStream m_out;

    /**
     * Constructor.
     * 
     * @param link Is the link <code>this</code> belongs to.
     * @param inputSocket Is the input source.
     * @param outputSocket Is the output drain.
     */
    public UnidirectionalForwarderThread(Link link, Socket inputSocket, 
        Socket outputSocket) {
        Assert.notNull(link);
        Assert.notNull(inputSocket);
        Assert.notNull(outputSocket);
        
        m_link = link;
        m_inputSocket = inputSocket;
        m_outputSocket = outputSocket;
    }

    /**
     * @return Return <code>true</code> if forwarding is done.
     */
    public boolean isDone() {
        return m_done;
    }

    /**
     * Forwards data from input to output.
     * 
     * {@inheritDoc}
     */
    public void run() {
        m_in = null;
        m_out = null;
        try {
            m_in = m_inputSocket.getInputStream();
            m_out = m_outputSocket.getOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int readBytes = 0;

            while (!m_done) {
                while (readBytes == 0) {
                    try {
                        readBytes 
                            = m_in.read(buffer, 0, BUFFER_SIZE);
                    } catch (IOException e) {
                        if (m_done) {
                            return;
                        }
                        break;
                    }
                }
                
                if (readBytes < 0) {
                    m_done = true;
                } else if (readBytes > 0) {
                    try {
                        m_out.write(buffer, 0, readBytes);
                    } catch (IOException e) {
                        if (m_done) {
                            return;
                        }
                    }
                    readBytes = 0;
                }
            }
        } catch (Throwable t) {
            s_logger.error("There was a problem while forwarding data. "
                + "Forwarder thread will now end smoothly.", t);
        } finally {
            smoothHalt();
            m_link.done();
        }
    }

    /**
     * Smmothly stops the forwarder thread.
     */
    public void smoothHalt() {
        m_done = true;
        try {
            if (m_in != null) {
                if (m_inputSocket != null) {
                    m_inputSocket.shutdownInput();
                } else {
                    m_in.close();
                }
                m_in = null;
            }
        } catch (Exception e) {
            s_logger.debug(
                "Exception while smoothly closing the input side.", e);
        }
        try {
            if (m_out != null) {
                m_out.flush();
                if (null != m_outputSocket) {
                    m_outputSocket.shutdownOutput();
                } else {
                    m_out.close();
                }
                m_out = null;
            }
        } catch (Exception e) {
            s_logger.debug(
                "Exception while smoothly closing the output side.", e);
        }
    }
    
    /**
     * Immediately stops the forwarder thread.
     */
    public void halt() {
        m_done = true;
        if (m_inputSocket != null) {
            try {
                m_inputSocket.close();
            } catch (IOException e) {
                s_logger.debug("Exception while closing input socket.", e);
            }
        }
        if (m_outputSocket != null) {
            try {
                m_outputSocket.close();
            } catch (IOException e) {
                s_logger.debug("Exception while closing output socket.", e);
            }
        }
        if (m_in != null) {
            try {
                m_in.close();
            } catch (IOException e) {
                s_logger.debug("Exception while closing input stream.", e);
            }
        }
        if (m_out != null) {
            try {
                m_out.close();
            } catch (IOException e) {
                s_logger.debug("Exception while closing output stream.", e);
            }
        }
    }
}
