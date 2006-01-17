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

package ch.elca.el4j.services.monitoring.notification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.services.daemonmanager.Daemon;

/**
 * This class is used to notify on events which are daemon manager based.
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
public final class DaemonManagerNotificationHelper {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DaemonManagerNotificationHelper.class);
    
    /**
     * Hide default constructor.
     */
    private DaemonManagerNotificationHelper() {
    } 

    /**
     * Method to log that current daemon does not have an identification. This
     * method will always throw an exception.
     * 
     * @throws MisconfigurationRTException
     *             Will be thrown in every case.
     */
    public static void notifyMissingDaemonIdentification()
        throws MisconfigurationRTException {
        String message = "Current daemon does not have an identification.";
        s_logger.error(message);
        throw new MisconfigurationRTException(message);
    }
    
    /**
     * Method to log that that the given daemon has no daemon observer
     * registered. This method will always throw an exception.
     * 
     * @param daemon
     *            Is the daemon where a daemon observer is missing.
     * @throws MisconfigurationRTException
     *             Will be thrown in every case.
     */
    public static void notifyNoDaemonObserverRegistered(Daemon daemon)
        throws MisconfigurationRTException {
        String message = "There is no daemon observer registered for daemon '" 
            + daemon.getIdentification() + "'.";
        s_logger.error(message);
        throw new MisconfigurationRTException(message);
    }

    /**
     * Method to log that that the given daemon has broken boundary condition.
     * This method will always throw an exception.
     * 
     * @param value
     *            Is the value which not satisfy.
     * @param min
     *            Is the minimum allowed value.
     * @param max
     *            Is the maximum allowed value.
     * @param property
     *            is the property which is not correctly set.
     * @param daemon
     *            Is the daemon where the property is not set properly.
     * @throws MisconfigurationRTException
     *             Will be thrown in every case.
     */
    public static void notifyBrokenBoundaryCondition(
        long value, long min, long max, String property, Daemon daemon) 
        throws MisconfigurationRTException {
        String message = "Boundary condition broken for property '" + property
            + "' of demaon '" + daemon.getIdentification() + "'. Value is "
            + value + ", minimum is " + min + ", maximum is " + max + ".";
        s_logger.error(message);
        throw new MisconfigurationRTException(message);
    }
}