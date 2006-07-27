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
 * This exception is used to inform that given daemons could not be stopped.
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
public class DaemonsStillRunningRTException extends BaseRTException {
    /**
     * Is a set of daemons which could not be stopped.
     */
    private Set m_stillRunningDaemons;

    /**
     * Constructor.
     * 
     * @param stillRunningDaemons
     *            Is the set of daemons which could not be stopped.
     */
    public DaemonsStillRunningRTException(Set stillRunningDaemons) {
        super("");
        if (CollectionUtils.isEmpty(stillRunningDaemons)) {
            throw new MisconfigurationRTException(
                "Daemon set must not be null or empty.");
        }

        if (!CollectionUtils.containsOnlyObjectsOfType(
            stillRunningDaemons, Daemon.class)) {
            throw new MisconfigurationRTException(
                "Daemon set must only contain classes of type '"
                + Daemon.class.getName() + "'.");
        }
        
        /**
         * Copy daemons into new set.
         */
        m_stillRunningDaemons 
            = new HashSet(stillRunningDaemons);
        
        /**
         * Create human readable error message.
         */
        StringBuffer sb = new StringBuffer();
        sb.append("Following daemon(s) could not be stopped: {");
        Iterator it = stillRunningDaemons.iterator();
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
     * @return Returns the still running daemons.
     */
    public Set getStillRunningDaemons() {
        return m_stillRunningDaemons;
    }
}
