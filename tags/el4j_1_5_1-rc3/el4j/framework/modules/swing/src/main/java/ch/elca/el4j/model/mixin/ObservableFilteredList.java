/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.model.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

import ch.elca.el4j.util.collections.helpers.Filter;

/**
 * An observable filtered list.
 * 
 * Remark: Some convenience methods are not implemented such as {@link #listIterator()} and {@link #subList(int, int)}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @param <E>    the type of the list items
 *
 * @author Stefan Wismer (SWI)
 */
public class ObservableFilteredList<E> implements ObservableList<E>, ObservableListListener {
	/**
	 * The unfiltered list.
	 */
	private ObservableList<E> m_list;
	
	/**
	 * The listeners that have to be notified.
	 */
	private List<ObservableListListener> m_listeners;
	
	/**
	 * The filter to apply to the list.
	 */
	private Filter<E> m_filter;
	
	/**
	 * @param list      the unfiltered list
	 * @param filter    the filter to apply to the list
	 */
	public ObservableFilteredList(ObservableList<E> list, Filter<E> filter) {
		m_list = list;
		m_filter = filter;
		m_listeners = new CopyOnWriteArrayList<ObservableListListener>();
		m_list.addObservableListListener(this);
	}
	
	/**
	 * @return    the currently used filter.
	 */
	public Filter<E> getFilter() {
		return m_filter;
	}

	/**
	 * @param filter    The new filter to apply. All iterators over this list also change the filter.
	 */
	@SuppressWarnings("unchecked")
	public void setFilter(Filter<E> filter) {
		// this method could be improved to create more precise change notifications (but there seems to be no benefit)
		if (size() > 0) {
			// copy all elements
			List<E> oldElems = new ArrayList(Arrays.asList(toArray()));
			for (ObservableListListener listener : m_listeners) {
				listener.listElementsRemoved(this, 0, oldElems);
			}
		}
		// change filter
		m_filter = filter;
		
		if (size() > 0) {
			for (ObservableListListener listener : m_listeners) {
				listener.listElementsAdded(this, 0, size());
			}
		}
	}

	// listener related methods
	/** {@inheritDoc} */
	public void addObservableListListener(ObservableListListener listener) {
		m_listeners.add(listener);
	}

	/** {@inheritDoc} */
	public void removeObservableListListener(ObservableListListener listener) {
		m_listeners.remove(listener);
	}

	/** {@inheritDoc} */
	public boolean supportsElementPropertyChanged() {
		return m_list.supportsElementPropertyChanged();
	}
	
