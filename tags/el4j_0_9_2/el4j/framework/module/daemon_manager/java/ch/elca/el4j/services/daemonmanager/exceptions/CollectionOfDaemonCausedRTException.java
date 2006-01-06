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

import java.util.Set;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.util.codingsupport.CollectionUtils;

/**
 * This exception is used to summarize daemon caused exceptions.
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
public class CollectionOfDaemonCausedRTException extends BaseRTException {
    /**
     * List of daemon caused exceptions.
     */
    private final Set m_daemonCausedExceptions;
    
    /**
     * Constructor.
     * 
     * @param daemonCausedExceptions
     *            Is a list which conatins only daemon caused exceptions.
     */
    public CollectionOfDaemonCausedRTException(Set daemonCausedExceptions) {
        super("Collection of daemon caused exceptions.");
        if (CollectionUtils.isEmpty(daemonCausedExceptions)) {
            throw new MisconfigurationRTException(
                "Daemon set must not be null or empty.");
        }
        if (!CollectionUtils.containsOnlyObjectsOfType(daemonCausedExceptions, 
            DaemonCausedRTException.class)) {
            throw new MisconfigurationRTException(
                "Set must only contain daemon caused exceptions.");
        }
        m_daemonCausedExceptions = daemonCausedExceptions;
    }

    /**
     * @return Returns the daemonCausedExceptions.
     */
    public Set getDaemonCausedExceptions() {
        return m_daemonCausedExceptions;
    }
}
