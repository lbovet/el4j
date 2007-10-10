package com.silvermindsoftware.hitch.swing;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.text.ParseException;
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
 * ListFormatter provides completion while text is being input
 * into the JFormattedTextField.Completion is only done if the
 * user is inserting text at the end of the document.Completion
 * is done by way of the SpinnerListModel method findNextMatch.
 * This is largely a copy of the SpinnerListFormatter found in
 * JDK 1.5 sources. A new version was written because the JDK
 * version was not extensible and did not allow for the ability
 * to ehance the stringToValue.
 *
 * The stringToValue was enhanced to iterate through the model
 * list and compare the String values of the list objects and
 * the string value passed to the formatter. The first equal
 * will return. So, if multiple objects have the same toString
 * value, only the first will be returned.
 */
public class EnhancedSpinnerListFormatter extends
		JFormattedTextField.AbstractFormatter {
	private DocumentFilter filter;
	private EnhancedSpinnerListModel model;


	public EnhancedSpinnerListFormatter(EnhancedSpinnerListModel model) {
		this.model = model;
	}

	public String valueToString(Object value) throws ParseException {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	public Object stringToValue(String string) throws ParseException {

		for (Object item : model.getList()) {
			if (item.toString().equals(string)) return item;
		}
		return null;
	}

	protected DocumentFilter getDocumentFilter() {
		if (filter == null) {
			filter = new Filter();
		}
		return filter;
	}


	private class Filter extends DocumentFilter {
		public void replace(FilterBypass fb, int offset, int length,
							String string, AttributeSet attrs) throws
				BadLocationException {
			if (string != null && (offset + length) ==
					fb.getDocument().getLength()) {
				Object next = model.findNextMatch(
						fb.getDocument().getText(0, offset) +
								string);
				String value = (next != null) ? next.toString() : null;

				if (value != null) {
					fb.remove(0, offset + length);
					fb.insertString(0, value, null);
					getFormattedTextField().select(offset +
							string.length(),
							value.length());
					return;
				}
			}
			super.replace(fb, offset, length, string, attrs);
		}

		public void insertString(FilterBypass fb, int offset,
								 String string, AttributeSet attr)
				throws BadLocationException {
			replace(fb, offset, 0, string, attr);
		}
	}
}

