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
package ch.elca.el4j.tests.util.collections;

import java.util.NoSuchElementException;

import ch.elca.el4j.util.collections.TransformedList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;

import junit.framework.TestCase;

// Checkstyle: MagicNumber off

/**
 * Tests for the extended array list (duh!).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class ExtendedListTest extends TestCase {
    private ExtendedArrayList<Integer> m_0,m_a,m_b,m_c;
    private ExtendedArrayList<String> n;
    private TransformedList<Integer, String> m; 
    
    @Override
    protected void setUp() throws Exception {
        m_0 = new ExtendedArrayList<Integer>(1, 2, 3, 4, 5);
        m_a = new ExtendedArrayList<Integer>(1, 2, 3, 4, 6, 5);
        m_b = new ExtendedArrayList<Integer>(1, 2, 3, 4, 5, 6);
        m_c = new ExtendedArrayList<Integer>(2, 4, 6);
        m = m_0.mapped(new Function<Integer, String>() {
            public String apply(Integer d) {
                return d.toString();
            }
        });
        n = new ExtendedArrayList<String>("4", "2");
    }  
    
    public void testEquals() {
        assertFalse(m_0.equals(m_a));
        assertFalse(m_0.equals(m_b));
        assertFalse(m_a.equals(m_b));
    }
    
    public void testRemove() {
        m_a.remove((Integer) 6);
        m_b.remove((Integer) 6);
        assertEquals(m_a, m_b);
    }
    
    public void testAdd() {
        m_0.add(6);
        assertEquals(m_0, m_b);
    }
    
    public void testOrderLike() {
        try {
            m_0.orderLike(m_c);
            fail();
        } catch (NoSuchElementException e) { }
        m_b = new ExtendedArrayList<Integer>(m_a);
        m_a.orderLike(m_c);
        assertEquals(m_a.subList(0, 3), m_c);
        assertTrue(m_b.containsAll(m_a));
    }
    
    public void testTransformedOrderLike() {
        m.orderLike(n);
        assertEquals(m_0.subList(0,2),new ExtendedArrayList<Integer>(4,2));
    }
    
    public void testGetOnly() {
        assertEquals(
            m_a.getOnly(new Filter<Integer>() {
                public boolean accepts(Integer i) {
                    return i % 2 == 0;
                }
            }), m_c
        );
    }
}

// Checkstyle: MagicNumber on