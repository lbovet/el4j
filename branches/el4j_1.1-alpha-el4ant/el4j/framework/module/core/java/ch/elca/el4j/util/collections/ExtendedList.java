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
package ch.elca.el4j.util.collections;

import java.util.List;

import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;

/**
 * A slightly extended Iterable.
 * 
 * @param <T> the member type.
 * 
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
public interface ExtendedList<T> extends List<T> {
    /**
     * @param c the element type for the new array
     * @return an new array containing this list's contents
     */
    T[] toArray(Class<T> c);
    
    /**
     * Convenience method returning a filtered view on this collection.
     * @param filter the filter deciding which elements are included
     */
    FilteredList<T> filtered(Filter<? super T> filter);
    
    
    /**
     * Convenience method returning a {@link TransformedList} view to this
     * list.
     * @param function the transformation function to apply to each element 
     * @return see above
     */
    public <O> TransformedList<T, O> mapped(
        Function<? super T, O> function); 
}