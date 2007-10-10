package com.silvermindsoftware.hitch.binding.components;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.silvermindsoftware.hitch.binding.SpecialBinding;

public class ListBinding implements SpecialBinding {
    private Property property;
    
    public ListBinding(String property) {
        this(BeanProperty.create(property));
    }
    
    public ListBinding(Property property) {
        this.property = property;
    }

    public AutoBinding createBinding(Object modelObject, String modelProperty,
            JComponent formComponent) {
        Property p = BeanProperty.create(modelProperty);
        List list = (List)p.getValue(modelObject);
        JListBinding lb = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, list, (JList)formComponent);
        
        // show property
        lb.setDetailBinding(property);
        
        return lb;
    }

}
