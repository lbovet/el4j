/*
 * AbstractExtendedDaemon.java
 *
 * Project: xxxx
 *
 * WHEN           WHO           WHAT            DESCRIPTION
 * 06.10.2005     str           create
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.daemonmanager.exceptions.DaemonAlreadyStartedRTException;

/**
 * This class extend default Leaf daemon to add memory shared and
 *  startup/shutdown level fonctionality
 *
 *
 * @author Stéphane Rose (STR)
 */
public abstract class AbstractExtendedDaemon extends AbstractDaemon implements
    Cloneable {

    /**
     * The minimum level used for startup and shutdown.
     */
    public static final int MIN_LEVEL = 0;

    /**
     * The maximum level used for startup and shutdown.
     */
    public static final int MAX_LEVEL = 12;

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(AbstractExtendedDaemon.class);

    /**
     * A map shared with others daemon.
     */
    private Map m_memoryShared = null;

    /**
     * The startup level of this daemon. As default use the
     */
    private int m_startupLevel = (MIN_LEVEL + MAX_LEVEL) / 2;
    /**
     *
     */
    private int m_shutdownLevel = MIN_LEVEL - 1; // no shutdown level

    /**
     * Number of instance for this daemon.
     */
    private int m_instance = 1;

    /**
     * True if first instance of daemon pool.
     */
    private boolean m_isFirstInstance = false;

    /**
     * @param memoryShared xx
     */
    public void setMemorySharedMap(Map memoryShared) {
        m_memoryShared = memoryShared;
    }

    /**
     * @return xx
     */
    protected Map getMemorySharedMap() {
        // never return null, return an empty map
        if (m_memoryShared == null)
            m_memoryShared = new HashMap(0);
        return m_memoryShared;
    }

    /**
     * @param level
     */
    public void setStartupLevel(int level) {
        synchronized (m_daemonLock) {
            if (level >= MIN_LEVEL && level <= MAX_LEVEL)
                m_startupLevel = level;
            else
                s_logger.error("Daemon with identification '"
                    + getIdentification() + "' has incorrect startup level ("
                    + level + "). " + "Must be between " + MIN_LEVEL + " and "
                    + MAX_LEVEL);
        }
    }

    /**
     * @param level
     */
    public void setShutdownLevel(int level) {
        synchronized (m_daemonLock) {
            if (level >= MIN_LEVEL && level <= MAX_LEVEL)
                m_shutdownLevel = level;
            else
                s_logger.error("Daemon with identification '"
                    + getIdentification() + "' has incorrect stutdown level ("
                    + level + "). " + "Must be between " + MIN_LEVEL + " and "
                    + MAX_LEVEL);
        }
    }

    /**
     * @param level
     */
    public void setStartupLevel(String level) {
        if (level != null) {
            int intLevel;
            try {
                intLevel = Integer.parseInt(level);
            } catch (NumberFormatException e) {
                s_logger.error("Daemon with identification '"
                    + getIdentification()
                    + "' has incorrect number startup level (" + level + "). "
                    + "Must be between " + MIN_LEVEL + " and " + MAX_LEVEL, e);
                return;
            }
            setStartupLevel(intLevel);
        } else
            s_logger.error("Daemon with identification '" + getIdentification()
                + "' has null startup level. " + "Must be between " + MIN_LEVEL
                + " and " + MAX_LEVEL);
    }

    /**
     * @param level
     */
    public void setShutdownLevel(String level) {
        if (level != null) {
            int intLevel;
            try {
                intLevel = Integer.parseInt(level);
            } catch (NumberFormatException e) {
                s_logger.error("Daemon with identification '"
                    + getIdentification()
                    + "' has incorrect number shutdown level (" + level + "). "
                    + "Must be between " + MIN_LEVEL + " and " + MAX_LEVEL, e);
                return;
            }
            setShutdownLevel(intLevel);
        } else
            s_logger.error("Daemon with identification '" + getIdentification()
                + "' has null shutdown level. " + "Must be between "
                + MIN_LEVEL + " and " + MAX_LEVEL);
    }

    /**
     * @return
     */
    public int getStartupLevel() {
        synchronized (m_daemonLock) {
            return m_startupLevel;
        }
    }

    /**
     * @return
     */
    public int getShutdownLevel() {
        synchronized (m_daemonLock) {
            return m_shutdownLevel;
        }
    }

    /**
     * @param level
     * @return
     * @throws DaemonAlreadyStartedRTException
     */
    public final boolean startDaemon(int level)
        throws DaemonAlreadyStartedRTException {
        synchronized (m_daemonLock) {
            if (level == m_startupLevel) {
                s_logger.info("Start daemon " + getIdentification()
                    + " at level " + level);
                startDaemon();
                return true;
            } else
                return false;
        }
    }

    /**
     * @param level
     * @param timeout
     * @return
     */
    public final boolean stopDaemon(int level, long timeout) {
        int shutdownLevel = getShutdownLevel();
        // in case no level defined
        if ( shutdownLevel < MIN_LEVEL) {
            s_logger.info("Stop daemon " + getIdentification() + " at level "
                + level);
            doStop();
            return true;
        }

        if (level == shutdownLevel) {
            s_logger.info("Stop daemon " + getIdentification() + " at level "
                + level);
            doStop();
            s_logger.info("Trying to join daemon '" + getIdentification()
                + "' for "+timeout+" millisecond. ");
            try {
                joinDaemon(timeout);
                s_logger.info("Join daemon '" + getIdentification()
                    + "' terminated");
            } catch (InterruptedException e) {
                s_logger.debug("Join of daemon '" + getIdentification()
                    + "' has been interrupted.");
            }
            return true;
        } else
            return false;
    }

    /**
     * {@inheritDoc}
     *
     * All available information will be returned in twiki style.
     */
    public String getInformation() {
        String info = super.getInformation();
        synchronized (m_daemonLock) {
            StringBuffer sb = new StringBuffer(info);
            appendPriorityInformation(sb);
            return sb.toString();
        }
    }

    protected void appendPriorityInformation(StringBuffer sb) {
        sb.append("   * Startup level is set to ");
        sb.append(getStartupLevel());
        sb.append(NEWLINE);
        if (getShutdownLevel() < MIN_LEVEL)
            sb.append("   * Shutdown level is not set");
        else {
            sb.append("   * Shutdown level is set to ");
            sb.append(getShutdownLevel());
        }
        sb.append(NEWLINE);
        sb.append(NEWLINE);
    }

    /**
     * @return the number of instance of this daemon.
     */
    public int getInstance() {
        return m_instance;
    }

    /**
     * @param instance set the number of instance for this daemon.
     */
    public void setInstance(int instance) {
        m_instance = instance;
    }

    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException {
        s_logger.debug("clone()");
        return super.clone();
    }

    /**
     * {@inheritDoc}
     */
    public void sendHeartbeat() {
        super.sendHeartbeat();
    }

    /**
     * @return True if this daemon is the first instance of a set of same daemon
     */
    public boolean isFirstInstance() {
        return m_isFirstInstance;
    }

    /**
     * @param isFirstInstance Set if this daemon is the first of a set of same
     * daemon.
     */
    public void setFirstInstance(boolean isFirstInstance) {
        m_isFirstInstance = isFirstInstance;
    }
}
