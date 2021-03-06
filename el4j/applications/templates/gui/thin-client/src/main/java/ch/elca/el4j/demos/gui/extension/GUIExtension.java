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
package ch.elca.el4j.demos.gui.extension;

import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import ch.elca.el4j.services.gui.swing.ActionsContext;
import ch.elca.el4j.services.gui.swing.GUIApplication;

/**
 * This interface provides extension points to the GUI template (internal use only!)
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public interface GUIExtension {
	/**
	 * @param application    the main form
	 */
	public void setApplication(GUIApplication application);
	
	/**
	 * @param menubar    the {@link JMenuBar} to extend
	 */
	public void extendMenuBar(JMenuBar menubar);
	
	/**
	 * @param toolbar    the {@link JToolBar} to extend
	 */
	public void extendToolBar(JToolBar toolbar);
	
	/**
	 * @return    a list of actions that can be called from outside
	 */
	public List<String> getActions();
	
	/**
	 * @return    the actionsContext to resolve the actions given by {@link #getActions()}.
	 */
	public ActionsContext getActionsContext();
}
