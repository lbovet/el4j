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
package ch.elca.el4j.util.socketstatistics;

import java.lang.management.ManagementFactory;
import java.net.SocketImpl;
import java.net.SocketImplFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogFactory;
import ch.elca.el4j.util.socketstatistics.genericlogger.GenericLogger;

/**
 * Implementation of SocketImplFactory for the creation of SocketImplLogger.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class LoggerSocketFactory implements SocketImplFactory {

	/**
	 * Reference to SocketStatistics.
	 */
	private static SocketStatistics s_ss = null;

	/**
	 * Generic Logger.
	 */
	private final GenericLogger m_logger = GenericLogFactory.getLogger(SocketStatistics.class);

	/**
	 * Modified version which also registers the corresponding MXbean (SocketStatisticsMXBean) on the MBeanServer.
	 * {@inheritDoc}
	 */
	@Override
	public SocketImpl createSocketImpl() {
		if (s_ss == null) {
			s_ss = new SocketStatistics();
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			try {
				ObjectName name = new ObjectName("ch.elca.el4j.util.socketStatistics:type=SocketStatistics");
				if (!mbs.isRegistered(name)) {
					mbs.registerMBean(s_ss, name);
					m_logger.log("info", "MXBean SocketStatisticsMXBean successfully registered on MBeanServer");
				}
			} catch (MalformedObjectNameException e) {
				m_logger.log("info",
					"Failed to register MXBean SocketStatisticsMXBean on MBeanServer (MalformedObjectNameException)");
			} catch (NullPointerException e) {
				m_logger.log("info",
					"Failed to register MXBean SocketStatisticsMXBean on MBeanServer (NullPointerException)");
			} catch (InstanceAlreadyExistsException e) {
				m_logger.log("info",
					"Failed to register MXBean SocketStatisticsMXBean on MBeanServer (MBean already registered)");
			} catch (MBeanRegistrationException e) {
				m_logger.log("info",
					"Failed to register MXBean SocketStatisticsMXBean on MBeanServer (MBeanRegistrationException)");
			} catch (NotCompliantMBeanException e) {
				m_logger.log("info",
					"Failed to register MXBean SocketStatisticsMXBean on MBeanServer (NotCompliantMBeanException)");
			}

		}
		return new SocketImplLogger();
	}

}
