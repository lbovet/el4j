package com.silvermindsoftware.hitch.binding.components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;

import com.silvermindsoftware.hitch.binding.SpecialBinding;

public class TableBinding implements SpecialBinding {
    protected String[] propertyNames;
    protected String[] columnLabels;
    protected Class[] columnClasses;
    
    public TableBinding(String[] propertyNames, String[] columnLabels) {
        this.propertyNames = propertyNames;
        this.columnLabels = columnLabels;
        this.columnClasses = null;
    }
    
    public TableBinding(String[] propertyNames, String[] columnLabels, Class[] columnClasses) {
        this(propertyNames, columnLabels);
        this.columnClasses = columnClasses;
    }

    public AutoBinding createBinding(Object modelObject, String modelProperty, JComponent formComponent) {
        Property modelProp = BeanProperty.create(modelProperty);
        List list = (List)modelProp.getValue(modelObject);
        JTableBinding tb = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, list, (JTable)formComponent);
        for (int i = 0; i < propertyNames.length; i++) {
            Property prop = BeanProperty.create(propertyNames[i]);
            ColumnBinding cb = tb.addColumnBinding(prop);
            cb.setColumnName(columnLabels[i]);
            if (columnClasses != null) {
                cb.setColumnClass(columnClasses[i]);
            }
        }

        return tb;
    }

}
