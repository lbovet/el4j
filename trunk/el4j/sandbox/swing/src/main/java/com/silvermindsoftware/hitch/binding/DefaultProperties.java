package com.silvermindsoftware.hitch.binding;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;

/**
 * This class holds a mapping of objects or classes to their default property.
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
public class DefaultProperties {
    /**
     * The mapping of objects or classes to their default property.
     */
    private Map<Object, Property<?, ?>> m_defaultProperties;
    
    /**
     * Constructor with support for common GUI elements.
     */
    public DefaultProperties() {
        m_defaultProperties = new HashMap<Object, Property<?, ?>>();
        
        register(JButton.class, "text");
        register(JTextField.class, "text");
        register(JTextArea.class, "text");
        register(JLabel.class, "text");
        register(JFormattedTextField.class, "value");
        register(JRadioButton.class, "selected");
        register(JCheckBox.class, "selected");
        register(JToggleButton.class, "selected");
        register(JSpinner.class, "value");
        register(JSlider.class, "value");
    }
    
    /**
     * Registers a new default property for a widget or widget class.
     * @param widget           the widget or widget class
     * @param property         the property as String
     */
    public void register(Object widget, String property) {
        m_defaultProperties.put(widget, BeanProperty.create(property));
    }
    
    /**
     * Registers a new default property for a widget or widget class.
     * @param widget           the widget or widget class
     * @param property         the property as Property
     */
    @SuppressWarnings("unchecked")
    public void register(Object widget, Property property) {
        m_defaultProperties.put(widget, property);
    }
    
    /**
     * Returns the default property for a widget or widget class.
     * @param widget           the widget or widget class
     * @return                 the default property
     */
    @SuppressWarnings("unchecked")
    public Property getDefaultProperty(Object widget) {
        Class widgetClass;
        if (!(widget instanceof Class)) {
            if (m_defaultProperties.containsKey(widget)) {
                return m_defaultProperties.get(widget);
            } else {
                widgetClass = widget.getClass();
            }
        } else {
            widgetClass = (Class) widget;
        }
        
        while (m_defaultProperties.get(widgetClass) == null
            && widgetClass != Object.class) {
            
            widgetClass = widgetClass.getSuperclass();
        }
        return m_defaultProperties.get(widgetClass);
    }
}
