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
import java.util.List;
import java.util.NoSuchElementException;

import ch.elca.el4j.util.collections.ExtendedReorderableList;

/**
 * Utility methods for lists.
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
public final class ListUtil {
    /** this is a utility class. */
    private ListUtil() { }
    
    /** 
     * Finds the first element with index at least
     * {@code startAt} that equals {@code o}. This method may take linear time.
     * 
     * @param list the list to search
     * @param t the element to look for
     * @return the index of the first matching element
     * @throws NoSuchElementException if no such element exists.
     */    
    public static <T> int find(List<T> list, T t, int startAt)
        throws NoSuchElementException {
        for (int id = startAt; id < list.size(); id++) {
            if (list.get(id).equals(t)) {
                return id;
            }
        }      
        throw new NoSuchElementException(t.toString());        
    }
    
    /**
     * See {@link ExtendedReorderableList#orderLike(List)}.
     * @param <T> see there
     * @param list see there
     * @param example see there
     */
    public static <T> void orderLike(ExtendedReorderableList<T> list,
                                     List<? extends T> example) {
        for (int i = 0; i < example.size(); i++) {
            list.swap(i, find(list, example.get(i), i));
        }
    }
    
    /**
     * Copies the list's contents to an array.
     * @param <T> the arrays element type 
     * @param list the list to copy
     * @param c the element type for the new array
     * @return an new array containing this list's contents
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<? extends T> list, Class<T> c) {
        return list.toArray((T[]) Array.newInstance(c, list.size()));
    }
    
    /** swaps the elements at locations {@code i} and {@code j}. 
     * If this list is a {@link ExtendedReorderableList}, its swap method
     * is invoked, otherwise, set/get is used.
     * 
     * This behaves like Lists always declared a convenience swap method,
     * that is overridden if ReordableRandomAccessList is implemented.
     * @param list see above
     * @param i see above
     * @param j see above
     */
    public static <T> void swap(List<T> list, int i, int j) {
        if (list instanceof ExtendedReorderableList) {
            ((ExtendedReorderableList<T>) list).swap(i, j);
        } else {
            defaultSwap(list, i, j);
        }
    }
    
    /** swaps the elements at locations {@code i} and {@code j} by set/get.
     */
    static <T> void defaultSwap(List<T> list, int i, int j) {
        T t = list.get(i);
        list.set(i, list.get(j));
        list.set(j, t);        
    }
}
