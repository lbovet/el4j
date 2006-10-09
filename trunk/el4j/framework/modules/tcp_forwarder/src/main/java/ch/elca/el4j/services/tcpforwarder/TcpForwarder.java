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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//Checkstyle: MagicNumber off

/**
 * A TcpForwarder represents a service intended to forward TCP traffic 
 * directed to a specific port. Specifically, it permits to programmatically
 * halt and resume network connectivity.
 * 
 * <p>Since this is a Java class, it can not intercept traffic not destined for 
 * it. However, it can masquerade for a remote application by forwarding traffic
 * to and from it achieving the same effect but for requiring the application to
 * connect on the forwarder's port.  
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
public class TcpForwarder implements Runnable {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(TcpForwarder.class);
    
    /** The listen port. */
    protected int m_port;

    /** The socket address to forward traffic to. */
    protected SocketAddress m_target;

    /** The set of active links. */
    protected Set<Link> m_active = Collections.synchronizedSet(
        new LinkedHashSet<Link>());

    /** The server socket channel. */
    private ServerSocketChannel m_ssc;

    /**
     * Creates a new TcpForwarder that listens on port <code>listenport</code>
     * and forwards all traffic to <code>targetport</code> on 127.0.0.1.
     * 
     * @param listenport
     *            Input port
     * @param targetport
     *            Forwarding port
     */
    public TcpForwarder(int listenport, int targetport) {
        this(listenport, new InetSocketAddress(targetport));
    }

    /**
     * Creates a new TcpForwarder that listens on port <code>listenport</code>
     * and forwards all traffic to <code>target</code>.
     * 
     * @param port    Source port
     * @param target  Forwarding Socket Address
     */
    public TcpForwarder(int port, SocketAddress target) {
        m_port = port;
        m_target = target;
        new Thread(this).start();
    }

    /** this method is not intended to be invoked from outside. */
    public void run() {
        try {
            while (true) {
                try {
                    m_ssc = ServerSocketChannel.open();
                    m_ssc.socket().bind(new InetSocketAddress(m_port));
                } catch (IOException e) {
                    s_logger.warn("Binding to socket failed. Aborting...", e);
                    return;
                }
                s_logger.debug("Server socket successfully bound to address "
                    + m_ssc.socket().getLocalSocketAddress());

                while (true) {
                    SocketChannel in;
                    SocketChannel out;
                    try {
                        in = m_ssc.accept();
                        s_logger.debug("Connection accepted");
                        out = SocketChannel.open(m_target);
                    } catch (ClosedChannelException e) {
                        break;
                    } catch (IOException e) {
                        s_logger.warn("Connection failed. Aborting...", e);
                        return;
                    }
                    new Link(this, in, out);
                }
                try {
                    if (m_ssc != null) {
                        m_ssc.close();
                    }
                } catch (IOException e) {
                    s_logger.warn("Closing server socket channel failed.", e);
                }
                m_ssc = null;

                // wait until we are needed again
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        s_logger.warn("Unexpected exception occurred", e);
                    }
                }
            }
        } finally {
            unplug();
        }
    }

    /**
     * stops simulating a total network failure.
     */
    public void plug() {
        synchronized (this) {
            notify();
        }
    }

    /** 
     * simulates a complete network failure by dropping all active connections
     * and ceasing to accept new ones.
     */
    public void unplug() {
        try {
            if (m_ssc != null) {
                m_ssc.close();
            }
        } catch (IOException e) {
            s_logger.warn("Closing server socket channel failed.", e);
        }
        synchronized (m_active) {
            for (Link l : m_active) {
                l.cut();
            }
        }
    }
}
//Checkstyle: MagicNumber on
