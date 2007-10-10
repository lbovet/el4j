package com.silvermindsoftware.hitch;

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

import java.awt.Container;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.BindingGroup;

import com.silvermindsoftware.hitch.binding.SpecialBinding;

public interface Binder {
    
    /**
     * Returns a validating binding group containing all bindings
     * between the <code>container</code> and its associated model.
     * 
     * @param container    The component containing a reference to the model and the
     *                     corresponding Swing components to show its properties
     * @param modelId      The optional model identifier (used if container contains
     *                     multiple models)
     * @return             the binding group
     */
    public BindingGroup getAutoBinding(Container container, String... modelId);
    
    /**
     * Returns a binding group containing all bindings
     * between the <code>container</code> and its associated model.
     * 
     * @param container    The component containing a reference to the model and the
     *                     corresponding Swing components to show its properties
     * @param performValidate  determines if a validation should be performed
     * @param modelId      The optional model identifier (used if container contains
     *                     multiple models)
     * @return             the binding group
     */
    public BindingGroup getAutoBinding(Container container, boolean performValidate, String... modelId);
    
    /**
     * Registers a custom binding strategy.
     * 
     * @param component     the component to bind
     * @param binding       the custom binding
     */
    public void registerBinding(JComponent component, SpecialBinding binding);
}
