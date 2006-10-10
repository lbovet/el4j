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

import java.net.Socket;

import org.springframework.util.Assert;

/**
 * This class represents an established, forwarding connection.
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
public class Link {
    /**
     * The control interface keeping track of <code>this</code>.
     */
    protected final TcpForwarder m_tcpForwarder;
    
    /**
     * The listen/input socket.
     */
    protected final Socket m_listenSocket;
    
    /**
     * The target/output socket.
     */
    protected final Socket m_targetSocket;
    
    /**
     * Is the request forwarder thread (listenSocket -> targetSocket).
     */
    protected final UnidirectionalForwarderThread m_requestForwarderThread;
    
    /**
     * Is the response forwarder thread (targetSocket -> listenSocket).
     */
    protected final UnidirectionalForwarderThread m_responseForwarderThread;

    /**
     * Establishes a forwarding link between <code>listenSocket</code> and
     * <code>targetSocket</code>.
     * 
     * @param tcpForwarder
     *            The control interface keeping track of <code>this</code>.
     * @param listenSocket Is the listen socket.
     * @param targetSocket Is the target socket.
     */
    public Link(TcpForwarder tcpForwarder, Socket listenSocket, 
        Socket targetSocket) {
        Assert.notNull(tcpForwarder);
        Assert.notNull(listenSocket);
        Assert.notNull(targetSocket);
        
        m_tcpForwarder = tcpForwarder;
        m_listenSocket = listenSocket;
        m_targetSocket = targetSocket;

        m_requestForwarderThread = new UnidirectionalForwarderThread(
            this, m_listenSocket, m_targetSocket);
        m_responseForwarderThread = new UnidirectionalForwarderThread(
            this, m_targetSocket, m_listenSocket);
        
        m_tcpForwarder.m_activeLinks.add(this);
        m_requestForwarderThread.start();
        m_responseForwarderThread.start();
    }

    /**
     * Immediately aborts <code>this</code>.
     */
    protected void cut() {
        m_requestForwarderThread.halt();
        m_responseForwarderThread.halt();
    }

    /**
     * Called by workers to tell <code>this</code> that they are done.
     */
    protected synchronized void done() {
        if (m_requestForwarderThread.isDone() 
            && m_responseForwarderThread.isDone()) {
            m_tcpForwarder.m_activeLinks.remove(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Link; in=[" + m_listenSocket + "], out=[" 
            + m_targetSocket + "]";
    }
}