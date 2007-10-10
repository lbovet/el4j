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

public class JSpinnerComponentHandler
        extends AbstractComponentHandler<JSpinner, Object, Object>
        implements ComponentHandler<JSpinner, Object, Object>{

    private String valueProperty;
    private String[] compareProperties;

    protected Class getSetterParameterType() {
        return Object.class;
    }

    protected String getGetterName() {
        return "getValue";
    }

    protected String getSetterName() {
        return "setValue";
    }


    public boolean isPopulateHandleable(JSpinner component, Object modelPropertyValue) {
        return modelPropertyValue != null;
    }

    public Object preProcessPopulate(JSpinner spinner, Object modelPropertyValue) {

        // if compareProperties have values then use them to select the proper Index
        if (compareProperties != null && compareProperties.length > 0) {

            Object selectedItem = modelPropertyValue;
            //iterate model

            // check if SpinnerModel is a SpinnerListModel and iterate and compare
            if (spinner.getModel() instanceof SpinnerListModel) {

                SpinnerListModel spinnerListModel = (SpinnerListModel) spinner.getModel();

                for (Object item : spinnerListModel.getList()) {
                    boolean isEqual = true;
                    for (String property : compareProperties) {

                        // get value on current item
                        Object itemValue = getPropertyValue(item, property);

                        // if model object is prmitive 1st class compare
                        if (isBaseType(modelPropertyValue)) {
                            if (itemValue == null) {
                                if (modelPropertyValue != null) {
                                    isEqual = false;
                                    break;
                                }
                            } else if (!itemValue.equals(modelPropertyValue)) {
                                isEqual = false;
                                break;
                            }
                        } else {
                            // otherwise find the property

                            Object nestedPropertyValue = getPropertyValue(modelPropertyValue, property);

                            // compare
                            if (itemValue == null) {
                                if (nestedPropertyValue != null) {
                                    isEqual = false;
                                    break;
                                }
                            } else if (!itemValue.equals(nestedPropertyValue)) {
                                isEqual = false;
                                break;
                            }

                        }

                    }

                    if (isEqual) {
                        selectedItem = item;
                        break;
                    }

                }
            }

            return selectedItem;

        } else {
            // otherwise just set the object
            return modelPropertyValue;
        }
    }

    /**
     * If the selected item in the JSpinner is not the desired value to be set on the
     * model object property, you can use the valueProperty to specify a property on the JSpinner
     * selected item that will be used as the value that is set on the model object property.
     *
     * @param component
     * @param componentValue
     * @return
     */
    public Object preProcessUpdate(JSpinner component, Object componentValue) {
        return getPropertyValue(componentValue, valueProperty);
    }

    public void setCompareProperties(String compareProperties) {
        this.compareProperties = compareProperties.split(",");
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty = valueProperty;
    }

}
