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
package ch.elca.helloworld.demos;

import org.springframework.util.StringUtils;

import ch.elca.el4j.util.codingsupport.PreconditionRTException;
import ch.elca.helloworld.services.Calculator;

// Checkstyle: UncommentedMain off

/**
 * Sample demo for the Hello World project.
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
public class MainCalculator {
    /**
     * Hide constructor.
     */
    protected MainCalculator() { }
    
    /**
     * Main method.
     * 
     * @param args Are the calculator parameters.
     */
    public static void main(String[] args) {
        // Checkstyle: MagicNumber off
        if (args == null || args.length < 3) {
            printUsage();
            return;
        }
        // Checkstyle: MagicNumber on
        
        boolean printUsage = true;
        String operation = args[0];
        if (StringUtils.hasText(operation)) { 
            try {
                Calculator c = new Calculator();
                if ("div".equalsIgnoreCase(operation)) {
                    String p1 = args[1];
                    String p2 = args[2];
                    double dividend = Double.parseDouble(p1);
                    double divisor = Double.parseDouble(p2);
                    double result = c.div(dividend, divisor);
                    System.out.println("Division: " 
                        + dividend + " / " + divisor + " = " + result);
                    printUsage = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter numbers only!");
            } catch (PreconditionRTException e) {
                System.out.println(e.getMessage());
            }
        }

        if (printUsage) {
            printUsage();
        }
    }
    
    /**
     * Prints the usage of this class.
     */
    public static void printUsage() {
        System.out.println(
            "Usage:   java MainCalculator <operation> <parameters...>");
        System.out.println("Example: java MainCalculator div 6.8 3.9");
    }
}
//Checkstyle: UncommentedMain on
