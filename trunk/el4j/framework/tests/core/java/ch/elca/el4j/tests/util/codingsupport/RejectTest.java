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

package ch.elca.el4j.tests.util.codingsupport;

import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.util.codingsupport.Reject;

import junit.framework.TestCase;

/**
 * This tests check the behavior of the {@link
 * ch.elca.el4j.util.codingsupport.Reject} class.
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
public class RejectTest extends TestCase {

    /**
     * Checks {@link Reject#ifNull(Object)}.
     */
    public void testIfNull() {
        try {
            Reject.ifNull(null);
            fail("Bad implementatino of Reject.isNull(null)");
        } catch (Exception e) { }
        try {
            Reject.ifNull(new Object());
        } catch (Exception e) {
            fail("Bad implementatino of Reject.isNull(new Object())");
        }
    }
    
    /**
     * Checks {@link Reject#ifEmpty(List)}.
     */
    public void testIfEmptyCollection() {
        try {
            Reject.ifEmpty((List) null);
            fail("Bad implementatino of Reject.isEmpty((List) null)");
        } catch (Exception e) { }
        ArrayList list = new ArrayList();
        try {
            Reject.ifEmpty(list);
            fail("Bad implementatino of Reject.isEmpty(new ArrayList())");
        } catch (Exception e) { }
        try {
            list.add(new Object());
            Reject.ifEmpty(list);
        } catch (Exception e) {
            fail("Bad implementatino of Reject.isEmpty('nonempty list')");
        }
    }
    
    /**
     * Checks {@link Reject#ifEmpty(String)}.
     */
    public void testIfEmptyString() {
        try {
            Reject.ifEmpty((String) null);
            fail("Bad implementatino of Reject.isEmpty((String) null)");
        } catch (Exception e) { }
        try {
            Reject.ifEmpty(" ");
            fail("Bad implementatino of Reject.isEmpty(\" \")");
        } catch (Exception e) { }
        try {
            Reject.ifEmpty(" test");
        } catch (Exception e) {
            fail("Bad implementatino of Reject.isEmpty(\" test\")");
        }
    }
}
