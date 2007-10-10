package com.silvermindsoftware.hitch.binding;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;

public class DefaultProperties {
    private Map<Object, Property> defaultProperties;
    
    public DefaultProperties() {
        defaultProperties = new HashMap<Object, Property>();
        
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
     * Registers a new default property for a widget or widget class
     * @param widget           the widget or widget class
     * @param property         the property as String
     */
    public void register(Object widget, String property) {
        defaultProperties.put(widget, BeanProperty.create(property));
    }
    
    /**
     * Registers a new default property for a widget or widget class
     * @param widget           the widget or widget class
     * @param property         the property as Property
     */
    public void register(Object widget, Property property) {
        defaultProperties.put(widget, property);
    }
    
    /**
     * Returns the default property for a widget or widget class
     * @param widget           the widget or widget class
     * @return                 the default property
     */
    public Property getDefaultProperty(Object widget) {
        if (!(widget instanceof Class)) {
            if (defaultProperties.containsKey(widget)) {
                return defaultProperties.get(widget);
            } else {
                widget = widget.getClass();
            }
        }
        return defaultProperties.get(widget);
    }
}
