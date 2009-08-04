/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE), Adrian Moos (AMS)
 */
public class CollectionUtils {
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(CollectionUtils.class);
	
	/**
	 * Hidden constructor.
	 */
	protected CollectionUtils() { }

	/**
	 * Null save check if a collection is empty.
	 *
	 * @param c Is the given collection
	 * @return Returns true if the given collection is null or empty.
	 */
	public static boolean isEmpty(Collection<?> c) {
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
	public static <T> List<T> asList(List<T> list) {
		return list == null ? new LinkedList<T>() : list;
	}

	/**
	 * Method to always return a set object.
	 *
	 * @param set
	 *            Is the set which should not be null.
	 * @return Returns the given set if it is not null. Otherwise it returns an
	 *         empty set.
	 */
	public static <T> Set<T> asSet(Set<T> set) {
		return set == null ? new HashSet<T>() : set;
	}

	/**
	 * Method to always return a map object.
	 *
	 * @param map
	 *            Is the map which should not be null.
	 * @return Returns the given map if it is not null. Otherwise it returns an
	 *         empty map.
	 */
	public static <K,V> Map<K,V> asMap(Map<K,V> map) {
		return map == null ? new HashMap<K,V>() : map;
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
		Collection<?> c, Class<?>[] containingClassTypes) {
		Reject.ifNull(c);
		Reject.ifEmpty(containingClassTypes);
		Iterator<?> it = c.iterator();
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
						Class<?> containingClassType = containingClassTypes[i];
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
		Collection<?> c, Class<?> containingClassType) {
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
	 *
	 * @param <T> the backing list's element type
	 * @param <O> this list's element type
	 * @param list Is the list to transform.
	 * @param function the transformation function to apply to each element
	 * @return see above
	 */
	public static <T, O> TransformedList<T, O> mapped(List<T> list,
		Function<? super T, O> function) {
		return new DefaultTransformedList<T, O>(list, function);
	}
	
	/**
	 * @param base
	 *            Is the collection to enrich.
	 * @param annex
	 *            Is the collection with the items to enrich the base
	 *            collection.
	 * @return Return the complemented base collection.
	 */
	@SuppressWarnings("unchecked")
	public static Collection nullSaveAddAll(Collection base, Collection annex) {
		Assert.notNull(base);
		if (annex != null) {
			base.addAll(annex);
		}
		return base;
	}
}
