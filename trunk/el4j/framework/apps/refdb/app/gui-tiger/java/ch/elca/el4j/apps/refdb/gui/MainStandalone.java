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

package ch.elca.el4j.apps.refdb.gui;

// Checkstyle: UncommentedMain off

/**
 * This class is used to start the RefDB-Application in standalone mode.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Adrian Moos (AMS)
 */
public final class MainStandalone {
    /**
     * Hide default constructor.
     */
    private MainStandalone() { }

    /**
     * Start method.
     * 
     * @param args
     *            Are the command line arguments. These are ignored.
     */
    public static void main(String[] args) {
        String startupContext 
            = "classpath:scenarios/springrcp/refdb/startup/*.xml";
        String[] applicationContexts = {
            "classpath*:mandatory/*.xml",
            "classpath*:scenarios/db/raw/*.xml",
            "classpath*:scenarios/dataaccess/hibernate/*-repository-hibernate-config.xml",
            "classpath*:scenarios/dataaccess/hibernate/hibernate*.xml",
            "classpath:scenarios/springrcp/refdb/application/*.xml"
        };
        MainCommon.launchApplication(startupContext, applicationContexts);
    }
}
//Checkstyle: UncommentedMain on
