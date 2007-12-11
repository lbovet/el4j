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
package ch.elca.el4j.services.gui.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table cell renderer used for tables where only complete rows can be selected.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class DefaultNoFocusTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * {@inheritDoc}
     * 
     * Highlight selected row. Focused cell will not be specially highlighted.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column) {
        
        // Change colors of selected cell
        if (isSelected) {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            super.setForeground(table.getForeground());
            super.setBackground(table.getBackground());
        }

        // Set table font for cell
        setFont(table.getFont());

        // Never highlight focused cell
        setBorder(noFocusBorder);
        
        // Set value for cell
        setValue(convertValueObject(value));

        return this;
    }
    
    /**
     * Converts the given value object into one that can be set as value of a 
     * text field.
     * 
     * @param value Is the value object to convert.
     * @return Returns the converted value object.
     */
    protected Object convertValueObject(Object value) {
        return value;
    }
}
