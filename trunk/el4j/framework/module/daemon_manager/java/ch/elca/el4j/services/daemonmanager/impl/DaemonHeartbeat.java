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

package ch.elca.el4j.services.daemonmanager.impl;


/**
 * This class is a value object to save the last heartbeat time and the counter
 * of missed heartbeats.
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
public class DaemonHeartbeat {
    /**
     * Contains the timestamp of the last heartbeat.
     */
    private long m_lastHeartbeat = System.currentTimeMillis();
    
    /**
     * Contains the number of missed heartbeats.
     */
    private int m_numberOfMissedHeartbeats = 0;

    /**
     * Sets the last heartbeat to current time millis.
     */
    public void setLastHeartbeatToNow() {
        m_lastHeartbeat = System.currentTimeMillis();
    }
    
    /**
     * @return Returns the lastHeartbeat.
     */
    public long getLastHeartbeat() {
        return m_lastHeartbeat;
    }

    /**
     * Method to increase the number of missed heartbeats.
     */
    public void increaseNumberOfMissedHeartbeats() {
        m_numberOfMissedHeartbeats++;
    }
    
    /**
     * Method to reset the counter of missed heartbeats.
     */
    public void resetNumberOfMissedHeartbeats() {
        m_numberOfMissedHeartbeats = 0;
    }
    
    /**
     * @return Returns the numberOfMissedHeartbeats.
     */
    public int getNumberOfMissedHeartbeats() {
        return m_numberOfMissedHeartbeats;
    }
}
