package com.silvermindsoftware.hitch.binding;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;


import com.silvermindsoftware.hitch.validation.ValidatingBindingListener;
import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

/**
 * This class is used for creating bindings.
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
public class BindingFactory {
    /**
     * An instance of the factory (used for singleton).
     */
    private static BindingFactory s_bindingFactory;
    
    /**
     * Default mapping of properties.
     */
    private static final DefaultProperties DEFAULT_PROPERTIES
        = new DefaultProperties();
    
    /**
     * Map containing user defined mappings.
     */
    private Map<JComponent, SpecialBindingCreator> m_specialBinding;
    
    
    /**
     * The hidden constructor.
     */
    protected BindingFactory() {
        m_specialBinding = new HashMap<JComponent, SpecialBindingCreator>();
    }
    
    /**
     * @return    an instance of this class
     */
    public static BindingFactory getInstance() {
        if (s_bindingFactory == null) {
            s_bindingFactory = new BindingFactory();
        }
        return s_bindingFactory;
    }
    
    /**
     * @return      the default properties
     */
    public static DefaultProperties getDefaultProperties() {
        return DEFAULT_PROPERTIES;
    }
    
    /**
     * Registers a custom binding strategy.
     * 
     * @param component     the component to bind
     * @param binding       the custom binding
     */
    public void register(JComponent component, SpecialBindingCreator binding) {
        m_specialBinding.put(component, binding);
    }
    
    /**
     * @param modelObject       the instance of the model
     * @param modelProperty     the property of the model to bound
     * @param formComponent     the form component to bound to
     * @return                  a AutoBinding object representing the binding
     */
    @SuppressWarnings("unchecked")
    public AutoBinding getBinding(Object modelObject, String modelProperty,
        JComponent formComponent) {
        
        // is a special binding registered?
        if (m_specialBinding.containsKey(formComponent)) {
            return m_specialBinding.get(formComponent).createBinding(
                modelObject, modelProperty, formComponent);
        } else {
            // create a default binding
            Property modelPropertyName = BeanProperty.create(modelProperty);
            Property fromPropertyName = DEFAULT_PROPERTIES.getDefaultProperty(
                formComponent);
            return Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                modelObject, modelPropertyName,
                formComponent, fromPropertyName);
        }
    }
    
    /**
     * @param formComponent    the GUI component
     * @param responder        the validationResponder
     * @return                 a {@link BindingListener} that performs the
     *                         validation
     */
    public BindingListener getValidationListener(JComponent formComponent,
        ValidationResponder responder) {
        
        if (m_specialBinding.containsKey(formComponent)) {
            m_specialBinding.get(formComponent).addValidation(formComponent);
        }
        return new ValidatingBindingListener(responder);
    }
}
