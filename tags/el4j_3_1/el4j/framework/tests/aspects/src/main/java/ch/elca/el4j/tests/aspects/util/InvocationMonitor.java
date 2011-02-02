/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.aspects.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Invocation monitor to count per class and see the invocation order.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public final class InvocationMonitor {
	/**
	 * Private thread-save class-specific counter variables.
	 */
	private static Map<Class<?>, Integer> s_classCounterMap;
	
	/**
	 * Private thread-save list of count invocations.
	 */
	private static List<Class<?>> s_invocationList;
	
	/**
	 * Static constructor clears the invocation monitor.
	 */
	static {
		clear();
	}
	
	/**
	 * Hide default constructor.
	 */
	private InvocationMonitor() { }
	
	/**
	 * Clears the invocation monitor.
	 */
	public static synchronized void clear() {
		s_classCounterMap = Collections.synchronizedMap(new HashMap<Class<?>, Integer>());
		s_invocationList = Collections.synchronizedList(new ArrayList<Class<?>>());
	}
	
	/**
	 * Initializes the static counter to zero for the given clazz.
	 * 
	 * @param clazz Is the counter class.
	 */
	public static synchronized void initCounter(Class<?> clazz) {
		Assert.notNull(clazz);
		s_classCounterMap.remove(clazz);
		s_classCounterMap.put(clazz, 0);
	}

	/**
	 * @return Returns the current counter value of the given class.
	 * 
	 * @param clazz Is the counter class.
	 */
	public static synchronized int getCounter(Class<?> clazz) {
		Assert.isTrue(s_classCounterMap.containsKey(clazz));
		return s_classCounterMap.get(clazz);
	}
	
	/**
	 * Increments the counter variable of the given class.
	 * 
	 * @param clazz Is the counter class.
	 */
	public static synchronized void incrementCounter(Class<?> clazz) {
		Assert.isTrue(s_classCounterMap.containsKey(clazz));
		int i = s_classCounterMap.get(clazz);
		s_classCounterMap.put(clazz, i + 1);
		s_invocationList.add(clazz);
	}
	
	/**
	 * @return Returns a copy of the invocation list.
	 */
	public static synchronized List<Class<?>> getInvocationList() {
		return new ArrayList<Class<?>>(s_invocationList);
	}
}
