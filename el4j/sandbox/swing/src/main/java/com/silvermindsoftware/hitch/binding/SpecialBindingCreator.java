package com.silvermindsoftware.hitch.binding;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;

/**
 * A user defined "binding template" for a special widget.
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
public interface SpecialBindingCreator {
    /**
     * Create the concrete binding.
     * 
     * @param modelObject       the instance of the model
     * @param modelProperty     the property of the model to bind
     * @param formComponent     the widget to bound to
     * @return                  the corresponding binding
     */
    @SuppressWarnings("unchecked")
    public AutoBinding createBinding(Object modelObject,
        String modelProperty, JComponent formComponent);
    
    /**
     * Add validation capability.
     * 
     * @param formComponent     the widget showing the values
     */
    public void addValidation(JComponent formComponent);
}
