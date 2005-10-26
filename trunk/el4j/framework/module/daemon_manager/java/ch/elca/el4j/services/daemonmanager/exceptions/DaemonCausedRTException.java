/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.services.daemonmanager.exceptions;

import org.springframework.util.StringUtils;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.daemonmanager.Daemon;

/**
 * This class is used to wrap a throwable which was thrown by a daemon.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class DaemonCausedRTException extends BaseRTException {

    /**
     * Is the cause.
     */
    private final Throwable m_cause;

    /**
     * Is the caused daemon.
     */
    private final Daemon m_causedDaemon;

    /**
     * Constructor.
     * 
     * @param causedDaemon Is the daemon which throwed the throwable.
     * @param cause Is the throwable which was thrown by daemon.
     */
    public DaemonCausedRTException(Daemon causedDaemon, Throwable cause) {
        super(createDaemonCausedMessage(cause));
        m_causedDaemon = causedDaemon;
        m_cause = cause;
    }

    /**
     * Creates a message for the daemon caused exception.
     * 
     * @param cause Is the throwable which was thrown by daemon.
     * @return Returns the created message.
     */
    private static String createDaemonCausedMessage(Throwable cause) {
        String message = cause.getMessage();
        if (!StringUtils.hasLength(message)) {
            message = "Daemon caused exception with no message.";
        }
        return message;
    }

    /**
     * @return Returns the cause.
     */
    public Throwable getCause() {
        return m_cause;
    }

    /**
     * @return Returns the causedDaemon.
     */
    public Daemon getCausedDaemon() {
        return m_causedDaemon;
    }
}
