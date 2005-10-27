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

package ch.elca.el4j.tests.daemonmanager.helpers;

import ch.elca.el4j.services.daemonmanager.Daemon;
import ch.elca.el4j.services.daemonmanager.DaemonObserver;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonCausedRTException;

/**
 * This is a daemon observer for the actor daemon.
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
public class ActorDaemonObserver implements DaemonObserver {
    /**
     * Call counter for method <code>terminatedNormally</code>.
     */
    private int m_callCounterTerminatedNormally = 0;

    /**
     * Call counter for method <code>terminatedExceptionally</code>.
     */
    private int m_callCounterTerminatedExceptionally = 0;

    /**
     * Call counter for method <code>exceptionOccurred</code>.
     */
    private int m_callCounterExceptionOccurred = 0;

    /**
     * Call counter for method <code>receiveHeartbeat</code>.
     */
    private int m_callCounterReceiveHeartbeat = 0;
    
    /**
     * Contains the throwed <code>Throwable</code> of method 
     * <code>terminatedExceptionally</code>.
     */
    private Throwable 
        m_throwedThrowableInTerminatedExceptionally = null;
    
    /**
     * Contains the throwed <code>DaemonCausedRTException</code> of method 
     * <code>exceptionOccurred</code>.
     */
    private DaemonCausedRTException 
        m_throwedDaemonCausedExceptionInExceptionOccurred = null;
    
    /**
     * {@inheritDoc}
     */
    public synchronized void terminatedNormally(Daemon daemon) {
        m_callCounterTerminatedNormally++;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void terminatedExceptionally(
        Daemon daemon, Throwable t) {
        m_callCounterTerminatedExceptionally++;
        m_throwedThrowableInTerminatedExceptionally = t;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void exceptionOccurred(DaemonCausedRTException e) {
        m_callCounterExceptionOccurred++;
        m_throwedDaemonCausedExceptionInExceptionOccurred = e;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void receiveHeartbeat(Daemon daemon) {
        m_callCounterReceiveHeartbeat++;
    }

    /**
     * @return Returns the callCounterExceptionOccurred.
     */
    public final synchronized int getCallCounterExceptionOccurred() {
        return m_callCounterExceptionOccurred;
    }

    /**
     * @return Returns the callCounterReceiveHeartbeat.
     */
    public final synchronized int getCallCounterReceiveHeartbeat() {
        return m_callCounterReceiveHeartbeat;
    }

    /**
     * @return Returns the callCounterTerminatedExceptionally.
     */
    public final synchronized int getCallCounterTerminatedExceptionally() {
        return m_callCounterTerminatedExceptionally;
    }

    /**
     * @return Returns the callCounterTerminatedNormally.
     */
    public final synchronized int getCallCounterTerminatedNormally() {
        return m_callCounterTerminatedNormally;
    }

    /**
     * @return Returns the throwedDaemonCausedExceptionInExceptionOccurred.
     *         After calling this method, the reference will be reset to
     *         <code>null</code>.
     */
    public final synchronized DaemonCausedRTException 
    getThrowedDaemonCausedExceptionInExceptionOccurred() {
        DaemonCausedRTException e 
            = m_throwedDaemonCausedExceptionInExceptionOccurred;
        m_throwedDaemonCausedExceptionInExceptionOccurred = null;
        return e;
    }

    /**
     * @return Returns the throwedThrowableInTerminatedExceptionally. After
     *         calling this method, the reference will be reset to
     *         <code>null</code>.
     */
    public final synchronized Throwable 
    getThrowedThrowableInTerminatedExceptionally() {
        Throwable t = m_throwedThrowableInTerminatedExceptionally;
        m_throwedThrowableInTerminatedExceptionally = null;
        return t;
    }
}
