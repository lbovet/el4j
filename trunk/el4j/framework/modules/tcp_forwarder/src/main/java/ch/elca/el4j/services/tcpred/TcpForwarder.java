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
package ch.elca.el4j.services.tcpred;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
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
 * @author Florian Suess (FLS)
 * @author Alex Mathey (AMA)
 */
public class TcpForwarder implements Runnable {

    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(TcpForwarder.class);
    
    /** The listen port. */
    int m_port;

    /** The socket address to forward traffic to. */
    SocketAddress m_target;

    /** The server socket channel. */
    ServerSocketChannel m_ssc;

    /** The set of active links. */
    Set<Link> m_active = new HashSet<Link>();
    
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
        this(listenport, new InetSocketAddress(localhost(), targetport));
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

    /** 
     * Called to log an error.
     * 
     * @param msg Message to print
     * @param e   The exception that occured
     */
    static void log(String msg, Exception e) {
        System.err.println(msg);
        e.printStackTrace();
    }

    /**
     * @return an InetAddress for the loopback-interface
     */
    static InetAddress localhost() {
        try {
            return Inet4Address.getByAddress(new byte[] {127, 0, 0, 1});
        } catch (UnknownHostException e) {
            s_logger.warn("Unknown host", e);
            return null;
        }
    }

    /**
     * Closes a selectable channel.
     * 
     * @param c
     *            The channel to close
     */
    static void silentClose(SelectableChannel c) {
        try {
            c.close();
        } catch (IOException e) {
            s_logger.warn("closing failed, skipping ...", e);
        }
    }

    /** this method is not intended to be invoked from outside. */
    public void run() {
        while (true) {
            try {
                m_ssc = ServerSocketChannel.open();
                m_ssc.socket().bind(new InetSocketAddress(localhost(), m_port));
            } catch (IOException e) {
                s_logger.warn("binding to socket failed, aborting ...", e);
                return;
            }
            s_logger.debug("ServerSocket bound to "
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
                    s_logger.warn("accepting incoming connection failed,"
                        + " aborting...", e);
                    return;
                }
                new Link(this, in, out);
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
        silentClose(m_ssc);
        for (Link l : m_active) {
            l.cut();
        }
    }

    /**
     * This class represents a connection receiver.
     *
     * @author Alex Mathey (AMA)
     */
    static class Receiver extends Thread {
        /**
         * The socket to read from.
         */
        SocketChannel m_in;
        
        /**
         * Buffer to read data into.
         */
        ByteBuffer m_buf = ByteBuffer.allocate(8192);
        
        /**
         * Indicates whether reading is finished.
         */
        boolean m_done = false;

        /**
         * Constructor.
         * 
         * @param in
         *            The socket to read from
         */
        Receiver(SocketChannel in) {
            m_in = in;
        }

        /**
         * Receives a connection.
         * 
         * @return The TCP message corresponding to the read data
         * @throws ClosedChannelException
         *             if the connection has been has been closed unexpectedly
         */
        AbstractTcpMsg receive() throws ClosedChannelException {
            try {
                m_buf.clear();
                int l = m_in.read(m_buf);
                if (l == -1) {
                    return AbstractTcpMsg.FIN;
                } else {
                    m_buf.flip();
                    return new AbstractTcpMsg.Data(m_buf);
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
                        return AbstractTcpMsg.FIN;
                    }
                    if (m.equals("An existing connection was forcibly closed "
                        + "by the remote host")) {
                        return AbstractTcpMsg.RST;
                    }
                }
                s_logger.warn("unrecognized exception, assuming the channel "
                    + "was closed", e);
                return AbstractTcpMsg.FIN;
            }
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            s_logger.debug(toString() + " started");
            while (!m_done) {
                AbstractTcpMsg msg;
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
         * processes a received message.
         * 
         * @param msg Message to print.
         */
        protected void process(AbstractTcpMsg msg) {
            s_logger.debug(toString() + " " + msg.toString());
            m_done = msg.last();
        }

        /** 
         * this receiver is about to terminate.
         */
        protected void done() { }
    }

    // in this file just for convenience, may be moved later
    /**
     * Client simulator.
     */
    static class ClientSimulator extends Thread {

        /**
         * The port to connect to.
         */
        final int m_port;

        /**
         * Constructor.
         * @param port The port to connect to.
         */
        ClientSimulator(int port) {
            m_port = port;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            try {
                final int BUFFER_SIZE = 256;
                SocketChannel sc = SocketChannel.open(new InetSocketAddress(
                    Inet4Address.getLocalHost(), m_port));
                ByteBuffer b = ByteBuffer.allocate(BUFFER_SIZE);
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    b.put((byte) i);
                }
                b.flip();
                sc.write(b);
                s_logger.debug(getClass().getName() + " has written");
                b.flip();
                sc.read(b);
                s_logger.debug(getClass().getName() + " has read");
                System.out.println(b);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}

//Checkstyle: MagicNumber on