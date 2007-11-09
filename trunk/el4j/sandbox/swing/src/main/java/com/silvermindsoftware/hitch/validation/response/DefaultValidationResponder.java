package com.silvermindsoftware.hitch.validation.response;

import java.awt.Color;

import javax.swing.JComponent;

/**
 * A default ValidationResponder that makes the background of the corresponding
 * GUI element red.
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
public class DefaultValidationResponder implements ValidationResponder {
    /**
     * Identifier for valid color in component's client property.
     */
    private static final String VALID_COLOR = "Valid Color";
    
    /**
     * Color to mark value as invalid.
     */
    private Color m_invalidColor;

    /**
     * Default constructor. Makes background reddish if value is invalid.
     */
    public DefaultValidationResponder() {
        // Checkstyle: MagicNumber off
        this(new Color(255, 128, 128));
        // Checkstyle: MagicNumber on
    }
    
    /**
     * @param color    the background color if value is invalid
     */
    public DefaultValidationResponder(Color color) {
        m_invalidColor = color;
    }
    
    /** {@inheritDoc} */
    public void setValid(JComponent component) {
        if (component != null) {
            if (component.getBackground().equals(m_invalidColor)) {
                component.setBackground(
                    (Color) component.getClientProperty(VALID_COLOR));
            }
        }
    }
    
    /** {@inheritDoc} */
    public void setValid(JComponent component, boolean valid) {
        if (valid) {
            setValid(component);
        } else {
            setInvalid(component, "");
        }
    }
    
    /** {@inheritDoc} */
    public void setInvalid(JComponent component, String message) {
        if (component != null) {
            if (!component.getBackground().equals(m_invalidColor)) {
                component.putClientProperty(
                    VALID_COLOR, component.getBackground());
                component.setBackground(m_invalidColor);
            }
        }
    }
}
