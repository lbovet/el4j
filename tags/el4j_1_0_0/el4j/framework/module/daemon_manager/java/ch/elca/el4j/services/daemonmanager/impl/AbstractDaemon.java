/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.services.daemonmanager.impl;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.daemonmanager.Daemon;
import ch.elca.el4j.services.daemonmanager.DaemonObserver;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonAlreadyStartedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonNotStartedRTException;
import ch.elca.el4j.services.monitoring.notification.DaemonManagerNotificationHelper;
import ch.elca.el4j.util.codingsupport.NumberUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class is the standard implementation of a daemon. Below you can find 
 * job execution behaviour of properties <code>sleepTimeAfterJob</code> and
 * <code>minPeriodicity</code>:
 * 
 * <table border='1'>
 * <tr>
 *   <th>sleepTimeAfterJob</th>
 *   <th>minPeriodicity</th>
 *   <th>Effect</th>
 * </tr>
 * <tr>
 *   <td>0</td>
 *   <td>0</td>
 *   <td rowspan='3'>
 *     Jobs will be executed without any sleeping in-between.
 *   </td>
 * </tr>
 * <tr>
 *   <td>Long.MAX_VALUE</td>
 *   <td>0</td>
 * </tr>
 * <tr>
 *   <td>0</td>
 *   <td>Long.MAX_VALUE</td>
 * </tr>
 * <tr>
 *   <td><code>x</code></td>
 *   <td>Long.MAX_VALUE</td>
 *   <td>
 *     Jobs will be executed again after <code>x</code> milliseconds. The
 *     time <code>x</code> is measured from the end of the job until the 
 *     new start of the job.
 *   </td>
 * </tr>
 * <tr>
 *   <td>Long.MAX_VALUE</td>
 *   <td><code>x</code></td>
 *   <td>
 *     <ul>
 *       <li>
 *         If a job lasts less than <code>x</code> milliseconds, the next 
 *         job will be executed after <code>x</code> milliseconds measured 
 *         from the beginning of the former job 
 *         (jobs executed periodically).
 *       </li>
 *       <li>
 *         If a job lasts more than <code>x</code> milliseconds, it will be
 *         executed immediately after the former job.
 *       </li>
 *     </ul>
 *   </td>
 * </tr>
 * <tr>
 *   <td><code>y</code></td>
 *   <td><code>x</code></td>
 *   <td>
 *     If the difference between <code>x</code> and the due time of the former 
 *     job is greater than <code>y</code>, <code>y</code> milliseconds will be 
 *     lept. Otherwise the job will be executed every <code>x</code> 
 *     milliseconds.
 *   </td>
 * </tr>
 * <tr>
 *   <td>Long.MAX_VALUE</td>
 *   <td>Long.MAX_VALUE</td>
 *   <td>
 *     The job of the daemon will be executed only once.
 *     <b>This is the default.</b>
 *   </td>
 * </tr>
 * </table>
 * 
 * For property <code>order</code> see {@link org.springframework.core.Ordered}.
 * It is used for ordered starting and stopping of daemons. Lowest order number
 * of all daemon means that this daemon will be started as first and stopped as 
 * last.
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
public abstract class AbstractDaemon 
    implements Daemon, Runnable, Serializable {
    
    /**
     * Is the max allowed sleep time after job has been done.
     */
    public static final long MAX_ALLOWED_SLEEP_TIME_AFTER_JOB = Long.MAX_VALUE;
    
    /**
     * Is the min allowed sleep time after job has been done.
     */
    public static final long MIN_ALLOWED_SLEEP_TIME_AFTER_JOB = 0;

    /**
     * Default sleep time after job has been done.
     */
    public static final long DEFAULT_SLEEP_TIME_AFTER_JOB = Long.MAX_VALUE;

    /**
     * Is the max allowed periodicity.
     */
    public static final long MAX_ALLOWED_PERIODICITY = Long.MAX_VALUE;
    
    /**
     * Is the min allowed periodicity.
     */
    public static final long MIN_ALLOWED_PERIODICITY = 0;

    /**
     * Default periodicity.
     */
    public static final long DEFAULT_PERIODICITY = Long.MAX_VALUE;

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
        = LogFactory.getLog(AbstractDaemon.class);
    
    /**
     * This object is used as lock for daemon data.
     */
    protected final Object m_daemonLock = new Object(); 

    /**
     * Special lock object for daemon thread.
     */
    protected final Object m_daemonThreadLock = new Object();

    /**
     * Flag to indicate if a daemon should stop working. On beginning it is set
     * to <code>false</code>.
     */
    private boolean m_doStop = false;
    
    /**
     * Flag to indicate if a daemon should read once again its configuration. On
     * beginning it is set to <code>false</code>.
     */
    private boolean m_doReconfigure = false;
    
    /**
     * Flag to indicate if this daemon is running.
     */
    private boolean m_daemonRunning = false;
    
    /**
     * Is the thread where this daemon is running.
     */
    private Thread m_daemonThread;
    
    /**
     * The identification string of this daemon.
     */
    private String m_identification;
    
    /**
     * Is the observer for the daemon. In this implementation a daemon can only 
     * have one observer.
     */
    private DaemonObserver m_daemonObserver;
    
    /**
     * This is the time in milliseconds the daemon will wait after he has done
     * his job to start it again. This property collaborates with property
     * <code>minPeriodicity</code> where the property
     * <code>minPeriodicity</code> has more weight.
     */
    private long m_sleepTimeAfterJob = DEFAULT_SLEEP_TIME_AFTER_JOB;
    
    /**
     * This is the minimal periodicity in milliseconds to execute the job. This
     * property collaborates with property <code>sleepTimeAfterJob</code> where
     * the property <code>minPeriodicity</code> has more weight. 
     * 
     */
    private long m_minPeriodicity = DEFAULT_PERIODICITY;
    
    /**
     * Is the order the daemon is started and stopped. Low value means daemon 
     * started early and stopped lately. By default it is set to 
     * <code>Integer.MAX_VALUE</code>, so it is like non-ordered.
     */
    private int m_order = Integer.MAX_VALUE;

    /**
     * Time when this daemon has been instantiated.
     */
    private final Date m_creationDate = new Date();

    /**
     * Last time when daemon was started.
     */
    private Date m_lastStartDate;

    /**
     * Date format object to make timestamps readable.
     */
    private final DateFormat m_dateFormat 
        = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, 
            Locale.GERMANY);

    /**
     * This is a reference to the throwed runtime exception in method 
     * <code>run</code>.
     */
    private RuntimeException m_throwedRuntimeExceptionInRunMethod = null; 
    
    /**
     * {@inheritDoc}
     */
    public final void doStop() {
        synchronized (m_daemonLock) {
            m_doStop = true;
        }
    }
    
    /**
     * @return Returns <code>true</code> if the daemon should be stopped.
     */
    protected final boolean shouldDaemonStop() {
        synchronized (m_daemonLock) {
            return m_doStop;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void doReconfigure() {
        synchronized (m_daemonLock) {
            m_doReconfigure = true;
        }
    }
    
    /**
     * @return Returns <code>true</code> if the daemon should be reconfigured.
     */
    protected final boolean shouldDaemonReconfigure() {
        synchronized (m_daemonLock) {
            return m_doReconfigure;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * All available information will be returned in twiki style.
     */
    public String getInformation() {
        synchronized (m_daemonLock) {
            StringBuffer sb = new StringBuffer();
            sb.append("---# Daemon '");
            sb.append(getIdentification());
            sb.append("'");
            sb.append(NEWLINE);
            
            appendGeneralInformation(sb);
            appendConfigurationInformation(sb);
            
            return sb.toString();
        }
    }

    /**
     * Method to append general daemon information.
     * 
     * @param sb
     *            Is the string buffer where to append information.
     */
    private void appendGeneralInformation(StringBuffer sb) {
        sb.append("---## General");
        sb.append(NEWLINE);
        sb.append("   * Daemon created on ");
        sb.append(m_dateFormat.format(getCreationDate()));
        sb.append(".");
        sb.append(NEWLINE);
        if (getLastStartDate() == null) {
            sb.append("   * Daemon has never been started until now.");
            sb.append(NEWLINE);
        } else {
            sb.append("   * Daemon has been started last on ");
            sb.append(m_dateFormat.format(getLastStartDate()));
            sb.append(".");
            sb.append(NEWLINE);
            
            if (isDaemonRunning()) {
                double uptimeInSeconds 
                    = (System.currentTimeMillis() 
                    - getLastStartDate().getTime()) 
                    / NUMBER_OF_MILLIS_PER_SECOND;
                sb.append("   * Daemon is currently working. ");
                sb.append("It is working since ");
                sb.append(uptimeInSeconds);
                sb.append("s.");
                sb.append(NEWLINE);
            } else {
                sb.append("   * Daemon is currently not working.");
                sb.append(NEWLINE);
            }
            sb.append(NEWLINE);
        }
    }

    /**
     * Method to append daemon configuration information.
     * 
     * @param sb
     *            Is the string buffer where to append information.
     */
    private void appendConfigurationInformation(StringBuffer sb) {
        sb.append("---## Configuration");
        sb.append(NEWLINE);
        sb.append("   * Min periodicity is set to ");
        sb.append(getMinPeriodicity());
        sb.append("ms.");
        sb.append(NEWLINE);
        sb.append("   * Sleep time after job is set to ");
        sb.append(getSleepTimeAfterJob());
        sb.append("ms.");
        sb.append(NEWLINE);
        
        int order = getOrder();
        if (order < Integer.MAX_VALUE) {
            sb.append("   * Order is set to ");
            sb.append(getOrder());
            sb.append(".");
        } else {
            sb.append("   * Not ordered.");
        }
        sb.append(NEWLINE);
        
        sb.append(NEWLINE);
    }

    /**
     * {@inheritDoc}
     */
    public final String getIdentification() {
        return m_identification;
    }
    
    /**
     * Method to set the identification of the daemon.
     * 
     * @param identification Is the identification string to set.
     */
    public final void setIdentification(String identification) {
        m_identification = identification;
    }

    /**
     * {@inheritDoc}
     * 
     * In this implementation only one daemon observer can be registered.
     */
    public void registerDaemonObserver(DaemonObserver observer) {
        m_daemonObserver = observer;
    }

    /**
     * {@inheritDoc}
     */
    public void unregisterDaemonObserver(DaemonObserver observer) {
        if (m_daemonObserver == observer) {
            m_daemonObserver = null;
        }
    }

    /**
     * @return Returns the minPeriodicity.
     */
    public final long getMinPeriodicity() {
        synchronized (m_daemonLock) {
            return m_minPeriodicity;
        }
    }

    /**
     * @param minPeriodicity
     *            The minPeriodicity to set.
     */
    public final void setMinPeriodicity(long minPeriodicity) {
        synchronized (m_daemonLock) {
            m_minPeriodicity = minPeriodicity;
        }
    }

    /**
     * @return Returns the sleepTimeAfterJob.
     */
    public final long getSleepTimeAfterJob() {
        synchronized (m_daemonLock) {
            return m_sleepTimeAfterJob;
        }
    }

    /**
     * @param sleepTimeAfterJob
     *            The sleepTimeAfterJob to set.
     */
    public final void setSleepTimeAfterJob(long sleepTimeAfterJob) {
        synchronized (m_daemonLock) {
            m_sleepTimeAfterJob = sleepTimeAfterJob;
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Flags to stop and reconfigure are reset.
     */
    public final synchronized void startDaemon()
        throws DaemonAlreadyStartedRTException {
        s_logger.info("Demaon with identification '" 
            + getIdentification() + "' has been started.");
        
        synchronized (m_daemonThreadLock) {
            if (isDaemonAlive()) {
                throw new DaemonAlreadyStartedRTException("Daemon '" 
                    + getIdentification() 
                    + "' can not be run twice. It has been already started.");
            }
            
            m_doStop = false;
            m_doReconfigure = false;
            m_daemonThread = new Thread(this);
            m_daemonThread.start();
            m_lastStartDate = new Date();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isDaemonRunning() {
        synchronized (m_daemonLock) {
            return m_daemonRunning;
        }
    }
    
    /**
     * @param isRunning
     *            Is <code>true</code> if daemon is running.
     */
    protected final void setDaemonRunning(boolean isRunning) {
        synchronized (m_daemonLock) {
            m_daemonRunning = isRunning;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void joinDaemon(long timeout) throws InterruptedException {
        Thread localThread;
        synchronized (m_daemonThreadLock) {
            if (m_daemonThread == null) {
                throw new DaemonNotStartedRTException(
                    "Daemon must be started before being able to join on it.");
            }
            localThread = m_daemonThread;
        }
            
        try {
            localThread.join(timeout);
            ifDaemonIsStillAliveSetPriorityToLowest();
        } catch (InterruptedException e) {
            ifDaemonIsStillAliveSetPriorityToLowest();
            throw e;
        }
    }
    
    /**
     * Method to set the priority of daemon to lowest if the daemon is still 
     * alive.
     */
    private final void ifDaemonIsStillAliveSetPriorityToLowest() {
        synchronized (m_daemonThreadLock) {
            if (m_daemonThread.isAlive()) {
                m_daemonThread.setPriority(Thread.MIN_PRIORITY);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isDaemonAlive() {
        synchronized (m_daemonThreadLock) {
            return m_daemonThread != null && m_daemonThread.isAlive();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void run() {
        try {
            synchronized (m_daemonLock) {
                m_daemonRunning = true;
                m_throwedRuntimeExceptionInRunMethod = null;
            }
            
            Throwable throwedThrowable = null;
            
            checkNeededConfiguration();
            s_logger.debug("Daemon '" + getIdentification() 
                + "' has been succeeded needed configuration tests.");
            try {
                /**
                 * Initialisation of the daemon.
                 */
                initializeDaemon();
                
                /**
                 * Let daemon work while it should not stop.
                 */
                while (!shouldDaemonStop()) {
                    doDaemonJob();
                }
            } catch (Throwable t) {
                throwedThrowable = t;
                s_logger.info(
                    "Throwable was thrown while executing daemon.", t);
            } finally {
                try {
                    cleanup();
                } catch (Throwable t) {
                    s_logger.info("Exception has been occurred while executing "
                        + "cleanup method of daemon '" + getIdentification() 
                        + "'.", t);
                    if (throwedThrowable == null) {
                        throwedThrowable = t;
                    }
                }
            }
            
            if (throwedThrowable == null) {
                s_logger.info("Daemon '" + getIdentification() 
                    + "' will be terminated normally.");
                sendTerminatedNormally();
            } else {
                s_logger.info("Daemon '" + getIdentification() 
                    + "' will be terminated exceptionally.");
                sendTerminatedExceptionally(throwedThrowable);
            }
        } catch (RuntimeException e) {
            synchronized (m_daemonLock) {
                m_throwedRuntimeExceptionInRunMethod = e;
            }
            s_logger.error("Runtime exception has been thrown in run method of "
                + "daemon '" + getIdentification() + "'.", e);
            throw e;
        } finally {
            synchronized (m_daemonLock) {
                m_daemonRunning = false;
            }
        }
    }

    /**
     * @return Returns the <code>RuntimeException</code> which was
     *         thrown in method <code>run</code>. After calling this method,
     *         the reference will be reset to <code>null</code>.
     */
    public final RuntimeException getThrowedRuntimeExceptionInRunMethod() {
        synchronized (m_daemonLock) {
            RuntimeException e = m_throwedRuntimeExceptionInRunMethod;
            m_throwedRuntimeExceptionInRunMethod = null;
            return e;
        }
    }

    /**
     * In this method the daemon will be initialized.
     */
    private void initializeDaemon() {
        try {
            init();
            s_logger.debug("Init method on daemon '" 
                + getIdentification()
                + "' has been executed successfully.");
        } catch (RuntimeException re) {
            s_logger.info("Exception has been occurred while executing "
                + "init method of daemon '" + getIdentification() 
                + "'.", re);
            sendExceptionOccurred(re);
        } finally {
            sendHeartbeat();
            s_logger.debug("Heartbeat sent after initializing daemon '"
                + getIdentification() + "'.");
        }
    }
    
    /**
     * In this method the daemon work is done. Further represents this method
     * one period.
     */
    private void doDaemonJob() {
        long daemonWorkStartTime = System.currentTimeMillis();
        try {
            /**
             * Reconfigure the daemon if it is necessary.
             */
            if (shouldDaemonReconfigure()) {
                reconfigure();
                synchronized (m_daemonLock) {
                    m_doReconfigure = false;
                }
                s_logger.info("Daemon '" + getIdentification()
                    + "' has been successfully reconfigured.");
            }
            runJob();
        } catch (RuntimeException re) {
            if (shouldDaemonReconfigure()) {
                s_logger.info("Exception has been occurred while "
                    + "reconfiguring daemon '" + getIdentification()
                    + "'.");
            } else {
                s_logger.info("Exception has been occurred while "
                    + "running daemon job '" + getIdentification()
                    + "'.");
            }
            sendExceptionOccurred(re);
        } finally {
            sendHeartbeat();
            s_logger.debug("Heartbeat sent after running daemon job '"
                + getIdentification() + "'.");
        }
        long daemonWorkEndTime = System.currentTimeMillis();
        
        /**
         * Do not sleep if the daemon should stop.
         */
        if (!shouldDaemonStop()) {
            sleepBetweenDaemonJobs(daemonWorkStartTime, daemonWorkEndTime);
        }
    }

    /**
     * Method to sleep between daemon jobs.
     * 
     * @param start
     *            Is the time when the daemon work was started.
     * @param end
     *            Is the time when the daemon work has been stopped.
     */
    private void sleepBetweenDaemonJobs(long start, long end) {
        long sleepTime = calculateSleepTime(start, end);
        long beforeSleep = System.currentTimeMillis();
        try {
            if (sleepTime > 0) {
                s_logger.debug("Daemon '" + getIdentification()
                    + "' will now sleep for " + sleepTime + "ms.");
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException ie) {
            s_logger.debug("Daemon '" + getIdentification()
                + "' has been interrupted while sleeping between "
                + "executing jobs.");
        }
        long afterSleep = System.currentTimeMillis();
        
        if (afterSleep - beforeSleep < sleepTime) {
            s_logger.debug("Daemon '" + getIdentification()
                + "' slept just for " + (afterSleep - beforeSleep)
                + "ms. Expected sleeping time was " + sleepTime
                + "ms.");
        }
    }

    /**
     * Method to calculate sleeping time.
     * 
     * @param start
     *            Is the time when the daemon work was started.
     * @param end
     *            Is the time when the daemon work has been stopped.
     * @return Returns the calculated sleeping time.
     */
    private long calculateSleepTime(long start, long end) {
//        Reject.ifFalse(end >= start, 
//            "End time must be greater or equals than start time.");
        long sleepTime = 0;
        
        long sleepTimeAfterJob;
        long minPeriodicity;
        synchronized (m_daemonLock) {
            sleepTimeAfterJob = m_sleepTimeAfterJob;
            minPeriodicity = m_minPeriodicity;
        }
        
        /**
         * If one of the two properties is less or equals zero, then there is
         * no time to sleep.
         */
        if (sleepTimeAfterJob > 0 && minPeriodicity > 0) {
            long latterDueTime = end - start;
            if (latterDueTime < 0) {
                latterDueTime = 0;
            }
            
            /**
             * If the latter due time for the daemon job is smaller than the 
             * given minimal periodicity it could be possible that we can go
             * to sleep, otherwise there is no time to sleep.
             */
            if (minPeriodicity > latterDueTime) {
                /**
                 * Calculate the maximal possible sleep time. This is the time 
                 * which lasts until the next job must be started.
                 */
                long maxPossibleSleepTime = minPeriodicity - latterDueTime;
                if (maxPossibleSleepTime > sleepTimeAfterJob) {
                    /**
                     * The given sleep time after job was smaller or equals, so 
                     * we sleep only this given time.
                     */
                    sleepTime = sleepTimeAfterJob;
                } else {
                    /**
                     * The given sleep time after job was greater, so we sleep 
                     * the maximal possible time.
                     */
                    sleepTime = maxPossibleSleepTime;
                }
            }
        }
        return sleepTime;
    }

    /**
     * Method to check if the daemon configuration is properly set. This method
     * will be called before starting daemon work. If this method will be
     * overridden, the super call should be implicitly made!
     */
    protected void checkNeededConfiguration() {
        if (!StringUtils.hasLength(m_identification)) {
            DaemonManagerNotificationHelper
                .notifyMissingDaemonIdentification();
        }
        if (m_daemonObserver == null) {
            DaemonManagerNotificationHelper
                .notifyNoDaemonObserverRegistered(this);
        }
        synchronized (m_daemonLock) {
            if (!NumberUtils.isNumberInsideBoundaries(m_sleepTimeAfterJob, 
                MIN_ALLOWED_SLEEP_TIME_AFTER_JOB, 
                MAX_ALLOWED_SLEEP_TIME_AFTER_JOB)) {
                DaemonManagerNotificationHelper
                    .notifyBrokenBoundaryCondition(m_sleepTimeAfterJob, 
                        MIN_ALLOWED_SLEEP_TIME_AFTER_JOB, 
                        MAX_ALLOWED_SLEEP_TIME_AFTER_JOB, 
                        "sleepTimeAfterJob", this);
            }
            if (!NumberUtils.isNumberInsideBoundaries(m_minPeriodicity, 
                MIN_ALLOWED_PERIODICITY, MAX_ALLOWED_PERIODICITY)) {
                DaemonManagerNotificationHelper
                    .notifyBrokenBoundaryCondition(m_minPeriodicity, 
                        MIN_ALLOWED_PERIODICITY, MAX_ALLOWED_PERIODICITY, 
                        "minPeriodicity", this);
            }
        }
    }

    /**
     * This method will be invoked if a heartbeat should be sent. This method
     * should not be overridden.
     */
    protected void sendHeartbeat() {
        m_daemonObserver.receiveHeartbeat(this);
    }

    /**
     * This method will be invoked if an exception has been occured while
     * executing the daemon work. This method should not be overridden.
     * 
     * @param t
     *            Is the throwable which has been throwed.
     */
    protected void sendExceptionOccurred(Throwable t) {
        DaemonCausedRTException dce;
        if (t instanceof DaemonCausedRTException) {
            dce = (DaemonCausedRTException) t;
        } else {
            dce = new DaemonCausedRTException(this, t);
        }
        m_daemonObserver.exceptionOccurred(dce);
    }

    /**
     * This method will be invoked if a daemon has been stopped and everything
     * went well. This method should not be overridden.
     */
    protected void sendTerminatedNormally() {
        m_daemonObserver.terminatedNormally(this);
    }

    /**
     * This method will be invoked if a daemon has been stopped and an exception
     * has been occurred while processing. This method should not be overridden.
     * 
     * @param t
     *            Is the throwable which has been throwed.
     */
    protected void sendTerminatedExceptionally(Throwable t) {
        m_daemonObserver.terminatedExceptionally(this, t);
    }

    /**
     * This method will be invoked before starting the daemon job the first
     * time. Method <code>checkNeededConfiguration</code> will be invoked
     * before this method. This method should be overridden. By default this
     * method does nothing.
     * 
     * @throws DaemonCausedRTException
     *             Is the exception which should be thrown if a problem occurs.
     */
    protected void init() throws DaemonCausedRTException { }
    
    /**
     * This method will be invoked everytime before running the daemon job, but
     * only if the method <code>doReconfigure</code> has been invoked before.
     * This method should be overridden. By default this method does nothing.
     * 
     * @throws DaemonCausedRTException
     *             Is the exception which should be thrown if a problem occurs.
     */
    protected void reconfigure() throws DaemonCausedRTException { }

    /**
     * This main method will be invoked everytime the daemon should execute its
     * job. This method must be overridden!
     * 
     * @throws DaemonCausedRTException
     *             Is the exception which should be thrown if a problem occurs.
     */
    protected abstract void runJob() throws DaemonCausedRTException;

    /**
     * This method will be invoked after daemon has been stopped or an exception
     * has occurred while executing the daemon. This method should be
     * overridden. By default this method does nothing.
     * 
     * @throws DaemonCausedRTException
     *             Is the exception which should be thrown if a problem occurs.
     */
    protected void cleanup() throws DaemonCausedRTException { }

    /**
     * @return Returns the creationDate.
     */
    public final Date getCreationDate() {
        synchronized (m_daemonLock) {
            return m_creationDate;
        }
    }

    /**
     * @return Returns the lastStartDate.
     */
    public final Date getLastStartDate() {
        synchronized (m_daemonThreadLock) {
            return m_lastStartDate;
        }
    }

    /**
     * @param order
     *            Is the order the daemon is started and stopped. Low value
     *            means daemon started early and stopped lately.
     */
    public void setOrder(int order) {
        synchronized (m_daemonLock) {
            m_order = order;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOrder() {
        synchronized (m_daemonLock) {
            return m_order;
        }
    }
}
