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
package ch.elca.el4j.services.persistence.hibernate.offlining.test.notifications;

import java.util.Arrays;

/**
 * Notification called from a method to track the correct order of method calls.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author David Bernhard (DBD)
 */
public class Notification {

	/** Method name. */
	private final String m_name;

	/** Method arguments, if necessary. */
	private final Object m_args;
	
	/** 
	 * New notification with method name.
	 * @param name The method name.
	 */
	public Notification(String name) {
		m_name = name;
		m_args = null;
	}

	/** 
	 * New notification with name and arguments.
	 * @param name The name.
	 * @param args The arguments. 
	 */
	public Notification(String name, Object args) {
		m_name = name;
		m_args = args;
	}

	/**
	 * Get the name.
	 * @return The name.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Get the args.
	 * @return The args.
	 */
	public Object getArgs() {
		return m_args;
	}
	
	/**
	 * Validate a notification. This is set by subclassing the expected
	 * notification; the processor calls expected.validate(recieved) and
	 * fails if the method returns false.
	 * @param target The recieved or target notification.
	 * @return <code>true</code> if the target is valid, <code>false</code>
	 * to fail the processor.
	 */
	public boolean validate(Notification target) {

		// 1. Names must match. 
		boolean isOk = (m_name.equals(target.m_name));
		
		// 2a. If args != null, args must either match as such ...
		if (m_args != null && !m_args.equals(target.m_args)) {
		
			// Varargs management stuff.
			
			// 2b. or if both are arrays, the elements must match ...
			if (m_args instanceof Object[] && target.m_args instanceof Object[]) {
				isOk &= Arrays.equals((Object[]) m_args, (Object[]) target.m_args);
			} else if (target.m_args instanceof Object[]) {
			
				// 2c. or, if args is an array but our argument is not,
				// match the first element.
				Object[] array = (Object[]) target.m_args;
				isOk &= array.length == 1 & m_args.equals(array[0]);
			} else {
				isOk = false;
			}
		}
		return isOk;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return m_name + "(" + argToString(m_args + ")");
	}
	
	/**
	 * Perform toString on objects, iterate over arrays.
	 * @param arg The argument.
	 * @return A string representation.
	 */
	private static String argToString(Object arg) {
		if (arg instanceof Object[]) {
			Object[] array = (Object[]) arg;
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (Object entry : array) {
				builder.append(argToString(entry));
				builder.append(", ");
			}
			builder.delete(builder.length() - 2, builder.length());
			builder.append("]");
			return builder.toString();
		} else {
			return arg.toString();
		}
	}
}
