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

package ch.elca.el4j.demos.daemonmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.daemonmanager.DaemonManager;
import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonsStillRunningRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.MissingHeartbeatsRTException;

/**
 * This class is the controller for the daemon manager. On exception it does
 * exit with a specific error code. With this behaviour it is possible to wrap
 * this class for example with java service wrapper to decide what to do after
 * a crash (restart or not).
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
public final class Controller {
    /**
     * Application context inclusive config location.
     */
    public static final String[] INCLUSIVE_CONFIG_LOCATION 
        = {"classpath*:mandatory/*.xml", 
            "daemon_manager_demos/common/*.xml", 
            "daemon_manager_demos/controller/*.xml"};

    /**
     * Flag to tell application context if bean definition overriding is 
     * allowed.
     */
    public static final boolean ALLOW_BEAN_DEFINITION_OVERRIDING = true;
    
    /**
     * Bean name of the daemon manager.
     */
    public static final String DAEMON_MANAGER_BEAN_NAME = "daemonManagerOne";
    
    /**
     * Exit code if heartbeats had missed.
     */
    public static final int EXIT_CODE_MISSING_HEARTBEATS = -10;

    /**
     * Exit code if daemon caused exceptions occurred.
     */
    public static final int EXIT_CODE_DAEMON_CAUSED_EXCEPTIONS = -11;

    /**
     * Exit code if daemons of daemon manager do still running before starting.
     */
    public static final int EXIT_CODE_DAEMONS_STILL_RUNNING = -12;

    /**
     * Exit code if there was a throwable for an unknown reason.
     */
    public static final int EXIT_CODE_UNKNOWN_REASON = -20;

    /**
     * Exit code if daemon manager has been gracefully terminated.
     */
    public static final int EXIT_CODE_GRACEFULLY_TERMINATED = 0;

    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(Controller.class);
    
    /**
     * Hide constructor.
     */
    private Controller() { }
    
    /**
     * Main method.
     * 
     * @param args Are the arguments from console.
     */
    public static void main(String[] args) {
        ModuleApplicationContext appContext = new ModuleApplicationContext(
            INCLUSIVE_CONFIG_LOCATION, ALLOW_BEAN_DEFINITION_OVERRIDING);
        DaemonManager daemonManager 
            = (DaemonManager) appContext.getBean(DAEMON_MANAGER_BEAN_NAME);
        try {
            daemonManager.process();
            s_logger.info("Daemon manager controller terminated gracefully.");
            System.exit(EXIT_CODE_GRACEFULLY_TERMINATED);
        } catch (MissingHeartbeatsRTException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "missing heartbeats.", e);
            System.exit(EXIT_CODE_MISSING_HEARTBEATS);
        } catch (CollectionOfDaemonCausedRTException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "daemon caused exceptions.", e);
            System.exit(EXIT_CODE_DAEMON_CAUSED_EXCEPTIONS);
        } catch (DaemonsStillRunningRTException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "daemons which where still running before starting.", e);
            System.exit(EXIT_CODE_DAEMONS_STILL_RUNNING);
        } catch (RuntimeException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "an unknown reason.", e);
            System.exit(EXIT_CODE_UNKNOWN_REASON);
        }
    }
}
