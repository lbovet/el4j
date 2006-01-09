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

import java.text.MessageFormat;

/**
 * This is the parent exception of all the unchecked exceptions in EL4J.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author  Alain Borlet-Hote (ABH)
 * @author  Philipp Oser (POS)
 * @author  Paul E. Sevin&ccedil; (PES)
 */
public class BaseRTException extends WrapperRTException
    implements InternationalizableException {

    /**
     * Contains either the message of the exception or
     *  when message format is used, the messageFormat.
     * The message field in the Throwable class is not used (rationale:
     *  it cannot be set without creating a new object).
     */
    protected String m_message;

    /**
     * The parameters to substitute.
     */
    protected Object[] m_messageFormatParameters;

    /**
      * The constructor with a message in MessageFormat, with parameters,
      *  and with a wrapped exception (with all the formal parameters).
      *
      * @param message the message of this exception
      * @param parameters the parameters to substitute in the message
      * @param wrappedException the exception that is wrapped in this exception
      */
    protected BaseRTException(String message,
                              Object[] parameters,
                              Throwable wrappedException) {
        super(wrappedException);
        m_message = message;
        m_messageFormatParameters = parameters;
    }

    /**
      * The constructor with a message in MessageFormat and with parameters.
      *
      * @param message the message of this exception
      * @param parameters the parameters to substitute in the message
      */
    protected BaseRTException(String message,
                              Object[] parameters) {
        this(message, parameters, null);
    }

    /**
     * The constructor with exception.
     *
     * @param exception the exception that is wrapped in this exception
     */
    public BaseRTException(Throwable exception) {
        this("RuntimeException from EL4J", null, exception);
    }

    // implementation of InternationalizableException

    public String getFormatString() {
        return m_message;
    }

    public void setFormatString(String formatString) {
        m_message = formatString;
    }

    public Object[] getFormatParameters() {
        return m_messageFormatParameters;
    }

    /**
     * @return the message to return to the user (with the
     *   parameters substitued when necessary)
     */
    public String getMessage() {
        if (m_messageFormatParameters != null) {
            return MessageFormat.format(m_message,
                                        m_messageFormatParameters);
        } else {
            return m_message;
        }
    }
}
