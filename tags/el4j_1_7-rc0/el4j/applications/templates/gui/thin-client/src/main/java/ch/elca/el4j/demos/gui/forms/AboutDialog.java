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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.services.gui.swing.GUIApplication;

/**
 * A standard About dialog.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Component("aboutDialog")
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {
	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(AboutDialog.class);
	
	/**
	 * The resource map.
	 */
	protected transient ResourceMap resourceMap;
	
	/**
	 * The applicationContext.
	 */
	protected ApplicationContext applicationContext;
	
	
	/**
	 * The main panel.
	 */
	protected JPanel panel;
	
	/**
	 * The about text.
	 */
	protected JLabel infoLabel;
	/**
	 * The button for closing the about dialog.
	 */
	protected JButton closeButton;

	/**
	 * @param application   the GUI application
	 */
	@Autowired
	public AboutDialog(GUIApplication application) {
		applicationContext = application.getSpringContext();
		resourceMap = application.getContext().getResourceMap(AboutDialog.class);

		setTitle(getRes("aboutTitle"));
		
		createComponents();

		// assign actions
		ApplicationActionMap actionMap = application.getContext().getActionMap(this);
		closeButton.setAction(actionMap.get("close"));

		add(panel);

		// inject values from properties file
		resourceMap.injectComponents(this);

		// little hack to make button larger
		String space = getRes("close.space");
		if (space != null) {
			Insets s = closeButton.getMargin();
			s.right = Integer.parseInt(space);
			s.left = s.right;
			closeButton.setMargin(s);
		}

		// prepare to show
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
	}
	
	/**
	 * Create the form components.
	 */
	private void createComponents() {
		panel = new JPanel(new BorderLayout());
		
		// about-text on the right
		infoLabel = new JLabel(getAboutText());
		infoLabel.setName("infoLabel");
		// Checkstyle: MagicNumber off
		infoLabel.setBorder(new EmptyBorder(3, 6, 3, 3));
		// Checkstyle: MagicNumber on
		
		// image on the left
		String aboutImage = getRes("aboutImage");
		if (aboutImage != null) {
			ImageIcon icon = createImageIcon(aboutImage);
			infoLabel.setIcon(icon);
		}
		
		panel.add(infoLabel, BorderLayout.CENTER);
		
		// button to close the dialog
		closeButton = new JButton();
		closeButton.setSelected(true);
		getRootPane().setDefaultButton(closeButton);
		
		// make button right aligned
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.TRAILING);
		JPanel closePanel = new JPanel(layout);
		closePanel.add(closeButton);

		// compose button and separator
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
		bottom.add(new JSeparator(SwingConstants.HORIZONTAL));
		bottom.add(closePanel);

		panel.add(bottom, BorderLayout.SOUTH);
		
	}

	/**
	 * Close the dialog.
	 */
	@Action
	public void close() {
		dispose();
	}
	
	/**
	 * @return   the about text
	 */
	protected String getAboutText() {
		String revision = getRes("revision");
		revision = revision.replaceAll("[^0-9]", "");
		return getRes("aboutText").replace("#Revision#", revision);
	}

	/**
	 * @param id    the resource ID
	 * @return      the String associated with the given resource ID
	 */
	protected String getRes(String id) {
		return resourceMap.getString(id);
	}

	/**
	 * @param path           the path to the image
	 * @return               an ImageIcon, or null if the path was invalid.
	 */
	protected ImageIcon createImageIcon(String path) {
		try {
			java.net.URL imgURL = applicationContext.getResource(path).getURL();
			return new ImageIcon(imgURL, "");
		} catch (IOException e) {
			s_logger.error("Couldn't find file: " + path);
			return null;
		}
	}
}
