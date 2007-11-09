package com.silvermindsoftware.hitch.validation.response;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.validation.ValidatedProperty;

import com.silvermindsoftware.hitch.binding.BindingFactory;
import com.silvermindsoftware.hitch.validation.ValidatingBindingListener;

/**
 * A validating cell editor for tables.
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
public class ValidatingCellEditor extends AbstractCellEditor implements
        TableCellEditor {

    /**
     * The BeansBinding between model and cell editor.
     */
    Binding<?, ?, ?, ?> m_binding;
    
    /**
     * The property currently being edited.
     */
    ValidatedProperty m_property;

    /** {@inheritDoc} */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        Component component = null;
        
        m_property = (ValidatedProperty) value;
        Object propValue = m_property.getValue();
        
        component = table.getDefaultEditor(propValue.getClass())
                .getTableCellEditorComponent(table, propValue, isSelected,
                        rowIndex, vColIndex);

        if (m_binding != null && m_binding.isBound()) {
            m_binding.unbind();
        }

        BindingFactory factory = BindingFactory.getInstance();
        m_binding = factory.getBinding(m_property.getParent(),
                m_property.getProperty(), (JComponent) component);
        m_binding.addBindingListener(new ValidatingBindingListener(
                new DefaultValidationResponder()));
        m_binding.bind();

        return component;
    }

    /** {@inheritDoc} */
    public Object getCellEditorValue() {
        m_binding.unbind();
        
        // get new value from model
        return BeanProperty.create(m_property.getProperty()).getValue(
                m_property.getParent());
    }
    
    /** {@inheritDoc} */
    @Override
    public void cancelCellEditing() {
        m_binding.unbind();
        
        super.cancelCellEditing();
    }
}