	// methods to react on changes on the original list
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public void listElementsAdded(ObservableList list, int index, int length) {
		int visibleElementsAdded = 0;
		for (int i = 0; i < length; i++) {
			if (m_filter.accepts((E) list.get(index + i))) {
				visibleElementsAdded++;
				
			}
		}
		if (visibleElementsAdded > 0) {
			for (ObservableListListener listener : m_listeners) {
				listener.listElementsAdded(this, getIndexInFilteredList(index), visibleElementsAdded);
			}
		}
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public void listElementsRemoved(ObservableList list, int index, List oldElements) {
		for (Object object : oldElements) {
			if (m_filter.accepts((E) object)) {
				int filteredIndex = getIndexInFilteredList(index > 0 ? index - 1 : 0);
				for (ObservableListListener listener : m_listeners) {
					listener.listElementsRemoved(this, index > 0 ? filteredIndex + 1 : 0, oldElements);
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public void listElementReplaced(ObservableList list, int index, Object oldElement) {
		if (m_filter.accepts((E) oldElement)) {
			if (m_filter.accepts((E) list.get(index))) {
				// old and new elements are visible -> replaced
				for (ObservableListListener listener : m_listeners) {
					listener.listElementReplaced(this, getIndexInFilteredList(index), oldElement);
				}
			} else {
				// old element is visible, new is not -> removed
				int filteredIndex = getIndexInFilteredList(index > 0 ? index - 1 : 0);
				for (ObservableListListener listener : m_listeners) {
					listener.listElementsRemoved(this, index > 0 ? filteredIndex + 1 : 0, Arrays.asList(oldElement));
				}
			}
		} else {
			if (m_filter.accepts((E) list.get(index))) {
				// old element is not visible, new is -> added
				for (ObservableListListener listener : m_listeners) {
					listener.listElementsAdded(this, getIndexInFilteredList(index), 1);
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public void listElementPropertyChanged(ObservableList list, int index) {
		if (m_filter.accepts((E) list.get(index))) {
			for (ObservableListListener listener : m_listeners) {
				listener.listElementPropertyChanged(this, getIndexInFilteredList(index));
			}
		}
	}
	
	

	/** {@inheritDoc} */
	public boolean add(E o) {
		return m_list.add(o);
	}

	/** {@inheritDoc} */
	public void add(int index, E element) {
		m_list.add(getIndexInBackedList(index), element);
	}

	/** {@inheritDoc} */
	public boolean addAll(Collection<? extends E> c) {
		return m_list.addAll(c);
	}

	/** {@inheritDoc} */
	public boolean addAll(int index, Collection<? extends E> c) {
		return m_list.addAll(getIndexInBackedList(index), c);
	}

	/** {@inheritDoc} */
	public void clear() {
		m_list.clear();
	}

	/** {@inheritDoc} */
	public boolean contains(Object o) {
		for (E item : m_list) {
			boolean equals = (o == null ? item == null : o.equals(item));
			if (equals && m_filter.accepts(item)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	public boolean containsAll(Collection<?> c) {
		for (Object object : c) {
			if (!contains(object)) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	public E get(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		int currentIndex = 0;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				if (currentIndex == index) {
					return item;
				}
				currentIndex++;
			}
		}
		throw new IndexOutOfBoundsException();
	}

	/** {@inheritDoc} */
	public int indexOf(Object o) {
		int currentIndex = 0;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				if (o == null ? item == null : o.equals(item)) {
					return currentIndex;
				}
				currentIndex++;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	public boolean isEmpty() {
		return size() == 0;
	}

	/** {@inheritDoc} */
	public Iterator<E> iterator() {
		Iter<E> iter = new Iter<E>();
		m_list.addObservableListListener(iter);
		return iter; 
	}

	/** {@inheritDoc} */
	public int lastIndexOf(Object o) {
		int currentIndex = 0;
		int lastIndex = -1;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				if (o == null ? item == null : o.equals(item)) {
					lastIndex = currentIndex;
				}
				currentIndex++;
			}
		}
		return lastIndex;
	}

	/** {@inheritDoc} */
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
		//return m_list.listIterator();
	}

	/** {@inheritDoc} */
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
		//return m_list.listIterator(index);
	}

	/** {@inheritDoc} */
	public boolean remove(Object o) {
		return m_list.remove(o);
	}

	/** {@inheritDoc} */
	public E remove(int index) {
		return m_list.remove(getIndexInBackedList(index));
	}

	/** {@inheritDoc} */
	public boolean removeAll(Collection<?> c) {
		return m_list.removeAll(c);
	}

	/** {@inheritDoc} */
	public boolean retainAll(Collection<?> c) {
		return m_list.retainAll(c);
	}

	/** {@inheritDoc} */
	public E set(int index, E element) {
		return m_list.set(getIndexInBackedList(index), element);
	}

	/** {@inheritDoc} */
	public int size() {
		int size = 0;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				size++;
			}
		}
		return size;
	}

	/** {@inheritDoc} */
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
		//return m_list.subList(fromIndex, toIndex);
	}

	/** {@inheritDoc} */
	public Object[] toArray() {
		Object[] result = new Object[size()];
		int index = 0;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				result[index] = item;
				index++;
			}
		}
		
		return result;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		T[] result = a;
		// resize array if necessary
		if (a.length < size) {
			result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		}
		
		// insert values
		int index = 0;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				result[index] = (T) item;
				index++;
			}
		}
		
		// write null item if array is larger than necessary
		if (a.length > size) {
			result[size] = null;
		}
		return result;
	}
	
	/**
	 * @param filteredIndex    the index in the filtered list
	 * @return                 the index of the corresponding list item in the unfiltered list or
	 *                         <code>-1</code> if index is invalid
	 */
	private int getIndexInBackedList(int filteredIndex) {
		int currentIndex = 0;
		int index = 0;
		for (E item : m_list) {
			if (m_filter.accepts(item)) {
				if (index == filteredIndex) {
					return currentIndex;
				}
				index++;
			}
			currentIndex++;
		}
		return -1;
	}
	
	/**
	 * @param index    the index in the unfiltered list
	 * @return         the index of the corresponding list item in the filtered list or
	 *                 <code>-1</code> if index is invalid or is filtered
	 */
	private int getIndexInFilteredList(int index) {
		if (index < 0 || index >= m_list.size()) {
			return -1;
		}
		int filteredIndex = -1;
		for (int i = 0; i <= index; i++) {
			if (m_filter.accepts(m_list.get(i))) {
				filteredIndex++;
			}
		}
		return filteredIndex;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");

		Iterator<E> i = iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			E o = i.next();
			buf.append(o == this ? "(this Collection)" : String.valueOf(o));
			hasNext = i.hasNext();
			if (hasNext) {
				buf.append(", ");
			}
		}

		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * The observable iterator. Remove is not supported.
	 */
	private class Iter<I> implements Iterator<I>, ObservableListListener {
		/**
		 * The cursor that points to the current position in the unfiltered list.
		 */
		private int m_cursor = 0;
		
		/** {@inheritDoc} */
		public boolean hasNext() {
			int currentIndex = m_cursor + 1;
			while (currentIndex < m_list.size()) {
				if (m_filter.accepts(m_list.get(currentIndex))) {
					return true;
				}
				currentIndex++;
			}
			return false;
		}
		
		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		public I next() {
			m_cursor++;
			while (m_cursor < m_list.size()) {
				if (m_filter.accepts(m_list.get(m_cursor))) {
					return (I) m_list.get(m_cursor);
				}
				m_cursor++;
			}
			throw new NoSuchElementException();
		}
		
		/** 
		 * Remove is not supported.
		 */
		public void remove() {
			throw new UnsupportedOperationException();
			//m_list.remove(index);
		}
		
		
		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		public void listElementsAdded(ObservableList list, int index, int length) {
			if (index <= m_cursor) {
				m_cursor += length;
			}
		}
		
		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		public void listElementsRemoved(ObservableList list, int index, List oldElements) {
			if (index <= m_cursor) {
				m_cursor -= oldElements.size();
			}
		}
		
		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		public void listElementReplaced(ObservableList list, int index, Object oldElement) {
			// no impact on m_index
		}
		
		/** {@inheritDoc} */
		@SuppressWarnings("unchecked")
		public void listElementPropertyChanged(ObservableList list, int index) {
			// no impact on m_index
		}
	}
}
