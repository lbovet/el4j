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

package ch.elca.el4j.tests.daemonmanager.helpers;

import ch.elca.el4j.util.codingsupport.AbstractDefaultEnum;

/**
 * This class is an enumeration for possible daemon actions.
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
public final class DaemonAction extends AbstractDefaultEnum {
    /**
     * Daemon has been initialized.
     */
    public static final DaemonAction INIT 
        = new DaemonAction("INIT", 1);

    /**
     * Daemon has been reconfigured.
     */
    public static final DaemonAction RECONFIGURE 
        = new DaemonAction("RECONFIGURE", 2);

    /**
     * Daemon has been cleaned up.
     */
    public static final DaemonAction CLEANUP 
        = new DaemonAction("CLEANUP", 3);

    /**
     * Private constructor.
     * 
     * @param name Is the name of the daemon action.
     * @param code Is the code of the daemon action.
     */
    private DaemonAction(String name, int code) {
        super(name, code);
    }

    /**
     * Returns the daemon action with given name. The name of daemon actions are
     * case sensitive.
     * 
     * @param name Is the name of the actor role.
     * @return Returns the actor role.
     */
    public static DaemonAction get(String name) {
        return (DaemonAction) AbstractDefaultEnum.get(DaemonAction.class, name);
    }
}
