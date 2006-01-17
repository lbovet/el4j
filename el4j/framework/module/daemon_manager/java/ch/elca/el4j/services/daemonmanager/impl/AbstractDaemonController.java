/*
 * AbstractDaemonController.java
 *
 * Project: xxxx
 *
 * WHEN           WHO           WHAT            DESCRIPTION
 * 07.10.2005     str           create
 *
 * Copyright 2005 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */

package ch.elca.el4j.services.daemonmanager.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.daemonmanager.DaemonManager;
import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonsStillRunningRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.MissingHeartbeatsRTException;

/**
 * This abstract class provide the implementation for a control of a set of
 * extanded daemon <code>AbstractExtendedDaemon</code>. You need just to
 * implement 3 method corresponding to your daemons configuration
 *
 *
 *
 * @author Stéphane Rose (STR)
 */
public abstract class AbstractDaemonController implements WrapperListener,
    Runnable {

    /**
     * Exit code if heartbeats had missed.
     */
    public static final String START_AFTER_CRASH_DATE = "CRASH_DATE";

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
     * Exit code if there was a trowable for an unknown reason.
     */
    public static final int EXIT_CODE_UNKNOWN_REASON = -20;

    /**
     * Exit code if daemon manager has been gracefully terminated.
     */
    public static final int EXIT_CODE_GRACEFULLY_TERMINATED = 0;

    /**
     * The name of the file create to know if the daemon manager crash or finish
     * normally.
     */
    public static final String RUN_FILE = "Daemon.run";

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(AbstractDaemonController.class);

    /**
     * An reference of the daemon manager, used to start and stop daemons.
     */
    private DaemonManager m_daemonManager = null;


    /**
     *  Spring application context.
     */
    private ApplicationContext m_appContext = null;

    /**
     * The thread in which the daemon manager run.
     */
    private Thread m_manager = null;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     *
     */
    protected AbstractDaemonController() {
        // do nothing
    }

    /*---------------------------------------------------------------
     * Abstract methods
     *-------------------------------------------------------------*/
    /**
     * Put into a Map all data (constant, variable, object, etc..) you want
     * share between all daemon.
     * @return a custom map, can be null
     */
    protected abstract Map getMemorySharedMap();

    /**
     * Return the name of bean daemon definied in your config file
     * (normaly daemonManager.xml).
     * @return the bean id, cannot be null
     */
    protected abstract String getDaemonBeanName();

    /**
     * Return an array of String used to configure the
     * <code>ModuleApplicationContext</code>.
     * @see ch.elca.el4j.core.context.ModuleApplicationContext
     * @return an array of config paths
     */
    protected abstract String[] getInclusiveConfigLocation();

    /*---------------------------------------------------------------
     * Runnable method
     *-------------------------------------------------------------*/
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        s_logger.debug("run()");
        try {
            Map memoryShared = getMemorySharedMap();
            if( memoryShared == null)
                memoryShared = new HashMap();
            if( isStartAfterCrash() )
            {
                memoryShared.put(START_AFTER_CRASH_DATE, new Date());
            }

            Set daemons = m_daemonManager.getDaemons();
            Iterator it = daemons.iterator();
            while (it.hasNext()) {
                AbstractExtendedDaemon daemon = (AbstractExtendedDaemon) it
                    .next();
                daemon.setMemorySharedMap(memoryShared);
            }

            // create run file
            File file = new File(RUN_FILE);
            try {
                if( !file.createNewFile() ) {
                    s_logger.error("Can't create run file "+file.getAbsolutePath());
                }
            } catch(IOException e) {
                s_logger.error("Can't create run file "+file.getAbsolutePath(), e);
            }

            s_logger.info("Start batch");
            m_daemonManager.process();
            s_logger.info("Daemon manager controller terminated gracefully.");
            if( !file.delete() ) {
                s_logger.error("Can't delete file "+file.getAbsolutePath());
            }
            System.exit(EXIT_CODE_GRACEFULLY_TERMINATED);
        } catch (MissingHeartbeatsRTException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "missing heartbeats.", e);
            System.exit(EXIT_CODE_MISSING_HEARTBEATS);
        } catch (CollectionOfDaemonCausedRTException e) {
            s_logger.error("Daemon manager controller terminated in cause of "
                + "daemon caused exceptions.", e);
            Set set = e.getDaemonCausedExceptions();
            Iterator it = set.iterator();
            while( it.hasNext() ) {
                s_logger.error("Exception collection", (Throwable)it.next());
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

    /*---------------------------------------------------------------
     * WrapperListener Methods
     *-------------------------------------------------------------*/
    /**
     * The start method is called when the WrapperManager is signaled by the
     * native wrapper code that it can start its application. This method call
     * is expected to return, so a new thread should be launched if necessary.
     *
     * @param arg List of arguments used to initialize the application.
     *
     * @return Any error code if the application should exit on completion of
     *         the start method. If there were no problems then this method
     *         should return null.
     */

    public Integer start(String[] arg) {
        s_logger.debug("start()");
        /**
         * Load deamon manager from application context.
         */
        m_appContext = new ModuleApplicationContext(
            getInclusiveConfigLocation(), false);

        m_daemonManager = (DaemonManager)
            m_appContext.getBean(getDaemonBeanName());
        m_manager = new Thread(this);
        m_manager.start();
        return null;
    }

    /**
     * Called when the application is shutting down. The Wrapper assumes that
     * this method will return fairly quickly. If the shutdown code code could
     * potentially take a long time, then WrapperManager.signalStopping() should
     * be called to extend the timeout period. If for some reason, the stop
     * method can not return, then it must call WrapperManager.stopped() to
     * avoid warning messages from the Wrapper.
     *
     * @param exitCode
     *            The suggested exit code that will be returned to the OS when
     *            the JVM exits.
     * @return The exit code to actually return to the OS. In most cases, this
     *         should just be the value of exitCode, however the user code has
     *         the option of changing the exit code if there are any problems
     *         during shutdown.
     */
    public int stop(int exitCode) {
        s_logger.debug("stop()");
        m_daemonManager.doStopProcessing();
        try {
            m_manager.join();
        } catch (InterruptedException e) {
            s_logger.error("sad", e);
        }
        return exitCode;
    }

    /**
     * Called whenever the native wrapper code traps a system control signal
     * against the Java process. It is up to the callback to take any actions
     * necessary. Possible values are: WrapperManager.WRAPPER_CTRL_C_EVENT,
     * WRAPPER_CTRL_CLOSE_EVENT, WRAPPER_CTRL_LOGOFF_EVENT, or
     * WRAPPER_CTRL_SHUTDOWN_EVENT
     *
     * @param event The system control signal.
     */
    public void controlEvent(int event) {
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

    /**
     * This method verifies if the last shutdown was successful.
     * @return true if a crash is detected, false otherwise
     */
    protected boolean isStartAfterCrash() {
        File file = new File(RUN_FILE);
        if( file.exists() ) {
            if( !file.delete() )
                s_logger.error("Can't delete file "+file.getAbsolutePath());
            return true;
        }
        return false;
    }

    public ApplicationContext getApplicationContext() {
        return m_appContext;
    }
}
