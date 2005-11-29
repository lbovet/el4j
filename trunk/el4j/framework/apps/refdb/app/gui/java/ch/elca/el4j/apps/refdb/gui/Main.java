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

package ch.elca.el4j.apps.refdb.gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationLauncher;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * This class is used to start the RefDB-Application.
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
public final class Main {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(Main.class);
    
    /**
     * Startup spring configuration file. Used to quickly load beans to present 
     * something to the user.
     */
    public static final String STARTUP_CONTEXT_PATH
        = "classpath:refdb/startup.xml";
    
    /**
     * Main application context files. 
     */
    public static final String[] ROOT_CONTEXT_PATH 
        = {"classpath*:mandatory/*.xml"};
    
    /**
     * Hide default constructor.
     */
    private Main() { }

    /**
     * Start method.
     * 
     * @param args
     *            Are the command line arguments.
     */
    public static void main(String[] args) {
        try {
            ModuleApplicationContext rootAppContext
                = new ModuleApplicationContext(ROOT_CONTEXT_PATH, false); 
            new ApplicationLauncher(STARTUP_CONTEXT_PATH, rootAppContext);
        } catch (Exception e) {
            s_logger.fatal("Reference-Database-Application exited "
                + "exceptionally! See stack trace for details.", e);
            System.exit(1);
        }
    }
}
