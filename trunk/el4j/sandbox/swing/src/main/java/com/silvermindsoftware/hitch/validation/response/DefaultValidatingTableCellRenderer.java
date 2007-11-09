package com.silvermindsoftware.hitch.validation.response;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingbinding.validation.ValidatedProperty;

/**
 * A validating cell renderer for tables.
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
public class DefaultValidatingTableCellRenderer
    extends DefaultTableCellRenderer {
    
    /** {@inheritDoc} */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Object v = ((ValidatedProperty) value).getValue();
        
        Component renderer = table.getDefaultRenderer(v.getClass())
                .getTableCellRendererComponent(table, v, isSelected, hasFocus,
                        row, column);

        renderer.setForeground(Color.BLACK);
     
        // Checkstyle: MagicNumber off
        if (!((ValidatedProperty) value).isValid()) {
            renderer.setBackground(new Color(255, 128, 128));
        } else {
            renderer.setBackground(new Color(255, 255, 255));
        }
        // Checkstyle: MagicNumber on
        
        return renderer;
    }
}