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

package ch.elca.el4j.tests.daemonmanager;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonManagerIsNotProcessingRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonManagerIsProcessingRTException;
import ch.elca.el4j.services.daemonmanager.impl.DaemonManagerImpl;
import ch.elca.el4j.tests.daemonmanager.helpers.ActorDaemon;
import ch.elca.el4j.tests.daemonmanager.helpers.ActorRole;
import ch.elca.el4j.tests.daemonmanager.helpers.DaemonManagerProcessor;

import junit.framework.TestCase;

/**
 * This test case is used to test daemon manager class 
 * <code>ch.elca.el4j.services.daemonmanager.impl.DaemonManagerImpl</code>.
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
public class DaemonManagerTest extends TestCase {
    
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DaemonManagerTest.class);
    
    /**
     * Simple test with one daemon which act as normal.
     * 
     * @throws InterruptedException
     *             Will be thrown if a thread was interrupted.
     */
    public void testDaemonManagerWithOneNormalDaemon() 
        throws InterruptedException {
        
        s_logger.info("Test 'testDaemonManagerWithOneNormalDaemon' started.");

        final long DAEMON_MANAGER_MAX_DAEMON_STARTUP_DELAY = 200;
        final long DAEMON_MANAGER_MIN_DAEMON_STARTUP_DELAY = 50;
        final long DAEMON_MANAGER_DAEMON_JOIN_TIMEOUT = 2000;
        final long DAEMON_MANAGER_CHECK_PERIOD = 2000;
        final long DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT = 10000;
        final long DAEMON_MANAGER_CACHED_INFORMATION_MESSAGE_TIMEOUT = 0;
        final int DAEMON_MANAGER_MAX_MISSING_HEARTBEATS = 0;

        final long DAEMON_MIN_PERIODICITY 
            = DAEMON_MANAGER_CHECK_PERIOD / 2;
        final long DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN 
            = (long) (DAEMON_MIN_PERIODICITY * 0.2);
        final long DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX
            = (long) (DAEMON_MIN_PERIODICITY * 0.8);
        
        /**
         * Instantiate daemon manager.
         */
        DaemonManagerImpl daemonManager = createDaemonManagerImpl(
            DAEMON_MANAGER_CHECK_PERIOD, 
            DAEMON_MANAGER_DAEMON_JOIN_TIMEOUT, 
            DAEMON_MANAGER_MIN_DAEMON_STARTUP_DELAY, 
            DAEMON_MANAGER_MAX_DAEMON_STARTUP_DELAY, 
            DAEMON_MANAGER_CACHED_INFORMATION_MESSAGE_TIMEOUT, 
            DAEMON_MANAGER_MAX_MISSING_HEARTBEATS);
        
        /**
         * Check conditions before starting processing of daemon manager.
         */
        checkConditionsBeforeProcessingDaemonManager(daemonManager);

        s_logger.info("Daemon manager information before started:\n" 
            + daemonManager.getInformation());

        /**
         * Start processing in a seperate thread. This is realized with the 
         * daemon manager processor. Afterwards sleep for one check period of
         * daemon manager, so it is proved that daemon manager can also process
         * without any daemons.
         */
        DaemonManagerProcessor dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        sleep(DAEMON_MANAGER_CHECK_PERIOD);
        checkIfDaemonManagerIsStillProcessing(dmp);
        

        /**
         * Check conditions while processing on daemon manager.
         */
        checkConditionsWhileProcessingDaemonManager(daemonManager);
        checkIfDaemonManagerIsStillProcessing(dmp);
        
        s_logger.info("Daemon manager information while processing no "
            + "daemons:\n" + daemonManager.getInformation());
        
        /**
         * Add a daemon, do checks for it and let the daemon some time work.
         */
        checkIfAddingDaemonWorks(daemonManager, DAEMON_MIN_PERIODICITY, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX, 
            DAEMON_MANAGER_CHECK_PERIOD);
        sleep(DAEMON_MANAGER_CHECK_PERIOD * 2);
        s_logger.info("Daemon manager information while processing one "
            + "daemon:\n" + daemonManager.getInformation());
        checkIfDaemonManagerIsStillProcessing(dmp);
        
        /**
         * Stop daemon manager and sleep two check periods so the daemon manager
         * has time to stop.
         */
        stopAndCheckStoppedDaemonManager(daemonManager, dmp, 
            DAEMON_MANAGER_CHECK_PERIOD, 
            DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
        
        /**
         * Check if daemon manager has moved daemon to pending daemons and
         * daemon is no more alive so it can be started again. Further reset the
         * invoke checking flags and counters of actor daemon.
         */
        checkAndRecoverStoppedDaemon(daemonManager);
        
        
        /**
         * Start same daemon manager a second time. For that the stop flag must
         * be reset. At the end do sleep a while so daemons can work.
         */
        daemonManager.canStartProcessing();
        dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        sleep(DAEMON_MANAGER_CHECK_PERIOD * 2);
        checkIfDaemonManagerIsStillProcessing(dmp);
        
        /**
         * Check if daemon has been started.
         */
        assertEquals("Registered daemon has not been started.", 
            1, daemonManager.getNumberOfRunningDaemons());
        checkIfDaemonManagerIsStillProcessing(dmp);

        s_logger.info("Daemon manager information while processing one "
            + "daemon again:\n" + daemonManager.getInformation());

        /**
         * Stop daemon manager and sleep so the daemon manager has time to stop.
         */
        stopAndCheckStoppedDaemonManager(daemonManager, dmp, 
            DAEMON_MANAGER_CHECK_PERIOD, 
            DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
    }

    /**
     * Method to create a daemon manager impl<.
     * 
     * @param checkPeriod
     *            Is the check period.
     * @param daemonJoinTimeout
     *            Is the daemon join timeout.
     * @param minDaemonStartupDelay
     *            Is the min daemon startup delay.
     * @param maxDaemonStartupDelay
     *            Is the max daemon startup delay.
     * @param cachedInformationMessageTimeout
     *            Is the cached information message timeout.
     * @param maxMissingHeartbeats
     *            Is the max missing heartbeats.
     * @return Returns the created daemon manager impl.
     */
    protected DaemonManagerImpl createDaemonManagerImpl(long checkPeriod, 
        long daemonJoinTimeout, long minDaemonStartupDelay, 
        long maxDaemonStartupDelay, long cachedInformationMessageTimeout, 
        int maxMissingHeartbeats) {
        DaemonManagerImpl daemonManager = new DaemonManagerImpl();
        daemonManager.setCheckPeriod(
            checkPeriod);
        daemonManager.setDaemonJoinTimeout(
            daemonJoinTimeout);
        daemonManager.setMinDaemonStartupDelay(
            minDaemonStartupDelay);
        daemonManager.setMaxDaemonStartupDelay(
            maxDaemonStartupDelay);
        daemonManager.setCachedInformationMessageTimeout(
            cachedInformationMessageTimeout);
        daemonManager.setMaxMissingHeartbeats(
            maxMissingHeartbeats);
        return daemonManager;
    }
    
    /**
     * Method to create an actor daemon.
     * 
     * @param identification
     *            Is the identification.
     * @param actorRole
     *            Is the role the daemon must act.
     * @param minPeriodicity
     *            Is the min periodicity.
     * @param sleepTimeAfterJob
     *            Is the time to sleep after job run.
     * @param simulateDurationOfOneJobRunMin
     *            Is the min run duration to simulate.
     * @param simulateDurationOfOneJobRunMax
     *            Is the max run duration to simulate.
     * @param numberOfSuccessfulRuns
     *            Is the number of successful runs.
     * @return Returns the created actor daemon.
     */
    protected ActorDaemon createActorDaemon(String identification, 
        ActorRole actorRole, long minPeriodicity, long sleepTimeAfterJob,
        long simulateDurationOfOneJobRunMin, 
        long simulateDurationOfOneJobRunMax, int numberOfSuccessfulRuns) {
        ActorDaemon daemon = new ActorDaemon();
        daemon.setIdentification(identification);
        daemon.setActorRole(actorRole);
        daemon.setMinPeriodicity(minPeriodicity);
        daemon.setSleepTimeAfterJob(sleepTimeAfterJob);
        daemon.setSimulateDurationOfOneJobRunMin(
            simulateDurationOfOneJobRunMin);
        daemon.setSimulateDurationOfOneJobRunMax(
            simulateDurationOfOneJobRunMax);
        daemon.setNumberOfSuccessfulJobRuns(
            numberOfSuccessfulRuns);
        return daemon;
    }

    /**
     * Method to check if the daemon manager is still processing.
     * 
     * @param dmp
     *            Is the daemon manager processor where method
     *            <code>process</code> is called.
     */
    private void checkIfDaemonManagerIsStillProcessing(
        DaemonManagerProcessor dmp) {
        assertTrue("Daemon manager should still process.", dmp.isAlive());
    }

    /**
     * Method where conditions before processing will be checked.
     * 
     * @param daemonManager
     *            Is the daemon manager to check.
     */
    private void checkConditionsBeforeProcessingDaemonManager(
        DaemonManagerImpl daemonManager) {
        assertFalse("Daemon manager is processing.", 
            daemonManager.isProcessing());

        assertNotNull("No empty set was returned.", 
            daemonManager.getDaemons());
        daemonManager.setDaemons(null);
        assertFalse("No daemon was given but answer was 'true'.", 
            daemonManager.addDaemon(null));
        assertFalse("No daemon was given but answer was 'true'.", 
            daemonManager.removeDaemon(null));
        
        try {
            daemonManager.getRunningDaemons();
            fail("No exception was thrown.");
        } catch (DaemonManagerIsNotProcessingRTException e) {
            s_logger.debug("'DaemonManagerIsNotProcessingRTException' "
                + "successfully catched.");
        }
        try {
            daemonManager.getNumberOfRunningDaemons();
            fail("No exception was thrown.");
        } catch (DaemonManagerIsNotProcessingRTException e) {
            s_logger.debug("'DaemonManagerIsNotProcessingRTException' "
                + "successfully catched.");
        }
    }
    
    /**
     * Method where conditions while processing will be checked.
     * 
     * @param daemonManager
     *            Is the daemon manager to check.
     */
    private void checkConditionsWhileProcessingDaemonManager(
        DaemonManagerImpl daemonManager) {
        assertTrue("Daemon manager is not processing.", 
            daemonManager.isProcessing());
        
        try {
            daemonManager.setDaemons(null);
            fail("No exception was thrown.");
        } catch (DaemonManagerIsProcessingRTException e) {
            s_logger.debug("'DaemonManagerIsProcessingRTException' "
                + "successfully catched.");
        }
        try {
            daemonManager.getDaemons();
            fail("No exception was thrown.");
        } catch (DaemonManagerIsProcessingRTException e) {
            s_logger.debug("'DaemonManagerIsProcessingRTException' "
                + "successfully catched.");
        }
        daemonManager.getRunningDaemons();
        daemonManager.getNumberOfRunningDaemons();
    }
    
    /**
     * Method to check if adding a daemon work correctly.
     * 
     * @param daemonManager
     *            Is the daemon manger to check.
     * @param minPeriodicity
     *            Is the min periodicity.
     * @param simulateDurationOfOneJobRunMin
     *            Is the min simulate duration of one job run.
     * @param simulateDurationOfOneJobRunMax
     *            Is the max simulate duration of one job run.
     * @param checkPeriod
     *            Is the check period.
     */
    private void checkIfAddingDaemonWorks(DaemonManagerImpl daemonManager, 
        long minPeriodicity, long simulateDurationOfOneJobRunMin, 
        long simulateDurationOfOneJobRunMax, long checkPeriod) {
        ActorDaemon daemon = createActorDaemon("Test daemon #1", 
            ActorRole.NORMAL, minPeriodicity, Long.MAX_VALUE, 
            simulateDurationOfOneJobRunMin, simulateDurationOfOneJobRunMax, 
            Integer.MAX_VALUE);
        
        assertEquals("There are already daemons.", 
            0, daemonManager.getNumberOfRunningDaemons());
        daemonManager.addDaemon(daemon);
        assertEquals("Registered daemon has not been started.", 
            1, daemonManager.getNumberOfRunningDaemons());
    }
    
    /**
     * Method to stop and check the given daemon manager.
     * 
     * @param daemonManager
     *            Is the daemon manager to stop.
     * @param dmp
     *            Is the daemon manager processor which will be stopped.
     * @param checkPeriod
     *            Is the check period.
     * @param processorJoinTimeout
     *            Is the processor join timeout.
     * @throws InterruptedException
     *             Will be thrown if a thread was interrupted.
     */
    private void stopAndCheckStoppedDaemonManager(
        DaemonManagerImpl daemonManager, DaemonManagerProcessor dmp,
        long checkPeriod, long processorJoinTimeout) 
        throws InterruptedException {
        daemonManager.doStopProcessing();
        sleep(checkPeriod * 2);
        dmp.join(processorJoinTimeout);
        assertFalse("Daemon manager is still processing.", 
            daemonManager.isProcessing());
        assertFalse("Daemon manager processor is still alive.",
            dmp.isAlive());
        assertTrue("Daemon manager has terminated processing exceptionally.", 
            dmp.doesProcessingTerminatedNormally());
    }

    /**
     * Method to check if daemon manager has moved daemon to pending daemons and
     * daemon is no more alive so it can be started again. Further reset the
     * invoke checking flags and counters of actor daemon.
     * 
     * @param daemonManager
     *            Is the daemon manager to check.
     */
    private void checkAndRecoverStoppedDaemon(DaemonManagerImpl daemonManager) {
        Set daemons = daemonManager.getDaemons();
        assertEquals("Daemon set has not correct size.", 1,
            daemons.size());
        ActorDaemon daemonNumberOne = (ActorDaemon) daemons.iterator().next();
        assertFalse("Daemon is still alive.", daemonNumberOne.isDaemonAlive());
        daemonNumberOne.resetFlagsAndCounters();
        s_logger.info("Daemon manager information while not processing but "
            + "containing one daemon:\n" + daemonManager.getInformation());
    }

    /**
     * Method to sleep for given millis.
     * 
     * @param timeToSleep
     *            Is the time in millis to sleep.
     */
    protected void sleep(long timeToSleep) {
        long now = System.currentTimeMillis();
        long endSleepAt = now + timeToSleep;
        boolean interrupted = true;
        while (interrupted && now < endSleepAt) {
            interrupted = false;
            long sleepTime = endSleepAt - now;
            s_logger.debug("Sleeping planned for " + sleepTime + "ms.");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                s_logger.debug("Sleeping interrupted.");
                interrupted = true;
            }
            now = System.currentTimeMillis();
        }
    }
    
    /**
     * Tests if daemon manager will be stopped if a daemon blocks.
     * 
     * @throws InterruptedException
     *             Will be thrown if a thread was interrupted.
     */
    public void testDaemonManagerWithOneBlockingDaemon() 
        throws InterruptedException {

        s_logger.info("Test 'testDaemonManagerWithOneBlockingDaemon' started.");

        final long DAEMON_MANAGER_MAX_DAEMON_STARTUP_DELAY = 200;
        final long DAEMON_MANAGER_MIN_DAEMON_STARTUP_DELAY = 50;
        final long DAEMON_MANAGER_DAEMON_JOIN_TIMEOUT = 2000;
        final long DAEMON_MANAGER_CHECK_PERIOD = 2000;
        final long DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT = 2000;
        final long DAEMON_MANAGER_CACHED_INFORMATION_MESSAGE_TIMEOUT = 0;
        final int DAEMON_MANAGER_MAX_MISSING_HEARTBEATS = 0;

        final long DAEMON_MIN_PERIODICITY 
            = DAEMON_MANAGER_CHECK_PERIOD / 2;
        final long DAEMON_SLEEP_TIME_AFTER_JOB 
            = Long.MAX_VALUE;
        final long DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN 
            = (long) (DAEMON_MIN_PERIODICITY * 0.2);
        final long DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX
            = (long) (DAEMON_MIN_PERIODICITY * 0.8);
        final int DAEMON_NUMBER_OF_SUCCESSFUL_RUNS 
            = 3;
        
        /**
         * Instantiate daemon manager.
         */
        DaemonManagerImpl daemonManager = createDaemonManagerImpl(
            DAEMON_MANAGER_CHECK_PERIOD, 
            DAEMON_MANAGER_DAEMON_JOIN_TIMEOUT, 
            DAEMON_MANAGER_MIN_DAEMON_STARTUP_DELAY, 
            DAEMON_MANAGER_MAX_DAEMON_STARTUP_DELAY, 
            DAEMON_MANAGER_CACHED_INFORMATION_MESSAGE_TIMEOUT, 
            DAEMON_MANAGER_MAX_MISSING_HEARTBEATS);
        
        ActorDaemon daemon = createActorDaemon("Test daemon #2", 
            ActorRole.BLOCKS, DAEMON_MIN_PERIODICITY, 
            DAEMON_SLEEP_TIME_AFTER_JOB, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN,
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX,
            DAEMON_NUMBER_OF_SUCCESSFUL_RUNS);

        daemonManager.addDaemon(daemon);
        assertFalse("Daemon must not be started before processing.", 
            daemon.isDaemonRunning());
        
        DaemonManagerProcessor dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        
        sleep(DAEMON_NUMBER_OF_SUCCESSFUL_RUNS * DAEMON_MIN_PERIODICITY
            + DAEMON_MANAGER_CHECK_PERIOD * 2);
        
        assertFalse("Daemon manager has not stopped processing at expected "
            + "time.", daemonManager.isProcessing());
        
        dmp.join(DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
        assertFalse("Daemon manager processor has not been stopped as "
            + "expected.", dmp.isAlive());
        assertTrue("Daemon manager processor has not been terminated by a "
            + "missing heartbeat exception.", 
            dmp.doesMissingHeartbeatsExceptionOccurred());
    }
    
    /**
     * Tests if daemon manager will be stopped if a daemon throws daemon
     * caused exceptions.
     * 
     * @throws InterruptedException
     *             Will be thrown if a thread was interrupted.
     */
    public void testDaemonManagerWithOneDaemonCausedExceptionThrowingDaemon() 
        throws InterruptedException {

        s_logger.info("Test " 
            + "'testDaemonManagerWithOneDaemonCausedExceptionThrowingDaemon' " 
            + "started.");

        final long DAEMON_MANAGER_MAX_DAEMON_STARTUP_DELAY = 200;
        final long DAEMON_MANAGER_MIN_DAEMON_STARTUP_DELAY = 50;
        final long DAEMON_MANAGER_DAEMON_JOIN_TIMEOUT = 2000;
        final long DAEMON_MANAGER_CHECK_PERIOD = 2000;
        final long DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT = 2000;
        final long DAEMON_MANAGER_CACHED_INFORMATION_MESSAGE_TIMEOUT = 0;
        final int DAEMON_MANAGER_MAX_MISSING_HEARTBEATS = 0;

        final long DAEMON_MIN_PERIODICITY 
            = DAEMON_MANAGER_CHECK_PERIOD / 2;
        final long DAEMON_SLEEP_TIME_AFTER_JOB 
            = Long.MAX_VALUE;
        final long DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN 
            = (long) (DAEMON_MIN_PERIODICITY * 0.2);
        final long DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX
            = (long) (DAEMON_MIN_PERIODICITY * 0.8);
        final int DAEMON_NUMBER_OF_SUCCESSFUL_RUNS 
            = 3;
        
        /**
         * Time the daemon manager should let work daemons.
         */
        final long DAEMON_WORKING_TIME = DAEMON_MANAGER_CHECK_PERIOD * 2;
        
        /**
         * Instantiate daemon manager.
         */
        DaemonManagerImpl daemonManager = createDaemonManagerImpl(
            DAEMON_MANAGER_CHECK_PERIOD, 
            DAEMON_MANAGER_DAEMON_JOIN_TIMEOUT, 
            DAEMON_MANAGER_MIN_DAEMON_STARTUP_DELAY, 
            DAEMON_MANAGER_MAX_DAEMON_STARTUP_DELAY, 
            DAEMON_MANAGER_CACHED_INFORMATION_MESSAGE_TIMEOUT, 
            DAEMON_MANAGER_MAX_MISSING_HEARTBEATS);
        
        /**
         * Needed variables by all sub tests.
         */
        DaemonManagerProcessor dmp = null;
        long sleepTimeUntilDaemonManagerShouldBeStopped = 0;
        
        /**
         * Exception while init daemon.
         */
        ActorDaemon daemon = createActorDaemon("Test daemon #3", 
            ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_INIT, 
            DAEMON_MIN_PERIODICITY, 
            DAEMON_SLEEP_TIME_AFTER_JOB, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN,
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX,
            DAEMON_NUMBER_OF_SUCCESSFUL_RUNS);

        daemonManager.addDaemon(daemon);
        dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        sleepTimeUntilDaemonManagerShouldBeStopped 
            = DAEMON_MANAGER_CHECK_PERIOD * 2;
        sleep(sleepTimeUntilDaemonManagerShouldBeStopped);
        checkDaemonCausedExceptionThrowingDaemon(daemonManager, daemon, dmp, 
            DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
        daemonManager.removeDaemon(daemon);

        
        /**
         * Exception while run job of daemon.
         */
        daemon = createActorDaemon("Test daemon #4", 
            ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN, 
            DAEMON_MIN_PERIODICITY, 
            DAEMON_SLEEP_TIME_AFTER_JOB, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN,
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX,
            DAEMON_NUMBER_OF_SUCCESSFUL_RUNS);
        
        daemonManager.canStartProcessing();
        daemonManager.addDaemon(daemon);
        dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        sleepTimeUntilDaemonManagerShouldBeStopped 
            = DAEMON_NUMBER_OF_SUCCESSFUL_RUNS * DAEMON_MIN_PERIODICITY
                + DAEMON_MANAGER_CHECK_PERIOD * 2;
        sleep(sleepTimeUntilDaemonManagerShouldBeStopped);
        checkDaemonCausedExceptionThrowingDaemon(daemonManager, daemon, dmp, 
            DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
        daemonManager.removeDaemon(daemon);

        /**
         * Exception while reconfigure daemon.
         */
        daemon = createActorDaemon("Test daemon #5", 
            ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE, 
            DAEMON_MIN_PERIODICITY, 
            DAEMON_SLEEP_TIME_AFTER_JOB, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN,
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX,
            DAEMON_NUMBER_OF_SUCCESSFUL_RUNS);
        
        daemonManager.canStartProcessing();
        daemonManager.addDaemon(daemon);
        dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        sleep(DAEMON_WORKING_TIME);
        checkIfDaemonManagerIsStillProcessing(dmp);
        daemonManager.doReconfigureDaemons();
        sleepTimeUntilDaemonManagerShouldBeStopped 
            = DAEMON_MANAGER_CHECK_PERIOD * 2;
        sleep(sleepTimeUntilDaemonManagerShouldBeStopped);
        checkDaemonCausedExceptionThrowingDaemon(daemonManager, daemon, dmp, 
            DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
        daemonManager.removeDaemon(daemon);

        /**
         * Exception while cleanup daemon.
         */
        daemon = createActorDaemon("Test daemon #6", 
            ActorRole.THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP, 
            DAEMON_MIN_PERIODICITY, 
            DAEMON_SLEEP_TIME_AFTER_JOB, 
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MIN,
            DAEMON_SIMULATE_DURATION_OF_ONE_JOB_RUN_MAX,
            DAEMON_NUMBER_OF_SUCCESSFUL_RUNS);
        
        daemonManager.canStartProcessing();
        daemonManager.addDaemon(daemon);
        dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        sleep(DAEMON_WORKING_TIME);
        checkIfDaemonManagerIsStillProcessing(dmp);
        daemonManager.removeDaemon(daemon);
        sleepTimeUntilDaemonManagerShouldBeStopped 
            = DAEMON_MANAGER_CHECK_PERIOD;
        sleep(sleepTimeUntilDaemonManagerShouldBeStopped);
        checkDaemonCausedExceptionThrowingDaemon(daemonManager, daemon, dmp, 
            DAEMON_MANAGER_PROCESSOR_JOIN_TIMEOUT);
        
        assertEquals("There is still a daemon registered.", 
            0, daemonManager.getDaemons().size());
    }

    /**
     * Checks if the given daemon terminates daemon manager as expected.
     * 
     * @param daemonManager
     *            Is the daemon manager to check.
     * @param daemon
     *            Is the daemon which throws a daemon caused exception.
     * @param dmp
     *            Is the daemon manager processor.
     * @param daemonManagerProcessorJoinTimeout
     *            Is the max join time in millis we should wait for daemon
     *            manager processor to die.
     * @throws InterruptedException
     *             Will be thrown if a thread was interrupted.
     */
    private void checkDaemonCausedExceptionThrowingDaemon(
        DaemonManagerImpl daemonManager, 
        ActorDaemon daemon,
        DaemonManagerProcessor dmp, 
        long daemonManagerProcessorJoinTimeout) 
        throws InterruptedException {
        
        assertFalse("Daemon manager has not stopped processing at expected "
            + "time.", daemonManager.isProcessing());
        
        dmp.join(daemonManagerProcessorJoinTimeout);
        assertFalse("Daemon manager processor has not been stopped as "
            + "expected.", dmp.isAlive());
        assertTrue("Daemon manager processor has not been terminated by a "
            + "'collection of daemon caused exceptions' exception.", 
            dmp.doesCollectionOfDaemonCausedExceptionOccurred());
        CollectionOfDaemonCausedRTException codce 
            = (CollectionOfDaemonCausedRTException) dmp.getRuntimeException();
        assertEquals("Set size of daemon caused exceptions is not one.",
            1, codce.getDaemonCausedExceptions().size());
        DaemonCausedRTException dce = (DaemonCausedRTException) 
            codce.getDaemonCausedExceptions().iterator().next();
        assertTrue("Caused daemon is not the expected one.", 
            dce.getCausedDaemon() == daemon);
    }
}
