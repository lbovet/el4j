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
package ch.elca.el4j.tests.daemonmanager.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.elca.el4j.services.daemonmanager.Daemon;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Class to register daemon actions.
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
public final class ActionRegister {
    /**
     * Map of daemon array lists. The key is the action the daemon was 
     * registered.
     */
    private static final Map DAEMON_ACTION_LIST = new HashMap();
    
    /**
     * Hide default constructor.
     */
    private ActionRegister() { }
    
    /**
     * Registers a daemon action.
     * 
     * @param target is the target daemon that registers an action.
     * @param action Is the action to register.
     */
    public static synchronized void registerDaemonAction(
        Daemon target, DaemonAction action) {
        Reject.ifNull(target);
        Reject.ifNull(action);
        List internalList;
        if (DAEMON_ACTION_LIST.containsKey(action)) {
            internalList = (List) DAEMON_ACTION_LIST.get(action);
        } else {
            internalList = new ArrayList();
            DAEMON_ACTION_LIST.put(action, internalList);
        }
        internalList.add(target);
    }
    
    /**
     * @param action Is the action a daemon lists must be returned for. 
     * @return Returns the daemon list for the given action. Never 
     *         <code>null</code>.
     */
    public static synchronized List getDaemonsByAction(DaemonAction action) {
        Reject.ifNull(action);
        List daemonList = new ArrayList();
        List internalList = (List) DAEMON_ACTION_LIST.get(action);
        if (internalList != null) {
            daemonList.addAll(internalList);
        }
        return daemonList;
    }
}
