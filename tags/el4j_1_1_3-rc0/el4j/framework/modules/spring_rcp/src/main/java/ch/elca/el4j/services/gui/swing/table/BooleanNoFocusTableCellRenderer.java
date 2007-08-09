/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Table cell renderer for boolean value. Cell will be displayed as check box.
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
public class BooleanNoFocusTableCellRenderer extends JCheckBox 
    implements TableCellRenderer {
    /**
     * Default constructor.
     */
    public BooleanNoFocusTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * {@inheritDoc}
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

        // Check box if value equals true
        setSelected((value != null && ((Boolean) value).booleanValue()));
        return this;
    }
}