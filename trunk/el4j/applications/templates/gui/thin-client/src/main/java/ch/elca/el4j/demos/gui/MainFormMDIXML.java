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
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.demos.gui.extension.GUIExtension;
import ch.elca.el4j.gui.swing.AbstractMDIApplication;
import ch.elca.el4j.gui.swing.ActionsContext;

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
	@SuppressWarnings("unchecked")
	@Override
	protected void startup() {
		m_actionsContext = ActionsContext.create(this, new MainFormActions(this));
		
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
		
		// implicitly register graphical exception handler
		getSpringContext().getBean("exceptionsForm");
		
		// register extensions
		Map<String, GUIExtension> extensions = (Map<String, GUIExtension>)
			getSpringContext().getBeansOfType(GUIExtension.class);
		
		for (GUIExtension extension : extensions.values()) {
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
}
