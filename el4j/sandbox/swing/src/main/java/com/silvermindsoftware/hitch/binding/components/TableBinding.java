package com.silvermindsoftware.hitch.binding.components;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingbinding.validation.ValidatedProperty;

import com.silvermindsoftware.hitch.binding.SpecialBindingCreator;
import com.silvermindsoftware.hitch.validation.response.DefaultValidatingTableCellRenderer;
import com.silvermindsoftware.hitch.validation.response.ValidatingCellEditor;

/**
 * This class creates bindings for tables.
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
public class TableBinding implements SpecialBindingCreator {
    /**
     * Which property to show in the table.
     */
    protected String[] m_propertyNames;
    
    /**
     * The column labels.
     */
    protected String[] m_columnLabels;
    
    /**
     * The value classes for each column.
     */
    protected Class<?>[] m_columnClasses;
    
    /**
     * @param propertyNames    which property to show in the table
     * @param columnLabels     the column labels
     */
    public TableBinding(String[] propertyNames, String[] columnLabels) {
        m_propertyNames = propertyNames;
        m_columnLabels = columnLabels;
        m_columnClasses = null;
    }
    
    /**
     * @param propertyNames    which property to show in the table
     * @param columnLabels     the column labels
     * @param columnClasses    the value classes for each column
     */
    public TableBinding(String[] propertyNames, String[] columnLabels,
        Class<?>[] columnClasses) {
        
        this(propertyNames, columnLabels);
        m_columnClasses = columnClasses;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding createBinding(Object modelObject, String modelProperty,
        JComponent formComponent) {
        
        Property modelProp = BeanProperty.create(modelProperty);
        List list = (List) modelProp.getValue(modelObject);
        
        JTableBinding tb = SwingBindings.createJTableBinding(
            UpdateStrategy.READ_WRITE, list, (JTable) formComponent);
        
        for (int i = 0; i < m_propertyNames.length; i++) {
            Property prop = BeanProperty.create(m_propertyNames[i]);
            ColumnBinding cb = tb.addColumnBinding(prop);
            
            cb.setColumnName(m_columnLabels[i]);
            if (m_columnClasses != null) {
                cb.setColumnClass(m_columnClasses[i]);
            }
        }
        
        return tb;
    }
    
    /** {@inheritDoc} */
    public void addValidation(JComponent formComponent) {
        ((JTable) formComponent).setDefaultRenderer(ValidatedProperty.class,
                new DefaultValidatingTableCellRenderer());
        ((JTable) formComponent).setDefaultEditor(ValidatedProperty.class,
                new ValidatingCellEditor());
    }

}
