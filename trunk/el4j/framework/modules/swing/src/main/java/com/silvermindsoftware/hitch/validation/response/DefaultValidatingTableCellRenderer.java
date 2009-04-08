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

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingbinding.validation.ValidatedProperty;

import ch.elca.el4j.services.gui.swing.GUIApplication;

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
	/**
	 * Color to mark value as invalid.
	 */
	protected final Color m_invalidColor;
	
	/**
	 * Color to mark row as selected.
	 */
	protected final Color m_selectedColor;
	
	/**
	 * The default contructor reading the invalidColor from Spring config.
	 */
	public DefaultValidatingTableCellRenderer() {
		m_invalidColor = null;
		m_selectedColor = null;
	}
	
	/**
	 * @param invalid    the color to mark value as invalid
	 * @param selected   the color to mark row as selected
	 */
	public DefaultValidatingTableCellRenderer(Color invalid, Color selected) {
		m_invalidColor = invalid;
		m_selectedColor = selected;
	}
	
	/** {@inheritDoc} */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		// check if values is validateable
		if (value == null || !ValidatedProperty.class.isAssignableFrom(
			value.getClass())) {
			
			return super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		}
		Object v = ((ValidatedProperty) value).getValue();
		Class<?> vClass = (v != null) ? v.getClass() : String.class;
		
		Component renderer = table.getDefaultRenderer(vClass)
				.getTableCellRendererComponent(table, v, isSelected, hasFocus,
						row, column);

		renderer.setForeground(Color.BLACK);
		
		if (!((ValidatedProperty) value).isValid()) {
			renderer.setBackground(getInvalidColor());
		} else {
			if (isSelected) {
				renderer.setBackground(getSelectedColor());
			} else {
				renderer.setBackground(Color.WHITE);
			}
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
	
	/**
	 * @return    the color to mark row as selected.
	 */
	private Color getSelectedColor() {
		if (m_selectedColor != null) {
			return m_selectedColor;
		} else {
			return (Color) GUIApplication.getInstance().getConfig()
			.get("selectedColor");
		}
	}
}