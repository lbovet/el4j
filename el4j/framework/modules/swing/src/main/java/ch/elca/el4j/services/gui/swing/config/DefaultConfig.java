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
package ch.elca.el4j.services.gui.swing.config;

import java.awt.Color;

import com.silvermindsoftware.hitch.validation.response.DefaultValidatingCellRenderer;
import com.silvermindsoftware.hitch.validation.response.DefaultValidatingComboBoxRenderer;
import com.silvermindsoftware.hitch.validation.response.DefaultValidatingTableCellRenderer;
import com.silvermindsoftware.hitch.validation.response.DefaultValidationResponder;
import com.silvermindsoftware.hitch.validation.response.ValidatingCellEditor;

import ch.elca.el4j.util.config.GenericConfig;

/**
 * This configuration class sets the default config.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class DefaultConfig extends GenericConfig {
	/**
	 * The default constructor setting the default config.
	 */
	public DefaultConfig() {
		// Checkstyle: MagicNumber off
		add("invalidColor", new Color(255, 128, 128));
		add("selectedColor", new Color(184, 207, 229));
		// Checkstyle: MagicNumber on
		
		add("validationResponder", new DefaultValidationResponder());
		add("cellRenderer", new DefaultValidatingCellRenderer());
		add("comboBoxRenderer", new DefaultValidatingComboBoxRenderer());
		add("tableCellRenderer", new DefaultValidatingTableCellRenderer());
		add("tableCellEditor", new ValidatingCellEditor());
	}
}
