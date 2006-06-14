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
package ch.elca.el4j.util.collections.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ch.elca.el4j.util.collections.ExtendedIterable;
import ch.elca.el4j.util.collections.helpers.Filter;

/**
 * A view on a backing list showing only the elements accepted by a filter.
 * It may be iterated over (for instance using the extended for-statement),
 * alternatively, you may copy its contents to an array or an ExtendedArrayList
 * (see {@link ExtendedArrayList#ExtendedArrayList(Iterable)}).
 * 
 * @param <T> the element type
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
public class FilteredList<T> implements ExtendedIterable<T> {
    /** The backing list. */
    private List<? extends T> m_backing;
    
    /** The backing filter. */
    private Filter<? super T> m_filter;
    
    /**
     * Constructor.
     * @param backing the backing list
     * @param filter the filter to apply
     */
    FilteredList(List<? extends T> backing, Filter<? super T> filter) {
        m_backing = backing;
        m_filter = filter;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(Class<T> c) {
        ArrayList<T> r = new ArrayList<T>();
        for (T t : this) {
            r.add(t);
        }
        return r.toArray((T[]) Array.newInstance(c, r.size()));
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<T> iterator() {
        return new Iter();
    }
    
    /** The iterator class. */
    public class Iter implements Iterator<T> {
        /** the backing list's index for the last returned element
         * or -1, if there is no such index.
         */
        int m_currentIndex = -1;
        
        /** the smallest backing index such that all elements with smaller
         * indices are either not accepted by the filter or have been
         * returned already.
         */
        int m_seekIndex = 0;
        
        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            int beyond = m_backing.size();
            if (m_seekIndex <= m_currentIndex) {
                m_seekIndex = m_currentIndex + 1;                
            }
            while (m_seekIndex < beyond 
                   && !m_filter.accepts(m_backing.get(m_seekIndex))) {
                
                m_seekIndex++;
            }
            return m_seekIndex < beyond;
        }

        /**
         * {@inheritDoc}
         */
        public T next() {
            if (hasNext()) {
                m_currentIndex = m_seekIndex;
                return m_backing.get(m_currentIndex);
            } else {
                throw new NoSuchElementException();
            }
            
        }

        /**
         * {@inheritDoc}
         */
        public void remove() throws IllegalStateException {
            if (m_currentIndex >= 0) {
                m_backing.remove(m_currentIndex);
                m_currentIndex = -1;
            } else {
                throw new IllegalStateException();
            }
        }
        
    }
}
