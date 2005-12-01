/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Exception that permits to wrap exceptions. Without it the stack trace would
 * be lost when there are several JVM involved because of serialization
 * (JDK<1.4). The wrapped exception is transmitted in a <code>byte[]</code>.
 * Unknown classes on client side can be detected, since the wrapped exception
 * is deserialized in a second step. In cases of failed deserialization the
 * stack trace stored as string will be printed.
 * <p>
 * This exception is only thought as a wrapper exception. This exception
 * should never directly thrown to the user.
 * <p>
 * This exception and its subclasses can be used over IIOP. Exception name,
 * stack trace and message are available as strings. The Java serialized
 * exception in the <code>byte[]</code> can be ignored.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *   "$Revision$", "$Date$", "$Author$"
 * );</script>
 **/
public class ExceptionWrapperException extends Exception {
    
    /*
     * Some features of the WrapperException such as printStackTrace method
     * overwriting could also be done here if this is required. This
     * exception is only a wrapper, thus it should never be thrown to the
     * client. If so, it must be a severe problem in EL4J. Thus additional
     * features of WrapperException are not present here.
     */

    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            ExceptionWrapperException.class);
    
    /**
     * Field holding the wrapped exception, declared transient to prevent
     * potential deserialization problems.
     */
    private static final Field s_messageField;

    /**
     * The class name of the wrapped exception.
     */
    private String m_exceptionClassName;
    
    /**
     * The message of the wrapped exception.
     */
    private String m_exceptionMessage;
    
    /**
     * The full stack trace of the wrapped exception.
     */
    private String m_wrappedStackTrace;
    
    /**
     * Is a runtime exception wrapped?
     */
    private boolean m_isRuntimeException;
    
    /**
     * Is an error wrapped?
     */
    private boolean m_isError;

    /**
     * The wrapped exception.
     */
    private transient Throwable m_wrappedException;
    
    /**
     * The serialized version of the original exception. This allows to
     * prevent ClassNotFoundExceptions on the client side if the class of the
     * original exception is not available on the client side.
     */
    private byte[] m_serializedWrappedException;
    
    
    /**
     * Static constructor to get the Field that maps the detailMessage field
     * of Throwable.
     */
    static
    {
        Field field = null;
        try {
            Class throwable = Throwable.class;
            field = throwable.getDeclaredField("detailMessage");
            field.setAccessible(true);
        } catch (SecurityException se) {
            s_logger.debug("<clinit>", se);
        } catch (NoSuchFieldException nsfe) {
            throw new Error(nsfe.toString());
        }
        s_messageField = field;
    }

    /**
     * Constructor that wraps the originalException.
     *
     */
    protected ExceptionWrapperException(String msg, Throwable t) {
        super(msg);
//        Assert.assertTrue(t != null, "originalException is null");

        m_wrappedException = t;
        m_exceptionMessage = t.getMessage();
        m_isRuntimeException = t instanceof RuntimeException;
        m_isError = t instanceof Error;
    }

    /**
     * Constructor that snaps the exception stack trace and serializes
     * it for use on another site.
     *
     */
    public ExceptionWrapperException(Throwable t) {
        this("Wrapped Throwable: " + t.getMessage(), t);
    }
    
    /**
     * Returns the class name of the wrapped exception.
     * This method is not useful in EL4J, but with IIOP.
     * @return the class name of the wrapped exception
     */
    public String getExceptionClassName() {
        return m_exceptionClassName;
    }

    /**
     * Returns the message of the wrapped exception.
     * This method is not useful in EL4J, but with IIOP.
     * @return the message of the wrapped exception
     */
    public String getExceptionMessage() {
        return m_exceptionMessage;
    }

    /**
     * Is the wrapped exception an Error?
     * This method is not useful in EL4J, but with IIOP.
     * @return <code>true</code> if the wrapped exception is an
     * <code>Error</code>, otherwise <code>false</code>.
     */
    public boolean isError() {
        return m_isError;
    }

    /**
     * Is the wrapped exception a runtime exception?
     * This method is not useful in EL4J, but with IIOP.
     * @return <code>true</code> if the wrapped exception is an
     * runtime exception, otherwise <code>false</code>.
     */
    public boolean isRuntimeException() {
        return m_isRuntimeException;
    }


    /**
     * Gets the original stack trace (i.e. the stack trace of the wrapped
     * exception).
     *
     * @return the original stack trace
     */
    public String getOriginalStackTrace() {
        if (m_wrappedStackTrace == null) {
            //we're here if called before any serialization
            m_wrappedStackTrace = Util.exceptionStackTrace(m_wrappedException);
        }
        return m_wrappedStackTrace;
    }

    /**
     * Gets the wrapped exception.<br>
     * This method overrides the wrapper exception behaviour. The exception
     * is deserialized if it is possible. If for any reason, the
     * exception cannot be deserialized (e.g. class not available), the
     * exception returned is a ch.elca.el4j.services.orb.RemoteRTException
     * containing the stack trace.
     *
     * @return the wrapped exception
     */
    public Throwable getWrappedException() {
        if (m_wrappedException != null) {
            // no serialization/deserialization has happened: we can return
            // the reference to the wrapped exception directly
            // We spare a serialization/deserialization :-)
            // Trace.debug(ExceptionWrapperException.class,
            // "getWrappedException", "Returned original exception "
            // + "without serialization :-))");

            return m_wrappedException;
        }

        if (m_serializedWrappedException != null) {
            m_wrappedException = (Throwable) SerializationTool.decodeObject(
                new ByteArrayInputStream(m_serializedWrappedException),
                 this);

            // restore the original stack trace
            if (m_wrappedException != null) {
                // the deserialization was OK
                if (m_wrappedException instanceof WrapperRTException
                    || m_wrappedException instanceof WrapperException) {
                    //those 2 classes handle their stack trace themselves...

                    return m_wrappedException;
                }
                //otherwise, put the saved stacktrace in the detailMessage
                //field (private member of Throwable) which is used when
                //calling printStacktrace
                if (s_messageField != null) {
                    try {
                        s_messageField.set(m_wrappedException,
                            m_wrappedStackTrace
                            + "\nIndicative client-side stack trace:");
                    } catch (IllegalAccessException iae) {
                        s_logger.debug("getWrappedException", iae);
                        // we can't do much more...
                    }
                    // now add the current stack trace so that one can read
                    // what the stack trace was on the client side
                    m_wrappedException.fillInStackTrace();
                }
            }
        }
//        else {
//            //m_serializedWrappedException==null
//            //this case is possible if the wrapped exception was not
//            //serializable. Hopefully we kept it stacktrace anyway...
//        }

        if (m_wrappedException == null) {
            m_wrappedException = new RemoteRTException(m_exceptionClassName,
                m_wrappedStackTrace);
        }
        return m_wrappedException;
    }

    /**
     * Fill the internal <code>m_stackTrace</code> field when serializing
     * the instance so that traces don't get lost (JDK<1.4).
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        if (m_wrappedStackTrace == null) {
            m_wrappedStackTrace = Util.exceptionStackTrace(m_wrappedException);
        }

        //now serialize the wrapped exception
        if (m_serializedWrappedException == null) {
            m_serializedWrappedException
                = SerializationTool.encodeObject(m_wrappedException);
            m_exceptionClassName = m_wrappedException.getClass().getName();
        }

        // now we can call the normal serialization process
        s.defaultWriteObject();
    }
}

