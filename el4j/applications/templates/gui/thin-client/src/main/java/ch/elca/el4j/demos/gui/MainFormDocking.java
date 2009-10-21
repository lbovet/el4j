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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.noos.xing.mydoggy.ToolWindowAnchor;

import ch.elca.el4j.demos.gui.extension.GUIExtension;
import ch.elca.el4j.services.gui.swing.ActionsContext;
import ch.elca.el4j.services.gui.swing.DockingApplication;
import ch.elca.el4j.services.gui.swing.util.MenuUtils;

// Checkstyle: MagicNumber off
/**
 * Sample Docking application that demonstrates how to use the framework.
 *
 * See also associated MainFormDocking.properties file that contains resources
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class MainFormDocking extends DockingApplication {
	
	/**
	 * Main definition of the GUI.
	 *  This method is called back by the GUI framework
	 */
	@Override
	protected void startup() {
		actionsContext = ActionsContext.create(this, new MainFormActions(this));
		
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
		menuBar.add(MenuUtils.createMenu(actionsContext, "fileMenu", fileMenuActionNames));
		menuBar.add(MenuUtils.createMenu(actionsContext, "editMenu", editMenuActionNames));
		menuBar.add(MenuUtils.createMenu(actionsContext, "demoMenu", demoMenuActionNames));
		menuBar.add(MenuUtils.createMenu(actionsContext, "helpMenu", helpMenuActionNames));
		return menuBar;
	}
	
	/**
	 * @return    the created tool bar
	 */
	protected JToolBar createToolBar() {
		ActionsContext actionsContext = getActionsContext();
		String[] toolbarActionNames = {"quit", "paste"};
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
