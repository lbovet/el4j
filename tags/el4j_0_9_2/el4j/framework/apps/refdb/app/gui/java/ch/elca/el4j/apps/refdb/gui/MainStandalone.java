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

package ch.elca.el4j.apps.refdb.gui;


/**
 * This class is used to start the RefDB-Application in standalone mode.
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
            = "classpath:refdb/standalone_startup.xml";
        String applicationContext 
            = "classpath:refdb/standalone_application.xml";
        MainCommon.main(new String[] {startupContext, applicationContext});
    }
}
