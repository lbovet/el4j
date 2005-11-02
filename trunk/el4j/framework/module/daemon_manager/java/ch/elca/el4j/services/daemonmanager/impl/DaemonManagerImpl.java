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

package ch.elca.el4j.services.daemonmanager.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.daemonmanager.Daemon;
import ch.elca.el4j.services.daemonmanager.DaemonManager;
import ch.elca.el4j.services.daemonmanager.DaemonObserver;
import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonAlreadyStartedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonManagerIsNotProcessingRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonManagerIsProcessingRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonsStillRunningRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.MissingHeartbeatsRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class manages daemons. It has a method <code>process</code> to let the
 * daemons work. There are several methods which only can be accessed if the 
 * daemon manager is processing and several if it is not. The daemon manager
 * can also process if it has no daemons at beginning. Further the daemon 
 * manager is also the daemon observer for each added daemon.
 * 
 * There are some properties which has to be explained:
 * <ul>
 * <li>
 *     The <code>checkPeriod</code> is the period in millis the daemon
 *     manager has to check if all started daemons do working correctly.
 * </li>
 * <li>
 *     The <code>maxMissingHeartbeats</code> is the number of heartbeats which
 *     can be missed for each daemon in series before processing of daemon
 *     manager will be stopped and a <code>MissingHeartbeatsRTException</code> 
 *     will be thrown.
 * </li>
 * <li>
 *     The <code>daemonJoinTimeout</code> is the time in millis the daemon
 *     manager should wait for a daemon to die.
 * </li>
 * <li>
 *     The <code>minDaemonStartupDelay</code> is the minimum time in millis the
 *     daemon manager should wait between starting two daemons.
 * </li> 
 * <li>
 *     The <code>maxDaemonStartupDelay</code> is the maximum time in millis the
 *     daemon manager should wait between starting two daemons.
 * </li>
 * <li>
 *     The <code>cachedInformationMessageTimeout</code> is the time in millis 
 *     the collected information message of method <code>getInformation</code>
 *     will be cached, because the invocation of this method is very time 
 *     consuming.
 * </li>
 * </ul>
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
public class DaemonManagerImpl implements DaemonManager, DaemonObserver {
    /**
     * Default daemon join timeout.
     */
    public static final long DEFAULT_DAEMON_JOIN_TIMEOUT = 10000;
    
    /**
     * Default heartbeat check period.
     */
    public static final long DEFAULT_CHECK_PERIOD = 2000;
    
    /**
     * Default max missing heartbeats.
     */
    public static final int DEFAULT_MAX_MISSING_HEARTBEATS = 5;
    
    /**
     * Default min daemon startup delay.
     */
    public static final long DEFAULT_MIN_DAEMON_STARTUP_DELAY = 100;
    
    /**
     * Default max daemon startup delay.
     */
    public static final long DEFAULT_MAX_DAEMON_STARTUP_DELAY = 500;

    /**
     * This is the time in millis the information message should be cached.
     */
    public static final long DEFAULT_CACHED_INFORMATION_MESSAGE_TIMEOUT = 1000;

    /**
     * Newline string.
     */
    protected static final String NEWLINE = "\n";
    
