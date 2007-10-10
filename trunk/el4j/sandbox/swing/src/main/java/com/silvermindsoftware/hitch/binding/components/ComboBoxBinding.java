package com.silvermindsoftware.hitch.binding.components;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.silvermindsoftware.hitch.binding.SpecialBinding;

public class ComboBoxBinding implements SpecialBinding {
    public AutoBinding createBinding(Object modelObject, String modelProperty,
            JComponent formComponent) {
        Property p = BeanProperty.create(modelProperty);
        List list = (List)p.getValue(modelObject);
        JComboBoxBinding cb = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, list, (JComboBox)formComponent);
        // TODO test combo box
        return cb;
    }
}
