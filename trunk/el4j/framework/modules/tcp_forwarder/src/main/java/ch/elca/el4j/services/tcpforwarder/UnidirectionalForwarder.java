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

import java.nio.channels.SocketChannel;

/**
 * This class represents a receiver forwarding everything it hears.
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
class UnidirectionalForwarder extends TcpForwarder.Receiver {
    /** The socket to forward to.*/
    SocketChannel m_out;
    /** Our controller. */
    Link m_link;

    /**
     * @param link
     *            our controller
     * @param in
     *            the socket to read from
     * @param out
     *            the socket to forward to
     */
    UnidirectionalForwarder(Link link, SocketChannel in, SocketChannel out) {
        super(in);
        m_link = link;
        m_out = out;
    }

    /**
     * {@inheritDoc}
     */
    protected void process(AbstractTcpMsg msg) {
        super.process(msg);
        msg.forward(m_out);
    }

    /**
     * {@inheritDoc}
     */
    protected void done() {
        m_link.done();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getClass().getName() + "[" + m_in.socket().getLocalPort() + ","
            + m_out.socket().getLocalPort() + "]";
    }
}
