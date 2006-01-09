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

package ch.elca.el4j.tests.services.exceptionhandler;

/**
 * This class is used to show the substitution and the reconfigure exception
 * handling strategies. 
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
public class B extends AImpl implements Adder {

    /** Number of add calls. */
    public static int s_numberOfAddCalls = 0;
    
    /** Number of concatenate calls. */
    public static int s_numberOfConcatCalls = 0;
    
    /** Whether concatenate calls should fail. */
    public static boolean s_concatFails = false;

    /** Resets the call counters. */
    public static void reset() {
        s_numberOfAddCalls = 0;
        s_numberOfConcatCalls = 0;
        s_concatFails = false;
    }
    
    /**
     * Concatenates the given three strings.
     *  
     * @param a The first string.
     * @param b The second string.
     * @return Returns the concatenation of the given three strings.
     */
    public String concat(String a, String b) {
        s_numberOfConcatCalls++;
        if (s_concatFails) {
            throw new UnsupportedOperationException();
        } else {
            return a.concat(b);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int add(int a, int b) {
        s_numberOfAddCalls++;
        throw new UnsupportedOperationException(); 
    }
}
