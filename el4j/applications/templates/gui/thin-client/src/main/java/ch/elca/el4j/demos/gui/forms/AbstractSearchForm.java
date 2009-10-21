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
package ch.elca.el4j.demos.gui.forms;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;


/**
 * This class represents a standard search dialog.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractSearchForm extends JPanel {
	/**
	 * The label stating something like 'Search for:'.
	 */
	protected JLabel searchFor;
	
	/**
	 * The text field containing the search string.
	 */
	protected JTextField searchField;
	
	/**
	 * The button to start the search.
	 */
	protected JButton searchButton;
	
	/**
	 * Optional components to set search options.
	 */
	protected JComponent[] options = null;
	
	/**
	 * The constructor.
	 */
	protected AbstractSearchForm() {
		createBasicComponents();
		createOptionalComponents();
		createLayout();
	}
	
	/**
	 * @return Returns the option components.
	 */
	public JComponent[] getOptions() {
		return options;
	}

	/**
	 * @param options Sets the option components.
	 */
	public void setOptions(JComponent[] options) {
		this.options = options;
	}
	
	
	/**
	 * Create the basic form components.
	 */
	private void createBasicComponents() {
		searchFor = new JLabel();
		searchFor.setName("searchFor");
		
		searchField = new JTextField();
		searchField.setName("searchField");
		
		searchButton = new JButton();
		searchButton.setName("searchButton");
	}
	
	/**
	 * Create the optional form components. Hook for subclasses.
	 */
	protected void createOptionalComponents() { }
	
	/**
	 * Layout the form components.
	 */
	private void createLayout() {
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(this);
		setLayout(layout);

		layout.row().left().add(searchFor);
		layout.row().grid().add(searchField, 2);
		layout.row().right().add(searchButton);
		if (options != null) {
			for (JComponent component : options) {
				layout.row().left().add(component);
			}
		}
	}
}
