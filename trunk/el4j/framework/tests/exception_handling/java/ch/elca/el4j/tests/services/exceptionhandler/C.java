/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.tests.services.exceptionhandler;

/**
 * This class is used to show the reconfigure exception handling strategy.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class C implements Adder {

    /** Number of calls. */
    public static int s_numberOfAddCalls = 0;
    
    /** Resets the counters. */
    public static void reset() {
        s_numberOfAddCalls = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public int add(int a, int b) {
        s_numberOfAddCalls++;
        return a + b;
    }
}
