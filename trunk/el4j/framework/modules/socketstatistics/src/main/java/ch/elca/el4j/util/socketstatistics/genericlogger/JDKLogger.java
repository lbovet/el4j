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

import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * The facade to the JDK logger.
 *
 * @author Jonas Hauenstein (JHN)
 */
public class JDKLogger extends GenericLogger {
	
	/**
	 * The JDK Logger.
	 */
	private Logger m_l;
	
	/**
	 * Constructor.
	 * This GenericLogger is using the JDK Logger for logging.
	 *
	 * @param name The name of the logger
	 */
	public JDKLogger(String name) {
		super(null);
		m_l = Logger.getLogger(name);
		m_logLevels.put("debug", m_l.isLoggable(Level.FINEST));
		m_logLevels.put("error", m_l.isLoggable(Level.SEVERE));
		m_logLevels.put("info", m_l.isLoggable(Level.INFO));
		m_logLevels.put("trace", m_l.isLoggable(Level.FINE));
		m_logLevels.put("warn", m_l.isLoggable(Level.WARNING));
	}
	
	/** {@inheritDoc} */
	public void log(String level, String msg) {
		if (m_l != null && m_logLevels.containsKey(level) && m_logLevels.get(level)) {
			//translate levels to jdk logger levels
			if (level.equals("info")) {
				m_l.info(msg);
			} else if (level.equals("warn")) {
				m_l.warning(msg);
			} else if (level.equals("error")) {
				m_l.severe(msg);
			} else if (level.equals("trace")) {
				m_l.fine(msg);
			} else if (level.equals("debug")) {
				m_l.finest(msg);
			}
		}
	}
	
}
