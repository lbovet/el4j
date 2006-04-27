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

package ch.elca.el4j.services.daemonmanager;

import java.util.Set;

import ch.elca.el4j.services.daemonmanager.exceptions.CollectionOfDaemonCausedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonAlreadyStartedRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonManagerIsNotProcessingRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonManagerIsProcessingRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.DaemonsStillRunningRTException;
import ch.elca.el4j.services.daemonmanager.exceptions.MissingHeartbeatsRTException;

/**
 * This interface is the manager of daemons. The daemons that he contains 
 * will be managed by himself. Methods where its name start with <code>do</code>
 * will be executed in asynchronous way.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface DaemonManager {
    
    /**
     * By invoking this method the daemon manager will stop processing. This
     * method works in asynchronous way, this means that daemons will be stopped
     * gracefully.
     */
    public void doStopProcessing();
    
    /**
     * This method will set the flag, which indicates if the daemon manager is
     * allowed to start processing, to <code>true</code>. This method must be 
     * called every time the daemon manager was stopped and want to be able to 
     * be started again.
     */
    public void canStartProcessing();
    
    /**
     * The call of this method will call <code>doReconfigure</code> on each 
     * daemon.
     */
    public void doReconfigureDaemons();

    /**
     * By calling this method, daemons will run and this method will work until
     * method <code>doStopProcessing</code> is called.
     * 
     * @throws MissingHeartbeatsRTException
     *             Will be thrown if a daemon had missed to send heartbeats.
     * @throws CollectionOfDaemonCausedRTException
     *             Will be thrown if one or more
     *             <code>DaemonCausedRTException</code>s has been thrown.
     * @throws DaemonsStillRunningRTException
     *             Will be thrown if daemons which should be started are still
     *             alive.
     */
    public void process() throws MissingHeartbeatsRTException, 
        CollectionOfDaemonCausedRTException, DaemonsStillRunningRTException;
    
    /**
     * Method to get daemons while not processing on daemon manager.
     * 
     * @return Returns the list of daemons.
     * @throws DaemonManagerIsProcessingRTException
     *             Is thrown if daemon manager is processing.
     */
    public Set getDaemons() 
        throws DaemonManagerIsProcessingRTException;
    
    /**
     * Method to set daemons while not processing on daemon manager.
     * 
     * @param daemons Is a list which contains daemons.
     * @throws DaemonManagerIsProcessingRTException
     *             Is thrown if daemon manager is processing.
     */
    public void setDaemons(Set daemons) 
        throws DaemonManagerIsProcessingRTException;
    
    /**
     * Method to add a daemon. If daemon manager is processing the given daemon
     * will immediatelly be started.
     * 
     * @param daemon
     *            Is the daemon to add.
     * @return Returns <code>true</code> if the daemon could be successfully
     *         added.
     * @throws DaemonAlreadyStartedRTException
     *             If daemon is still alive.
     */
    public boolean addDaemon(Daemon daemon)
        throws DaemonAlreadyStartedRTException;

    /**
     * Method to remove a daemon. If daemon manager is processing it will
     * immediatelly tell the daemon to stop. The daemon itself will be stopped
     * in an asynchronous way.
     * 
     * @param daemon
     *            Is the daemon to remove.
     * @return Returns <code>true</code> if the daemon could be successfully
     *         removed.
     */
    public boolean removeDaemon(Daemon daemon);

    /**
     * Method to get running daemons while processing.
     * 
     * @return Returns running daemons.
     * @throws DaemonManagerIsNotProcessingRTException
     *             Is thrown if daemon manager is not processing.
     */
    public Set getRunningDaemons() 
        throws DaemonManagerIsNotProcessingRTException;
    
    /**
     * Method to get the number of running daemons. This function is only 
     * available while processing.
     * 
     * @return Returns the number of running daemons.
     * @throws DaemonManagerIsNotProcessingRTException
     *             Is thrown if daemon manager is not processing.
     */
    public int getNumberOfRunningDaemons() 
        throws DaemonManagerIsNotProcessingRTException;

    /**
     * This method is used to get information about this manager and its daemons
     * in a human readable way.
     * 
     * @return Returns the information about this manager and its daemons as a
     *         string.
     */
    public String getInformation();
}