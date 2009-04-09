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
package ch.elca.el4j.tests.gui.model.mixin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.junit.Before;
import org.junit.Test;

import ch.elca.el4j.services.gui.model.mixin.ObservableFilteredList;
import ch.elca.el4j.util.collections.helpers.Filter;

/**
 * Test class to test {@link ObservableFilteredList}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class ObservableFilteredListTest {
	
	private Filter<Integer> m_filter;
	private ObservableList<Integer> m_list;
	
	@Before
	public void initFields() {
		m_filter = new OddFilter();
		m_list = ObservableCollections.observableList(new ArrayList<Integer>(
			Arrays.asList(1, 3, 5, 2, 7, 4, 3, 8, 6)));
	}

	private class OddFilter implements Filter<Integer> {
		public boolean accepts(Integer value) {
			return value % 2 == 0;
		}
	}
	
	private class EvenFilter implements Filter<Integer> {
		public boolean accepts(Integer value) {
			return value % 2 == 1;
		}
	}
	
	private static class Listener implements ObservableListListener {
		private enum EventType {add, remove, replace, change, undefined};
		
		private EventType lastEvent;
		private int lastIndex;
		private int lastLength;
		private Object lastOldElement;
		private List lastOldElements;
		
		public Listener() {
			reset();
		}
		
		private void reset() {
			lastEvent = EventType.undefined;
			lastIndex = -1;
			lastLength = -1;
			lastOldElement = null;
			lastOldElements = null;
		}
		
		
		public void listElementsAdded(ObservableList list, int index, int length) {
			lastEvent = EventType.add;
			lastIndex = index;
			lastLength = length;
		}
		public void listElementsRemoved(ObservableList list, int index, List oldElements) {
			lastEvent = EventType.remove;
			lastIndex = index;
			lastOldElements = oldElements;
		}
		public void listElementReplaced(ObservableList list, int index, Object oldElement) {
			lastEvent = EventType.replace;
			lastIndex = index;
			lastOldElement = oldElement;
		}
		public void listElementPropertyChanged(ObservableList list, int index) {
			lastEvent = EventType.change;
			lastIndex = index;
		}
		
		// methods to check if last event was the expected one
		
		public void verifyNoEvent() {
			assertEquals(EventType.undefined, lastEvent);
			assertEquals(-1, lastIndex);
			assertEquals(-1, lastLength);
			assertEquals(null, lastOldElement);
			assertEquals(null, lastOldElements);
		}
		
		public void verifyAdded(int index, int length) {
			assertEquals(EventType.add, lastEvent);
			assertEquals(index, lastIndex);
			assertEquals(length, lastLength);
			assertEquals(null, lastOldElement);
			assertEquals(null, lastOldElements);
			reset();
		}
		
		public void verifyRemoved(int index, List oldElements) {
			assertEquals(EventType.remove, lastEvent);
			assertEquals(index, lastIndex);
			assertEquals(-1, lastLength);
			assertEquals(null, lastOldElement);
			assertEquals(oldElements, lastOldElements);
			reset();
		}
		
		public void verifyReplaced(int index, Object oldElement) {
			assertEquals(EventType.replace, lastEvent);
			assertEquals(index, lastIndex);
			assertEquals(-1, lastLength);
			assertEquals(oldElement, lastOldElement);
			assertEquals(null, lastOldElements);
			reset();
		}
		
		public void verifyPropertyChanged(int index) {
			assertEquals(EventType.change, lastEvent);
			assertEquals(index, lastIndex);
			assertEquals(-1, lastLength);
			assertEquals(null, lastOldElement);
			assertEquals(null, lastOldElements);
			reset();
		}
	}

	@Test
	public void testObservableFilteredListReadOnly() {
		ObservableFilteredList<Integer> filteredList = new ObservableFilteredList<Integer>(m_list , m_filter);
		
		assertEquals(Integer.valueOf(2), filteredList.get(0));
		assertEquals(Integer.valueOf(4), filteredList.get(1));
		assertEquals(Integer.valueOf(8), filteredList.get(2));
		assertEquals(Integer.valueOf(6), filteredList.get(3));
		try {
			filteredList.get(-1);
			fail();
		} catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			filteredList.get(4);
			fail();
		} catch (IndexOutOfBoundsException e) {
			// OK
		}
		
		assertEquals(4, filteredList.size());
		
		Integer[] correctResult = new Integer[] {2, 4, 8, 6};
		assertArrayEquals(correctResult, filteredList.toArray());
		Integer[] tmp1 = new Integer[] {3, 2};
		assertArrayEquals(correctResult, filteredList.toArray(tmp1));
		
		Integer[] tmp2 = new Integer[] {0, 0, 0, 0, 0, 0};
		Integer[] tmp2Result = new Integer[] {2, 4, 8, 6, null, 0};
		assertArrayEquals(tmp2Result, filteredList.toArray(tmp2));
		
		//filteredList.add(0);
	}
	
	@Test
	public void testObservableFilteredListReadWrite() {
		ObservableFilteredList<Integer> filteredList = new ObservableFilteredList<Integer>(m_list, m_filter);
		
		filteredList.add(0);
		assertArrayEquals(new Integer[] {2, 4, 8, 6, 0}, filteredList.toArray());
		
		filteredList.add(2, 100);
		assertArrayEquals(new Integer[] {2, 4, 100, 8, 6, 0}, filteredList.toArray());
		
		try {
			filteredList.add(-1, 100);
			fail();
		} catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			filteredList.add(filteredList.size(), 100);
			fail();
		} catch (IndexOutOfBoundsException e) {
			// OK
		}
		
		// array is now [2, 4, 100, 8, 6, 0]
		assertEquals(0, filteredList.indexOf(2));
		assertEquals(1, filteredList.indexOf(4));
		assertEquals(2, filteredList.indexOf(100));
		assertEquals(5, filteredList.indexOf(0));
		
		assertEquals(-1, filteredList.indexOf(1));
		assertEquals(-1, filteredList.indexOf(3));
		assertEquals(-1, filteredList.indexOf(200));
		
		filteredList.add(3, 6);
		// array is now [2, 4, 100, 6, 8, 6, 0]
		assertEquals(3, filteredList.indexOf(6));
		assertEquals(5, filteredList.lastIndexOf(6));
		
		Iterator<Integer> it = filteredList.iterator();
		Integer[] tmp1 = new Integer[] {2, 4, 100, 6, 8, 6, 0};
		for (Integer number : tmp1) {
			assertTrue(it.hasNext());
			assertEquals(number, it.next());
		}
		assertFalse(it.hasNext());
		
		filteredList.remove(2);
		filteredList.remove(2);
		filteredList.remove(4);
		Integer[] tmp2 = new Integer[] {2, 4, 8, 6};
		assertArrayEquals(tmp2, filteredList.toArray());
		
		assertTrue(filteredList.contains(2));
		assertTrue(filteredList.contains(6));
		assertFalse(filteredList.contains(100));
		
		assertFalse(filteredList.contains(1));
	}
	
	@Test
	public void testObservableAddAndRemove() {
		ObservableFilteredList<Integer> filteredList = new ObservableFilteredList<Integer>(m_list , m_filter);
		
		Listener listener = new Listener();
		filteredList.addObservableListListener(listener);
		
		filteredList.add(0);
		listener.verifyAdded(4, 1);
		
		filteredList.add(0, 3);
		listener.verifyNoEvent();
		
		filteredList.remove(4);
		listener.verifyRemoved(4, Arrays.asList(0));
		
		filteredList.remove(0);
		listener.verifyRemoved(0, Arrays.asList(2));
		
		filteredList.remove(1);
		listener.verifyRemoved(1, Arrays.asList(8));
	}
	
	@Test
	public void testObservableReplace() {
		ObservableFilteredList<Integer> filteredList = new ObservableFilteredList<Integer>(m_list , m_filter);
		
		Listener listener = new Listener();
		filteredList.addObservableListListener(listener);
		
		filteredList.set(0, 6);
		listener.verifyReplaced(0, 2);
		
		filteredList.set(0, 7);
		listener.verifyRemoved(0, Arrays.asList(6));
		
		m_list.set(0, 20);
		listener.verifyAdded(0, 1);
		
	}
	
	@Test
	public void testReplaceFilter() {
		ObservableFilteredList<Integer> filteredList = new ObservableFilteredList<Integer>(m_list , m_filter);
		
		filteredList.setFilter(new EvenFilter());
		Integer[] correctResult = new Integer[] {1, 3, 5, 7, 3};
		assertArrayEquals(correctResult, filteredList.toArray());
	}
	
	@Test
	public void testNotifyIterator() {
		ObservableFilteredList<Integer> filteredList = new ObservableFilteredList<Integer>(m_list , m_filter);
		Iterator<Integer> it = filteredList.iterator();
		// set iterator on 2
		it.next();
		
		filteredList.add(1, 12);
		assertEquals(Integer.valueOf(12), it.next());
		
		//1, 3, 5, 2, 12, 7, 4, 3, 8, 6
		filteredList.retainAll(Arrays.asList(7, 3, 6));
		assertEquals(Integer.valueOf(6), it.next());
		
	}
}
