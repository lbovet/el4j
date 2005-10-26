/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.services.daemonmanager;

import ch.elca.el4j.services.daemonmanager.exceptions.DaemonAlreadyStartedRTException;

/**
 * This interface is used to describe a daemon. Methods where its name start
 * with <code>do</code> will be executed in asynchronous way.
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
public interface Daemon {
    
    /**
     * This method is used to tell the daemon that it should stop working. The
     * <code>do</code> in the method name means that this method is working in
     * an asynchronous way.
     */
    public void doStop();

    /**
     * This method is used to tell the daemon that it should reconfigure itself.
     * The <code>do</code> in the method name means that this method is
     * working in an asynchronous way.
     */
    public void doReconfigure();

    /**
     * This method is used to get information about this daemon in a human
     * readable way.
     * 
     * @return Returns the information about this daemon as a string.
     */
    public String getInformation();

    /**
     * This method is used to identify this daemon. The idea is to get a short
     * version of method <code>getInformation</code>.
     * 
     * @return Returns the identification string.
     */
    public String getIdentification();

    /**
     * This method is used to start the daemon immediately. A daemon can be
     * started again after it has been stopped.
     * 
     * @throws DaemonAlreadyStartedRTException
     *             If the daemon is still alive.
     */
    public void startDaemon() throws DaemonAlreadyStartedRTException;
    
    /**
     * This method is used to check if a daemon is running.
     * 
     * @return Returns <code>true</code> if the daemon is running.
     */
    public boolean isDaemonRunning();

    /**
     * This method is used to join the daemon. It will wait at most
     * <code>timeout</code> milliseconds for this daemon to die. A timeout of
     * <code>0</code> means to wait forever.
     * 
     * @param timeout
     *            Is the most wait time in milliseconds.
     * @throws InterruptedException
     *             Is thrown if someone has interrupted this daemon.
     */
    public void joinDaemon(long timeout) throws InterruptedException;

    /**
     * Method to check if this daemon is alive. A daemon is a alive if it has be
     * started (method <code>startDaemon</code>) and has not yet died.
     * 
     * @return Return <code>true</code> if the daemon is alive, otherwise
     *         <code>false</code>.
     */
    public boolean isDaemonAlive();
    
    /**
     * This method is used to register an observer on the current daemon. This
     * method is needed by daemon manager to get informed of happenings on this
     * daemon.
     * 
     * @param observer
     *            Is the daemon observer to register.
     */
    public void registerDaemonObserver(DaemonObserver observer);

    /**
     * This method is used to unregister the given observer which was registered
     * by using method <code>registerDaemonObserver</code>.
     * 
     * @param observer
     *            Is the daemon observer to unregister.
     */
    public void unregisterDaemonObserver(DaemonObserver observer);
}
