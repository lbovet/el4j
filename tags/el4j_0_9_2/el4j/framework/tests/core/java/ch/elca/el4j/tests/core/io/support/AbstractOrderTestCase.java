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

package ch.elca.el4j.tests.core.io.support;

import junit.framework.TestCase;

/**
 * Abstract test case for order tests.
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
public abstract class AbstractOrderTestCase extends TestCase {

    /**
     * Asserts that object <code>a</code> is before object <code>b</code>
     * in list <code>list</code>.
     * 
     * @param a an object.
     * @param b another object.
     * @param list the list.
     */
    protected void assertBefore(Object a, Object b, Object[] list) {
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(list);
        assertFalse(a.equals(b));
        
        int ia = -1;
        int ib = -1;
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(a)) {
                ia = i;
            } else if (list[i].equals(b)) {
                ib = i;
            }
        }
        if (ia == -1) {
            fail("Object a [" + a + "] is not in the list.");
        } else if (ib == -1) {
            fail("Object b [" + b + "] is not in the list.");
        }
        if (ia > ib) {
            fail("Object a [" + a + "] is after Object b [" + b + "]");
        }
    }
    
    /**
     * Asserts that all objects in <code>a</code> are before any object form
     * <code>b</code> in list <code>list</code>.
     * 
     * @param a an array of objects.
     * @param b another array of objects.
     * @param list the list.
     */
    protected void assertAllBefore(String[] a, String[] b, String[] list) {
        assertNotNull(a);
        assertNotNull(b);
        
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                assertBefore(a[i], b[j], list);
            }
        }
    }
    
    /**
     * Checks whether the given array contains at least one string that ends
     * with the given suffix.
     * 
     * @param suffix
     *      The string to match the array items' endings against.
     *      
     * @param s
     *      The array of strings to test.
     *      
     * @return Returns <code>true</code> if at least one string ends with the
     *      given suffix.
     */
    protected boolean containsStringEndingWith(String suffix, String[] s) {
        boolean result = false;
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                if (s[i].endsWith(suffix)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
