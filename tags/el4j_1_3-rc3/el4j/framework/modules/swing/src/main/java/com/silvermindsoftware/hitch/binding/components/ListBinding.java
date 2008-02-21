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

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;

import com.silvermindsoftware.hitch.binding.AbstractBindingCreator;

import ch.elca.el4j.gui.swing.GUIApplication;

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
public class ListBinding extends AbstractBindingCreator<JList> {
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
    public AutoBinding createBinding(Object object, JList formComponent) {
        List list = (List) object;
        JListBinding lb = SwingBindings.createJListBinding(
            m_updateStrategy, list, formComponent);
        
        // show property
        lb.setDetailBinding(m_property);
        
        return lb;
    }
    
    /** {@inheritDoc} */
    public void addValidation(JList formComponent) {
        ApplicationContext ctx
            = GUIApplication.getInstance().getSpringContext();
        formComponent.setCellRenderer(
            (ListCellRenderer) ctx.getBean("cellRenderer"));
    }

}
