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
import java.util.HashMap;
import java.util.Map;


/**
 * Base class for different facades to the actual logger interface.
 *
 * @author Jonas Hauenstein (JHN)
 */
public abstract class GenericLogger {
	
	/**
	 * Reference to the actually used logger for reflective calls.
	 */
	protected Object m_orgLogger;
	
	/**
	 * List of allowed log levels.
	 */
	protected Map<String, Boolean> m_logLevels = new HashMap<String, Boolean>();
	
	/**
	 * Constructor.
	 *
	 * @param originallogger Reference to the actually used logger for reflective calls
	 */
	public GenericLogger(Object originallogger) {
		this.m_orgLogger = originallogger;
	}

	/**
	 * Check in underlying logging facility with the passed method
	 * name if the logger is enabled for a particular level - 
	 * or in other words, if the method in fact returns true.
	 *
	 * @param methodname Name of the method to call on logging facility
	 * @return True if method returned true, false otherwise
	 */
	protected boolean checkForLevel(String methodname) {
		if (m_orgLogger != null) {
			try {
				Method m = m_orgLogger.getClass().getMethod(methodname);
				Object r = m.invoke(m_orgLogger);
				if (r instanceof Boolean) {
					return (Boolean) r;
				} else {
					return false;
				}
			} catch (SecurityException e) {
				return false;
			} catch (NoSuchMethodException e) {
				return false;
			} catch (IllegalArgumentException e) {
				return false;
			} catch (IllegalAccessException e) {
				return false;
			} catch (InvocationTargetException e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Is the underlying logger instance enabled for the parameter level?
	 *
	 * @param level Log level to check
	 * @return True if this Logger is enabled for the parameter level, false otherwise
	 */
	public boolean isLogEnabled(String level) {
		if (m_logLevels.containsKey(level)) {
			return m_logLevels.get(level);
		}
		return false;
	}
	
	/**
	 * Log a message under the specified log level.
	 * The level can be:
	 *  - debug
	 *  - error
	 *  - info
	 *  - trace
	 *  - warn
	 *
	 * @param level The log level of the message
	 * @param msg The log message
	 */
	public abstract void log(String level, String msg);
	
}
