package com.silvermindsoftware.hitch.swing;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright 2007 Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This is a pretty much a direct copy of the SpinnerListModel from JDK 1.5 sources.
 * It was copied out in order to expose methods that needed to be publicly available
 * in order to allow for completion to work properly with a list of complex objects
 * that present their human readable value from a toString(). The findNextMatch
 * method was exposed as public.
 */
public class EnhancedSpinnerListModel extends SpinnerListModel {

	private List list;
		private int index;


		/**
		 * Constructs a <code>SpinnerModel</code> whose sequence of
		 * values is defined by the specified <code>List</code>.
		 * The initial value (<i>current element</i>)
		 * of the model will be <code>values.get(0)</code>.
		 * If <code>values</code> is <code>null</code> or has zero
		 * size, an <code>IllegalArugmentException</code> is thrown.
		 *
		 * @param values the sequence this model represents
		 * @throws IllegalArugmentException if <code>values</code> is
		 *                                  <code>null</code> or zero size
		 */
		@SuppressWarnings({"JavadocReference"})
		public EnhancedSpinnerListModel(List<?> values) {
			if (values == null || values.size() == 0) {
				throw new IllegalArgumentException("SpinnerListModel(List) expects non-null non-empty List");
			}
			this.list = values;
			this.index = 0;
		}


		/**
		 * Constructs a <code>SpinnerModel</code> whose sequence of values
		 * is defined by the specified array.  The initial value of the model
		 * will be <code>values[0]</code>.  If <code>values</code> is
		 * <code>null</code> or has zero length, an
		 * <code>IllegalArugmentException</code> is thrown.
		 *
		 * @param values the sequence this model represents
		 * @throws IllegalArugmentException if <code>values</code> is
		 *                                  <code>null</code> or zero length
		 */
		@SuppressWarnings({"JavadocReference"})
		public EnhancedSpinnerListModel(Object[] values) {
			if (values == null || values.length == 0) {
				throw new IllegalArgumentException("SpinnerListModel(Object[]) expects non-null non-empty Object[]");
			}
			this.list = Arrays.asList(values);
			this.index = 0;
		}


		/**
		 * Constructs an effectively empty <code>SpinnerListModel</code>.
		 * The model's list will contain a single
		 * <code>"empty"</code> string element.
		 */
		public EnhancedSpinnerListModel() {
			this(new Object[]{"empty"});
		}


		/**
		 * Returns the <code>List</code> that defines the sequence for this model.
		 *
		 * @return the value of the <code>list</code> property
		 * @see #setList
		 */
		public List<?> getList() {
			return list;
		}


		/**
		 * Changes the list that defines this sequence and resets the index
		 * of the models <code>value</code> to zero.  Note that <code>list</code>
		 * is not copied, the model just stores a reference to it.
		 * <p/>
		 * This method fires a <code>ChangeEvent</code> if <code>list</code> is
		 * not equal to the current list.
		 *
		 * @param list the sequence that this model represents
		 * @throws IllegalArgumentException if <code>list</code> is
		 *                                  <code>null</code> or zero length
		 * @see #getList
		 */
		public void setList(List<?> list) {
			if ((list == null) || (list.size() == 0)) {
				throw new IllegalArgumentException("invalid list");
			}
			if (!list.equals(this.list)) {
				this.list = list;
				index = 0;
				fireStateChanged();
			}
		}


		/**
		 * Returns the current element of the sequence.
		 *
		 * @return the <code>value</code> property
		 * @see javax.swing.SpinnerModel#getValue
		 * @see #setValue
		 */
		public Object getValue() {
			return list.get(index);
		}


		/**
		 * Changes the current element of the sequence and notifies
		 * <code>ChangeListeners</code>.  If the specified
		 * value is not equal to an element of the underlying sequence
		 * then an <code>IllegalArgumentException</code> is thrown.
		 * In the following example the <code>setValue</code> call
		 * would cause an exception to be thrown:
		 * <pre>
		 * String[] values = {"one", "two", "free", "four"};
		 * SpinnerModel model = new SpinnerListModel(values);
		 * model.setValue("TWO");
		 * </pre>
		 *
		 * @param elt the sequence element that will be model's current value
		 * @throws IllegalArgumentException if the specified value isn't allowed
		 * @see javax.swing.SpinnerModel#setValue
		 * @see #getValue
		 */
		public void setValue(Object elt) {
			int index = list.indexOf(elt);
			if (index == -1) {
				throw new IllegalArgumentException("invalid sequence element");
			} else if (index != this.index) {
				this.index = index;
				fireStateChanged();
			}
		}


		/**
		 * Returns the next legal value of the underlying sequence or
		 * <code>null</code> if value is already the last element.
		 *
		 * @return the next legal value of the underlying sequence or
		 *         <code>null</code> if value is already the last element
		 * @see javax.swing.SpinnerModel#getNextValue
		 * @see #getPreviousValue
		 */
		public Object getNextValue() {
			return (index >= (list.size() - 1)) ? null : list.get(index + 1);
		}


		/**
		 * Returns the previous element of the underlying sequence or
		 * <code>null</code> if value is already the first element.
		 *
		 * @return the previous element of the underlying sequence or
		 *         <code>null</code> if value is already the first element
		 * @see javax.swing.SpinnerModel#getPreviousValue
		 * @see #getNextValue
		 */
		public Object getPreviousValue() {
			return (index <= 0) ? null : list.get(index - 1);
		}


		/**
		 * Returns the next object that starts with <code>substring</code>.
		 *
		 * @param substring the string to be matched
		 * @return the match
		 */
		public Object findNextMatch(String substring) {
			int max = list.size();

			if (max == 0) {
				return null;
			}
			int counter = index;

			do {
				Object value = list.get(counter);
				String string = value.toString();

				if (string != null && string.startsWith(substring)) {
					return value;
				}
				counter = (counter + 1) % max;
			} while (counter != index);
			return null;
		}

}
