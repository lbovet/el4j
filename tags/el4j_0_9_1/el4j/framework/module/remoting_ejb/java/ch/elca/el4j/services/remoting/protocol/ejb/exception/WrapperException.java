/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.remoting.protocol.ejb.exception;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utilitary exception to support wrapping of an exception
 * within this exception.<br>
 * This class supports the serialization of stack traces.
 * Even when using JDK &lt; 1.4, the stack trace of the wrapped exception (or of
 * self if the wrapped exception is null) is kept during (de)serialization so
 * that a client program can receive the original exception with the stack trace
 * produced on the server side. When viewed on the client side, an indicative
 * client stack trace is added so that the developer can see where the exception
 * comes from.<br>
 * If there is a wrapped exception, the stack trace that is returned is the one
 * of the wrapped exception, not the one of the current WrapperException
 * instance. For instance if A wraps B (A is of a class inheriting
 * WrapperException), a call to <code>printStackTrace</code> on A will give:<br>
 * <tt>A: message of A<br>
 * Caused by: B: message of B<br>
 * &nbsp;&nbsp;at .... [the stack trace of B].</tt>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *   "$Revision$", "$Date$", "$Author$"
 * );</script>
 *
 * @author Yves Martin (YMA)
 * @see WrapperRTException WrapperRTException gives the same features but as
 * a RuntimeException
 **/
public class WrapperException extends Exception {

    /**
     * The exception that is wrapped in this exception. It may be null
     *  when there is no exception wrapped in this exception.
     */
    protected Throwable m_exception = null;

    /**
     * The StackTrace of the wrapped exception, filled only once the instance
     * is serialized.
     */
    private String m_stackTrace;

    /**
     * Constructor with this exception message and a wrapped exception.<br>
     * If a wrapped exception is provided, the stacktrace of this wrapped
     * exception is kept in an internal field so that it can be serialized and
     * deserialized without losing the stacktrace (important for Java &lt; 1.4).
     * <br>
     * If no wrapped exception is provided, the own stacktrace of the current
     * instance is saved in an internal field.
     *
     * @param message a <code>String</code> value
     * @param wrappedException a <code>Throwable</code> value
     */
    public WrapperException(String message, Throwable wrappedException) {
        super(message);
        m_exception = wrappedException;
        m_stackTrace = null;
    }

    /**
     * Constructor with an exception to wrap.
     *
     * @param wrappedException the exception that is wrapped in this exception
     */
    public WrapperException(Throwable wrappedException) {
        this("", wrappedException);
    }

    /**
     * Gets the exception wrapped in this exception.
     *
     * @return the wrapped exception.
     */
    public Throwable getWrappedException() {
        return m_exception;
    }

    /**
     * {@inheritDoc}
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * {@inheritDoc}
     */
    public void printStackTrace(PrintStream ps) {
        synchronized (ps) {
            if (m_stackTrace != null) {
                //we have already been serialized and deserialized
                //whether we have a wrapped exception or not, it's the same
                ps.print(m_stackTrace);
            } else {
                if (m_exception != null) {
                    // we have never been deserialized, so the wrapped exception
                    // still has its stacktrace
                    ps.println(this);
                    ps.print("Caused by: ");
                    m_exception.printStackTrace(ps);
                } else {
                    //no wrapped exception, call super
                    super.printStackTrace(ps);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void printStackTrace(PrintWriter pw) {
        synchronized (pw) {
            if (m_stackTrace != null) {
                //we have already been serialized and deserialized
                //whether we have a wrapped exception or not, it's the same
                pw.print(m_stackTrace);
            } else {
                if (m_exception != null) {
                    // we have never been deserialized, so the wrapped exception
                    // still has its stacktrace
                    pw.println(this);
                    pw.print("Caused by: ");
                    m_exception.printStackTrace(pw);
                } else {
                    //no wrapped exception, call super
                    super.printStackTrace(pw);
                }
            }
        }
    }

    /**
     * Fill the internal <code>m_stackTrace</code> field when serializing
     * the instance so that traces don't get lost (JDK<1.4).
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        // This method is exactly the same in WrapperException and
        // WrapperRTException.  If you modify one, don't forget to check the
        // other.

        if (m_stackTrace == null) { //only if not already serialized
            // save the stacktrace
            m_stackTrace = Util.exceptionStackTrace(this);
        }

        // now that the stack trace field is set, save the fields
        s.writeObject(m_stackTrace);
        byte[] ba = null;
        if (m_exception != null) {
            ba = SerializationTool.encodeObject(m_exception);
            //ba is still null if the serialization failed
        }
        // we don't write the object itself because its deserialization
        // may fail. And if it fails, the InpuStream is left in an
        // indeterminate state so that we cannot recover
        // so, instead we write an array of bytes that itself contains
        // the serialized object. Reading this array of bytes should
        // never fail, and deserializing the object from the byte array
        // won't corrupt the inputstream if it fails.
        s.writeObject(ba);
    }

    /**
     * Adds the current stack trace to the know stack trace to indicate
     * where the exception has been deserialized and thus where the client
     * call came from.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        // This method is exactly the same in WrapperException and
        // WrapperRTException.  If you modify one, don't forget to check the
        // other.

        m_stackTrace = (String) in.readObject();
        m_exception = null;

        byte[] ba = (byte[]) in.readObject();
        if(ba != null) {
            // we have a wrapped exception
            m_exception = (Throwable) SerializationTool.decodeObject(ba);
            //m_exception is still null if the deserialization failed
        }

        // now add the current stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        this.fillInStackTrace();
        super.printStackTrace(pw);
        pw.flush();
        m_stackTrace += ("Indicative client-side stack trace:\n"
                + sw.toString());
    }
}
