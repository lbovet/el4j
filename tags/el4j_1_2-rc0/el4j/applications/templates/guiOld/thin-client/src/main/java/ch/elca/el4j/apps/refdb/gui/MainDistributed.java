/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.apps.refdb.gui;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;


// Checkstyle: UncommentedMain off

/**
 * This class is used to start the RefDB-Application in distributed mode.
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
public final class MainDistributed {
    /**
     * Hide default constructor.
     */
    private MainDistributed() { }
    
    /**
     * Start method.
     * 
     * @param args
     *            Are the command line arguments. These are ignored.
     */
    public static void main(String[] args) {
        String startupContext 
            = "classpath:scenarios/springrcp/refdb/startup/*.xml";
        String[] applicationContextPaths = {
            "classpath*:mandatory/*.xml",
            "classpath*:mandatory/refdb/*.xml",
            "classpath:scenarios/remoting/client/*.xml",
            "classpath:scenarios/springrcp/refdb/application/*.xml"
        };
        
        ModuleApplicationContextConfiguration mac = new ModuleApplicationContextConfiguration();
        mac.setInclusiveConfigLocations(applicationContextPaths);
        mac.setExclusiveConfigLocations(new String[] { "classpath*:mandatory/refdb/refdb-core-service-config.xml"});
        mac.setAllowBeanDefinitionOverriding(true);
                
        MainCommon.launchApplication(startupContext, mac);
    }
}
//Checkstyle: UncommentedMain on