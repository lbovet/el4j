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
package ch.elca.el4j.services.gui.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import org.jdesktop.application.Application;

/**
 * This class holds the Actions context, that enables to resolve the Swing Action given the action name.
 * It basically consists of an (ordered) list of objects having @Action annotated methods.
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
public final class ActionsContext {
	/**
	 * The array containing all instances to search for annotated methods.
	 */
	private List<Object> m_instancesWithActionMappings;
	
	/**
	 * The parent {@link ActionsContext} if any.
	 */
	private final ActionsContext m_parentContext;
	
	/**
	 * The private constructor.
	 * @param parentContext                  the parent {@link ActionsContext} if any
	 * @param instancesWithActionMappings    an array containing all instances to search for annotated methods
	 */
	private ActionsContext(ActionsContext parentContext, Object... instancesWithActionMappings) {
		m_parentContext = parentContext;
		m_instancesWithActionMappings = new ArrayList<Object>(Arrays.asList(instancesWithActionMappings));
	}
	
	/**
	 * Extend an ActionsContext.
	 * @param parentContext                  the parent {@link ActionsContext} to extend.
	 * @param instancesWithActionMappings    an array containing all instances to search for annotated methods
	 * @return                               the created ActionsContext
	 */
	public static ActionsContext extend(ActionsContext parentContext, Object... instancesWithActionMappings) {
		return new ActionsContext(parentContext, instancesWithActionMappings);
	}
	
	/**
	 * Extend the ActionsContext of {@link GUIApplication}.
	 * @param instancesWithActionMappings    an array containing all instances to search for annotated methods
	 * @return                               the created ActionsContext
	 */
	public static ActionsContext extendDefault(Object... instancesWithActionMappings) {
		return new ActionsContext(GUIApplication.getInstance().getActionsContext(), instancesWithActionMappings);
	}
	
	/**
	 * Create an ActionsContext.
	 * @param instancesWithActionMappings    an array containing all instances to search for annotated methods
	 * @return                               the created ActionsContext
	 */
	public static ActionsContext create(Object... instancesWithActionMappings) {
		return new ActionsContext(null, instancesWithActionMappings);
	}
	
	/**
	 * Add an instances to search for annotated methods.
	 * @param instanceWithActionMappings    an instances to search for annotated methods
	 */
	public void add(Object instanceWithActionMappings) {
		m_instancesWithActionMappings.add(instanceWithActionMappings);
	}
	
	/**
	 * Returns the first action object found for an action name.
	 *  (Looks in the internal list of candidate objects.)
	 *
	 * @param actionName   the action name as String
	 * @return             the corresponding action object
	 */
	public Action getAction(String actionName) {
		for (Object candidate : m_instancesWithActionMappings) {
			Action foundAction = getAction(candidate, actionName);
			if (foundAction != null) {
				return foundAction;
			}
		}
		if (m_parentContext != null) {
			return m_parentContext.getAction(actionName);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the action object for a specific object and action name.
	 * @param object        the object containing actions
	 * @param actionName    the action name as String
	 * @return              the corresponding action object
	 */
	public Action getAction(Object object, String actionName) {
		org.jdesktop.application.ApplicationContext ac
			= Application.getInstance().getContext();
		
		// it's important to use Object.class here. Otherwise @Action annotated methods in
		// super classes are not considered, which normally is not expected
		Action action = ac.getActionMap(Object.class, object).get(actionName);
		if (action == null && m_parentContext != null) {
			return m_parentContext.getAction(object, actionName);
		} else {
			return action;
		}
	}
}
