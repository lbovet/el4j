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

import javax.swing.JComponent;

import ch.elca.el4j.gui.swing.GUIApplication;

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
    protected static final String VALID_COLOR = "Valid Color";
    
    /**
     * Color to mark value as invalid.
     */
    protected final Color m_invalidColor;

    /**
     * The default contructor reading the invalidColor from Spring config.
     */
    public DefaultValidationResponder() {
        this((Color) GUIApplication.getInstance().getSpringContext()
            .getBean("invalidColor"));
    }
    /**
     * @param color    the background color if value is invalid
     */
    public DefaultValidationResponder(Color color) {
        m_invalidColor = color;
    }
    
    /** {@inheritDoc} */
    public void setValid(Object object, JComponent component, boolean valid) {
        if (valid) {
            setValid(object, component);
        } else {
            setInvalid(object, component, null);
        }
    }
    
    /** {@inheritDoc} */
    public void setValid(Object object, JComponent component) {
        if (component != null) {
            if (component.getBackground().equals(m_invalidColor)) {
                component.setBackground(
                    (Color) component.getClientProperty(VALID_COLOR));
            }
        }
    }
    
    /** {@inheritDoc} */
    public void setInvalid(Object object, JComponent component,
        String message) {
        
        if (component != null) {
            if (!component.getBackground().equals(m_invalidColor)) {
                component.putClientProperty(
                    VALID_COLOR, component.getBackground());
                component.setBackground(m_invalidColor);
            }
        }
    }
}
