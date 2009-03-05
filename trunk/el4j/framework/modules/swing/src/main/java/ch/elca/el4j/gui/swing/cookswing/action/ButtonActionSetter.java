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
package ch.elca.el4j.gui.swing.cookswing.action;

import javax.swing.AbstractButton;
import javax.swing.Action;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import ch.elca.el4j.gui.swing.ActionsContext;

import cookxml.cookswing.CookSwing;
import cookxml.core.DecodeEngine;
import cookxml.core.exception.SetterException;
import cookxml.core.interfaces.Setter;

/**
 * This class is a cookXml setter, which sets actions defined by "@Action"
 * for buttons.
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
public class ButtonActionSetter implements Setter {
	/**
	 * The class containing "@Action" annotated methods.
	 */
	private Object m_actionHolder;
	
	/**
	 * The default constructor: actionHolder is the associated class.
	 * @see CookSwing#CookSwing(Object)
	 */
	public ButtonActionSetter() { }
	
	/**
	 * @param actionHolder    the class containing "@Action" annotated methods
	 */
	public ButtonActionSetter(Object actionHolder) {
		m_actionHolder = actionHolder;
	}
	
	/** {@inheritDoc} */
	public void setAttribute(String ns, String tag,
		String attrNS, String attr, Object obj, Object value,
		DecodeEngine decodeEngine) throws SetterException {
		
		Object actionHolder = m_actionHolder;
		if (actionHolder == null) {
			actionHolder = decodeEngine.getVariable("this");
		}
		
		Action action = null;
		
		String actionName = (String) value;
		// Check attrValue if it contains class name
		String[] parsedValues = actionName.split("#");
		Class<?> cls = null;
		if (parsedValues.length == 2) {
			try {
				cls =  Class.forName(parsedValues[0]);
				ApplicationContext ac = Application.getInstance().getContext();
				action = ac.getActionMap(cls, actionHolder).get(parsedValues[1]);
			} catch (ClassNotFoundException e) {
				action = null;
			}
		}
		
		if (action == null) {
			ActionsContext actionsContext;
			if (actionHolder instanceof ActionsContextAware) {
				actionsContext = ((ActionsContextAware) actionHolder).getActionsContext();
			} else {
				// use ActionsContext of GUIApplication as parent context and extend it with the actionHolder
				actionsContext = ActionsContext.extendDefault(actionHolder);
			}
			
			action = actionsContext.getAction(actionName);
			if (action == null) {
				throw new ActionNotFoundException(actionName);
			}
		}
		
		AbstractButton button = (AbstractButton) obj;
		button.setAction(action);
	}
}
