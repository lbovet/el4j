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
package ch.elca.el4j.tests.util.collections;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.collections.FilteredList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;

import junit.framework.TestCase;

// Checkstyle: MagicNumber off
// Checkstyle: EmptyBlock off

/**
 * Tests FilteredList.
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
public class FilteredListTest extends TestCase {
    /** The list of non-negative integers. */
    static List<Integer> s_integers = new AbstractList<Integer>() {
        @Override
        public Integer get(int index) {
            return index;
        }

        @Override
        public int size() {
            return Integer.MAX_VALUE;
        }
    };
    
    /***/
    static final String UNSUPPORTED
        = "filtered views should throw UnsupportedOperationExceptions"
        + "on modification attempts";        

    
    /** The primes < 100. */
    List<Integer> m_expectedPrimes
        = new ExtendedArrayList<Integer>(
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
            53, 59, 61, 67, 71, 73, 79, 83, 89, 97
        );


    /***/
    public void testAdd() {
        try {
            getPrimes(10).add(1);
            fail(UNSUPPORTED);
        } catch (UnsupportedOperationException e) {
            
        }
    }
    
    /***/
    public void testRemove() {
        try {
            getPrimes(10).remove(1);
            fail(UNSUPPORTED);
        } catch (UnsupportedOperationException e) {
            
        }
    }
    
    /***/
    public void testIterator() {
        Iterator<Integer> odd = CollectionUtils.filtered(
            s_integers, 
            new DiscardMultiples(-2)
        ).iterator();
        
        assertEquals(1, (int) odd.next());
        assertEquals(3, (int) odd.next());
        assertEquals(5, (int) odd.next());
        
    }
    
    /***/
    public void testIteratorLayered() {
        Iterator<Integer> iter = getPrimes(10).iterator();
        for (Integer ex : m_expectedPrimes) {
            assertEquals(ex, iter.next());
        }
    }
    
    /***/
    public void testListIterator() {
        ListIterator<Integer> it = getPrimes(10).listIterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        assertEquals(2, (int) it.next());
        assertTrue(it.hasPrevious());
        assertEquals(3, (int) it.next());
        assertEquals(5, (int) it.next());
        assertTrue(it.hasPrevious());
        assertEquals(5, (int) it.previous());
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.nextIndex());
        assertEquals(5, (int) it.next());
        assertEquals(2, it.previousIndex());
        assertTrue(it.hasNext());
        assertEquals(7, (int) it.next());
    }
    
    /***/
    public void testListIteratorIndex() {
        ListIterator<Integer> it = s_integers.listIterator(10);
        ListIterator<Integer> odd = CollectionUtils.filtered(
            s_integers,
            new DiscardMultiples(-2)
        ).listIterator(it);
        assertEquals(9, (int) odd.previous());
        assertEquals(4, (int) odd.nextIndex());
    }
    
    /***/
    public void testSize() {
        assertEquals(
            m_expectedPrimes.size() - 1,
            CollectionUtils.filtered(
                m_expectedPrimes,
                new DiscardMultiples(-2)
            ).size()
        );
    }
    
    /** Returns a list of all primes less than {@code limit * limit}. */
    List<Integer> getPrimes(int limit) {
        FilteredList<Integer> candidates = CollectionUtils.filtered(
            s_integers,
            new Filter<Integer>() {
                public boolean accepts(Integer t) {
                    return t > 1;
                }
            }
        );
        
        ListIterator<Integer> candidateIterator = candidates.listIterator();
        while (true) {
            int prime = candidateIterator.next();
            if (prime > limit) { break; }
            
            candidates = candidates.filtered(new DiscardMultiples(prime));
            candidateIterator = candidates.listIterator(candidateIterator);
        }
        return candidates;
    }

    /** for given {@code n}, this filter accepts {@code n} and any number that
     * is not a multiple of n.*/
    private static class DiscardMultiples implements Filter<Integer> {
        /***/
        private int m_n;
        
        /***/
        DiscardMultiples(int n) {
            m_n = n;
        }

        /** {@inheritDoc} */
        public boolean accepts(Integer t) {
            return t == m_n || t % m_n != 0;
        }
    }
}
// Checkstyle: EmptyBlock on
// Checkstyle: MagicNumber on