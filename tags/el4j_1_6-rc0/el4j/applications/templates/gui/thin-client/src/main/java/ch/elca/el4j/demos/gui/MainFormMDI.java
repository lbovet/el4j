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
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.demos.gui.extension.GUIExtension;
import ch.elca.el4j.services.gui.swing.ActionsContext;
import ch.elca.el4j.services.gui.swing.MDIApplication;
import ch.elca.el4j.services.gui.swing.util.MenuUtils;

// Checkstyle: MagicNumber off
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
public class MainFormMDI extends MDIApplication {
	/**
	 * A example popup menu.
	 */
	protected JPopupMenu m_popup;

	/**
	 * Main definition of the GUI.
	 *  This method is called back by the GUI framework
	 */
	@Override
	protected void startup() {
		m_actionsContext = ActionsContext.create(this, new MainFormActions(this));
		
		getMainFrame().setJMenuBar(createMenuBar());
		showMain(createMainPanel());
	}
	
	/**
	 * @return    the created main panel
	 */
	@SuppressWarnings("unchecked")
	protected JComponent createMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar toolbar = createToolBar();
		panel.add(toolbar, BorderLayout.NORTH);
		
		createDefaultDesktopPane();
		
		m_popup = MenuUtils.createPopup(m_actionsContext, Arrays.asList("showDemo1", "showDemo2", "---", "quit"));
		m_desktopPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					m_popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		// implicitly register graphical exception handler
		getSpringContext().getBean("exceptionsForm");
		
		// register extensions
		Map<String, GUIExtension> extensions = (Map<String, GUIExtension>)
			getSpringContext().getBeansOfType(GUIExtension.class);
		
		for (GUIExtension extension : extensions.values()) {
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
		
		panel.add(m_desktopPane, BorderLayout.CENTER);
		return panel;
	}
	
	/**
	 * @return    the created menu bar
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		List<String> fileMenuActionNames = Arrays.asList("quit");
		List<String> editMenuActionNames = Arrays.asList("cut", "copy", "paste", "delete");
		List<String> demoMenuActionNames
			= Arrays.asList("showDemo1", "showDemo2", "showDemo3", "showDemo4", "---",
				"showSearch", "showRefDB", "---",
				"showDemo5", "sendExampleEvent", "throwException");
		List<String> helpMenuActionNames = Arrays.asList("help", "about");
		menuBar.add(MenuUtils.createMenu(m_actionsContext, "fileMenu", fileMenuActionNames));
		menuBar.add(MenuUtils.createMenu(m_actionsContext, "editMenu", editMenuActionNames));
		menuBar.add(MenuUtils.createMenu(m_actionsContext, "demoMenu", demoMenuActionNames));
		menuBar.add(MenuUtils.createMenu(m_actionsContext, "helpMenu", helpMenuActionNames));
		return menuBar;
	}
	
	/**
	 * @return    the created tool bar
	 */
	protected JToolBar createToolBar() {
		ActionsContext actionsContext = getActionsContext();
		String[] toolbarActionNames = {"quit"};
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		Border border = new EmptyBorder(2, 9, 2, 9);
		for (String actionName : toolbarActionNames) {
			JButton button = new JButton();
			button.setBorder(border);
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setAction(actionsContext.getAction(actionName));
			button.setFocusable(false);
			button.setText("");
			toolBar.add(button);
		}
		return toolBar;
	}
}
