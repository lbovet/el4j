/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ${groupId};

import ch.elca.el4j.util.codingsupport.Reject;

public class Calculator {

    /**
     * Division of two numbers.
     * 
     * @param n1 Is the dividend number.
     * @param n2 Is the divisor number.
     * @return Returns the addition result.
     */
    public static double div(double n1, double n2) {
        Reject.ifFalse(n2 != 0, "Divisor must not be zero!");
        return n1 / n2;
    }
    
    public static void main(String[] args) {
        System.out.println("Calculating 5 divided by 3");
        System.out.println("The result is " + div(5d, 3d));
    }
    
}