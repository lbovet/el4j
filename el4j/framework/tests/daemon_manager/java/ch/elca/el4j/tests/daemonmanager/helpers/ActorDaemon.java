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

package ch.elca.el4j.tests.daemonmanager.helpers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.daemonmanager.exceptions.DaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.impl.AbstractDaemon;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

import junit.framework.Assert;

/**
 * This daemon can act in different roles.
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
public class ActorDaemon extends AbstractDaemon {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(ActorDaemon.class);
    
    /**
     * Is the role this daemon should take.
     * Default is set to 'ActorRole.NORMAL'.
     */
    private ActorRole m_actorRole = ActorRole.NORMAL;
    
    /**
     * Is the number method <code>runJob</code> should be successfully executed,
     * independent of the actor's role.
     */
    private int m_numberOfSuccessfulJobRuns = 0;

    /**
     * Is the time in millis this daemon should sleep inside method 
     * <code>runJob</code> at minimum.
     */
    private long m_simulateDurationOfOneJobRunMin = 0;

    /**
     * Is the time in millis this daemon should sleep inside method 
     * <code>runJob</code> at maximum.
     */
    private long m_simulateDurationOfOneJobRunMax = 0;

    /**
     * Flag to indicate if method <code>checkNeededConfiguration</code> has been
     * called.
     */
    private boolean m_checkNeededConfigurationCalled;
    
    /**
     * Flag to indicate if method <code>init</code> has been called.
     */
    private boolean m_initCalled;

    /**
     * Flag to indicate if method <code>runJob</code> has been called.
     */
    private boolean m_runJobCalled;

    /**
     * Flag to indicate if method <code>reconfigure</code> has been called.
     */
    private boolean m_reconfigureCalled;

    /**
     * Flag to indicate if method <code>cleanup</code> has been called.
     */
    private boolean m_cleanupCalled;
    
    /**
     * Counts the invocation of method <code>runJob</code>.
     */
    private volatile int m_runJobCounter;
    
    /**
     * Default constructor.
     */
    public ActorDaemon() {
        resetFlagsAndCounters();
    }
    
    /**
     * @return Returns the actorRole.
     */
    public final ActorRole getActorRole() {
        return m_actorRole;
    }

    /**
     * @param actorRole
     *            The actorRole to set.
     */
    public final void setActorRole(ActorRole actorRole) {
        m_actorRole = actorRole;
    }

    /**
     * @return Returns the numberOfSuccessfulJobRuns.
     */
    public final int getNumberOfSuccessfulJobRuns() {
        return m_numberOfSuccessfulJobRuns;
    }

    /**
     * @param numberOfSuccessfulJobRuns
     *            The numberOfSuccessfulJobRuns to set.
     */
    public final void setNumberOfSuccessfulJobRuns(
        int numberOfSuccessfulJobRuns) {
        m_numberOfSuccessfulJobRuns = numberOfSuccessfulJobRuns;
    }

    /**
     * @return Returns the simulateDurationOfOneJobRunMin.
     */
    public final long getSimulateDurationOfOneJobRunMin() {
        return m_simulateDurationOfOneJobRunMin;
    }

    /**
     * @param simulateDurationOfOneJobRunMin
     *            The simulateDurationOfOneJobRunMin to set.
     */
    public final void setSimulateDurationOfOneJobRunMin(
        long simulateDurationOfOneJobRunMin) {
        m_simulateDurationOfOneJobRunMin = simulateDurationOfOneJobRunMin;
    }

    /**
     * @return Returns the simulateDurationOfOneJobRunMax.
     */
    public final long getSimulateDurationOfOneJobRunMax() {
        return m_simulateDurationOfOneJobRunMax;
    }

    /**
     * @param simulateDurationOfOneJobRunMax
     *            The simulateDurationOfOneJobRunMax to set.
     */
    public final void setSimulateDurationOfOneJobRunMax(
        long simulateDurationOfOneJobRunMax) {
        m_simulateDurationOfOneJobRunMax = simulateDurationOfOneJobRunMax;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void checkNeededConfiguration() {
        /**
         * Set flag that method has been called.
         */
        m_checkNeededConfigurationCalled = true;

        logMethodInvocation("checkNeededConfiguration", "entered");

        super.checkNeededConfiguration();
        
        if (m_actorRole == null) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Property 'actorRole' must be set.");
        }
        if (m_numberOfSuccessfulJobRuns < 0) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Property 'numberOfSuccessfulJobRuns' must be greater or "
                    + "equals to zero.");
        }
        if (m_simulateDurationOfOneJobRunMin < 0) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Property 'simulateDurationOfOneJobRunMin' must be greater or "
                    + "equals to zero.");
        }
        if (m_simulateDurationOfOneJobRunMax 
            < m_simulateDurationOfOneJobRunMin) {
            CoreNotificationHelper.notifyMisconfiguration(
                "Property 'simulateDurationOfOneJobRunMax' must be greater or"
                    + " equals to property 'simulateDurationOfOneJobRunMin'.");
        }
        
        logMethodInvocation("checkNeededConfiguration", "left");
    }
    
    /**
     * {@inheritDoc}
     */
    protected void init() throws DaemonCausedRTException {
        logMethodInvocation("init", "entered");

        if (!m_checkNeededConfigurationCalled) {
            Assert.fail("Check needed configuration must be called first.");
        } else if (m_initCalled) {
            Assert.fail("Init must be called only once.");
        }
        /**
         * Set flag that method has been called.
         */
        m_initCalled = true;
        ActionRegister.registerDaemonAction(this, DaemonAction.INIT);
        
        if (getActorRole() 
            == ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_INIT) {
            throwNewDaemonCausedException();
        } else if (getActorRole() 
            == ActorRole.THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_INIT) {
            throwNewNonDaemonCausedException();
        }

        logMethodInvocation("init", "left");
    }

    /**
     * {@inheritDoc}
     */
    protected void runJob() throws DaemonCausedRTException {
        logMethodInvocation("runJob", "entered");
        /**
         * Set flag that method has been called.
         */
        m_runJobCalled = true;

        m_runJobCounter++;
        if (m_runJobCounter > m_numberOfSuccessfulJobRuns) {
            if (getActorRole() 
                == ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN) {
                throwNewDaemonCausedException();
            } else if (getActorRole() 
                == ActorRole.THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN) {
                throwNewNonDaemonCausedException();
            } else if (getActorRole() 
                == ActorRole.BLOCKS) {
                block();
            }
        }
        
        long timeToSleep 
            = m_simulateDurationOfOneJobRunMin 
                + (long) (Math.random() 
                    * (m_simulateDurationOfOneJobRunMax 
                        - m_simulateDurationOfOneJobRunMin));
        sleep(timeToSleep);
    
        logMethodInvocation("runJob", "left");
    }

    /**
     * {@inheritDoc}
     */
    protected void reconfigure() throws DaemonCausedRTException {
        /**
         * Set flag that method has been called.
         */
        m_reconfigureCalled = true;
        ActionRegister.registerDaemonAction(this, DaemonAction.RECONFIGURE);
        logMethodInvocation("reconfigure", "entered");
        
        if (getActorRole() 
            == ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE) {
            throwNewDaemonCausedException();
        } else if (getActorRole() 
            == ActorRole.THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE) {
            throwNewNonDaemonCausedException();
        }
        
        logMethodInvocation("reconfigure", "left");
    }

    /**
     * {@inheritDoc}
     */
    protected void cleanup() throws DaemonCausedRTException {
        logMethodInvocation("cleanup", "entered");
        if (!m_initCalled) {
            Assert.fail("Init must be called first.");
        } else if (m_cleanupCalled) {
            Assert.fail("Cleanup must be called only once.");
        }
        /**
         * Set flag that method has been called.
         */
        m_cleanupCalled = true;
        ActionRegister.registerDaemonAction(this, DaemonAction.CLEANUP);
        
        if (getActorRole() 
            == ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP) {
            throwNewDaemonCausedException();
        } else if (getActorRole() 
            == ActorRole.THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP) {
            throwNewNonDaemonCausedException();
        }

        logMethodInvocation("cleanup", "left");
    }

    /**
     * {@inheritDoc}
     */
    public String getInformation() {
        String informationOfSuperDaemon = super.getInformation();
        StringBuffer sb = new StringBuffer(informationOfSuperDaemon);
            
        sb.append("---## Configuration of 'ActorDaemon'");
        sb.append(NEWLINE);
        appendPropertyConfigurationInformation(sb);
        appendMethodCallInformation(sb);
        sb.append(NEWLINE);

        return sb.toString();
    }

    /**
     * Method to append property configuration information.
     * 
     * @param sb
     *            Is the string buffer where to append.
     */
    private void appendPropertyConfigurationInformation(StringBuffer sb) {
        sb.append("   * Actor daemon is playing role '");
        sb.append(getActorRole().toString());
        sb.append("'.");
        sb.append(NEWLINE);
        sb.append("   * Number of successful job runs is set to ");
        sb.append(getNumberOfSuccessfulJobRuns());
        sb.append(".");
        sb.append(NEWLINE);
        sb.append("   * Minimal simulate duration of one job run is set to ");
        sb.append(getSimulateDurationOfOneJobRunMin());
        sb.append("ms.");
        sb.append(NEWLINE);
        sb.append("   * Maximal simulate duration of one job run is set to ");
        sb.append(getSimulateDurationOfOneJobRunMax());
        sb.append("ms.");
        sb.append(NEWLINE);
    }

    /**
     * Method to append method call information.
     * 
     * @param sb
     *            Is the string buffer where to append.
     */
    private void appendMethodCallInformation(StringBuffer sb) {
        sb.append("   * Method calls");
        sb.append(NEWLINE);
        sb.append("      * Method 'checkNeededConfiguration' was ");
        sb.append(isCheckNeededConfigurationCalled() ? "" : "not ");
        sb.append("called.");
        sb.append(NEWLINE);
        sb.append("      * Method 'init' was ");
        sb.append(isInitCalled() ? "" : "not ");
        sb.append("called.");
        sb.append(NEWLINE);
        sb.append("      * Method 'runJob' was ");
        sb.append(isRunJobCalled() ? "" : "not ");
        sb.append("called.");
        sb.append(NEWLINE);
        sb.append("      * Method 'reconfigure' was ");
        sb.append(isReconfigureCalled() ? "" : "not ");
        sb.append("called.");
        sb.append(NEWLINE);
        sb.append("      * Method 'cleanup' was ");
        sb.append(isCleanupCalled() ? "" : "not ");
        sb.append("called.");
        sb.append(NEWLINE);
    }
    
    /**
     * Method to log method invocation.
     * 
     * @param methodName Is the name of the method.
     * @param action Is the action.
     */
    private void logMethodInvocation(String methodName, String action) {
        s_logger.info("Method '" + methodName + "' of daemon '" 
            + getIdentification() + "' has been " + action + ".");
    }
    
    /**
     * Method which throws a daemon caused exception.
     * 
     * @throws DaemonCausedRTException Will be thrown everytime.
     */
    protected void throwNewDaemonCausedException() 
        throws DaemonCausedRTException {
        throw new DaemonCausedRTException(
            this, new RuntimeException(
                "Inner runtime exception of daemon caused exception."));
    }

    /**
     * Method which throws a non daemon caused exception.
     * 
     * @throws RuntimeException Will be thrown everytime.
     */
    protected void throwNewNonDaemonCausedException()
        throws RuntimeException {
        throw new RuntimeException(
                "Blank runtime exception.");
    }
    
    /**
     * Method to sleep for given millis.
     * 
     * @param timeToSleep Is the time in millis to sleep.
     */
    protected void sleep(long timeToSleep) {
        long now = System.currentTimeMillis();
        long endSleepAt = now + timeToSleep;
        boolean interrupted = true;
        while (interrupted && now < endSleepAt) {
            interrupted = false;
            long sleepTime = endSleepAt - now;
            s_logger.debug("Planned sleeping of daemon '" 
                + getIdentification() + "' is " + sleepTime + "ms.");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                s_logger.debug("Sleeping of daemon '" + getIdentification() 
                    + "' interrupted.");
                interrupted = true;
            }
            now = System.currentTimeMillis();
        }
    }
    
    /**
     * This method blocks current thread.
     */
    protected void block() {
        while (getActorRole() == ActorRole.BLOCKS) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                s_logger.debug("Interrupted while blocking daemon '" 
                    + getIdentification() + "'.");
            }
        }
    }

    /**
     * @return Returns the checkNeededConfigurationCalled.
     */
    public final boolean isCheckNeededConfigurationCalled() {
        return m_checkNeededConfigurationCalled;
    }

    /**
     * @return Returns the cleanupCalled.
     */
    public final boolean isCleanupCalled() {
        return m_cleanupCalled;
    }

    /**
     * @return Returns the initCalled.
     */
    public final boolean isInitCalled() {
        return m_initCalled;
    }

    /**
     * @return Returns the reconfigureCalled.
     */
    public final boolean isReconfigureCalled() {
        return m_reconfigureCalled;
    }

    /**
     * @return Returns the runJobCalled.
     */
    public final boolean isRunJobCalled() {
        return m_runJobCalled;
    }
    
    /**
     * Method to reset method called flags and invocation counters.
     */
    public final void resetFlagsAndCounters() {
        /**
         * Flags.
         */
        m_checkNeededConfigurationCalled = false;
        m_initCalled = false;
        m_runJobCalled = false;
        m_reconfigureCalled = false;
        m_cleanupCalled = false;
        
        /**
         * Counters.
         */
        m_runJobCounter = 0;
    }
    
    /**
     * Method to get the invocation count of method <code>runJob</code>.
     * 
     * @return Returns the run job counter.
     */
    public final int getRunJobCounter() {
        return m_runJobCounter;
    }
}
