package com.silvermindsoftware.hitch.handlers.component;

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

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class JListComponentHandler
	extends AbstractComponentHandler<JList, Object, Object>
	implements ComponentHandler<JList, Object, Object> {

	protected Class getSetterParameterType() {
		return int[].class;
	}

	public boolean isPopulateHandleable(JList component, Object modelPropertyValue) {
		return modelPropertyValue != null;
	}

	public Object preProcessPopulate(JList component, Object modelPropertyValue) {

		List<Integer> integerList = new ArrayList<Integer>();

		if (modelPropertyValue.getClass().isArray()) {

			// iterate objects and locate in component model
			List objectList =
				Arrays.asList(
					(Object[]) modelPropertyValue);

			ListModel dm = component.getModel();

			// allow user to configure custom comparator
			for (int i = 0, c = dm.getSize(); i < c; i++) {
				if (objectList.contains(dm.getElementAt(i))) {
					integerList.add(i);
				}
			}

		} else {
			ListModel dm = component.getModel();

			// allow user to configure custom comparator
			for (int i = 0, c = dm.getSize(); i < c; i++) {
				if (modelPropertyValue.equals(dm.getElementAt(i))) {
					integerList.add(i);
					break;
				}
			}
		}

		int[] retVal;

		if (integerList.size() == 0) {
			retVal = new int[]{-1};
		} else {
			retVal = new int[integerList.size()];
			int index = 0;
			for (Integer val : integerList) {
				retVal[index] = val;
			}
		}

		return retVal;
	}


	public Object preProcessUpdate(JList component, Object formFieldValue) {
		// if a single selection return a single object
		if (component.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
			Object[] selectedValues = (Object[]) formFieldValue;
			if (selectedValues.length == 0) {
				return null;
			} else {
				return selectedValues[0];
			}

		} else {
			//otherwise return an array
			return formFieldValue;
		}
	}


	protected String getSetterName() {
		return "setSelectedIndices";
	}

	protected String getGetterName() {
		return "getSelectedValues";
	}
}
