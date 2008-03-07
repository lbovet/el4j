/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.plugins.filecollector;

import java.text.MessageFormat;

/**
 * This is the parent exception of all the exceptions in EL4J. It provides a
 * few functionalities, that may or may not be used by child exceptions:
 * <ul>
 * <li>Support to substitute parameters in an exception message.</li>
 * </ul>
 * 
 * Internationalizing exception messages can be performed (external to this
 * class) with the help of the MessageSources of the spring framework.
 * 
 * 
 * This class uses the exception wrapping mechanism of the java.lang.Exception
 * class that was introduced with the JDK 1.4.
 * 
 * <script type="text/javascript">printFileStatus 
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$", 
 *  "$Author$" );
 * </script>
 * 
 * @author Alain Borlet-Hote (ABH), Philipp Oser (POS), Paul E. Sevinç (PES),
 *         Yves Martin (YMA), Martin Zeltner (MZE)
 * 
 * @see java.text.MessageFormat For more information on the format for the
 *      parameter substitution
 */
public class BaseException extends Exception {
    /**
     * Contains either the message of the exception or when message format is
     * used, the messageFormat. MessageFormat substitution is applied if the
     * result of getFormatParameters() is not null. The message field in the
     * Throwable class is not used (rationale: it cannot be set without creating
     * a new object).
     */
    protected String m_message;

    /**
     * The parameters to substitute. In subclasses, you may either use this
     * Object[] to hold the messageFormatParameters or (typically preferred)
     * define your own (typed and named) attributes that you need.
     */
    protected Object[] m_messageFormatParameters;

    /**
     * The constructor with a message in MessageFormat, with parameters, and
     * with a wrapped exception (with all the formal parameters).
     * 
     * @param message
     *            the message of this exception
     * @param parameters
     *            the parameters to substitute in the message
     * @param wrappedException
     *            the exception that is wrapped in this exception
     */
    public BaseException(String message, Object[] parameters,
            Throwable wrappedException) {
        super(wrappedException);
        m_message = message;
        m_messageFormatParameters = parameters;
    }

    /**
     * The constructor with a message in MessageFormat and parameters. No
     * Throwable or Exception is transfered.
     * 
     * @param message
     *            the message of this exception
     * @param parameters
     *            the parameters to substitute in the message
     */
    public BaseException(String message, Object[] parameters) {
        this(message, parameters, null);
    }

    /**
     * Constructor with a message and an exception.
     * 
     * @param message
     *            the message of this exception
     * @param exception
     *            the exception that is wrapped in this exception
     */
    public BaseException(String message, Throwable exception) {
        this(message, null, exception);
    }

    /**
     * Constructor with a message.
     * 
     * @param message
     *            the message of this exception
     */
    public BaseException(String message) {
        this(message, null, null);
    }

    /**
     * Constructor with an exception.
     * 
     * @param exception
     *            the exception that is wrapped in this exception
     */
    public BaseException(Throwable exception) {
        this("BaseException without message", null, exception);
    }

    /**
     * Returns the message pattern for <code>MessageFormat</code> or the
     * message of the exception.
     * 
     * @return the message of this exception (without any parameters substituted
     *         in it).
     */
    public String getFormatString() {
        return m_message;
    }

    /**
     * Sets a new format String. It replaces the default formatString by an
     * internationalized String.
     * 
     * @param formatString
     *            replaces the message
     */
    public void setFormatString(String formatString) {
        m_message = formatString;
    }

    /**
     * Gets parameters defined for the message. This should be overridden if you
     * have defined your own set of attributes.
     * 
     * @return array of arguments for <code>MessageFormat</code>
     */
    public Object[] getFormatParameters() {
        return m_messageFormatParameters;
    }

    /**
     * Get the normal message for the exception.
     * 
     * Please override the <code>Throwable.getLocalizedMessage()</code> to
     * automatically get the message particular for your Locale.
     * 
     * @return the message for the user (if necessary with the substituted
     *         parameters)
     */
    public String getMessage() {
        Object[] formatParameters = getFormatParameters();
        if (formatParameters != null) {
            return MessageFormat.format(m_message, formatParameters);
        } else {
            return m_message;
        }
    }
}