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
package ch.elca.el4j.gui.swing.actions;

import org.jdesktop.application.Action;

import ch.elca.el4j.gui.swing.ValueHolder;

/**
 * Grandparent class containing actions.
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
public class GrandparentActions {
	/**
	 * A variable to store which action got executed.
	 */
	protected final ValueHolder<String> m_stateHolder;
	
	/**
	 * the prefix to write into variableHolder.
	 */
	protected final String m_prefix;
	
	/**
	 * @param prefix         the prefix to write into variableHolder
	 * @param stateHolder    the variable to store which action got executed
	 */
	public GrandparentActions(String prefix, ValueHolder<String> stateHolder) {
		m_prefix = prefix;
		m_stateHolder = stateHolder;
	}
	
	/**
	 * Perform action A.
	 */
	@Action
	public void doA() {
		m_stateHolder.setValue(m_prefix + "Grandparent.doA");
	}
	
	/**
	 * Perform action B.
	 */
	@Action
	public void doB() {
		m_stateHolder.setValue(m_prefix + "Grandparent.doB");
	}
}
