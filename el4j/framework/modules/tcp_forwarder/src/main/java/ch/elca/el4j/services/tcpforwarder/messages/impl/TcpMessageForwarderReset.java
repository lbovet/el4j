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

import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.tcpforwarder.messages.TcpMessageForwarder;

/**
 * Tcp message forwarder to mark a reset of the tcp stream.
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
public class TcpMessageForwarderReset implements TcpMessageForwarder {
    /**
     * Is the only instance of this class.
     */
    public static final TcpMessageForwarderReset INSTANCE 
        = new TcpMessageForwarderReset();

    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(TcpMessageForwarderReset.class);

    /**
     * Hide default constructor.
     */
    protected TcpMessageForwarderReset() { }

    /**
     * {@inheritDoc}
     */
    public void forward(SocketChannel out) {
        try {
            // usually, this generates a FIN as well.
            // unless they try very hard, Java programs can't observe
            // the difference. The JDBC drivers of Oracle and db2 don't.
            out.socket().setSoLinger(true, 0);
            out.socket().shutdownOutput();
        } catch (Exception e) {
            s_logger.error(
                "Error while forwarding connection reset message.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean last() {
        return true;
    }
}
