package com.silvermindsoftware.hitch.binding;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;

/**
 * A user defined "binding template" for a special widget
 * 
 * @author SWI
 */
public interface SpecialBinding {
    /**
     * Create the concrete binding.
     * 
     * @param modelObject       the instance of the model
     * @param modelProperty     the property of the model to bind
     * @param formComponent     the widget to bound to
     * @return                  the corresponding binding
     */
    public AutoBinding createBinding(Object modelObject, String modelProperty, JComponent formComponent);
}
