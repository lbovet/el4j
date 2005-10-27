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

package ch.elca.el4j.tests.daemonmanager.helpers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.daemonmanager.DaemonManager;
import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonsStillRunningRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.MissingHeartbeatsRTException;

/**
 * This class is used to let the daemon manager process.
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
public class DaemonManagerProcessor extends Thread {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DaemonManagerProcessor.class);

    /**
     * Reference to the daemon manager to process.
     */
    protected final DaemonManager m_daemonManager;

    /**
     * Is the runtime exception which was thrown while processing.
     */
    protected RuntimeException m_runtimeException;
    
    /**
     * Is a special lock for this daemon manager processor.
     */
    protected final Object m_daemonManagerProcessorLock = new Object();
    
    /**
     * Constructor.
     * 
     * @param daemonManager
     *            Is the daemon manager to process.
     */
    public DaemonManagerProcessor(DaemonManager daemonManager) {
        m_daemonManager = daemonManager;
        
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        s_logger.debug("Daemon manager will be processed.");
        try {
            m_daemonManager.process();
            s_logger.debug("Daemon manager has gracefully stopped processing.");
        } catch (MissingHeartbeatsRTException e) {
            setRuntimeException(e);
            s_logger.info("Daemon manager received a missing heartbeat "
                + "exception while processing.");
        } catch (CollectionOfDaemonCausedRTException e) {
            setRuntimeException(e);
            s_logger.info("Daemon manager received a collection of daemon "
                + "caused exception while processing.");
        } catch (DaemonsStillRunningRTException e) {
            setRuntimeException(e);
            s_logger.info("Daemon manager received a daemons still running "
                + "exception while processing.");
        } catch (RuntimeException e) {
            setRuntimeException(e);
            s_logger.warn("Daemon manager received a runtime exception which "
                + "was not expected while processing.", e);
        }
    }

    /**
     * @return Returns the runtimeException.
     */
    public final RuntimeException getRuntimeException() {
        synchronized (m_daemonManagerProcessorLock) {
            return m_runtimeException;
        }
    }

    /**
     * @param runtimeException The runtimeException to set.
     */
    public final void setRuntimeException(RuntimeException runtimeException) {
        synchronized (m_daemonManagerProcessorLock) {
            m_runtimeException = runtimeException;
        }
    }
    
    /**
     * @return Returns <code>true</code> if daemon manager has terminated 
     * normally.
     */
    public final boolean doesProcessingTerminatedNormally() {
        return getRuntimeException() == null;
    }
    
    /**
     * @return Returns <code>true</code> if a
     * <code>MissingHeartbeatsRTException</code> has occurred.
     */
    public final boolean doesMissingHeartbeatsExceptionOccurred() {
        return !doesProcessingTerminatedNormally() 
            && getRuntimeException() instanceof MissingHeartbeatsRTException;
    }
    
    /**
     * @return Returns <code>true</code> if a
     * <code>CollectionOfDaemonCausedRTException</code> has occurred.
     */
    public final boolean doesCollectionOfDaemonCausedExceptionOccurred() {
        return !doesProcessingTerminatedNormally() 
            && getRuntimeException() 
                instanceof CollectionOfDaemonCausedRTException;
    }

    /**
     * @return Returns <code>true</code> if a
     * <code>DaemonsStillRunningRTException</code> has occurred.
     */
    public final boolean doesDaemonsStillRunningExceptionOccurred() {
        return !doesProcessingTerminatedNormally() 
            && getRuntimeException() 
                instanceof DaemonsStillRunningRTException;
    }
}
