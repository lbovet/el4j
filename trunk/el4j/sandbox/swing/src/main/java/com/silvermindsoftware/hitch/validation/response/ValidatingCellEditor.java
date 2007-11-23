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
package com.silvermindsoftware.hitch.validation.response;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.springframework.context.ApplicationContext;

import com.silvermindsoftware.hitch.binding.BindingFactory;
import com.silvermindsoftware.hitch.validation.ValidatingBindingListener;

import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.model.tablemodel.TableSorter;

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
    @SuppressWarnings("unchecked")
    Binding m_binding;
    
    /**
     * The table sorter if any.
     */
    TableSorter m_tableSorter;
    
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
        Class<?> propValueClass
            = (propValue != null) ? propValue.getClass() : String.class;
        
        component = table.getDefaultEditor(propValueClass)
            .getTableCellEditorComponent(table, propValue, isSelected,
                rowIndex, vColIndex);

        if (m_binding != null && m_binding.isBound()) {
            m_binding.unbind();
        }
        
        if (table.getModel() instanceof TableSorter) {
            m_tableSorter = (TableSorter) table.getModel();
            m_tableSorter.setSuppressChangeEvents(true);
        }

        BindingFactory factory = BindingFactory.getInstance();
        m_binding = factory.getBinding(m_property.getParent(),
            m_property.getProperty(), (JComponent) component);
        ApplicationContext ctx
            = GUIApplication.getInstance().getSpringContext();
        m_binding.addBindingListener(new ValidatingBindingListener(
            (ValidationResponder) ctx.getBean("responder")));
        m_binding.bind();
        
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    stopCellEditing();
                }
            }
        });

        return component;
    }

    /** {@inheritDoc} */
    public Object getCellEditorValue() {
        // get new value from model
        return BeanProperty.create(m_property.getProperty()).getValue(
            m_property.getParent());
    }
    
    /** {@inheritDoc} */
    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        // using removeCellEditorListener is a little bit a hack
        // but only this method is called after getCellEditorValue and
        // we cannot turn suppressChangeEvents off earlier
        if (m_binding != null && m_binding.isBound()) {
            m_binding.unbind();
            m_binding = null;
        }
        
        if (m_tableSorter != null) {
            m_tableSorter.setSuppressChangeEvents(false);
            m_tableSorter = null;
        }
        
        super.removeCellEditorListener(l);
    }
    
    
}
