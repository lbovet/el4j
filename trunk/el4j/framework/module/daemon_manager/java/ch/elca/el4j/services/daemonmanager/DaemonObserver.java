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

package ch.elca.el4j.services.daemonmanager;

import ch.elca.el4j.services.daemonmanager.exceptions.DaemonCausedRTException;


/**
 * This interface is used to observe a daemon.
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
public interface DaemonObserver {

    /**
     * This method will be called if the given daemon has been terminated
     * without any problems. This is the counterpiece of method
     * <code>terminatedExceptionally</code>.
     * 
     * @param daemon
     *            Is the daemon which has been terminated.
     */
    void terminatedNormally(Daemon daemon);

    /**
     * This method will be called if the given daemon has received a throwable
     * while stopping the it. This is the counterpiece of method
     * <code>terminatedNormally</code>.
     * 
     * @param daemon
     *            Is the daemon which has been terminated.
     * @param t
     *            Is the throwable which was thrown.
     */
    void terminatedExceptionally(Daemon daemon, Throwable t);

    /**
     * This method will be called if the given daemon has received a throwable
     * while do the work.
     * 
     * @param e
     *            Contains the throwed throwable and the caused daemon.
     */
    void exceptionOccurred(DaemonCausedRTException e);

    /**
     * This method will be called if the daemon has sent a heartbeat, to show
     * that it is still alive.
     * 
     * @param daemon
     *            Is the daemon where a heartbeat has been sent.
     */
    void receiveHeartbeat(Daemon daemon);
}
