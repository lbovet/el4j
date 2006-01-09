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

package ch.elca.el4j.services.daemonmanager.exceptions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.util.StringUtils;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.services.daemonmanager.Daemon;
import ch.elca.el4j.util.codingsupport.CollectionUtils;

/**
 * Exception to indicate that daemon heartbeats have missed.
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
public class MissingHeartbeatsRTException extends BaseRTException {
    
    /**
     * Set of daemons which have missed to send heartbeats.
     */
    private final Set m_daemonsWhereHeartbeatMissed;
    
    /**
     * Constructor which receives a list of deamos.
     * 
     * @param daemonsWhereHeartbeatMissed
     *            Is a set of daemons which have missed to send heartbeats.
     */
    public MissingHeartbeatsRTException(Set daemonsWhereHeartbeatMissed) {
        super("");
        if (CollectionUtils.isEmpty(daemonsWhereHeartbeatMissed)) {
            throw new MisconfigurationRTException(
                "Daemon set must not be null or empty.");
        }

        if (!CollectionUtils.containsOnlyObjectsOfType(
            daemonsWhereHeartbeatMissed, Daemon.class)) {
            throw new MisconfigurationRTException(
                "Daemon set must only contain classes of type '"
                + Daemon.class.getName() + "'.");
        }
        
        /**
         * Copy daemons into new set.
         */
        m_daemonsWhereHeartbeatMissed 
            = new HashSet(daemonsWhereHeartbeatMissed);
        
        /**
         * Create human readable error message.
         */
        StringBuffer sb = new StringBuffer();
        sb.append("There were heartbeats missed on following daemon(s): {");
        Iterator it = daemonsWhereHeartbeatMissed.iterator();
        boolean isNotFirst = false;
        while (it.hasNext()) {
            if (isNotFirst) {
                sb.append(", ");
            }
            isNotFirst = true;
            
            Daemon daemon = (Daemon) it.next();
            String identification = daemon.getIdentification();
            sb.append(StringUtils.hasLength(identification) 
                ? identification : "no identification");
        }
        sb.append("}.");
        setFormatString(sb.toString());
    }

    /**
     * @return Returns the daemonsWhereHeartbeatMissed.
     */
    public Set getDaemonsWhereHeartbeatMissed() {
        return m_daemonsWhereHeartbeatMissed;
    }
}
