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
package com.silvermindsoftware.hitch.binding.components;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.springframework.context.ApplicationContext;

import com.silvermindsoftware.hitch.binding.AbstractSpecialBindingCreator;

import ch.elca.el4j.gui.swing.GUIApplication;

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
public class TableBinding extends AbstractSpecialBindingCreator<JTable> {
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
     * Is column editable?
     */
    protected boolean[] m_columnEditable;
    
    /**
     * @param propertyNames    which property to show in the table
     * @param columnLabels     the column labels
     */
    public TableBinding(String[] propertyNames, String[] columnLabels) {
        this(propertyNames, columnLabels, null, null);
    }
    
    /**
     * @param propertyNames    which property to show in the table
     * @param columnLabels     the column labels
     * @param columnClasses    the value classes for each column
     */
    public TableBinding(String[] propertyNames, String[] columnLabels,
        Class<?>[] columnClasses) {
        
        this(propertyNames, columnLabels, columnClasses, null);
    }
    
    /**
     * @param propertyNames    which property to show in the table
     * @param columnLabels     the column labels
     * @param columnClasses    the value classes for each column
     * @param columnEditable   which properties are editable
     */
    public TableBinding(String[] propertyNames, String[] columnLabels,
        Class<?>[] columnClasses, boolean[] columnEditable) {
        
        m_propertyNames = propertyNames;
        m_columnLabels = columnLabels;
        m_columnClasses = columnClasses;
        m_columnEditable = columnEditable;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public AutoBinding createBinding(Object object, JTable formComponent) {
        List list = (List) object;
        
        JTableBinding tb = SwingBindings.createJTableBinding(
            m_updateStrategy, list, formComponent);
        
        for (int i = 0; i < m_propertyNames.length; i++) {
            Property prop = BeanProperty.create(m_propertyNames[i]);
            ColumnBinding cb = tb.addColumnBinding(prop);
            
            cb.setColumnName(m_columnLabels[i]);
            if (m_updateStrategy == UpdateStrategy.READ_WRITE) {
                if (m_columnEditable != null) {
                    cb.setEditable(m_columnEditable[i]);
                } else {
                    cb.setEditable(true);
                }
            } else {
                cb.setEditable(false);
            }
            if (m_columnClasses != null) {
                cb.setColumnClass(m_columnClasses[i]);
            }
        }
        
        return tb;
    }
    
    /** {@inheritDoc} */
    public void addValidation(JTable formComponent) {
        ApplicationContext ctx
            = GUIApplication.getInstance().getSpringContext();
        
        formComponent.setDefaultRenderer(ValidatedProperty.class,
            (TableCellRenderer) ctx.getBean("tableCellRenderer"));
        formComponent.setDefaultEditor(ValidatedProperty.class,
            (TableCellEditor) ctx.getBean("tableCellEditor"));
    }

}
