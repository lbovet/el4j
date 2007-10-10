package com.silvermindsoftware.hitch.handlers.component;

import javax.swing.*;

/**
 * Copyright 2007 Brandon Goodin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class JComboBoxComponentHandler
	extends AbstractComponentHandler<JComboBox, Object, Object>
	implements ComponentHandler<JComboBox, Object, Object>
{

    // internal reflection lookup variables
    private String valueProperty;
    private String[] compareProperties;

    protected Class getSetterParameterType() {
        if (compareProperties != null && compareProperties.length > 0) {
            return int.class;
        } else {
            return Object.class;
        }
    }

    /**
     * Selection of an item can happen in a couple ways:
     * <ol>
     * <li>
     * Use compareProperties to specify properties that are examined to determine equality between the
     * JComboBox item and the model object property. If the model object property is a primitive or
     * first class object the compareProperties are only used to lookup properties on the JComboBox item.
     * </li>
     * <li>
     * Allow the JComboBox to naturally select the appropriate item. You can accomplish this by
     * not declaring any compareProperties. If you opt to do this you will need to override equals and hashcode
     * on more complex object in order to guarantee equality. With primitives or first class object it should
     * select without need of alteration
     * </li>
     * </ol>
     *
     * @param component - the component we are populating
     * @param modelPropertyValue - the value we are populating it with
     * @return the value that is needed by the component
     */
    public Object preProcessPopulate(JComboBox component, Object modelPropertyValue) {
        // if compareProperties have values then use them to select the  proper Index
        if (modelPropertyValue == null) {
            return 0;
        } else if (compareProperties != null && compareProperties.length > 0) {

            int selectedIndex = -1;
            //iterate model
            ComboBoxModel cbm = component.getModel();
            for (int index = 0; index < cbm.getSize(); index++) {
                Object item = cbm.getElementAt(index);
                boolean isEqual = true;
                for (String property : compareProperties) {

                    // get value on current element
                    Object comboBoxItem = getPropertyValue(item, property);

                    // if model object is prmitive 1st class compare
                    if (isBaseType(modelPropertyValue)) {
                        if (comboBoxItem == null) {
							// todo: we are in the else part of if(modelPropertyValue == null)
							// todo: this is never null
// 							if (modelPropertyValue != null) {
                                isEqual = false;
                                break;
//                            }
                        } else if (!comboBoxItem.equals(modelPropertyValue)) {
                            isEqual = false;
                            break;
                        }
                    } else {
                        // otherwise find the property

                        Object nestedPropertyValue = getPropertyValue(modelPropertyValue, property);

                        // compare
                        if (comboBoxItem == null) {
                            if (nestedPropertyValue != null) {
                                isEqual = false;
                                break;
                            }
                        } else if (!comboBoxItem.equals(nestedPropertyValue)) {
                            isEqual = false;
                            break;
                        }

                    }

                }

                if (isEqual) {
                    selectedIndex = index;
                    break;
                }

            }

            return selectedIndex;

        } else

        {
            // otherwise just set the object
            return modelPropertyValue;
        }
    }

    /**
     * If the selected item in the JCombo box is not the desired value to be set on the
     * model object property, you can use the valueProperty to specify a property on the JComboBox
     * selected item that will be used as the value that is set on the model object property.
     */
    public Object preProcessUpdate(JComboBox component, Object componentValue) {
        return getPropertyValue(componentValue, valueProperty);
    }

    protected String getGetterName() {
        return "getSelectedItem";
    }

    protected String getSetterName() {
        if (compareProperties != null && compareProperties.length > 0) {
            return "setSelectedIndex";
        } else {
            return "setSelectedItem";
        }
    }

    public void setCompareProperties(String compareProperties) {
        this.compareProperties = compareProperties.split(",");
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty = valueProperty;
    }
}
