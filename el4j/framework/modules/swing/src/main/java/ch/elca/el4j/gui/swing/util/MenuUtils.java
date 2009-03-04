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
package ch.elca.el4j.gui.swing.util;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ch.elca.el4j.gui.swing.ActionsContext;

/**
 * This utility class helps to create menus using Sun's appFramework.
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
public final class MenuUtils {
	
	/**
	 * The hidden constructor.
	 */
	private MenuUtils() { }
	
	/**
	 * Creates a JMenu out of a String array containing action names. A
	 * separator is represented by the string "---"
	 *
	 * @param context        the actions context
	 * @param menuName       the menu name
	 * @param actionNames    the collection of menu items
	 * @return a JMenu
	 */
	public static JMenu createMenu(ActionsContext context, String menuName, Collection<String> actionNames) {
		JMenu menu = new JMenu();
		menu.setName(menuName);
		return initMenu(context, actionNames, menu);
	}
	
	/**
	 * Creates a JPopupMenu out of a String array containing action names. A
	 * separator is represented by the string "---"
	 *
	 * @param context        the actions context
	 * @param actionNames    the collection of menu items
	 * @return a JPopupMenu
	 */
	public static JPopupMenu createPopup(ActionsContext context, Collection<String> actionNames) {
		JPopupMenu menu = new JPopupMenu();
		return initMenu(context, actionNames, menu);
	}
	
	/**
	 * Fills a menu with menu items.
	 *
	 * @param <T>            the menu type (e.g. JMenu, JPopupMenu)
	 * @param context        the actions context
	 * @param actionNames    the collection of menu items
	 * @param menu           the menu to insert the items
	 * @return               a menu
	 */
	private static <T extends JComponent> T initMenu(ActionsContext context, Collection<String> actionNames, T menu) {
		for (String actionName : actionNames) {
			if (actionName.equals("---")) {
				menu.add(new JSeparator());
			} else {
				JMenuItem menuItem = new JMenuItem();
				menuItem.setAction(context.getAction(actionName));
				//menuItem.setIcon(null);
				menu.add(menuItem);
			}
		}
		return menu;
	}
}
