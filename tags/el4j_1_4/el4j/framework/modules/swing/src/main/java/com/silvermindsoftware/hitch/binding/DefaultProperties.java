/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
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
