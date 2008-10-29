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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import ch.elca.el4j.gui.swing.AbstractMDIApplication;

import cookxml.cookswing.CookSwing;

/**
 * Sample MDI application that demonstrates how to use the framework.
 *
 * See also associated MainFormMDI.properties file that contains resources
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
public class MainFormMDIXML extends AbstractMDIApplication {
	/**
	 * The desktop pane of this MDIApplication.
	 */
	protected JDesktopPane m_desktopPane;
	
	/**
	 * A example popup menu.
	 */
	private JPopupMenu m_popup;
	
	/**
	 * The toolbar.
	 */
	private JToolBar m_toolbar;
	
	/**
	 * Determines if user is admin (for activation demo).
	 */
	private boolean m_admin = false;
	
	/** {@inheritDoc} */
	@Override
	protected JDesktopPane getDesktopPane() {
		return m_desktopPane;
	}
	
	/**
	 * @return    a horizontal glue.
	 */
	protected Component createMenuGlue() {
		return Box.createHorizontalGlue();
	}
	
	/** {@inheritDoc} */
	@Override
	protected void initialize(String[] args) {
		
		/*GenericConfig overrideConfig = (GenericConfig) GUIApplication
			.getInstance().getSpringContext().getBean("overrideConfig");
		overrideConfig.setParent(GUIApplication.getInstance().getConfig());
		
		// use this to override config
		GUIApplication.getInstance().setConfig(overrideConfig);*/
	}
	
	/**
	 * Main definition of the GUI.
	 *  This method is called back by the GUI framework
	 */
	@Override
	protected void startup() {
		MainFormActions actions = new MainFormActions(this);
		super.addActionMappingInstance(actions);
		
		CookSwing cookSwing = new CookSwing(this);
		setMainFrame((JFrame) cookSwing.render("gui/main.xml"));
		
		m_desktopPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					m_popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		// register extensions
		Map<String, GUIExtension> extensions = (Map<String, GUIExtension>)
			getSpringContext().getBeansOfType(GUIExtension.class);
		
		for (GUIExtension extension : extensions.values()) {
			super.addActionMappingInstance(extension);
			
			extension.setApplication(this);
			extension.extendMenuBar(getMainFrame().getJMenuBar());
			extension.extendToolBar(m_toolbar);
			
			// inject properties because non-Actions don't do it automatically
			ResourceMap map = getContext().getResourceMap(extension.getClass());
			map.injectComponents(getMainFrame().getJMenuBar());
			map.injectComponent(m_toolbar);
		}
		
		showMain();
	}
	
	@Action
	public void showDemo6() {
		show("XMLDemoForm");
	}

	/**
	 * A "special" help only for admins (for demo purpose only).
	 */
	@Action(enabledProperty = "admin")
	public void help() {
		try {
			show("SecurityDemoForm");
		} catch (NoSuchBeanDefinitionException e) {
			JOptionPane.showMessageDialog(null,
				"This demo doesn't support security. "
				+ "Use swing-demo-secure-... instead.");
		}
		/*
		JHelp helpViewer = null;
		try {
			// Get the classloader of this class.
			ClassLoader cl = MainForm.class.getClassLoader();
			// Use the findHelpSet method of HelpSet to create a URL referencing
			// the helpset file.
			// Note that in this example the location of the helpset is implied
			// as being in the same
			// directory as the program by specifying "jhelpset.hs" without any
			// directory prefix,
			// this should be adjusted to suit the implementation.
			URL url = HelpSet.findHelpSet(cl, "jhelpset.hs");
			// Create a new JHelp object with a new HelpSet.
			helpViewer = new JHelp(new HelpSet(cl, url));
			// Set the initial entry point in the table of contents.
			helpViewer.setCurrentID("Simple.Introduction");
		} catch (Exception e) {
			System.err.println("API Help Set not found");
		}
		*/
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
}
