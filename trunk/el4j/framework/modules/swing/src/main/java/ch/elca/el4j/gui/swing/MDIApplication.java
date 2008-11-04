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
package ch.elca.el4j.gui.swing;

import javax.swing.JDesktopPane;

import ch.elca.el4j.gui.swing.mdi.WindowManager;
import ch.elca.el4j.gui.swing.mdi.WindowMenu;


/**
 * Parent class for MDI applications not using an XML GUI description.
 * MDI Applications using XML GUIs should use {@link AbstractMDIApplication}.
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
public abstract class MDIApplication extends AbstractMDIApplication {
	
	/**
	 * The desktop pane of this MDIApplication.
	 *  @see #createDefaultDesktopPane()
	 */
	protected JDesktopPane m_desktopPane;

	/**
	 * Helps to manage the mdi menu and pane.
	 */
	protected WindowManager m_windowManager;
	
	
	/** {@inheritDoc} */
	@Override
	protected JDesktopPane getDesktopPane() {
		return m_desktopPane;
	}

	/**
	 * Creates a default desktop pane with a default Menu This method could be
	 * overridden in case you would like another desktop pane. <br>
	 * Stores the created desktop pane in the {@link #m_desktopPane}
	 */
	protected void createDefaultDesktopPane() {
		m_desktopPane = new JDesktopPane();

		// create window manager and add window menu
		WindowMenu windowMenu = new WindowMenu();
		m_windowManager = new WindowManager(m_desktopPane, windowMenu);
		windowMenu.setWindowManager(m_windowManager);
		getMainFrame().getJMenuBar().add(windowMenu,
			getMainFrame().getJMenuBar().getMenuCount() - 1);
	}
}
