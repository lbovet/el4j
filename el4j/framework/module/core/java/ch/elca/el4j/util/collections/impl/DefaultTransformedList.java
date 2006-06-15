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

import java.util.AbstractList;
import java.util.List;

import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.FilteredList;
import ch.elca.el4j.util.collections.TransformedList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;

/**
 * Default implementation of {@link TransformedList}.
 * 
 * @param <I> the backing list's element type
 * @param <O> this list's element type
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
public class DefaultTransformedList<I, O> extends AbstractList<O> 
                                       implements TransformedList<I, O> {
    /** The backing List. */
    private List<? extends I> m_backing;
    
    /** The backing function. */
    private Function<? super I, O> m_function;
    
    /** Constructor.
     * @param backing the backing list
     * @param function the transformation function
     */
    public DefaultTransformedList(List<? extends I> backing,
                                  Function<? super I, O> function) {
        m_backing = backing;
        m_function = function;
    }
    
    /**
     * Returns an element of this list.
     * @param index the index of the element to return
     * @return the element
     */
    public O get(int index) {
        return m_function.apply(m_backing.get(index)); 
    }
    
    /**
     * {@inheritDoc}
     */
    public int size() { return m_backing.size(); }
    
    /**
     * {@inheritDoc}
     */
    public void swap(int i, int j) {
        CollectionUtils.swap(m_backing, i, j);
    }
    
    /**
     * {@inheritDoc}
     */    
    public void orderLike(List<? extends O> example) {
        CollectionUtils.orderLike(this, example);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public O[] toArray(Class<O> c) {
        return CollectionUtils.toArray(this, c);
    }

    /**
     * {@inheritDoc}
     */
    public <T> TransformedList<O, T> mapped(
        Function<? super O, T> function) {
        return new DefaultTransformedList<O, T>(this, function);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends I> getBacking() {
        return m_backing;
    }

    /**
     * {@inheritDoc}
     */
    public ExtendedWritableList<O> getOnly(Filter<? super O> filter) {
        return new ExtendedArrayList<O>(filtered(filter));
    }


    /** {@inheritDoc} */
    public FilteredList<O> filtered(Filter<? super O> filter) {
        return new DefaultFilteredList<O>(this, filter);
    }
}
