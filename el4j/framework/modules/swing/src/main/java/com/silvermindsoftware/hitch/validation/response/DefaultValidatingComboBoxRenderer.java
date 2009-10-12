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

import org.jdesktop.swingbinding.validation.ValidationCapability;

import com.silvermindsoftware.hitch.binding.PropertyUtil;

import ch.elca.el4j.services.gui.swing.GUIApplication;

/**
 * A validating cell renderer for comboboxes.
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
public class DefaultValidatingComboBoxRenderer
	extends DefaultListCellRenderer implements ComboBoxRenderer {
	
	/**
	 * Color to mark value as invalid.
	 */
	protected final Color m_invalidColor;
	
	/**
	 * The property to render.
	 */
	protected String m_property;
	
	/**
	 * Should invalid property values marked with different color?
	 */
	protected boolean m_validate;
	
	
	/**
	 * The default contructor reading the invalidColor from Spring config.
	 */
	public DefaultValidatingComboBoxRenderer() {
		this(null, null, false);
	}
	
	/**
	 * @param property    the property to render
	 * @param validate    validate property value?
	 */
	public DefaultValidatingComboBoxRenderer(String property,
		boolean validate) {
		this(null, property, validate);
	}
	
	/**
	 * @param color       the color to mark value as invalid
	 * @param property    the property to render
	 * @param validate    validate property value?
	 */
	public DefaultValidatingComboBoxRenderer(Color color, String property,
		boolean validate) {
		m_invalidColor = color;
		m_property = property;
		m_validate = validate;
	}
	
	/** {@inheritDoc} */
	public void setProperty(String property) {
		m_property = property;
	}
	
	/**
	 * @param validate   validate property value?
	 */
	public void setValidate(boolean validate) {
		m_validate = validate;
	}
	
	/** {@inheritDoc} */
	@Override
	public Component getListCellRendererComponent(JList list,
		Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		Component renderer;
		if (m_property != null) {
			Object propValue = PropertyUtil.create(m_property).getValue(value);
			renderer = super.getListCellRendererComponent(list,
					propValue, index, isSelected, cellHasFocus);
			
			if (m_validate) {
				if (value instanceof ValidationCapability) {
					ValidationCapability v = (ValidationCapability) value;
					if (!v.isValid(m_property)) {
						renderer.setBackground(getInvalidColor());
					}
				}
			}
		} else {
			renderer = super.getListCellRendererComponent(list,
				value, index, isSelected,
				cellHasFocus);
		}
		
		return renderer;
	}
	
	/**
	 * @return    the color to mark a value as invalid.
	 */
	private Color getInvalidColor() {
		if (m_invalidColor != null) {
			return m_invalidColor;
		} else {
			return (Color) GUIApplication.getInstance().getConfig()
			.get("invalidColor");
		}
	}
}
