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
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import ch.elca.el4j.util.collections.FilteredList;
import ch.elca.el4j.util.collections.TransformedList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;

/**
 * Default Implementation for FilteredList.
 * 
 * <p>This class is not thread safe.
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
public class DefaultFilteredList<T> extends AbstractSequentialList<T> 
                                 implements FilteredList<T> {
    /** The backing list. */
    private List<? extends T> m_backing;
    
    /** The backing filter. */
    private Filter<? super T> m_filter;
    
    /**
     * Constructor.
     * @param backing the backing List
     * @param filter the filter to apply
     */
    public DefaultFilteredList(
        List<? extends T> backing, Filter<? super T> filter) {
        
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
     * @throws UnsupportedOperationException
     */
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over the accepted elements.
     * The iterator is fail-fast iff the backing list's iterator is.
     */
    public Iterator<T> iterator() {
        return listIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public ListIterator<T> listIterator() {
        return new Iter(m_backing.listIterator(), 0);
    }

    /** {@inheritDoc} */
    @Override
    public ListIterator<T> listIterator(int index) {
        ListIterator<T> li = listIterator();
        try {
            for (int i = 0; i < index; i++) {
                li.next();
            }
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException();
        }
        return li;
    }
    
    /** {@inheritDoc} */
    public ListIterator<T> listIterator(ListIterator<? extends T> location) {
        return new Iter(location);
    }
    
    /** {@inheritDoc} */
    @Override
    public int size() {
        int c = 0;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            c++;
            it.next();
        }
        return c;
    }
    
    /**
     * Iterator class.
     */
    protected class Iter implements ListIterator<T> {
        /** The backing list iterator. */
        ListIterator<? extends T> m_backingIterator;
        
        /** cache of {@link #nextIndex()}. A negative value indicates
         * an invalid cache. */
        int m_index;
        
        /**
         * Constructor.
         * @param backing the backing iterator
         */
        Iter(ListIterator<? extends T> backing) {
            this(backing, Integer.MIN_VALUE / 2);
        }
        
        /**
         * Constructor.
         * @param backing the backing iterator
         * @param index this iterator's current index
         */
        Iter(ListIterator<? extends T> backing, int index) {
            m_backingIterator = backing;
            m_index = index;
        }
        
        /**
         * returns the index of the next element that would be returned by this
         * iterator.
         * @return see above
         */
        private int getIndex() {
            if (m_index < 0) {
                int c = 0;
                while (hasPrevious()) {
                    c++;
                    previous();
                }
                m_index = 0;
                for (int i = 0; i < c; i++) {
                    next();
                }
            }
            return m_index;
        }
        
        
        /**
         * {@inheritDoc}
         */
        // postcondition: m_backingIterator stands right before
        // the next accepted element (or at the list's end if there is no
        // such element)
        public boolean hasNext() {
            while (m_backingIterator.hasNext()) {
                if (m_filter.accepts(m_backingIterator.next())) {
                    m_backingIterator.previous();
                    return true;
                }
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        // postcondition: m_backingIterator stands right after
        // the previous accepted element (or at the list's start if there is no
        // such element)
        public boolean hasPrevious() {
            while (m_backingIterator.hasPrevious()) {
                if (m_filter.accepts(m_backingIterator.previous())) {
                    m_backingIterator.next();
                    return true;
                }
            }
            return false;            
        }

        /**
         * {@inheritDoc}
         */
        public T next() throws NoSuchElementException {
            if (hasNext()) {
                m_index++;
                return m_backingIterator.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * {@inheritDoc}
         */
        public T previous() {
            if (hasPrevious()) {
                m_index--;
                return m_backingIterator.previous();
            } else {
                throw new NoSuchElementException();
            }
        }
        
        /**
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * @throws UnsupportedOperationException
         */
        public void add(T o) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public int nextIndex() {
            return getIndex();
        }

        /** {@inheritDoc} */
        public int previousIndex() {
            return getIndex() - 1;
        }

        /**
         * @throws UnsupportedOperationException
         */
        public void set(T o) {
            throw new UnsupportedOperationException();
        }
    }

    /** {@inheritDoc} */
    public FilteredList<T> filtered(Filter<? super T> filter) {
        return new DefaultFilteredList<T>(this, filter);
    }

    /** {@inheritDoc} */
    public <O> TransformedList<T, O> mapped(Function<? super T, O> function) {
        return new DefaultTransformedList<T, O>(this, function);
    }
}
