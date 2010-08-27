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
package ch.elca.el4j.util.codingsupport;

import ch.elca.el4j.core.exceptions.BaseRTException;

/**
 * This exception is a base exception class for not fulfilled conditions.
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractConditionRTException extends BaseRTException {
	/**
	 * Constructor with only the type of condition.
	 *
	 * @param conditionType
	 *            Is the type of condition.
	 */
	public AbstractConditionRTException(String conditionType) {
		super("A {0} was not fulfilled.", new String[] {conditionType});
	}

	/**
	 * Constructor with type of condition and a message.
	 *
	 * @param conditionType
	 *            Is the type of condition.
	 * @param message
	 *            Is the message to explain what condition was no fulfilled.
	 */
	public AbstractConditionRTException(String conditionType, String message) {
		super("The {0} {1} was not fulfilled.", new String[] {conditionType,
			message});
	}
}