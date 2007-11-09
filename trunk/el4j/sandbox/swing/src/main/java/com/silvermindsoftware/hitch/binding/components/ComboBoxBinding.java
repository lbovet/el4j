package com.silvermindsoftware.hitch.binding.components;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.silvermindsoftware.hitch.binding.SpecialBindingCreator;
import com.silvermindsoftware.hitch.validation.response.DefaultValidatingCellRenderer;

/**
 * This class creates bindings for ComboBoxes.
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
public class ComboBoxBinding implements SpecialBindingCreator {
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding createBinding(Object modelObject, String modelProperty,
            JComponent formComponent) {
        Property p = BeanProperty.create(modelProperty);
        List list = (List) p.getValue(modelObject);
        JComboBoxBinding cb = SwingBindings.createJComboBoxBinding(
            UpdateStrategy.READ_WRITE, list, (JComboBox) formComponent);
        // TODO test combo box
        return cb;
    }
    
    /** {@inheritDoc} */
    public void addValidation(JComponent formComponent) {
        ((JComboBox) formComponent).setRenderer(
            new DefaultValidatingCellRenderer());
    }
}
