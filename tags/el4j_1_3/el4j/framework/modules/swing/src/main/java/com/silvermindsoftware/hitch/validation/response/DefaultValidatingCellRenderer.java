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

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.jdesktop.swingbinding.validation.ValidatedProperty;

import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * A validating cell renderer for lists.
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
public class DefaultValidatingCellRenderer extends DefaultListCellRenderer {
    /**
     * Color to mark value as invalid.
     */
    protected final Color m_invalidColor;
    
    /**
     * The default contructor reading the invalidColor from Spring config.
     */
    public DefaultValidatingCellRenderer() {
        this((Color) GUIApplication.getInstance().getSpringContext()
            .getBean("invalidColor"));
    }
    
    /**
     * @param color    the color to mark value as invalid
     */
    public DefaultValidatingCellRenderer(Color color) {
        m_invalidColor = color;
    }
    
    /** {@inheritDoc} */
    @Override
    public Component getListCellRendererComponent(JList list, 
        Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        Component renderer = super.getListCellRendererComponent(list,
                ((ValidatedProperty) value).getValue(), index, isSelected,
                cellHasFocus);
        
        if (!((ValidatedProperty) value).isValid()) {
            renderer.setBackground(m_invalidColor);
        }
        
        return renderer;
    }
}
