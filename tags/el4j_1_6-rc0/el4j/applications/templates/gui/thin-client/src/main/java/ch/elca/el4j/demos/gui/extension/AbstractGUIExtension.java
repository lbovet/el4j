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
package ch.elca.el4j.demos.gui.extension;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.jdesktop.beans.AbstractBean;

import ch.elca.el4j.services.gui.swing.ActionsContext;
import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.services.gui.swing.util.MenuUtils;

/**
 * An abstract default implementation of the {@link GUIExtension} (internal use only!).
 * 
 * Remark: It extends AbstractBean because property change support might be needed.
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
public abstract class AbstractGUIExtension extends AbstractBean implements GUIExtension {
	
	/**
	 * The main application which gets extended.
	 */
	protected GUIApplication m_application;
	
	/** {@inheritDoc} */
	public void setApplication(GUIApplication application) {
		m_application = application;
	}
	
	/** {@inheritDoc} */
	public void extendToolBar(JToolBar menubar) { }
	
	/** {@inheritDoc} */
	public ActionsContext getActionsContext() {
		return ActionsContext.extendDefault(this);
	}
	
	/**
	 * Creates a menu containing all actions in {@link #getActions()} using the
	 * actionContext given by {@link #getActionsContext()}.
	 * 
	 * @param menubar     the menubar to extend
	 * @param menuName    the name of the menu to add
	 */
	protected void extendMenuBarDefault(JMenuBar menubar, String menuName) {
		ActionsContext actionsContext = getActionsContext();
		JMenu menu = MenuUtils.createMenu(actionsContext, menuName, getActions());
		
		menubar.add(menu, menubar.getComponentCount() - 2);
	}

	
}
