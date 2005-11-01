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

import ch.elca.el4j.util.codingsupport.AbstractDefaultEnum;

/**
 * This class is an enumeration for possible behaviours of daemons.
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
public final class ActorRole extends AbstractDefaultEnum {
    /**
     * Actor with normal behaviour.
     */
    public static final ActorRole NORMAL 
        = new ActorRole("NORMAL", 1);

    /**
     * Actor which throws a <code>DaemonCausedRTException</code> while init.
     */
    public static final ActorRole 
        THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_INIT 
        = new ActorRole("THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_INIT", 2);

    /**
     * Actor which throws no <code>DaemonCausedRTException</code> while init.
     */
    public static final ActorRole 
        THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_INIT 
        = new ActorRole("THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_INIT", 3);

    /**
     * Actor which throws a <code>DaemonCausedRTException</code> while job run.
     */
    public static final ActorRole 
        THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN 
        = new ActorRole("THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN", 4);

    /**
     * Actor which throws no <code>DaemonCausedRTException</code> while job run.
     */
    public static final ActorRole 
        THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN 
        = new ActorRole("THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_JOB_RUN", 5);

    /**
     * Actor which throws a <code>DaemonCausedRTException</code> while 
     * reconfigure.
     */
    public static final ActorRole 
        THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE 
        = new ActorRole("THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE", 6);

    /**
     * Actor which throws no <code>DaemonCausedRTException</code> while 
     * reconfigure.
     */
    public static final ActorRole 
        THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE = new ActorRole(
            "THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_RECONFIGURE", 7);

    /**
     * Actor which throws a <code>DaemonCausedRTException</code> while clean up.
     */
    public static final ActorRole 
        THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP 
        = new ActorRole("THROWS_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP", 8);

    /**
     * Actor which throws no <code>DaemonCausedRTException</code> while clean 
     * up.
     */
    public static final ActorRole 
        THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP 
        = new ActorRole("THROWS_NO_DAEMON_CAUSED_EXCEPTION_WHILE_CLEAN_UP", 9);

    /**
     * Actor which blocks.
     */
    public static final ActorRole BLOCKS 
        = new ActorRole("BLOCKS", 10);
    
    /**
     * Private constructor.
     * 
     * @param name Is the name of the actor rule.
     * @param code Is the code of the actor rule.
     */
    private ActorRole(String name, int code) {
        super(name, code);
    }

    /**
     * Returns the actor role with given name. The name of actor roles are case
     * sensitive.
     * 
     * @param name Is the name of the actor role.
     * @return Returns the actor role.
     */
    public static ActorRole get(String name) {
        return (ActorRole) AbstractDefaultEnum.get(ActorRole.class, name);
    }
}
