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

package ch.elca.el4j.demos.daemonmanager;

import org.springframework.util.StringUtils;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.daemonmanager.DaemonManager;

//Checkstyle: UncommentedMain off

/**
 * This class is the console for a daemon manager. Depending on arguments of
 * method main, tasks will be executed.
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
public final class Console {

    /**
     * Application context inclusive config location.
     */
    public static final String[] INCLUSIVE_CONFIG_LOCATION 
        = {"classpath*:mandatory/*.xml", 
            "daemon_manager_demos/common/*.xml", 
            "daemon_manager_demos/console/*.xml"};

    /**
     * Flag to tell application context if bean definition overriding is 
     * allowed.
     */
    public static final boolean ALLOW_BEAN_DEFINITION_OVERRIDING = true;
    
    /**
     * Bean name of the daemon manager.
     */
    public static final String DAEMON_MANAGER_BEAN_NAME = "daemonManagerOne";
    
    /**
     * Argument to reconfigure daemon manager.
     */
    public static final String ARGUMENT_RECONFIGURE = "reconfigure";

    /**
     * Argument to get information of daemon manager.
     */
    public static final String ARGUMENT_INFORMATION = "information";

    /**
     * Argument to stop daemon manager.
     */
    public static final String ARGUMENT_STOP = "stop";
    
    /**
     * String array of the arguments.
     */
    public static final String[] ARGUMENTS 
        = {ARGUMENT_RECONFIGURE, ARGUMENT_INFORMATION, ARGUMENT_STOP};

    /**
     * Hide constructor.
     */
    private Console() { }
    
    /**
     * Main method.
     * 
     * @param args Are the arguments for the console.
     */
    public static void main(String[] args) {
        if (!checkArguments(args)) {
            printUsage();
        } else {
            ModuleApplicationContext appContext = new ModuleApplicationContext(
                INCLUSIVE_CONFIG_LOCATION, ALLOW_BEAN_DEFINITION_OVERRIDING);
            DaemonManager daemonManager 
                = (DaemonManager) appContext.getBean(DAEMON_MANAGER_BEAN_NAME);
            String argument = args[0].trim();
            if (ARGUMENT_RECONFIGURE.equalsIgnoreCase(argument)) {
                daemonManager.doReconfigureDaemons();
                printLine("Daemons are on reconfiguring now.");
                
            } else if (ARGUMENT_INFORMATION.equalsIgnoreCase(argument)) {
                String information = daemonManager.getInformation();
                printLine(information);
            } else if (ARGUMENT_STOP.equalsIgnoreCase(argument)) {
                daemonManager.doStopProcessing();
                printLine("Processing of daemon manager will be stopped.");
            }
        }
        printLine();
        System.exit(0);
    }

    /**
     * Method to check if the given arguments are legal.
     * 
     * @param args
     *            Are the arguments.
     * @return Returns <code>true</code> if the given arguments are legal.
     */
    private static boolean checkArguments(String[] args) {
        if (args != null 
            && args.length == 1 
            && StringUtils.hasText(args[0])) {
            String argument = args[0].trim();
            for (int i = 0; i < ARGUMENTS.length; i++) {
                if (ARGUMENTS[i].equalsIgnoreCase(argument)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Method to print out the usage message.
     */
    private static void printUsage() {
        printLine("Usage:");
        StringBuffer sb = new StringBuffer();
        sb.append("java " + Console.class.getName() + " (");
        for (int i = 0; i < ARGUMENTS.length; i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(ARGUMENTS[i]);
        }
        sb.append(")");
        printLine(sb.toString());
    }
    
    /**
     * Method to print out an empty line on console.
     */
    private static void printLine() {
        printLine("");
    }
    
    /**
     * Method to print out a line on console.
     * 
     * @param line Is the string to print out.
     */
    private static void printLine(String line) {
        System.out.println(line);
        System.out.flush();
    }
}
