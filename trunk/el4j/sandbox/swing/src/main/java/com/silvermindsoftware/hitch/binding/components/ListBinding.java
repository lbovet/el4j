package com.silvermindsoftware.hitch.binding.components;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.silvermindsoftware.hitch.binding.SpecialBindingCreator;
import com.silvermindsoftware.hitch.validation.response.DefaultValidatingCellRenderer;

/**
 * This class creates bindings for lists.
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
public class ListBinding implements SpecialBindingCreator {
    /**
     * Which property to show in the list.
     */
    private Property<?, ?> m_property;
    
    /**
     * @param property    which property to show in the list
     */
    public ListBinding(String property) {
        this(BeanProperty.create(property));
    }
    
    /**
     * @param property    which property to show in the list
     */
    public ListBinding(Property<?, ?> property) {
        m_property = property;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding createBinding(Object modelObject, String modelProperty,
            JComponent formComponent) {
        Property p = BeanProperty.create(modelProperty);
        List list = (List) p.getValue(modelObject);
        JListBinding lb = SwingBindings.createJListBinding(
            UpdateStrategy.READ_WRITE, list, (JList) formComponent);
        
        // show property
        lb.setDetailBinding(m_property);
        
        return lb;
    }
    
    /** {@inheritDoc} */
    public void addValidation(JComponent formComponent) {
        ((JList) formComponent).setCellRenderer(
            new DefaultValidatingCellRenderer());
    }

}
