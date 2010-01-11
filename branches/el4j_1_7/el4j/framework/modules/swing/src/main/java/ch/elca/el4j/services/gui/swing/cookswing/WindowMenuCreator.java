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
package ch.elca.el4j.services.gui.swing.cookswing;

import javax.swing.JDesktopPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.w3c.dom.Element;

import ch.elca.el4j.services.gui.swing.mdi.WindowManager;
import ch.elca.el4j.services.gui.swing.mdi.WindowMenu;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CookXmlException;
import cookxml.core.exception.CreatorException;
import cookxml.core.interfaces.Creator;

/**
 * The cookSwing creator for &lt;windowmenu&gt;s, which list all opened forms
 * inside the desktop pane and some operations on them.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class WindowMenuCreator implements Creator {

	/**
	 * This {@link MenuListener} lazily initialized the window menu. This
	 * is necessary because generally the desktop pane XML element is declared
	 * after the menu and therefore the cannot be resolved.
	 */
	private class LazyWindowMenuCreator implements MenuListener {
		/**
		 * The cookXML decoder engine.
		 */
		private DecodeEngine m_decodeEngine;
		
		/**
		 * The window menu.
		 */
		private WindowMenu m_menu;
		
		/**
		 * The id of the desktop pane XML element.
		 */
		private String m_desktopPaneId;
		
		/**
		 * @param decodeEngine     the cookXML decoder engine
		 * @param menu             the window menu
		 * @param desktopPaneId    the id of the desktop pane XML element
		 */
		public LazyWindowMenuCreator(DecodeEngine decodeEngine,
			WindowMenu menu, String desktopPaneId) {
			
			m_decodeEngine = decodeEngine;
			m_menu = menu;
			m_desktopPaneId = desktopPaneId;
		}
		
		/** {@inheritDoc} */
		public void menuSelected(MenuEvent e) {
			if (m_menu.getWindowManager() == null && m_decodeEngine != null) {
				JDesktopPane desktopPane = (JDesktopPane) m_decodeEngine
				.getCookXml().getId(m_desktopPaneId).object;
				WindowManager windowManager = new WindowManager(
					desktopPane, m_menu);
				m_menu.setWindowManager(windowManager);
				
				m_decodeEngine = null;
				
			}
		}
		
		/** {@inheritDoc} */
		public void menuDeselected(MenuEvent e) { }
		
		/** {@inheritDoc} */
		public void menuCanceled(MenuEvent e) { }
	}
	
	/** {@inheritDoc} */
	public Object create(String parentNS, String parentTag, Element elm,
		Object parentObj, DecodeEngine decodeEngine) throws CreatorException {

		WindowMenu windowMenu = new WindowMenu();
		windowMenu.addMenuListener(new LazyWindowMenuCreator(
			decodeEngine, windowMenu, elm.getAttribute("desktopPaneId")));
		
		return windowMenu;
	}

	/** {@inheritDoc} */
	public Object editFinished(String parentNS, String parentTag, Element elm,
		Object parentObj, Object obj, DecodeEngine decodeEngine)
		throws CookXmlException {
		
		return obj;
	}
}
