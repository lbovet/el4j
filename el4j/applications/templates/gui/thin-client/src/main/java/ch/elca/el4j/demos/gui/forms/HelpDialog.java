/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.ResourceMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.services.gui.swing.GUIApplication;

/**
 * A very simple help dialog just containing a label.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Rueedlinger (ARR)
 */
@Lazy
@Component("helpDialog")
public class HelpDialog extends JPanel {

	/**
	 * The resource map.
	 */
	protected transient ResourceMap resourceMap;

	/**
	 * The text when no help is available.
	 */
	protected JLabel infoLabel;
	
	/**
	 * @param application   the GUI application
	 */
	@Autowired
	public HelpDialog(GUIApplication application) {
		resourceMap = application.getContext().getResourceMap(HelpDialog.class);
		// create the form layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		createComponents();
		

		// inject values from properties file
		resourceMap.injectComponents(this);
	}
	
	/**
	 * Create the form components.
	 */
	private void createComponents() {
		
		infoLabel = new JLabel(getNoHelpText());
		infoLabel.setName("infoLabel");
		add(infoLabel, BorderLayout.CENTER);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		
	}
		
	/**
	 * @return   the text when the help is not available
	 */
	protected String getNoHelpText() {
		return getRes("noHelpText");
	}

	/**
	 * @param id    the resource ID
	 * @return      the String associated with the given resource ID
	 */
	protected String getRes(String id) {
		return resourceMap.getString(id);
	}


}
