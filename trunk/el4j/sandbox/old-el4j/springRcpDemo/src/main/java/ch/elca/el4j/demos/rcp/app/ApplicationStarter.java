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
// Checkstyle: UncommentedMain off
package ch.elca.el4j.demos.rcp.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationLauncher;

/**
 * This class is Starts the Master/Detail RCP Demo.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public final class ApplicationStarter {

    /**
     * Logger.
     */
    private static Log s_logger = LogFactory.getLog(ApplicationStarter.class);
    
    /**
     * Spring config locations.
     */
    private static final String[] CONFIG_LOCATIONS = {
        // Database access (connection properties, e.g)
        "classpath*:scenarios/db/raw/*.xml",
        // Application context config
        "classpath*:mandatory/context/*.xml",
        // Keyword service and dao config
        "classpath*:mandatory/keyword/*.xml",
        // Reference service and dao config
        "classpath*:mandatory/refdb/*.xml",
        // Module Hibernate config
        "classpath*:scenarios/dataaccess/hibernate/*.xml",
        // Keyword dao config (session factory)
        "classpath*:scenarios/dataaccess/hibernate/keyword/*.xml",
        // Reference dao config (session factory)
        "classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
        // Transaction config
        "classpath*:optional/interception/transactionJava5Annotations.xml"
    };
    
    /**
     * Hide Default Constructor as this class only has static methods
     * and instantiating doesn't make any sense.
     */
    private ApplicationStarter() { }
    
    
    
    /**
     * Main routine for the contact application.
     * 
     * @param args Parameters
     */
    public static void main(String[] args) {
        s_logger.info("Contact Application starting up");

        // In order to launch the platform, we have to construct an
        // application context that defines the beans (services) and
        // wiring. This is pretty much straight Spring.
        //
        // Part of this configuration will indicate the initial page to be
        // displayed.
        // The startup context defines elements that should be available
        // quickly such as a splash screen image.

        String startupContextPath = "classpath*:mandatory/startup-context.xml";

        // The ApplicationLauncher is responsible for loading the contexts,
        // presenting the splash screen, initializing the Application
        // singleton instance, creating the application window to display
        // the initial page.

        try {
            new ApplicationLauncher(startupContextPath, CONFIG_LOCATIONS);
        } catch (RuntimeException e) {
            s_logger.error("RuntimeException during startup", e);
        }
    }
}
//Checkstyle: UncommentedMain on