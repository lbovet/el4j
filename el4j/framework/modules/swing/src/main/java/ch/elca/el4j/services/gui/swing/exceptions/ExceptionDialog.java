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
package ch.elca.el4j.services.gui.swing.exceptions;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.services.gui.swing.GUIApplication;

import cookxml.cookswing.CookSwing;

/**
 * The default dialog that appears when an exception occurred.
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
@SuppressWarnings("serial")
public class ExceptionDialog extends JDialog {
	/**
	 * The generic error message.
	 */
	protected JLabel m_message;
	
	/**
	 * The button to show/hide the details.
	 */
	protected JButton m_detailsButton;
	
	/**
	 * The scroll pane that makes the details text area scrollable.
	 */
	protected JScrollPane m_detailsScrollPane;
	
	/**
	 * The details of the exception (in general this is the stacktrace).
	 */
	protected JTextArea m_details;
	
	/**
	 * The error image.
	 */
	protected JLabel m_errorImage;
	
	/**
	 * The resource map.
	 */
	protected ResourceMap m_resourceMap;
	
	/**
	 * @param details    the details to show in the dialog
	 */
	public ExceptionDialog(String details) {
		super(GUIApplication.getInstance().getMainFrame());
		
		m_resourceMap = GUIApplication.getInstance().getContext().getResourceMap(ExceptionDialog.class);
		
		CookSwing cookSwing = new CookSwing(this);
		URL url = m_resourceMap.getClassLoader().getResource(m_resourceMap.getResourcesDir() + "exceptionDialog.xml");
		setLayout(new BorderLayout());
		add((JPanel) cookSwing.render(url.getFile()));
		
		setModal(true);
		setTitle(getRes("exceptionDialogTitle"));
		m_message.setText(getRes("exceptionDialogMessage"));
		m_details.setText(details);
		
		// adjust font size of details
		m_details.setFont(m_details.getFont().deriveFont(12.0f));
		
		m_errorImage.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		
		pack();
		
		// hide details
		toggleDetails();
		
		// center dialog on the screen
		setLocationRelativeTo(null);
	}
	
	/**
	 * Close the dialog.
	 */
	@Action
	public void close() {
		dispose();
	}
	
	/**
	 * Show/hide the details.
	 */
	@Action
	public void toggleDetails() {
		Rectangle bounds = getBounds();
		if (m_detailsScrollPane.isVisible()) {
			m_detailsButton.setText(getRes("moreDetails"));
			bounds.height -= m_detailsScrollPane.getBounds().height;
			setBounds(bounds);
			m_detailsScrollPane.setVisible(false);
		} else {
			m_detailsButton.setText(getRes("lessDetails"));
			bounds.height += m_detailsScrollPane.getBounds().height;
			setBounds(bounds);
			m_detailsScrollPane.setVisible(true);
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	/**
	 * @param id    the resource ID
	 * @return      the String associated with the given resource ID
	 */
	protected String getRes(String id) {
		return m_resourceMap.getString(id);
	}

}
