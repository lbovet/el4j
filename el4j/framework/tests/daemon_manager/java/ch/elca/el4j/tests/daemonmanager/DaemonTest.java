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

package ch.elca.el4j.tests.daemonmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.tests.daemonmanager.helpers.ActorDaemon;
import ch.elca.el4j.tests.daemonmanager.helpers.ActorDaemonObserver;

import junit.framework.TestCase;

/**
 * This test case is used to test daemon class 
 * <code>ch.elca.el4j.services.daemonmanager.impl.AbstractDaemon</code>.
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
public class DaemonTest extends TestCase {
    /**
     * Is the time we should wait for a daemon to die.
     */
    public static final int DAEMON_JOIN_TIMEOUT = 2000;

    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DaemonTest.class);

    /**
     * Tests missconfigured daemon if they stop as expected.
     * 
     * @throws InterruptedException
     *             Will be thrown if the join of the daemon has been
     *             interrupted.
     */
    public void testMissconfiguredDaemons() throws InterruptedException {
        ActorDaemon daemon = new ActorDaemon();
        ActorDaemonObserver daemonObserver = new ActorDaemonObserver();
        
        daemon.setIdentification("");
        daemon.startDaemon();
        checkIfDaemonHasStoppedAndMissconfigurationExceptionHasOccurred(daemon);
        
        
        daemon.setIdentification("Missconfigured Daemon 2");
        daemon.startDaemon();
        checkIfDaemonHasStoppedAndMissconfigurationExceptionHasOccurred(daemon);

        daemon.setIdentification("Missconfigured Daemon 3");
        daemon.registerDaemonObserver(daemonObserver);
        daemon.setMinPeriodicity(-1);
        daemon.startDaemon();
        checkIfDaemonHasStoppedAndMissconfigurationExceptionHasOccurred(daemon);

        daemon.setIdentification("Missconfigured Daemon 4");
        daemon.registerDaemonObserver(daemonObserver);
        daemon.setSleepTimeAfterJob(-1);
        daemon.startDaemon();
        checkIfDaemonHasStoppedAndMissconfigurationExceptionHasOccurred(daemon);
    }
    
    /**
     * Tests normal acting with fast job daemon. The daemon will be rerun 
     * periodically. This test includes also the testing if overridden methods
     * where invoked in correct order.
     * 
     * @throws InterruptedException
     *             Will be thrown if the join of the daemon has been
     *             interrupted.
     */
    public void testNormalActingFastJobPeriodicallyDaemon() 
        throws InterruptedException {
        ActorDaemon daemon = new ActorDaemon();
        ActorDaemonObserver daemonObserver = new ActorDaemonObserver();
        
        final long PERIODICITY = 1000;
        final long MIN_RUN_JOB_DURATION = (long) (PERIODICITY * 0.2);
        final long MAX_RUN_JOB_DURATION = (long) (PERIODICITY * 0.8);
        final long NUMBER_OF_RUNS = 10;
        
        daemon.setIdentification("Fast job daemon executed periodically.");
        daemon.registerDaemonObserver(daemonObserver);
        daemon.setMinPeriodicity(PERIODICITY);
        daemon.setSleepTimeAfterJob(Long.MAX_VALUE);
        daemon.setSimulateDurationOfOneJobRunMin(MIN_RUN_JOB_DURATION);
        daemon.setSimulateDurationOfOneJobRunMax(MAX_RUN_JOB_DURATION);
        
        s_logger.info("Daemon information before started:\n" 
            + daemon.getInformation());
        
        checkMethodInvocationOrder(daemon, false, false, false, false, false);
        daemon.startDaemon();
        
        checkFastJobPeriodicallyDaemonWhileRunning(
            daemon, PERIODICITY, NUMBER_OF_RUNS);

        daemon.doStop();
        daemon.joinDaemon(DAEMON_JOIN_TIMEOUT);
        checkMethodInvocationOrder(daemon, true, true, true, true, true);
        
        s_logger.info("Daemon information after stopped:\n" 
            + daemon.getInformation());
        
        assertFalse("Daemon should not be started anymore.",
            daemon.isDaemonRunning());
    }

    /**
     * Method to check the periodically executed fast daemon job while running.
     * 
     * @param daemon
     *            Is the daemon to check.
     * @param periodicity
     *            Is the periodicity the test should made.
     * @param numberOfRuns
     *            Is the total number of check should be made.
     */
    private void checkFastJobPeriodicallyDaemonWhileRunning(
        ActorDaemon daemon, final long periodicity, final long numberOfRuns) {
        sleep(periodicity / 2);
        int i = 1;
        while (i <= numberOfRuns / 2) {
            int runJobCounter = daemon.getRunJobCounter();
            assertEquals("Job of daemon was not executed as expected.", i, 
                runJobCounter);
            s_logger.info("Daemon information while running:\n" 
                + daemon.getInformation());
            sleep(periodicity);
            i++;
            checkMethodInvocationOrder(daemon, true, true, true, false, false);
        }
        daemon.doReconfigure();
        while (i <= numberOfRuns) {
            int runJobCounter = daemon.getRunJobCounter();
            assertEquals("Job of daemon was not executed as expected.", i, 
                runJobCounter);
            s_logger.info("Daemon information while running:\n" 
                + daemon.getInformation());
            sleep(periodicity);
            i++;
            checkMethodInvocationOrder(daemon, true, true, true, true, false);
        }
    }
    
    /**
     * Method to check if the overridden method of abstract daemon where called
     * correctly.
     * 
     * @param daemon
     *            Is the daemon where to check.
     * @param checkNeededConfiguration
     *            Is the expected state for method
     *            <code>checkNeededConfiguration</code>.
     * @param init
     *            Is the expected state for method <code>init</code>.
     * @param runJob
     *            Is the expected state for method <code>runJob</code>.
     * @param reconfigure
     *            Is the expected state for method <code>reconfigure</code>.
     * @param cleanup
     *            Is the expected state for method <code>cleanup</code>.
     */
    private void checkMethodInvocationOrder(ActorDaemon daemon,
        boolean checkNeededConfiguration, 
        boolean init, 
        boolean runJob, 
        boolean reconfigure, 
        boolean cleanup) {
        
        assertEquals(
            "Method 'neededConfigurationChecked' of daemon incorrect called.", 
            checkNeededConfiguration, 
            daemon.isCheckNeededConfigurationCalled());
        assertEquals("Method 'init' of daemon incorrect called.", 
            init, daemon.isInitCalled());
        assertEquals("Method 'runJob' of daemon incorrect called.", 
            runJob, daemon.isRunJobCalled());
        assertEquals("Method 'reconfigure' of daemon incorrect called.", 
            reconfigure, daemon.isReconfigureCalled());
        assertEquals("Method 'cleanup' of daemon incorrect called.", 
            cleanup, daemon.isCleanupCalled());
    }
    

    /**
     * Tests normal acting with fast job daemon. The daemon will be rerun 
     * after sleeping a constant time after job is done.
     * 
     * @throws InterruptedException
     *             Will be thrown if the join of the daemon has been
     *             interrupted.
     */
    public void testNormalActingFastJobSleepAfterJobDaemon() 
        throws InterruptedException {
        ActorDaemon daemon = new ActorDaemon();
        ActorDaemonObserver daemonObserver = new ActorDaemonObserver();
        
        final long SLEEP_TIME_AFTER_JOB = 1000;
        final long RUN_JOB_DURATION = 200;
        final long APPROXIMATED_PERIODICITY 
            = SLEEP_TIME_AFTER_JOB + RUN_JOB_DURATION;
        final long NUMBER_OF_RUNS = 10;
        
        daemon.setIdentification(
            "Fast job daemon executed after a pause of run job end.");
        daemon.registerDaemonObserver(daemonObserver);
        daemon.setMinPeriodicity(Long.MAX_VALUE);
        daemon.setSleepTimeAfterJob(SLEEP_TIME_AFTER_JOB);
        daemon.setSimulateDurationOfOneJobRunMin(RUN_JOB_DURATION);
        daemon.setSimulateDurationOfOneJobRunMax(RUN_JOB_DURATION);
        s_logger.info("Daemon information before started:\n" 
            + daemon.getInformation());
        daemon.startDaemon();
        sleep(APPROXIMATED_PERIODICITY / 2);
        for (int i = 1; i < NUMBER_OF_RUNS; i++) {
            int runJobCounter = daemon.getRunJobCounter();
            assertEquals("Job of daemon was not executed as expected.", i, 
                runJobCounter);
            s_logger.info("Daemon information while running:\n" 
                + daemon.getInformation());
            sleep(APPROXIMATED_PERIODICITY);
        }
        daemon.doStop();
        daemon.joinDaemon(DAEMON_JOIN_TIMEOUT);
        s_logger.info("Daemon information after stopped:\n" 
            + daemon.getInformation());
        assertFalse("Daemon should not be started anymore.",
            daemon.isDaemonRunning());
    }

    /**
     * Method to check if the given daemon has been stopped and a
     * missconfiguration exception has occurred.
     * 
     * @param daemon
     *            Is the daemon to check.
     * @throws InterruptedException
     *             Will be thrown if the join of the daemon has been
     *             interrupted.
     */
    private void 
    checkIfDaemonHasStoppedAndMissconfigurationExceptionHasOccurred(
        ActorDaemon daemon) throws InterruptedException {
        daemon.joinDaemon(DAEMON_JOIN_TIMEOUT);
        assertFalse("Daemon should not be started anymore.",
            daemon.isDaemonRunning());
        
        RuntimeException re = daemon.getThrowedRuntimeExceptionInRunMethod();
        assertNotNull("There has no runtime exception occurred.", re);
        assertTrue("Throwed exception should be a missconfiguration exception.",
            re instanceof MisconfigurationRTException);
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
}
