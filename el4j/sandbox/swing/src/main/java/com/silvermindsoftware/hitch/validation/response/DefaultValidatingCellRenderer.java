package com.silvermindsoftware.hitch.validation.response;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.jdesktop.swingbinding.validation.ValidatedProperty;

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
    /** {@inheritDoc} */
    @Override
    public Component getListCellRendererComponent(JList list, 
        Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        Component renderer = super.getListCellRendererComponent(list,
                ((ValidatedProperty) value).getValue(), index, isSelected,
                cellHasFocus);
        
        if (!((ValidatedProperty) value).isValid()) {
            // Checkstyle: MagicNumber off
            renderer.setBackground(new Color(255, 128, 128));
            // Checkstyle: MagicNumber on
            
        } /* else {
            // not necessary
            //renderer.setBackground(new Color(255, 255, 255));
        }*/
        return renderer;
    }
}
