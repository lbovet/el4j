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

import java.util.NoSuchElementException;
import java.util.RandomAccess;

import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;

/**
 * An extended interface, random access list permitting element reordering, but
 * not neccessarily inserting/removing elements. 
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
public interface ExtendedReorderableList<T> extends ExtendedList<T>, 
                                                    RandomAccess {
                                                      
    
    /** exchanges the elements located at indices {@code i} and {@code j}.
     * @param i ...
     * @param j ...
     */
    public void swap(int i, int j);
    
    /**
     * Reorders this list's elements by example. This method makes sure that 
     * the result will start with the sublist {@code example} as far as 
     * element equality is concerned, or throws
     * a {@code NoSuchElementException} if this is not possible. 
     * In either case, the order of the remaining elements may be affected.
     * This method may require quadratic time.
     * 
     * <p>Formally, if this method completes normally, the condition 
     * &forall; {@code  i < example.size(); 
     *   c.apply(this.get(i)).equals(example.get(i))} 
     *  is true.
     * @param example a list whose elements are in the descired order
     * @throws NoSuchElementException if the example's order can not be
     *                                duplicated by reordering alone.
     **/
    public void orderLike(java.util.List<? extends T> example)
        throws NoSuchElementException;
    
    /**
     * Convenience method returning a {@link TransformedList} view to this
     * list.
     * @param function the transformation function to apply to each element 
     * @return see above
     */
    public <O> TransformedList<T, O> mapped(
        Function<? super T, O> function); 
    
    /**
     * returns a new list containing only the elements matching the filter.
     * @param filter the filter deciding which elements to include
     * @return see above
     */
    public ExtendedWritableList<T> getOnly(Filter<? super T> filter);
}
