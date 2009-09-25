/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.socketstatistics.genericlogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * LogFactory that returns a GenericLogger.
 *
 * This Factory searches in all loaded classes of the calling thread
 * for a logging facility and returns the corresponding GenericLogger.
 *
 * The search order is as follows:
 *  - SLF4J (org.slf4j.LoggerFactory)
 *  - Apache Commons Logging (org.apache.commons.logging)
 *  - Log4J (org.apache.log4j)
 *
 * If none of these logging facilities is found, it uses the JDK 
 * logging (java.util.logging).
 *
 * @author Jonas Hauenstein (JHN)
 */
public class GenericLogFactory {
	
	/**
	 * Map for storage of already initiated loggers.
	 * Map key is the calling threads classloader
	 */
	private static Map<ClassLoader, GenericLogger> s_loggerMap = new WeakHashMap<ClassLoader, GenericLogger>();
	
	/**
	 * Return a GenericLogger named corresponding to the class passed as parameter.
	 *
	 * @param clazz The returned logger will be named after clazz 
	 * @return The GenericLogger
	 */
	public static GenericLogger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}
	
	/**
	 * Return a GenericLogger named corresponding to the name parameter.
	 *
	 * @param name The name of the logger
	 * @return The GenericLogger
	 */
	public static GenericLogger getLogger(String name) {
		Object ret;
		GenericLogger logger = null;
		//check if there is already a logger for this thread
		if (s_loggerMap.containsKey(Thread.currentThread().getContextClassLoader())) {
			return s_loggerMap.get(Thread.currentThread().getContextClassLoader());
		}
		
		if ((ret = fetchlogger("org.slf4j.LoggerFactory", "getLogger", name)) != null) {
			logger = new SLF4JLogger(ret);
			s_loggerMap.put(Thread.currentThread().getContextClassLoader(), logger);
		} else if ((ret = fetchlogger("org.apache.commons.logging.LogFactory", "getLog", name)) != null) {
			logger = new CommonsLoggingLogger(ret);
			s_loggerMap.put(Thread.currentThread().getContextClassLoader(), logger);
		} else if ((ret = fetchlogger("org.apache.log4j.Logger", "getLoggger", name)) != null) {
			logger = new Log4JLogger(ret);
			s_loggerMap.put(Thread.currentThread().getContextClassLoader(), logger);
		} else {
			logger = new JDKLogger(name);
			s_loggerMap.put(Thread.currentThread().getContextClassLoader(), logger);
		}
		
		return logger;
	}
	
	/**
	 * Search for a logging factory and a corresponding logger in the calling threads ClassLoader.
	 *
	 * @param factoryclass The full name of the logger factory
	 * @param factorymethod The name of the method to create a now logger from the factory
	 * @param logname The desired name of the created logger
	 * @return an Object representing the logger class or null if no matching loggerfactory is found
	 */
	private static Object fetchlogger(String factoryclass, String factorymethod, String logname) {
		
		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		try {
			Class factory = Class.forName(factoryclass, true, ccl);
			Method fm = factory.getMethod(factorymethod, String.class);
			Object o = fm.invoke(factory, logname);
			return o;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
		
	}
	
	
	
}