    /**
     * Number of millis a second has.
     */
    protected static final double NUMBER_OF_MILLIS_PER_SECOND = 1000.0;
    
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DaemonManagerImpl.class);
    
    /**
     * Set of running <code>Daemon</code> classes.
     */
    protected final Set m_runningDaemons = new HashSet();
    
    /**
     * Set of <code>Daemon</code> classes which must be added.
     */
    protected final Set m_daemonsToAdd = new HashSet();

    /**
     * Set of <code>Daemon</code> classes which must be removed.
     */
    protected final Set m_daemonsToRemove = new HashSet();

    /**
     * Set of <code>Daemon</code> classes which has been terminated.
     */
    protected final Set m_terminatedDaemons = new HashSet();

    /**
     * Map of <code>DaemonHeartbeat</code>s which contains the last heartbeat
     * and a counter of missed heartbeats of a daemon. The daemon where the data
     * belongs to is used as key.
     */
    protected final Map m_daemonHeartbeats = new HashMap();
    
    /**
     * Set which contains <code>DaemonCausedRTException</code>s. They are
     * received from observer methods.
     */
    protected final Set m_daemonCausedExceptions = new HashSet();
    
    /**
     * This object is used as lock for daemon manager data.
     */
    protected final Object m_daemonManagerLock = new Object();
    
    /**
     * This object is used as monitor to watch if daemon manager must stop 
     * processing.
     */
    protected final Object m_daemonManagerStopProcessingMonitor = new Object();
    
    /**
     * Time when this daemon manager has been instantiated.
     */
    private final Date m_creationDate = new Date();

    /**
     * Last time when method <code>process</code> was called.
     */
    private Date m_lastProcessCallDate;
    
    /**
     * Flag to indicate if the daemon manager is currently processing.
     */
    private boolean m_processing = false;

    /**
     * Flag to indicate that the manager should stop processing. At beginning
     * it is set to <code>false</code>.
     */
    private boolean m_doStopProcessing = false;
    
    /**
     * Time one should wait for a daemon to die.
     */
    private long m_daemonJoinTimeout = DEFAULT_DAEMON_JOIN_TIMEOUT;
    
    /**
     * Period in milliseconds the daemon manager should check if it has received
     * heartbeats and if daemon caused exceptions occurred.
     */
    private long m_checkPeriod = DEFAULT_CHECK_PERIOD;
    
    /**
     * This is the number of times a daemon can miss to send a heartbeat before
     * a <code>MissingHeartbeatsRTException</code> will be thrown.
     */
    private int m_maxMissingHeartbeats = DEFAULT_MAX_MISSING_HEARTBEATS;
    
    /**
     * This is the minimum delay in milliseconds between the start of two 
     * daemons.
     */
    private long m_minDaemonStartupDelay = DEFAULT_MIN_DAEMON_STARTUP_DELAY;

    /**
     * This is the maximum delay in milliseconds between the start of two 
     * daemons.
     */
    private long m_maxDaemonStartupDelay = DEFAULT_MAX_DAEMON_STARTUP_DELAY;
    
    /**
     * Is the time in millis the generated information message of method 
     * <code>getInformation</code> will be cached.
     */
    private long m_cachedInformationMessageTimeout 
        = DEFAULT_CACHED_INFORMATION_MESSAGE_TIMEOUT;
    
    /**
     * Is the cached information message of this daemon manager.
     */
    private String m_cachedInformationMessage;
    
    /**
     * Is the timestamp when the cached information message was created.
     */
    private long m_cachedInformationMessageTimestamp;
    
    /**
     * Date format object to make timestamps readable.
     */
    private final DateFormat m_dateFormat 
        = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, 
            Locale.GERMANY);
    
    /**
     * {@inheritDoc}
     */
    public final void doStopProcessing() {
        synchronized (m_daemonManagerLock) {
            m_doStopProcessing = true;
        }
        synchronized (m_daemonManagerStopProcessingMonitor) {
            m_daemonManagerStopProcessingMonitor.notifyAll();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void canStartProcessing() {
        synchronized (m_daemonManagerLock) {
            m_doStopProcessing = false;
        }
    }
    
    /**
     * Method to check if this daemon manager should stop processing.
     * 
     * @return Returns <code>true</code> if this daemon manager should stop
     *         processing.
     */
    protected final boolean shouldStopProcessing() {
        synchronized (m_daemonManagerLock) {
            return m_doStopProcessing;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Will call method <code>doReconfigure</code> of each <b>running</b> 
     * daemon.
     */
    public void doReconfigureDaemons() {
        synchronized (m_daemonManagerLock) {
            Iterator it = m_runningDaemons.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                daemon.doReconfigure();
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * It will periodically checked if daemons has produced exceptions and if
     * heartbeats were sent in time. Further planned daemons to be added will be
     * started and planned daemons to be removed will be stopped and removed.
     * If in one daemon an exception has occurred or one daemon has missed to
     * send a heartbeat in time, this method will throw the corresponding
     * exception.
     */
    public synchronized void process() throws MissingHeartbeatsRTException, 
        CollectionOfDaemonCausedRTException,
        DaemonsStillRunningRTException {
        
        if (shouldStopProcessing()) {
            s_logger.info("Processing immediatelly stopped after calling "
                + "method 'process'. Demaons were not started. Please call "
                + "first method 'canStartProcessing' to be able to start "
                + "processing of daemon manager again.");
            return;
        }
        
        RuntimeException exceptionToThrow = null;
        try {
            setProcessing(true);
        
            /**
             * Check if daemons, which should be started, are still alive.
             */
            checkForDaemonZombiesInPendingDaemons();
            
            /**
             * Do inital stop and start work. Check also if a daemon has already
             * threw an exception.
             */
            stopRemovedDaemons();
            startAddedDaemons();
            checkDaemonCausedExceptions();
            
            /**
             * Set next daemon heartbeats check to now plus one check period,
             * that started daemons have time to startup.
             */
            long nextDaemonHeartbeatsCheck 
                = System.currentTimeMillis() + m_checkPeriod;
            
            /**
             * Work while daemon should not stop processing. 
             */
            while (!shouldStopProcessing()) {
                nextDaemonHeartbeatsCheck
                    = sleepUntilNextDaemonHeartbeatsCheck(
                        nextDaemonHeartbeatsCheck);
                checkDaemonCausedExceptions();
                checkDaemonHeartbeats();
            }
        } catch (RuntimeException e) {
            exceptionToThrow = e;
        } finally {
            RuntimeException e = cleanupDaemons();
            if (exceptionToThrow == null) {
                exceptionToThrow = e;
            }
            setProcessing(false);
        }

        if (exceptionToThrow == null) {
            s_logger.info(
                "Processing has been stopped gracefully.");
        } else {
            s_logger.error("Processing has been stopped brutal.", 
                exceptionToThrow);
            throw exceptionToThrow;
        }
    }

    /**
     * Method to cleanup daemons.
     * 
     * @return Returns the exception which should be definitly thrown at the end
     *         of processing.
     */
    protected RuntimeException cleanupDaemons() {
        RuntimeException exceptionToThrow = null;
        try {
            stopRemovedDaemons();
        } catch (RuntimeException e) {
            exceptionToThrow = e;
        }
        try {
            stopAndRecoverDaemons();
        } catch (RuntimeException e) {
            if (exceptionToThrow == null) {
                exceptionToThrow = e;
            }
        }
        return exceptionToThrow;
    }

    /**
     * Method to check if pending daemons do still running.
     * 
     * @throws DaemonsStillRunningRTException
     *             Will be thrown if a daemon in pending set is still alive.
     */
    protected void checkForDaemonZombiesInPendingDaemons()
        throws DaemonsStillRunningRTException {
        synchronized (m_daemonManagerLock) {
            /**
             * Immediately return if there are no zombie daemons.
             */
            if (m_daemonsToAdd.size() <= 0) {
                return;
            }
            
            Set stillRunningDaemons = new HashSet();
            Iterator it = m_daemonsToAdd.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                if (daemon.isDaemonAlive()) {
                    stillRunningDaemons.add(daemon);
                }
            }
            if (stillRunningDaemons.size() > 0) {
                throw new DaemonsStillRunningRTException(stillRunningDaemons);
            }
        }
    }

    /**
     * Method to stop and recover all running daemons.
     */
    protected void stopAndRecoverDaemons() {
        /**
         * Stop and join all daemons which must be recovered.
         */
        if (!stopAndJoinDaemons(m_runningDaemons)) {
            return;
        }

        synchronized (m_daemonManagerLock) {
            Iterator it = m_runningDaemons.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                
                DaemonHeartbeat dh 
                    = (DaemonHeartbeat) m_daemonHeartbeats.get(daemon);
                if (dh == null) {
                    CoreNotificationHelper.notifyMisconfiguration(
                        "Daemon '"
                        + daemon.getIdentification() + "' has no heartbeat"
                        + "object.");
                }
                dh.resetNumberOfMissedHeartbeats();
                
                it.remove();
                if (m_daemonsToAdd.add(daemon)) {
                    s_logger.info("Daemon '" + daemon.getIdentification() 
                        + "' could be recovered successfully.");
                    m_terminatedDaemons.remove(daemon);
                } else {
                    CoreNotificationHelper.notifyMisconfiguration(
                        "Daemon '" 
                        + daemon.getIdentification() + "' could not be moved " 
                        + "from running to pending daemons. It seams that it " 
                        + "is not under control of this daemon manager.");
                }
            }
        }
    }

    /**
     * Method to unregister and remove all daemons which are in set to remove.
     */
    protected void stopRemovedDaemons() {
        /**
         * Stop and join all daemons which must be removed.
         */
        if (!stopAndJoinDaemons(m_daemonsToRemove)) {
            return;
        }

        synchronized (m_daemonManagerLock) {
            /**
             * Cleanup and remove each daemon.
             */
            Iterator it = m_daemonsToRemove.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();

                daemon.unregisterDaemonObserver(this);
                m_daemonHeartbeats.remove(daemon);
                
                it.remove();
                if (m_terminatedDaemons.remove(daemon)) {
                    s_logger.info("Terminated daemon '" 
                        + daemon.getIdentification() 
                        + "' could be removed successfully.");
                } else {
                    s_logger.info("Not terminated daemon '" 
                        + daemon.getIdentification() 
                        + "' could be removed successfully.");
                }
            }
        }
    }

    /**
     * Method to stop and join all daemons of a set.
     * 
     * @param daemons
     *            Is a set of daemons where the work has to do.
     * @return Returns <code>true</code> if the given set contains daemons.
     */
    protected boolean stopAndJoinDaemons(Set daemons) {
        Set daemonsLocally;
        synchronized (m_daemonManagerLock) {
            /**
             * Return immediately if there are no daemons.
             */
            if (CollectionUtils.isEmpty(daemons)) {
                return false;
            } else {
                daemonsLocally = new HashSet(daemons);
            }
        }
    
        /**
         * First tell every to be stopped daemon, 
         * that it should stop its work.
         */
        Iterator it = daemonsLocally.iterator();
        while (it.hasNext()) {
            Daemon daemon = (Daemon) it.next();
            s_logger.info("Trying to stop daemon '" 
                + daemon.getIdentification() + "'.");
            daemon.doStop();
        }
        
        /**
         * Then try to join every daemon.
         */
        it = daemonsLocally.iterator();
        long daemonJoinTimeout 
            = getDaemonJoinTimeout();
        long daemonJoinEndTime 
            = System.currentTimeMillis() + daemonJoinTimeout;
        while (it.hasNext()) {
            Daemon daemon = (Daemon) it.next();
            long now = System.currentTimeMillis();
            if (now < daemonJoinEndTime) {
                long restedJoinTimeout = (daemonJoinEndTime - now);
                if (restedJoinTimeout > daemonJoinTimeout) {
                    restedJoinTimeout = daemonJoinTimeout;
                }
                s_logger.info("Trying to join daemon '" 
                    + daemon.getIdentification() + "'.");
                try {
                    daemon.joinDaemon(getDaemonJoinTimeout());
                } catch (InterruptedException e) {
                    s_logger.debug("Join of daemon '" 
                        + daemon.getIdentification() 
                        + "' has been interrupted.");
                }
            } else {
                s_logger.info("No time left to join daemon '" 
                    + daemon.getIdentification() + "'.");
            }
            
            if (daemon.isDaemonAlive()) {
                s_logger.info("Join of daemon '" 
                    + daemon.getIdentification() 
                    + "' finished but it is still alive. "
                    + "Perhaps the daemon join timeout is set to low.");
            } else {
                s_logger.info("Join of daemon '" 
                    + daemon.getIdentification() 
                    + "' successfully finished.");
            }
        }
        
        return true;
    }

    /**
     * Method to register and start all daemons which are in set to add.
     * 
     * @throws DaemonAlreadyStartedRTException
     *             If daemon is still alive.
     */
    protected void startAddedDaemons() throws DaemonAlreadyStartedRTException {
        synchronized (m_daemonManagerLock) {
            /**
             * Return immediately if there are no daemons to add.
             */
            if (m_daemonsToAdd.size() <= 0) {
                return;
            }
            

            Iterator it = m_daemonsToAdd.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                
                DaemonHeartbeat dh = new DaemonHeartbeat();
                m_daemonHeartbeats.put(daemon, dh);
                daemon.registerDaemonObserver(this);
            }
            
            it = m_daemonsToAdd.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                daemon.startDaemon();

                it.remove();
                if (m_runningDaemons.add(daemon)) {
                    s_logger.info("Daemon '" + daemon.getIdentification() 
                        + "' could be added successfully.");
                } else {
                    CoreNotificationHelper.notifyMisconfiguration(
                        "Daemon '" 
                        + daemon.getIdentification() + "' could not be moved " 
                        + "from pending to running daemons. It seams that it " 
                        + "is not under control of this daemon manager.");
                }
                
                if (it.hasNext()) {
                    sleepRandomDelayForNextStart();
                }
            }
        }
        
    }

    /**
     * Method to delay startup between daemons.
     */
    protected void sleepRandomDelayForNextStart() {
        long sleepTime = m_minDaemonStartupDelay + (long) (
            Math.random() * (m_maxDaemonStartupDelay - m_minDaemonStartupDelay)
            );
        s_logger.debug("Startup will be delayed " + sleepTime + "ms.");
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            s_logger.debug("Delayed startup process interrupted.");
        }
    }

    /**
     * Method to check if any daemon caused exceptions has occurred.
     * 
     * @throws CollectionOfDaemonCausedRTException
     *             Will be thrown if there were daemon caused exceptions.
     */
    protected void checkDaemonCausedExceptions() 
        throws CollectionOfDaemonCausedRTException {
        synchronized (m_daemonManagerLock) {
            if (m_daemonCausedExceptions.size() > 0) {
                Set daemonCausedExceptions 
                    = new HashSet(m_daemonCausedExceptions);
                m_daemonCausedExceptions.clear();
                throw new CollectionOfDaemonCausedRTException(
                    daemonCausedExceptions);
            }
        }
    }

    /**
     * Method to sleep until the next heartbeat check is due.
     * 
     * @param nextDaemonHeartbeatsCheck
     *            Is the time when this method should return.
     * @return Returns the time for the over next daemon heartbeats check. 
     */
    protected long sleepUntilNextDaemonHeartbeatsCheck(
        long nextDaemonHeartbeatsCheck) {
        long now = System.currentTimeMillis();
        
        if (now >= nextDaemonHeartbeatsCheck) {
            s_logger.warn("No time to wait until next daemon heartbeats check! "
                + "It seams that the heartbeat check period is set to low.");
            return now + getCheckPeriod();
        } else {
            if (s_logger.isDebugEnabled()) {
                Date d = new Date(nextDaemonHeartbeatsCheck);
                s_logger.debug("Next daemon heartbeats check at '" 
                    + m_dateFormat.format(d) + "'.");
            }

            boolean interrupted = true;
            while (!shouldStopProcessing() 
                && interrupted
                && now < nextDaemonHeartbeatsCheck) {
                interrupted = false;
                long sleepTime = nextDaemonHeartbeatsCheck - now;
                
                try {
                    synchronized (m_daemonManagerStopProcessingMonitor) {
                        m_daemonManagerStopProcessingMonitor.wait(sleepTime);
                    }
                } catch (InterruptedException e) {
                    s_logger.debug("Interrupted while waiting for next daemon " 
                        + "heartbeats check.");
                    interrupted = true;
                }
                now = System.currentTimeMillis();
            }
            
            return nextDaemonHeartbeatsCheck + getCheckPeriod();
        }
    }

    /**
     * Method to check if the daemon has sent heartbeats.
     * 
     * @throws MissingHeartbeatsRTException
     *             Will be thrown if one or more daemons has not sent heartbeats
     *             for a given number in series.
     */
    protected void checkDaemonHeartbeats() throws MissingHeartbeatsRTException {
        synchronized (m_daemonManagerLock) {
            /**
             * Calculate the oldest acceptable heartbeat time.
             */
            long oldestAcceptableHeartbeat 
                = System.currentTimeMillis() - getCheckPeriod();
            
            Set daemonsWhereHeartbeatsMissedAndCounterTransgressed 
                = new HashSet();
            
            Iterator it = m_runningDaemons.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                /**
                 * Do not check heartbeats of terminated daemons.
                 */
                if (m_terminatedDaemons.contains(daemon)) {
                    continue;
                }
                
                DaemonHeartbeat dh 
                    = (DaemonHeartbeat) m_daemonHeartbeats.get(daemon);
                if (dh.getLastHeartbeat() < oldestAcceptableHeartbeat) {
                    dh.increaseNumberOfMissedHeartbeats();
                } else {
                    dh.resetNumberOfMissedHeartbeats();
                }
                
                if (dh.getNumberOfMissedHeartbeats() > m_maxMissingHeartbeats) {
                    daemonsWhereHeartbeatsMissedAndCounterTransgressed.add(
                        daemon);
                    s_logger.error("Demaon '" + daemon.getIdentification() 
                        + "' has missed more than " + m_maxMissingHeartbeats 
                        + " heartbeat(s).");
                } else if (dh.getNumberOfMissedHeartbeats() > 0) {
                    s_logger.warn("Demaon '" + daemon.getIdentification() 
                        + "' has missed to send a heartbeat. " 
                        + "Total missed heartbeats in series (" 
                        + dh.getNumberOfMissedHeartbeats()
                        + "). Maximal acceptable missed heartbeats in series (" 
                        + m_maxMissingHeartbeats + ").");
                } else {
                    s_logger.debug("Demaon '" + daemon.getIdentification() 
                        + "' has sent heartbeat as expected.");
                }
            }
            
            if (daemonsWhereHeartbeatsMissedAndCounterTransgressed.size() > 0) {
                throw new MissingHeartbeatsRTException(
                    daemonsWhereHeartbeatsMissedAndCounterTransgressed);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * This method will never return <code>null</code>. Instead an empty set 
     * will be returned.
     */
    public Set getDaemons() throws DaemonManagerIsProcessingRTException {
        if (isProcessing()) {
            throw new DaemonManagerIsProcessingRTException(
                "Can not get daemons while daemon manager "
                    + "is processing. Please stop processing first.");
        }

        Set daemons = new HashSet();
        synchronized (m_daemonManagerLock) {
            daemons.addAll(m_daemonsToAdd);
        }
        return daemons;
    }

    /**
     * {@inheritDoc}
     */
    public void setDaemons(Set daemons) 
        throws DaemonManagerIsProcessingRTException {
        if (isProcessing()) {
            throw new DaemonManagerIsProcessingRTException(
                "Daemons can not be replaced while daemon manager "
                    + "is processing. Please stop processing first.");
        }
        
        Set newDaemons = daemons == null ? new HashSet() : daemons;
        
        if (!CollectionUtils.containsOnlyObjectsOfType(
                newDaemons, Daemon.class)) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Set must only contain "
                + "objects which implements the interface '"
                + Daemon.class.getName() + "'.");
        }
        
        Iterator it = newDaemons.iterator();
        while (it.hasNext()) {
            Daemon daemon = (Daemon) it.next();
            if (daemon.isDaemonAlive()) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Given daemons must not be alive.");
            }
        }
        
        synchronized (m_daemonManagerLock) {
            m_daemonsToAdd.clear();
            m_daemonsToAdd.addAll(newDaemons);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean addDaemon(Daemon daemon) 
        throws DaemonAlreadyStartedRTException {
        boolean success = false;
        synchronized (m_daemonManagerLock) {
            if (daemon != null 
                && !daemon.isDaemonAlive()
                && !m_runningDaemons.contains(daemon)
                && !m_daemonsToRemove.contains(daemon)) {
                success = m_daemonsToAdd.add(daemon);
                if (success && isProcessing()) {
                    startAddedDaemons();
                }
            }
        }
        return success;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeDaemon(Daemon daemon) {
        boolean success = false;
        synchronized (m_daemonManagerLock) {
            if (daemon != null) {
                if (m_daemonsToAdd.contains(daemon)) {
                    success = m_daemonsToAdd.remove(daemon);
                } else if (m_runningDaemons.contains(daemon)) {
                    success = m_runningDaemons.remove(daemon);
                    if (success) {
                        m_daemonsToRemove.add(daemon);
                    }
                }
            }
        }
        if (success && isProcessing()) {
            stopRemovedDaemons();
        }
        return success;
    }

    /**
     * {@inheritDoc}
     */
    public Set getRunningDaemons()
        throws DaemonManagerIsNotProcessingRTException {
        if (!isProcessing()) {
            throw new DaemonManagerIsNotProcessingRTException(
                "Can not get running daemons while the daemon manager "
                    + "is not processing.");
        }

        Set daemons = new HashSet();
        synchronized (m_daemonManagerLock) {
            daemons.addAll(m_runningDaemons);
            daemons.addAll(m_daemonsToRemove);
        }
        return daemons;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public Set getPendingDaemons()
//        throws DaemonManagerIsNotProcessingRTException {
//        if (!isProcessing()) {
//            throw new DaemonManagerIsNotProcessingRTException(
//                "Can not get pending daemons while the daemon manager "
//                    + "is not processing.");
//        }
//
//        Set daemons = new HashSet();
//        synchronized (m_daemonManagerLock) {
//            daemons.addAll(m_daemonsToAdd);
//        }
//        return daemons;
//    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfRunningDaemons()
        throws DaemonManagerIsNotProcessingRTException {
        if (!isProcessing()) {
            throw new DaemonManagerIsNotProcessingRTException(
                "Can not get the number of running daemons while the "
                    + "daemon manager is not processing.");
        }

        synchronized (m_daemonManagerLock) {
            return m_runningDaemons.size() + m_daemonsToRemove.size();
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    public int getNumberOfPendingDaemons()
//        throws DaemonManagerIsNotProcessingRTException {
//        if (!isProcessing()) {
//            throw new DaemonManagerIsNotProcessingRTException(
//                "Can not get the number of pending daemons while the "
//                    + "daemon manager is not processing.");
//        }
//
//        synchronized (m_daemonManagerLock) {
//            return m_daemonsToAdd.size();
//        }
//    }

    /**
     * {@inheritDoc}
     * 
     * All available information will be returned in twiki style. Method 
     * <code>getInformation</code> of daemons will be called too. The created
     * string message will be cached for in property 
     * <code>cachedInformationMessageTimeout</code> defined milliseconds. This
     * is made because collecting information means that every monitor of every
     * read property must be owned.
     * 
     */
    public String getInformation() {
        synchronized (m_daemonManagerLock) {
            if (System.currentTimeMillis() 
                - m_cachedInformationMessageTimestamp 
                > m_cachedInformationMessageTimeout) {
                StringBuffer sb = new StringBuffer();
                sb.append("---+ Daemon Manager");
                sb.append(NEWLINE);
                appendGeneralInformation(sb);
                appendConfigurationInformation(sb);
                appendDaemonInformation(sb);
                m_cachedInformationMessage = sb.toString();
                m_cachedInformationMessageTimestamp 
                    = System.currentTimeMillis();
            }
            return m_cachedInformationMessage;
        }
    }

    /**
     * Method to append general information of this daemon manager.
     * 
     * @param sb
     *            Is the string buffer where information must be appended.
     */
    private void appendGeneralInformation(StringBuffer sb) {
        sb.append("---++ General");
        sb.append(NEWLINE);
        sb.append("   * Daemon manager created on ");
        sb.append(m_dateFormat.format(getCreationDate()));
        sb.append(".");
        sb.append(NEWLINE);
        if (getLastProcessCallDate() == null) {
            sb.append("   * Daemon manager has never started processing " 
                + "until now.");
            sb.append(NEWLINE);
        } else {
            sb.append("   * Daemon manager has started processing last "
                + "on ");
            sb.append(m_dateFormat.format(getLastProcessCallDate()));
            sb.append(".");
            sb.append(NEWLINE);

            if (isProcessing()) {
                double uptimeInSeconds 
                    = (System.currentTimeMillis() 
                    - getLastProcessCallDate().getTime()) 
                    / NUMBER_OF_MILLIS_PER_SECOND;
                sb.append("   * Daemon manager is currently processing. ");
                sb.append("It is processing since ");
                sb.append(uptimeInSeconds);
                sb.append("s.");
                sb.append(NEWLINE);
            } else {
                sb.append("   * Daemon manager is currently not processing.");
                sb.append(NEWLINE);
            }
        }
        sb.append(NEWLINE);
    }

    /**
     * Method to append configuration information of this daemon manager.
     * 
     * @param sb
     *            Is the string buffer where information must be appended.
     */
    private void appendConfigurationInformation(StringBuffer sb) {
        sb.append("---++ Configuration");
        sb.append(NEWLINE);
        sb.append("   * Check period is set to ");
        sb.append(getCheckPeriod());
        sb.append("ms.");
        sb.append(NEWLINE);
        sb.append("   * Max missing heartbeats is set to ");
        sb.append(getMaxMissingHeartbeats());
        sb.append(".");
        sb.append(NEWLINE);
        sb.append("   * Daemon join timeout is set to ");
        sb.append(getDaemonJoinTimeout());
        sb.append("ms.");
        sb.append(NEWLINE);
        sb.append("   * Min daemon startup delay is set to ");
        sb.append(getMinDaemonStartupDelay());
        sb.append("ms.");
        sb.append(NEWLINE);
        sb.append("   * Max daemon startup delay is set to ");
        sb.append(getMaxDaemonStartupDelay());
        sb.append("ms.");
        sb.append(NEWLINE);
        sb.append(NEWLINE);
    }

    /**
     * Method to append daemon information.
     * 
     * @param sb
     *            Is the string buffer where information must be appended.
     */
    private void appendDaemonInformation(StringBuffer sb) {
        sb.append("---++ Daemons");
        sb.append(NEWLINE);
        sb.append("---+++ Daemons to add");
        sb.append(NEWLINE);
        appendInformationOfDaemons(sb, m_daemonsToAdd, 
            "   * No daemons which must be added available.",
            "   * ", " daemon(s) which must be added available.");

        sb.append("---+++ Running daemons");
        sb.append(NEWLINE);
        appendInformationOfDaemons(sb, m_runningDaemons, 
            "   * No daemons are running currently.",
            "   * ", " daemon(s) are/is running currently.");
        
        sb.append("---+++ Daemons to remove");
        sb.append(NEWLINE);
        appendInformationOfDaemons(sb, m_daemonsToRemove, 
            "   * No daemons which must be removed available.",
            "   * ", " daemon(s) which must be removed available.");
    }

    /**
     * Method to append daemon information to the given string buffer of daemons
     * available in given set.
     * 
     * @param sb
     *            Is the string buffer where information must be appended.
     * @param daemons
     *            Is the set of daemons.
     * @param noDaemonsMessage
     *            Is the message to print out if the given set is empty.
     * @param hasDaemonsPrefix
     *            Is the message to print out before the size of set, if the
     *            given set is not empty.
     * @param hasDaemonsSuffix
     *            Is the message to print out after the size of set, if the
     *            given set is not empty.
     */
    private void appendInformationOfDaemons(StringBuffer sb, Set daemons, 
        String noDaemonsMessage, String hasDaemonsPrefix, 
        String hasDaemonsSuffix) {
        int size = daemons.size();
        if (size <= 0) {
            sb.append(noDaemonsMessage);
            sb.append(NEWLINE);
            sb.append(NEWLINE);
        } else {
            sb.append(hasDaemonsPrefix);
            sb.append(size);
            sb.append(hasDaemonsSuffix);
            sb.append(NEWLINE);
            Iterator it = daemons.iterator();
            while (it.hasNext()) {
                Daemon daemon = (Daemon) it.next();
                String daemonInformation = daemon.getInformation();
                sb.append(daemonInformation);
                sb.append(NEWLINE);
                sb.append(NEWLINE);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * In this implementation the method calls only method 
     * <code>doRemoveDaemon</code> for the given daemon. Daemon is added to
     * list of terminated daemons.
     */
    public void terminatedNormally(Daemon daemon) {
        s_logger.debug("Demaon '" + daemon.getIdentification() 
            + "' has been terminated normally.");
        synchronized (m_daemonManagerLock) {
            if (m_runningDaemons.contains(daemon)
                || m_daemonsToRemove.contains(daemon)) {
                m_terminatedDaemons.add(daemon);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * In this implementation the given throwable and daemon will be packed in 
     * a <code>DaemonCausedRTException</code>, but only if the throwable is not 
     * an instance of <code>DaemonCausedRTException</code>. This exception will
     * be added to the internal set of daemon caused exceptions, which will be 
     * thrown to the caller of method <code>process</code> on next periodic
     * check. At the end the daemon will be removed by using method 
     * <code>doRemoveDaemon</code>. Daemon is added to list of terminated 
     * daemons.
     */
    public void terminatedExceptionally(Daemon daemon, Throwable t) {
        Reject.ifNull(daemon);
        Reject.ifNull(t);
        
        DaemonCausedRTException dce;
        if (t instanceof DaemonCausedRTException) {
            dce = (DaemonCausedRTException) t;
        } else {
            dce = new DaemonCausedRTException(daemon, t);
        }
        
        synchronized (m_daemonManagerLock) {
            if (m_daemonCausedExceptions.add(dce)) {
                s_logger.debug("Demaon '" + daemon.getIdentification() 
                    + "' has been terminated exceptionally.", t);
            } else {
                s_logger.debug("Demaon '" + daemon.getIdentification() 
                    + "' has been terminated exceptionally. "
                    + "Cause has been logged before.");
            }
        
            if (m_runningDaemons.contains(daemon)
                || m_daemonsToRemove.contains(daemon)) {
                m_terminatedDaemons.add(daemon);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * In this implementation the <code>DaemonCausedRTException</code> will be
     * added to the internal set of daemon caused exceptions, which will be
     * thrown to the caller of method <code>process</code> on next periodic
     * check.
     */
    public void exceptionOccurred(DaemonCausedRTException e) {
        Reject.ifNull(e);
        Daemon daemon = e.getCausedDaemon();
        Throwable t = e.getCause();
        daemon.doStop();
        s_logger.warn("Demaon '" + daemon.getIdentification() 
            + "' will be stopped in cause of an occurred exception.", t);
        synchronized (m_daemonManagerLock) {
            m_daemonCausedExceptions.add(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * In this implementation the current time will be saved and associated with
     * the given daemon.
     */
    public void receiveHeartbeat(Daemon daemon) {
        Reject.ifNull(daemon);
        synchronized (m_daemonManagerLock) {
            if (m_daemonsToRemove.contains(daemon) 
                || m_runningDaemons.contains(daemon)) {
                DaemonHeartbeat dh 
                    = (DaemonHeartbeat) m_daemonHeartbeats.get(daemon);
                dh.setLastHeartbeatToNow();
            } else {
                s_logger.warn("A heartbeat has been sent for unknown daemon of "
                    + "class '" + daemon.getClass().getName()
                    + "' and identification '"
                    + daemon.getIdentification() + "'.");
            }
        }
    }

    /**
     * @return Returns the daemon join timeout.
     */
    public final long getDaemonJoinTimeout() {
        synchronized (m_daemonManagerLock) {
            return m_daemonJoinTimeout;
        }
    }

    /**
     * @param daemonJoinTimeout The daemon join timeout to set.
     */
    public final void setDaemonJoinTimeout(long daemonJoinTimeout) {
        synchronized (m_daemonManagerLock) {
            m_daemonJoinTimeout = daemonJoinTimeout;
        }
    }

    /**
     * @return Returns the check period.
     */
    public final long getCheckPeriod() {
        synchronized (m_daemonManagerLock) {
            return m_checkPeriod;
        }
    }

    /**
     * @param checkPeriod The check period to set.
     */
    public final void setCheckPeriod(long checkPeriod) {
        synchronized (m_daemonManagerLock) {
            m_checkPeriod = checkPeriod;
        }
    }

    /**
     * @return Returns the max daemon startup delay.
     */
    public final long getMaxDaemonStartupDelay() {
        synchronized (m_daemonManagerLock) {
            return m_maxDaemonStartupDelay;
        }
    }

    /**
     * @param maxDaemonStartupDelay The max daemon startup delay to set.
     */
    public final void setMaxDaemonStartupDelay(long maxDaemonStartupDelay) {
        synchronized (m_daemonManagerLock) {
            m_maxDaemonStartupDelay = maxDaemonStartupDelay;
        }
    }

    /**
     * @return Returns the max missing heartbeats.
     */
    public final int getMaxMissingHeartbeats() {
        synchronized (m_daemonManagerLock) {
            return m_maxMissingHeartbeats;
        }
    }

    /**
     * @param maxMissingHeartbeats The max missing heartbeats to set.
     */
    public final void setMaxMissingHeartbeats(int maxMissingHeartbeats) {
        synchronized (m_daemonManagerLock) {
            m_maxMissingHeartbeats = maxMissingHeartbeats;
        }
    }

    /**
     * @return Returns the min daemon startup delay.
     */
    public final long getMinDaemonStartupDelay() {
        synchronized (m_daemonManagerLock) {
            return m_minDaemonStartupDelay;
        }
    }

    /**
     * @param minDaemonStartupDelay The min daemon startup delay to set.
     */
    public final void setMinDaemonStartupDelay(long minDaemonStartupDelay) {
        synchronized (m_daemonManagerLock) {
            m_minDaemonStartupDelay = minDaemonStartupDelay;
        }
    }

    /**
     * @return Returns <code>true</code> if the daemon manager is processing.
     */
    public final boolean isProcessing() {
        synchronized (m_daemonManagerLock) {
            return m_processing;
        }
    }
    
    /**
     * @param processing
     *            Is the processing state to set.
     */
    protected final void setProcessing(boolean processing) {
        synchronized (m_daemonManagerLock) {
            m_processing = processing;
            if (processing) {
                m_lastProcessCallDate = new Date();
            }
        }
    }

    /**
     * @return Returns a date object with the time, this daemon manager was
     *         instantiated.
     */
    public final Date getCreationDate() {
        return new Date(m_creationDate.getTime());
    }

    /**
     * @return Returns a date object with the time, method <code>process</code>
     *         was last called.
     */
    public final Date getLastProcessCallDate() {
        synchronized (m_daemonManagerLock) {
            return m_lastProcessCallDate == null 
                ? null 
                    : new Date(m_lastProcessCallDate.getTime());
        }
    }

    /**
     * @return Returns the cached information message timeout.
     */
    public final long getCachedInformationMessageTimeout() {
        synchronized (m_daemonManagerLock) {
            return m_cachedInformationMessageTimeout;
        }
    }

    /**
     * @param cachedInformationMessageTimeout
     *            The cached information message timeout to set.
     */
    public final void setCachedInformationMessageTimeout(
        long cachedInformationMessageTimeout) {
        synchronized (m_daemonManagerLock) {
            m_cachedInformationMessageTimeout = cachedInformationMessageTimeout;
        }
    }
}
