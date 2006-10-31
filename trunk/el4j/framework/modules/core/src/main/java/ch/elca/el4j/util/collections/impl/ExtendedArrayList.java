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
package ch.elca.el4j.util.collections.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.FilteredList;
import ch.elca.el4j.util.collections.TransformedList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;

/**
 * A default implementation of the ExtendedWritableList interface.
 * 
 * @param <T> the member type
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
public class ExtendedArrayList<T> extends ArrayList<T> 
    implements ExtendedWritableList<T> {
    
    /** creates an empty list. */
    public ExtendedArrayList() { }
    
    /** creates a list containing the arguments.
     * @param ts .
     */
    public ExtendedArrayList(T... ts) {
        add(ts);
    }
    
    /** Creates a list by shallow-copying {@code iter}.
     * @param iter the iterable to copy.
     */
    public ExtendedArrayList(Iterable<T> iter) {
        for (T t : iter) {
            add(t);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void add(T... ts) {
        for (T t : ts) {
            add(t);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(T... ts) {
        for (T t : ts) {
            remove(t);
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public void swap(int i, int j) {
        T t = get(i);
        set(i, get(j));
        set(j, t);
    }

    /**
     * {@inheritDoc}
     */
    public void orderLike(List<? extends T> example) 
        throws NoSuchElementException {
        
        CollectionUtils.orderLike(this, example);
    }

    /**
     * {@inheritDoc}
     */
    public T[] toArray(Class<T> c) {
        return CollectionUtils.toArray(this, c);
    }

    /**
     * {@inheritDoc}
     */
    public <O> TransformedList<T, O> mapped(Function<? super T, O> function) {
        return new DefaultTransformedList<T, O>(this, function);
    }

    /** {@inheritDoc} */
    public FilteredList<T> filtered(Filter<? super T> filter) {
        return new DefaultFilteredList<T>(this, filter);
    }
}