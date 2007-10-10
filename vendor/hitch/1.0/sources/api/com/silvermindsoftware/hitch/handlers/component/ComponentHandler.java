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

import java.lang.reflect.Method;

public interface ComponentHandler<ComponentType, ModelType, UIType> {

    /**
     * This method should check for certain conditions that determine whether population should
     * even occur for this component.
     * <p/>
     * Example from JListComponentHandler:
     * <code>
     * <pre>
     * public class JListComponentHandler
     *   extends AbstractComponentHandler<JList, Object, Object>
     *   implements ComponentHandler<JList, Object, Object> {
     * ...
     *   public boolean isPopulateHandleable(JList component, Object modelPropertyValue) {
     *     return modelPropertyValue != null;
     *   }
     * ...
     * }
     * </pre>
     * </code>
     *
     * @param component
     * @param modelPropertyValue
     * @return
     */
    public boolean isPopulateHandleable(ComponentType component, ModelType modelPropertyValue);

    /**
     * This method should check for certain conditions that determine whether an update of the model
     * should even occur from this component. There is currently no scenario in hitch where this is
     * required. It was added as a counterpart to #isPopulateHandleable in the anticipation that it
     * may be useful for developers who create custom component handlers.
     *
     * @param component
     * @return
     */
    public boolean isUpdateHandleable(ComponentType component);

    /**
     * This is the name of the method that retrieves the value from the component. The
     * method name should be spelled out completely. For example if the component is
     * JTextField this method would return "getText". If the occasion ever requires it
     * you may pass different values back from this method @see #getComponentSetter for
     * example.
     *
     * @param component
     * @return
     */
    public Method getComponentGetter(ComponentType component);

    /**
     * This is the name of the method that sets the value on the component. The
     * method name should be spelled out completely. For example if the component is
     * JTextField this method would return "setText". If the occasion ever requires it
     * you may pass different values back from this method @see JComboBoxComponentHandler#getSetterName.
     * <p/>
     * Example from JComboBoxComponentHandler
     * <p/>
     * <code>
     * <pre>
     * public class JComboBoxComponentHandler
     *   extends AbstractComponentHandler<JComboBox, Object, Object>
     *   implements ComponentHandler<JComboBox, Object, Object>
     * {
     * ...
     * protected String getSetterName() {
     *   if (compareProperties != null && compareProperties.length > 0) {
     *     return "setSelectedIndex";
     *   } else {
     *     return "setSelectedItem";
     *   }
     * ...
     * }
     * </pre>
     * </code>
     *
     * @param component
     * @return
     */
    public Method getComponentSetter(ComponentType component);

    /**
     * <h1><b>
     * Right up front let's make it clear that you should not use this method for performing type conversion
     * </b></h1>
     * <p/>
     * This method is meant to massage existing data into a state that can be converted. Let's
     * take a look at the TextComponentHandler. You can see that the TextComponentHandler examines
     * if the modelPropertyValue is null and if it is alters it's representation to an empty String.
     * The reason for this is that JTextField will display a null as 'null' and that is undesireable.
     * It would not be appropriate to deal with this in a TypeHandler. For a more complex example you
     * can take a look at the @see JComboBoxComponentHandler#preProcessPopulate which is a very
     * involved  implementation.
     * <p/>
     * <b>TextComponent Handler Example</b><br/>
     * <code>
     * <pre>
     * public class TextComponentHandler
     *   extends AbstractComponentHandler<JComponent, Object, Object>
     *   implements ComponentHandler<JComponent, Object, Object>
     * {
     * ...
     *   public Object preProcessPopulate(JComponent component, Object modelPropertyValue) {
     *     return modelPropertyValue == null ? "" : modelPropertyValue;
     *   }
     * ...
     * }
     * </pre>
     * </code>
     *
     * @param component
     * @param modelPropertyValue
     * @return
     */
    public UIType preProcessPopulate(ComponentType component, ModelType modelPropertyValue);

    /**
     * <h1><b>
     * Right up front let's make it clear that you should not use this method for performing type conversion
     * </b></h1>
     * <p/>
     * Just like @see #preProcessPopulate the preProcessUpdate method is meant to massage data
     * into a state that can be converted.
     * </p>
     * Following is an example of preProcessUpdate as used in the JListComponentHandler
     * <code>
     * <pre>
     * public class JListComponentHandler
     *   extends AbstractComponentHandler<JList, Object, Object>
     *   implements ComponentHandler<JList, Object, Object> {
     * ...
     * 	public Object preProcessUpdate(JList component, Object formFieldValue) {
     * 	  // if a single selection return a single object
     * 	  if (component.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
     * 	    Object[] selectedValues = (Object[]) formFieldValue;
     * 	    if (selectedValues.length == 0) {
     * 	      return null;
     * 	    } else {
     * 	      return selectedValues[0];
     * 		}
     * 	  } else {
     * 	    //otherwise return an array
     * 	    return formFieldValue;
     * 	  }
     *  }
     * ...
     * }
     * </pre>
     * </code>
     *
     * @param component
     * @param modelPropertyValue
     * @return
     */
    public ModelType preProcessUpdate(ComponentType component, UIType modelPropertyValue);

    /**
     * This method handles post processing of a populate. There is currently no scenario in hitch
     * where this is required. It was added as a counterpart to #preProcessPopulate in the
     * anticipation that it may be useful for developers who create custom component handlers.
     *
     * @param component
     */
    public void postProcessPopulate(ComponentType component);

    /**
     * This method handles post processing of an update. There is currently no scenario in hitch
     * where this is required. It was added as a counterpart to #preProcessUpdate in the
     * anticipation that it may be useful for developers who create custom component handlers.
     *
     * @param component
     */
    public void postProcessUpdate(ComponentType component);

    /**
     * This method provides the default read only state for this component. For example, the
     * JLabel is generally used as a read only component so its default is set to true.
     * <p/>
     * <code>
     * <pre>
     * public class JLabelComponentHandler extends TextComponentHandler {
     *   public boolean getDefaultReadOnly() {
     *     return true;
     *   }
     * }
     * </pre>
     * </code>
     *
     * @return
     */
    public boolean getDefaultReadOnly();

}
