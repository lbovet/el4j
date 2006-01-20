/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.daemonmanager;

import org.tanukisoftware.wrapper.WrapperManager;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.daemonmanager.DaemonManager;
import ch.elca.el4j.services.daemonmanager.impl.AbstractDaemonManagerController;

//Checkstyle: UncommentedMain off

/**
 * Sample for the daemon manager controller with java service wrapper.
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
public class WrapperController extends AbstractDaemonManagerController {
    /**
     * {@inheritDoc}
     */
    protected DaemonManager createDaemonManager() {
        ModuleApplicationContext appContext = new ModuleApplicationContext(
            Controller.INCLUSIVE_CONFIG_LOCATION,
            Controller.ALLOW_BEAN_DEFINITION_OVERRIDING);
        DaemonManager daemonManager 
            = (DaemonManager) appContext.getBean(
                Controller.DAEMON_MANAGER_BEAN_NAME);
        return daemonManager;
    }
    
    /**
     * Main method to instantiate this class.
     * 
     * @param args Are the wrapper arguments.
     */
    public static void main(String[] args) {
        WrapperManager.start(new WrapperController(), args);
    }
}
