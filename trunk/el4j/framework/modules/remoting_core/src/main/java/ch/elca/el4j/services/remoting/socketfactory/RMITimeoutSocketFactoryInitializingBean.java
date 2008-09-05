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
package ch.elca.el4j.services.remoting.socketfactory;

import java.io.IOException;
import java.rmi.server.RMISocketFactory;

import org.springframework.beans.factory.InitializingBean;

/**
 * This Bean is used initialize the new {@link RMISocketFactory}, more
 * precisely the {@link RMITimeoutSocketFactoryInitializingBean}. It sets the
 * timeout to the value defined in the spring configuration file rmi-timeout.xml
 * (in mandatory) and then sets the global socket factory from which RMI gets
 * sockets to the new {@link RMITimeoutSocketFactoryInitializingBean}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Dominik Zindel (DZI)
 */
public class RMITimeoutSocketFactoryInitializingBean implements InitializingBean {

	/**
	 * The SocketTimeout.
	 */
	private int m_timeout;

	/**
	 * The constructor for the Bean.
	 * @throws IOException
	 */
	public RMITimeoutSocketFactoryInitializingBean() throws IOException {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws IOException {
		RMITimeoutSocketFactory factory = new RMITimeoutSocketFactory(m_timeout);
		if (RMISocketFactory.getSocketFactory() == null) {
			RMISocketFactory.setSocketFactory(factory);
		}
	}

	/**
	 * @return Returns the timeout.
	 */
	public int getTimeout() {
		return m_timeout;
	}

	/**
	 * @param timeout
	 *            Is the timeout to set.
	 */
	public void setTimeout(int timeout) {
		m_timeout = timeout;
	}
}
