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

import javax.swing.JOptionPane;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.gui.richclient.ApplicationLauncher;

/**
 * Abstract class to start the RefDB-Application.
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
            
            String stackTrace = ExceptionUtils.getStackTrace(e);
            String dialogTitle = "Unknown exception occured";
            String dialogMessage = message + "\n\n" + stackTrace;
            JOptionPane.showMessageDialog(null, dialogMessage, 
                dialogTitle, JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
