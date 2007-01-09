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

package ch.elca.el4j.applications.refdb.gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.services.gui.richclient.ApplicationLauncher;
import ch.elca.el4j.services.gui.richclient.utils.DialogUtils;

/**
 * Abstract class to start the RefDB-Application.
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
public final class MainCommon {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(MainCommon.class);
    
    /**
     * Hide default constructor.
     */
    private MainCommon() { }

    /**
     * Start method for the application.
     * 
     * @param startupContext
     *            Is a small spring context used while starting the application.
     * @param applicationContexts
     *            Are big spring contexts that are used for application to run.
     */
    public static void launchApplication(String startupContext, 
        String[] applicationContexts) {
        try {
            new ApplicationLauncher(startupContext, applicationContexts);
        } catch (Exception e) {
            String message = "Reference-Database-Application exited "
                + "exceptionally for an unknown reason! See stack trace for "
                + "details.";
            s_logger.fatal(message, e);
            DialogUtils.showErrorMessageDialog(e, null);
            System.exit(1);
        }
    }
    
    /**
     * Start method for the application.
     * 
     * @param startupContext
     *            Is a small spring context used while starting the application.
     * @param applicationContextConfiguration
     *            The configuration of the classpath application context
     */
    public static void launchApplication(
        String startupContext,
        ModuleApplicationContextConfiguration applicationContextConfiguration) {
        try {
            new ApplicationLauncher(startupContext,
                applicationContextConfiguration);
        } catch (Exception e) {
            String message = "Reference-Database-Application exited "
                + "exceptionally for an unknown reason! See stack trace for "
                + "details.";
            s_logger.fatal(message, e);
            DialogUtils.showErrorMessageDialog(e, null);
            System.exit(1);
        }
    }
}
