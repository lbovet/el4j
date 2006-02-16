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
package ch.elca.el4j.services.daemonmanager.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import ch.elca.el4j.services.daemonmanager.DaemonManager;
import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonsStillRunningRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.MissingHeartbeatsRTException;

/**
 * Abstract daemon manager controller for java service wrapper.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 * @author Stéphane Rose (STR)
 */
public abstract class AbstractDaemonManagerController 
    implements WrapperListener, Runnable {
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
     * Default run indicator file path.
     */
    public static final String DEFAULT_RUN_INDICATOR_FILE_PATH 
        = "daemon-manager-is-running.txt";

    /**
     * Default daemon manager controller join timeout.
     */
    public static final long DEFAULT_DAEMON_MANAGER_CONTROLLER_JOIN_TIMEOUT 
        = 10000;

    /**
     * Private logger of this class.
     */
    private static Log s_logger
        = LogFactory.getLog(AbstractDaemonManagerController.class);

    /**
     * Daemon manager to work on.
     */
    protected DaemonManager m_daemonManager = null;

    /**
     * Thread the daemon manager is running.
     */
    protected Thread m_thread;

    /**
     * Hook method. Will be invoked before starting the daemon manager.
     * 
     * @param startAfterCrash
     *            Is <code>true</code> if the daemon manager crashed at last 
     *            execution.
     * @return Return <code>true</code> if the daemon manager should be started.
     */
    protected boolean preDaemonManagerControllerStart(
        boolean startAfterCrash) {
        // By default do nothing. Let daemon manager controller start.
        return true;
    }

    /**
     * @return Returns the created daemon manager.
     */
    protected abstract DaemonManager createDaemonManager();

    /**
     * It is recommended to override this method.
     * 
     * @return Returns the file path, where the file that indicates that the
     *         daemon manager is currently running, will be saved.
     */
    protected String getRunInicatorFilePath() {
        return DEFAULT_RUN_INDICATOR_FILE_PATH;
    }

    /**
     * @return Returns <code>true</code> if the run indicator file could be
     *         successfully written on disk.
     */
    protected boolean createRunIndicatorFile() {
        boolean success = false;
        try {
            File file = new File(getRunInicatorFilePath());
            success = file.createNewFile();
            if (success) {
                s_logger.debug("Run indicator file successfully created at '" 
                    + file.getAbsolutePath() + "'.");
            } else {
                s_logger.error("Run indicator file could not be created at '" 
                    + file.getAbsolutePath() + "'! Perhaps it already exits "
                    + "(like after a crash) or executing user has no write "
                    + "permission.");
            }
        } catch (IOException e) {
            s_logger.error("Could not create the run indicator file with path '"
                + getRunInicatorFilePath() + "'.", e);
            success = false;
        }
        return success;
    }
    
    /**
     * @return Returns <code>true</code> if the run indicator file exists. 
     */
    protected boolean doesRunIndicatorFileExists() {
        File file = new File(getRunInicatorFilePath());
        boolean fileExists = file.isFile() && file.exists();
        if (fileExists) {
            s_logger.debug("Run indicator file exists at '" 
                + file.getAbsolutePath() + "'.");
        } else {
            s_logger.debug("No run indicator file exists at '" 
                + file.getAbsolutePath() + "'!");
        }
        return fileExists;
    }
    
    /**
     * @return Returns <code>true</code> if the run indicator file could be
     *         successfully deleted from disk.
     */
    protected boolean deleteRunIndicatorFile() {
        File file = new File(getRunInicatorFilePath());
        boolean success = file.delete();
        if (success) {
            s_logger.debug("Run indicator file successfully delete from '" 
                + file.getAbsolutePath() + "'.");
        } else {
            s_logger.error("Run indicator file could not be deleted from '" 
                + file.getAbsolutePath() + "'!");
        }
        return success;
    }
    
    /**
     * It is recommended to override this method.
     * 
     * @return Returns the daemon manager controller join timeout.
     */
    protected long getDaemonManagerControllerJoinTimeout() {
        return DEFAULT_DAEMON_MANAGER_CONTROLLER_JOIN_TIMEOUT;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Do not override this method!
     */
    public void run() {
        s_logger.debug("Run method entered.");
        boolean fileExists = doesRunIndicatorFileExists();
        if (fileExists) {
            s_logger.info("Seams that last run of daemon manager controller "
                + "crashed. Run indicator file will now be deleted.");
            deleteRunIndicatorFile();
        }
        
        if (!preDaemonManagerControllerStart(fileExists)) {
            s_logger.info("Daemon manager controller will not be started "
                + "because answer of pre daemon manager start method was "
                + "false.");
            return;
        }
        
        try {
            createRunIndicatorFile();
            s_logger.info("Daemon manager controller started.");
            m_daemonManager.process();
            s_logger.info("Daemon manager controller terminated gracefully.");
            deleteRunIndicatorFile();
            postGracefullyTerminated();
        } catch (MissingHeartbeatsRTException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "missing heartbeats.", e);
            System.exit(EXIT_CODE_MISSING_HEARTBEATS);
        } catch (CollectionOfDaemonCausedRTException e) {
            Set daemons = e.getDaemonCausedExceptions();
            int numberOfExceptions = daemons.size();
            s_logger.error("Daemon manager controller terminated in cause of "
                + "daemon caused exceptions. See the " + numberOfExceptions 
                + "exception(s) below.", e);
            Iterator it = daemons.iterator();
            int exceptionNumber = 1;
            while (it.hasNext()) {
                s_logger.error("Daemon caused exception #" + exceptionNumber 
                    + " of " + numberOfExceptions + ".", (Throwable) it.next());
                exceptionNumber++;
            }
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

    /**
     * This method will be invoked if the daemon manager stopped processing
     * successfully. By default nothing will be done.
     */
    protected void postGracefullyTerminated() {
        // System.exit(EXIT_CODE_GRACEFULLY_TERMINATED);
    }

    /**
     * {@inheritDoc}
     * 
     * Creates and starts the daemon manager in a new thread.
     * 
     * Do not override this method!
     */
    public Integer start(String[] arg) {
        s_logger.debug("Start method entered.");
        m_daemonManager = createDaemonManager();
        m_thread = new Thread(this);
        m_thread.start();
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * Stops processing of daemon manager and waits the internal thread, where 
     * the daemon manager is running to die.
     * 
     * Do not override this method!
     */
    public int stop(int exitCode) {
        s_logger.debug("Stop method with exit code " + exitCode + " entered.");
        m_daemonManager.doStopProcessing();
        try {
            long timeout = getDaemonManagerControllerJoinTimeout();
            m_thread.join(timeout);
            if (m_thread.isAlive()) {
                s_logger.warn("The daemon manager controller is still alive "
                    + "after a timeout of " + timeout + "ms! The controller "
                    + "will now be stopped.");
            }
        } catch (InterruptedException e) {
            s_logger.warn("Join of thread where the daemon manager controller "
                + "is running has been interrupted.", e);
        }
        s_logger.debug("Stop method left.");
        return exitCode;
    }

    /**
     * {@inheritDoc}
     */
    public void controlEvent(int event) {
        s_logger.debug("Control event method with event #" + event 
            + " entered.");
        if (!WrapperManager.isControlledByNativeWrapper()) {
            // We are not being controlled by the Wrapper, so
            // handle the event ourselves.
            if ((event == WrapperManager.WRAPPER_CTRL_C_EVENT)
                || (event == WrapperManager.WRAPPER_CTRL_CLOSE_EVENT)
                || (event == WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT)) {
                WrapperManager.stop(0);
            }
        }
    }
}
