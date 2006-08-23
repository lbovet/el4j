/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.util.codingsupport;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.util.collections.ExtendedReorderableList;
import ch.elca.el4j.util.collections.FilteredList;
import ch.elca.el4j.util.collections.TransformedList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;
import ch.elca.el4j.util.collections.impl.DefaultFilteredList;
import ch.elca.el4j.util.collections.impl.DefaultTransformedList;

/**
 * This class supports methods to handle with collections. It covers only gaps
 * of class <code>org.springframework.util.CollectionUtils</code>.
 *
 * 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE), Adrian Moos (AMS)
 */
public final class CollectionUtils {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(CollectionUtils.class);
    
    /**
     * Hidden constructor.
     */
    private CollectionUtils() { }

    /**
     * Null save check if a collection is empty.
     * 
     * @param c Is the given collection
     * @return Returns true if the given collection is null or empty.
     */
    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }
    
    /**
     * Method to always return a list object.
     * 
     * @param list
     *            Is the list which should not be null.
     * @return Returns the given list if it is not null. Otherwise it returns an
     *         empty list.
     */
    public static List asList(List list) {
        return list == null ? new LinkedList() : list;
    }

    /**
     * Method to always return a set object.
     * 
     * @param set
     *            Is the set which should not be null.
     * @return Returns the given set if it is not null. Otherwise it returns an
     *         empty set.
     */
    public static Set asSet(Set set) {
        return set == null ? new HashSet() : set;
    }

    /**
     * Method to always return a map object.
     * 
     * @param map
     *            Is the map which should not be null.
     * @return Returns the given map if it is not null. Otherwise it returns an
     *         empty map.
     */
    public static Map asMap(Map map) {
        return map == null ? new HashMap() : map;
    }

    /**
     * Method to check if a collection contains only objects which are equals, a
     * subclass or implements one of the given classes. If the given collection
     * is empty, <code>true</code> will be returned.
     * 
     * @param c
     *            Is the collection to check.
     * @param containingClassTypes
     *            Are the class types which are expected.
     * @return Returns <code>true</code> if the given collection contains only
     *         objects which are equals, a subclass or implements one of the 
     *         given classes
     */
    public static boolean containsOnlyObjectsOfType(
        Collection c, Class<?>[] containingClassTypes) {
        Reject.ifNull(c);
        Reject.ifNull(containingClassTypes);
        Reject.ifFalse(containingClassTypes.length > 0);
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Class<?> elementClass = it.next().getClass();
            boolean noClassMatches = true;
            for (int i = 0; noClassMatches && i < containingClassTypes.length; 
                i++) {
                Class<?> containingClassType = containingClassTypes[i];
                noClassMatches 
                    = !containingClassType.isAssignableFrom(elementClass);
            }
            if (noClassMatches) {
                if (s_logger.isDebugEnabled()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Found object of type '");
                    sb.append(elementClass.getName());
                    sb.append("' which is not in assignable form for one of ");
                    sb.append("the following types: ");
                    for (int i = 0; i < containingClassTypes.length; i++) {
                        Class containingClassType = containingClassTypes[i];
                        if (i > 0) { sb.append(", "); }
                        sb.append(containingClassType.getName());
                    }
                    s_logger.debug(sb.toString());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Method to check if a collection contains only objects which are equals, a
     * subclass or implements the given class. If the given collection is empty,
     * <code>true</code> will be returned.
     * 
     * @param c
     *            Is the collection to check.
     * @param containingClassType
     *            Is the class type which is expected.
     * @return Returns <code>true</code> if the given collection contains only
     *         objects which are equals, a subclass or implements the given
     *         class.
     */
    public static boolean containsOnlyObjectsOfType(
        Collection c, Class containingClassType) {
        Reject.ifNull(c);
        Reject.ifNull(containingClassType);
        return containsOnlyObjectsOfType(c, new Class[] {containingClassType});
    }
    
    
    /** 
     * Finds the first element with index at least
     * {@code startAt} that equals {@code o}. This method may take linear time.
     * 
     * @param list the list to search
     * @param <T> the list's element type (duh!)
     * @param t the element to look for
     * @param startAt the index to start the search at
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
    
    /** swaps the elements at locations {@code i} and {@code j}. 
     * If this list is a {@link ExtendedReorderableList}, its swap method
     * is invoked, otherwise, set/get is used.
     * 
     * This behaves like Lists always declared a convenience swap method,
     * that is overridden if ReordableRandomAccessList is implemented.
     * @param list see above
     * @param i see above
     * @param j see above
     * @param <T> .
     */
    public static <T> void swap(List<T> list, int i, int j) {
        if (list instanceof ExtendedReorderableList) {
            ((ExtendedReorderableList<T>) list).swap(i, j);
        } else {
            defaultSwap(list, i, j);
        }
    }
    
    /** 
     * swaps the elements at locations {@code i} and {@code j} by set/get.
     * @param list .
     * @param i .
     * @param j .
     * @param <T> .
     */
    private static <T> void defaultSwap(List<T> list, int i, int j) {
        T t = list.get(i);
        list.set(i, list.get(j));
        list.set(j, t);        
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
    
    /**
     *  returns a filtered view on {@code list}.
     * @param <T> .
     * @param list see above
     * @param filter the filter deciding which elements to include
     * @return the filtered view
     */
    public static <T> FilteredList<T> filtered(List<? extends T> list, 
                                               Filter<? super T> filter) {
        return new DefaultFilteredList<T>(list, filter);
    }
    
    /**
     * Convenience method returning a {@link TransformedList} view to the
     * supplied list.
     * @param function the transformation function to apply to each element 
     * @return see above
     */
    public static <T, O> TransformedList<T, O> mapped(List<T> list, 
        Function<? super T, O> function) {
        return new DefaultTransformedList<T, O>(list, function);
    }
}
