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
package ch.elca.el4j.services.tcpforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    
    /**
     * The listen port.
     */
    protected int m_listenPort;

    /**
     * The internet socket address to forward traffic to.
     */
    protected InetSocketAddress m_targetAddress;

    /**
     * The set of active links.
     */
    protected Set<Link> m_activeLinks = Collections.synchronizedSet(
        new LinkedHashSet<Link>());

    /**
     * The used server socket.
     */
    private ServerSocket m_serverSocket = null;

    /**
     * Forwarder to listen and forward to local ports.
     * 
     * @param listenPort Is the input port.
     * @param targetPort Is the output port.
     */
    public TcpForwarder(int listenPort, int targetPort) {
        this(listenPort, getLocalSocketAddress(targetPort));
    }

    /**
     * Forwarder to listen on local port and forward to given target address.
     * 
     * @param listenPort Is the input port.
     * @param targetAddress Is the target internet socket address to forward to.
     */
    public TcpForwarder(int listenPort, InetSocketAddress targetAddress) {
        m_listenPort = listenPort;
        m_targetAddress = targetAddress;
        new Thread(this).start();
    }
    
    /**
     * @param port Is the local port for the socket.
     * @return Returns the InetSocketAddress with the given port number for 
     *         local host.
     */
    public static InetSocketAddress getLocalSocketAddress(int port) {
        InetSocketAddress result = null;
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            result = new InetSocketAddress(localhost, port);
        } catch (UnknownHostException e) {
            s_logger.debug("Unable to get the InetAddress of local host.", e);
        }
        if (result == null) {
            result = new InetSocketAddress("localhost", port);
        }
        return result;
    }

    /**
     * This method is not intended to be invoked from the outside.
     */
    public void run() {
        try {
            while (true) {
                try {
                    m_serverSocket = new ServerSocket(m_listenPort);
                } catch (IOException e) {
                    s_logger.warn("Binding server socket to local port " 
                        + m_listenPort + " failed. Aborting...", e);
                    return;
                }
                s_logger.debug("Server socket successfully bound to address "
                    + m_serverSocket.getLocalSocketAddress());

                while (true) {
                    Socket listenSocket = null;
                    Socket targetSocket = null;
                    try {
                        listenSocket = m_serverSocket.accept();
                        s_logger.debug("Connection accepted; "
                            + listenSocket.toString());
                        s_logger.debug("Trying to open the target socket at [" 
                            + m_targetAddress + "].");
                        targetSocket = new Socket(
                            m_targetAddress.getAddress(), 
                            m_targetAddress.getPort());
                    } catch (IOException e) {
                        if (listenSocket != null) {
                            try {
                                listenSocket.close();
                            } catch (IOException eInner) {
                                s_logger.debug(
                                    "Due to an exception the listening "
                                    + "socket should be closed, but there was "
                                    + "an exception while closing it.", eInner);
                            }
                        }
                        if (targetSocket != null) {
                            try {
                                targetSocket.close();
                            } catch (IOException eInner) {
                                s_logger.debug(
                                    "Due to an exception the target "
                                    + "socket should be closed, but there was "
                                    + "an exception while closing it.", eInner);
                            }
                        }
                        
                        if (m_serverSocket.isClosed()) {
                            s_logger.warn(
                                "Server socket on local port " 
                                + m_listenPort + " closed.", e);
                            m_serverSocket = null;
                            break;
                        }
                        s_logger.error("Connection from local port " 
                            + m_listenPort + " to target address " 
                            + m_targetAddress + " failed. Aborting...", e);
                        return;
                    }
                    new Link(this, listenSocket, targetSocket);
                }
                try {
                    if (m_serverSocket != null) {
                        m_serverSocket.close();
                    }
                } catch (IOException e) {
                    s_logger.warn("Closing server socket failed.", e);
                }
                m_serverSocket = null;

                // wait until we are needed again
                synchronized (this) {
                    try {
                        wait();
                        s_logger.debug(
                            "Tcp forwarder awoken. Will continue work...");
                    } catch (InterruptedException e) {
                        s_logger.debug(
                            "Tcp forwarder interrupted. Will continue work...");
                    }
                }
            }
        } finally {
            unplug();
        }
    }

    /**
     * Starts forwarding tcp messages.
     */
    public void plug() {
        synchronized (this) {
            notify();
        }
    }

    /** 
     * Stops forwarding tcp messages.
     */
    public void unplug() {
        try {
            if (m_serverSocket != null) {
                m_serverSocket.close();
            }
        } catch (IOException e) {
            s_logger.warn("Closing server socket failed.", e);
        }
        synchronized (m_activeLinks) {
            for (Link l : m_activeLinks) {
                l.cut();
            }
        }
    }
}
