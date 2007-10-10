package com.silvermindsoftware.hitch.binding;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

public class BindingFactory {
    private static final DefaultProperties defaultProperties = new DefaultProperties();
    private Map<JComponent, SpecialBinding> specialBinding;
    
    public BindingFactory() {
        specialBinding = new HashMap<JComponent, SpecialBinding>();
    }
    
    /**
     * Registers a custom binding strategy.
     * 
     * @param component     the component to bind
     * @param binding       the custom binding
     */
    public void register(JComponent component, SpecialBinding binding) {
        specialBinding.put(component, binding);
    }
    
    /**
     * @return      the default properties
     */
    public static DefaultProperties getDefaultProperties() {
        return defaultProperties;
    }

    /**
     * @param modelObject       the instance of the model
     * @param modelProperty     the property of the model to bound
     * @param formComponent     the form component to bound to
     * @return                  a AutoBinding object representing the binding
     */
    public AutoBinding getBinding(Object modelObject, String modelProperty, JComponent formComponent) {
        // is a special binding registered?
        if (specialBinding.containsKey(formComponent)) {
            return specialBinding.get(formComponent).createBinding(modelObject, modelProperty, formComponent);
        } else {
            // create a default binding
            Property modelPropertyName = BeanProperty.create(modelProperty);
            Property fromPropertyName = defaultProperties.getDefaultProperty(formComponent);
            return Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, modelObject, modelPropertyName, formComponent, fromPropertyName);
        }
    }
}
