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
package ch.elca.el4j.demos.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import ch.elca.el4j.gui.swing.DockingApplication;

// Checkstyle: MagicNumber off
/**
 * Sample Docking application that demonstrates how to use the framework.
 *
 * See also associated MainFormDocking.properties file that contains resources
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
public class MainFormDocking extends DockingApplication {
	/**
	 * Determines if user is admin (for activation demo).
	 */
	protected boolean m_admin = false;
	
	/**
	 * Main definition of the GUI.
	 *  This method is called back by the GUI framework
	 */
	@Override
	protected void startup() {
		MainFormActions actions = new MainFormActions(this);
		super.addActionMappingInstance(actions);
		
		getMainFrame().setJMenuBar(createMenuBar());
		showMain(createMainPanel());
	}
	
	/**
	 * @return    the created main panel
	 */
	protected JComponent createMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar toolbar = createToolBar();
		panel.add(toolbar, BorderLayout.NORTH);
		
		// implicitly register graphical exception handler
		getSpringContext().getBean("exceptionsForm");
		
		// register extensions
		Map<String, GUIExtension> extensions = (Map<String, GUIExtension>)
			getSpringContext().getBeansOfType(GUIExtension.class);
		
		for (GUIExtension extension : extensions.values()) {
			super.addActionMappingInstance(extension);
			
			extension.setApplication(this);
			extension.extendMenuBar(getMainFrame().getJMenuBar());
			extension.extendToolBar(toolbar);
			
			// inject properties because non-Actions don't do it automatically
			ResourceMap map = getContext().getResourceMap(extension.getClass());
			map.injectComponents(getMainFrame().getJMenuBar());
			map.injectComponent(toolbar);
		}
		
		// set default size (if size has not yet been saved by AppFW)
		getMainFrame().getContentPane().setPreferredSize(
			new Dimension(640, 480));
		
		panel.add((Component) getToolWindowManager(), BorderLayout.CENTER);
		
		return panel;
	}
	
	/**
	 * Show search dialog in a toolbox.
	 */
	@Action
	public void showSearch() {
		show("searchForm", "Search", ToolWindowAnchor.LEFT);
	}

	/**
	 * A "special" help only for admins (for demo purpose only).
	 */
	@Action//(enabledProperty = "admin")
	public void help() {
		try {
			show("securityDemoForm");
		} catch (NoSuchBeanDefinitionException e) {
			JOptionPane.showMessageDialog(null,
				"This demo doesn't support security. "
				+ "Use swing-demo-secure-... instead.");
		}
		show("helpDialog");
	}
	
	/**
	 * Toggle admin flag (for visibility of menu entry).
	 */
	@Action
	public void toggleAdmin() {
		// enable help menuItem
		boolean oldAdmin = m_admin;
		m_admin = !m_admin;
		firePropertyChange("admin", oldAdmin, m_admin);
	}
	
	/**
	 * Indicates whether permission "admin" is set
	 *  (used via enabledProperty field of \@Action).
	 *  @return    <code>true</code> if user has admin rights
	 */
	public boolean isAdmin() {
		return hasRole("ROLE_SUPERVISOR");
	}
	
	/**
	 * @param requestedRole    the role to check
	 * @return                 <code>true</code> if user has specified role.
	 */
	public boolean hasRole(String requestedRole) {
		/*  commented out for now (until acegi security is set up):
		 *
		GrantedAuthority[] authorities = SecurityContextHolder.getContext()
			.getAuthentication().getAuthorities();

		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals(requestedRole)) {
				return true;
			}
		}*/
		return m_admin;
	}

	/**
	 * @return    the created menu bar
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		String[] fileMenuActionNames = {"quit"};
		String[] editMenuActionNames = {"cut", "copy", "paste", "delete"};
		String[] demoMenuActionNames
			= {"showDemo1", "showDemo2", "showDemo3", "showDemo4", "---",
				"showSearch", "showRefDB", "---",
				"showDemo5", "sendExampleEvent", "throwException"};
		String[] helpMenuActionNames = {"help", "toggleAdmin", "about"};
		menuBar.add(createMenu("fileMenu", fileMenuActionNames));
		menuBar.add(createMenu("editMenu", editMenuActionNames));
		menuBar.add(createMenu("demoMenu", demoMenuActionNames));
		menuBar.add(createMenu("helpMenu", helpMenuActionNames));
		return menuBar;
	}
	
	/**
	 * @return    the created tool bar
	 */
	protected JToolBar createToolBar() {
		String[] toolbarActionNames = {"quit", "paste"};
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		Border border = new EmptyBorder(2, 9, 2, 9);
		for (String actionName : toolbarActionNames) {
			JButton button = new JButton();
			button.setBorder(border);
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setAction(getAction(actionName));
			button.setFocusable(false);
			button.setText("");
			toolBar.add(button);
		}
		return toolBar;
	}
}
