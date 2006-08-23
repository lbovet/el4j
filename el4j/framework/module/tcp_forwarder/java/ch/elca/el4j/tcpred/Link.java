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
package ch.elca.el4j.tcpred;

import java.nio.channels.SocketChannel;

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
 * @author Adrian Moos (AMS)
 * @author Florian Suess (FLS)
 * @author Alex Mathey (AMA)
 */
class Link {
    
    /** The control interface keeping track of <code>this</code>. */
    final TcpForwarder m_ti;
    /** The input socket channel. */
    final SocketChannel m_in;
    /** The output socket channel. */
    final SocketChannel m_out;
    /** Number of running links. */
    private int m_running = 2;

    /**
     * establishes a forwarding Link between <code>in</code> and
     * <code>out.</code>
     * 
     * @param ti
     *            the control interface keeping track of <code>this</code>
     * @param in
     *            input socket channel
     * @param out
     *            output socket channel
     */
    Link(TcpForwarder ti, SocketChannel in, SocketChannel out) {
        m_ti = ti;
        m_in = in;
        m_out = out;

        ti.m_active.add(this);
        new UnidirectionalForwarder(this, m_in, m_out).start();
        new UnidirectionalForwarder(this, m_out, m_in).start();
    }

    /**
     * immediately aborts <code>this</code>.
     */
    void cut() {
        TcpForwarder.silentClose(m_in);
        TcpForwarder.silentClose(m_out);
        // the forwarders get a concurrentCloseException
    }

    /**
     * called by workers to tell <code>this</code> that they are done.
     */
    synchronized void done() {
        m_running--;
        if (m_running == 0) {
            m_ti.m_active.remove(this);
        }
    }
}