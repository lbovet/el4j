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
package ch.elca.el4j.services.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * A simple DB-Logger that counts the number of DB roundtrips (single-threaded, i.e. roundtrips are not associated with
 * executing thread). It is a minimalistic wrapper for {@link MonitorFactory},
 * so use JAMons MonitorFactory directly if you need more control.
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
public final class DbLogger {
	/**
	 * Private logger of this class.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(DbLogger.class);
	
	/**
	 * The hidden constructor.
	 */
	private DbLogger() { }
	
	/**
	 * Start monitoring.
	 */
	public static void enable() {
		MonitorFactory.enable();
	}
	
	/**
	 * Stop monitoring.
	 */
	public static void disable() {
		MonitorFactory.disable();
	}
	
	/**
	 * @return    the number of DB roundtrips detected since monitor has been activated
	 *            (which is always done on startup) or last reset. If monitoring is not available <code>-1</code> is
	 *            returned.
	 */
	public static int getRoundtripCount() {
		int roundtrips = 0;
		if (MonitorFactory.getRootMonitor().getMonitors() != null) {
			for (Monitor monitor : MonitorFactory.getRootMonitor().getMonitors()) {
				// search for execution of (Prepared)Statements
				if (monitor.getLabel().contains("Statement.execute")) {
					roundtrips += monitor.getHits();
				}
			}
		} else {
			s_logger.warn("JAMon JDBC interceptor not found.");
			roundtrips = -1;
		}
		return roundtrips;
	}
	
	/**
	 * Reset monitoring. All acquired values get lost.
	 */
	public static void reset() {
		MonitorFactory.reset();
	}
}
