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

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class represents a TCP message and provides the capability to 
 * forward it.
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
abstract class AbstractTcpMsg {

    /** 
     * Represents a message initiating a standard connection close. 
     * This is a singleton since it has no state. 
     */
    static final AbstractTcpMsg FIN = new AbstractTcpMsg() {
        /**
         * {@inheritDoc}
         */
        void forward(SocketChannel out) {
            try {
                out.socket().shutdownOutput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        boolean last() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return "TcpMsg.FIN";
        }
    };
    
    /** 
     * represents a message initiating a forced connection close 
     * (i.e. a TCP Reset).
     * This is a singleton since it has no state. 
     */
    static final AbstractTcpMsg RST = new AbstractTcpMsg() {
        /**
         * {@inheritDoc}
         */
        void forward(SocketChannel out) {
            try {
                // usually, this generates a FIN as well.
                // unless they try very hard, Java programs can't observe
                // the difference. The JDBC drivers of Oracle and db2 don't.
                out.socket().setSoLinger(true, 0);
                out.socket().shutdownOutput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        boolean last() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return "TcpMsg.RST";
        }
    };
    
    /**
     * forwards the message over <code>out</code>.
     * 
     * @param out
     *            the socket to forward to
     */
    abstract void forward(SocketChannel out);

    /** 
     * @return true iff this message is the last message in the stream
     */
    abstract boolean last();

    /**
     * provides a meaningful textual description for tracing.
     * 
     * @return A textual represenation of the TCP message
     */
    public abstract String toString();

    /** 
     * Represents some bytes of data.
     */
    static class Data extends AbstractTcpMsg {
        
        /**
         * Holds the bytes contained in this message.
         */
        private ByteBuffer m_buffer;

        /** 
         * @param b holds the bytes contained in this message, i.e. b.get()
         * returns the first byte of data
         */
        Data(ByteBuffer b) {
            m_buffer = b;
        }

        /**
         * {@inheritDoc}
         */
        void forward(SocketChannel out) {
            try {
                out.write(m_buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        boolean last() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return "TcpMsg.Data: " + m_buffer.limit() + " bytes";
        }
    }

}
