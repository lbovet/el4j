/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.daemonmanager.Daemon;
import ch.elca.el4j.services.daemonmanager.impl.AbstractDaemon;
import ch.elca.el4j.services.daemonmanager.impl.DaemonManagerImpl;
import ch.elca.el4j.tests.daemonmanager.helpers.ActionRegister;
import ch.elca.el4j.tests.daemonmanager.helpers.DaemonAction;
import ch.elca.el4j.tests.daemonmanager.helpers.DaemonManagerProcessor;

import junit.framework.TestCase;

/**
 * This test case is used to test daemon manager features like the daemon
 * factory and the ordered daemon startup and shutdown.
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
public class DaemonManagerFeaturesTest extends TestCase {
    
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DaemonManagerFeaturesTest.class);

    /**
     * Tests daemon manager with daemon factories.
     */
    public void testDaemonManagerWithDaemonFactroies() {
        s_logger.info("Test 'testDaemonManagerWithDaemonFactroies' started.");
        DaemonManagerImpl daemonManager = createDaemonManagerOne();
        Set daemons = daemonManager.getDaemons();
        assertEquals("Number of daemon incorrect.", 36, daemons.size());
        
        int daemonACounter = 0;
        int daemonBCounter = 0;
        int daemonCCounter = 0;
        int daemonDCounter = 0;
        Iterator it = daemons.iterator();
        while (it.hasNext()) {
            Daemon daemon = (Daemon) it.next();
            String id = daemon.getIdentification();
            if (id.startsWith("Daemon A")) {
                daemonACounter++;
            } else if (id.startsWith("Daemon B")) {
                daemonBCounter++;
            } else if (id.startsWith("Daemon C")) {
                daemonCCounter++;
            } else if (id.startsWith("Daemon D")) {
                daemonDCounter++;
            } else {
                fail("Unexpected daemon with name '" + id + "'.");
            }
        }
        assertEquals("Number of A daemons incorrect.", 5, daemonACounter);
        assertEquals("Number of B daemons incorrect.", 20, daemonBCounter);
        assertEquals("Number of C daemons incorrect.", 10, daemonCCounter);
        assertEquals("Number of D daemons incorrect.", 1, daemonDCounter);
    }
    
    /**
     * Tests the ordered starting of daemons in daemon manager.
     * 
     * @throws InterruptedException
     *             Will be thrown if a thread was interrupted.
     */
    public void testDaemonManagerWithOrderedDaemons()
        throws InterruptedException {
        DaemonManagerImpl daemonManager = createDaemonManagerOne();
        Set daemons = daemonManager.getDaemons();
        assertEquals("Number of daemon incorrect.", 36, daemons.size());
        
        // Check action lists. They should be empty.
        List actionList 
            = ActionRegister.getDaemonsByAction(DaemonAction.CLEANUP);
        assertTrue("Action list cleanup already polluted.", 
            actionList.isEmpty());
        actionList = ActionRegister.getDaemonsByAction(DaemonAction.INIT);
        assertTrue("Action list init already polluted.", 
            actionList.isEmpty());

        // Start daemon manager.
        DaemonManagerProcessor dmp = new DaemonManagerProcessor(daemonManager);
        dmp.start();
        
        // Wait until all daemons has been started up.
        sleep(daemonManager.getMaxDaemonStartupDelay() * 18);
        for (int i = 20; 
            daemonManager.getNumberOfRunningDaemons() < 36 && i > 0; i--) {
            sleep(daemonManager.getMaxDaemonStartupDelay());
        }
        assertEquals("Unexpected number of running daemons.", 36, 
            daemonManager.getNumberOfRunningDaemons());
        sleep(daemonManager.getMaxDaemonStartupDelay());
        
        // Check action lists.
        actionList = ActionRegister.getDaemonsByAction(DaemonAction.CLEANUP);
        assertTrue("Action list cleanup already polluted.", 
            actionList.isEmpty());
        actionList = ActionRegister.getDaemonsByAction(DaemonAction.INIT);
        assertEquals("Unexpected number of init daemon actions.", 36, 
            actionList.size());
        
        // Check order in action list init.
        Iterator it = actionList.iterator();
        int actionNumber = 1;
        while (it.hasNext()) {
            AbstractDaemon daemon = (AbstractDaemon) it.next();
            String id = daemon.getIdentification();
            if (!(id.startsWith("Daemon A") && actionNumber <= 5
                || id.startsWith("Daemon B") && actionNumber <= 25
                || id.startsWith("Daemon D") && actionNumber <= 26
                || id.startsWith("Daemon C") && actionNumber <= 36)
                || actionNumber <= 0) {
                fail("Unexpected daemon init order.");
            }
            actionNumber++;
        }
        
        // Stop daemon manager.
        stopAndCheckStoppedDaemonManager(daemonManager, dmp, 
            daemonManager.getCheckPeriod(), 10000);

        // Check action lists.
        actionList = ActionRegister.getDaemonsByAction(DaemonAction.INIT);
        assertEquals("Unexpected number of init daemon actions.", 36, 
            actionList.size());
        actionList = ActionRegister.getDaemonsByAction(DaemonAction.CLEANUP);
        assertEquals("Unexpected number of cleanup daemon actions.", 36, 
            actionList.size());

        // Check order in action list init.
        it = actionList.iterator();
        actionNumber = 36;
        while (it.hasNext()) {
            AbstractDaemon daemon = (AbstractDaemon) it.next();
            String id = daemon.getIdentification();
            if (!(id.startsWith("Daemon A") && actionNumber <= 5
                || id.startsWith("Daemon B") && actionNumber <= 25
                || id.startsWith("Daemon D") && actionNumber <= 26
                || id.startsWith("Daemon C") && actionNumber <= 36)
                || actionNumber <= 0) {
                fail("Unexpected daemon cleanup order.");
            }
            actionNumber--;
        }
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
     * Tests bad daemon manager.
     */
    public void testBadDaemonManager() {
        try {
            createBadDaemonManagerOne();
            fail("Bad daemon manager could be created successfully!");
        } catch (RuntimeException e) {
            s_logger.info("Execption while getting bad daemon manager caught "
                + "correctly.", e);
        }
    }

    /**
     * @return Returns created daemon manager.
     */
    private DaemonManagerImpl createDaemonManagerOne() {
        ModuleApplicationContext appContext = new ModuleApplicationContext(
            new String[] {"classpath*:mandatory/*.xml", 
                "daemon_manager_tests/testDaemonManagers.xml", 
                "daemon_manager_tests/testCorrectDaemons.xml"}, false);
        DaemonManagerImpl daemonManager 
            = (DaemonManagerImpl) appContext.getBean("daemonManagerOne");
        return daemonManager;
    }
    
    /**
     * @return Returns created bad daemon manager one.
     */
    private DaemonManagerImpl createBadDaemonManagerOne() {
        ModuleApplicationContext appContext = new ModuleApplicationContext(
            new String[] {"classpath*:mandatory/*.xml", 
                "daemon_manager_tests/testDaemonManagers.xml", 
                "daemon_manager_tests/testBadDaemons.xml"}, false);
        DaemonManagerImpl daemonManager 
            = (DaemonManagerImpl) appContext.getBean("daemonManagerOne");
        return daemonManager;
    }
}
