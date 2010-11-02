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

/**
 * The facade to the Log4J logger.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class Log4JLogger extends GenericLogger {
	
	/**
	 * Constructor.
	 * This GenericLogger is using the L4J Logger for logging.
	 *
	 * @param originallogger Reference to the actually used logger for reflective calls
	 */
	public Log4JLogger(Object originallogger) {
		super(originallogger);
		m_logLevels.put("debug", checkForLevel("isDebugEnabled"));
		m_logLevels.put("error", checkForLevel("isErrorEnabled"));
		m_logLevels.put("info", checkForLevel("isInfoEnabled"));
		m_logLevels.put("trace", checkForLevel("isTraceEnabled"));
		m_logLevels.put("warn", checkForLevel("isWarnEnabled"));
	}
	
	/** {@inheritDoc} */
	public void log(String level, String msg) {
		if (m_orgLogger != null && m_logLevels.containsKey(level) && m_logLevels.get(level)) {
			try {
				Method m = m_orgLogger.getClass().getMethod(level, Object.class);
				m.invoke(m_orgLogger, msg);
			} catch (Exception e) { }
		}
		
	}
	
}
