/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.services.remoting.loadbalancing.client.redirectuponfailure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.RemoteAccessException;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.services.remoting.protocol.loadbalancing.NoProtocolAvailableRTException;
import ch.elca.el4j.tests.services.remoting.loadbalancing.common.BusinessObject;

/**
 * This class tests the idempotent invocation interceptor that handles retrials
 * by itself. <script type="text/javascript">printFileStatus ("$URL$",
 * "$Revision$", "$Date$", "$Author$" );</script>
 *
 * @author Stefan Pleisch (SPL)
 */
public class LbClientRedirectUponFailureTest {
	
	/** Define the server names and ports to connect to */
	private static final String ServerNameAndPort1 = "localhost:8091" ;
	private static final String ServerNameAndPort2 = "localhost:8095" ;
	private static final String ServerNameAndPort3 = "localhost:8098" ;
	

	public LbClientRedirectUponFailureTest() {
		super();
		m_applicationContext = new ModuleApplicationContext(
			getInclusiveConfigLocations(), (String[]) null, false, null);
	} // <init>

	/**
	 * Tests whether the server/DB properly abort the transaction upon reception
	 * of a special flag.
	 *
	 * @see #THROW_IDEMPOTENTINVOCATION_EXCEPTION
	 */
	@Test
	public void testNextProtocol() {
		getLog().debug("Starting test 'testIdempotence'....");
		try {
			s_logger.debug("-- Calling with hello 1") ;
			String result = getTestObj().call("hello1") ;
			assertEquals(ServerNameAndPort1 + " should have executed this code.",
				ServerNameAndPort1,
				result);
			s_logger.debug("-- Calling with hello 2") ;
			result = getTestObj().call("hello2") ;
			assertEquals(ServerNameAndPort1 + " should have executed this code.",
				ServerNameAndPort1,
				result);
			s_logger.debug("-- Calling with hello 3") ;
			result = getTestObj().call("hello3") ;
			assertEquals(ServerNameAndPort1 + " should have executed this code.",
				ServerNameAndPort1,
				result);
			try {
				result = getTestObj().call(BusinessObject.COMMIT_SUICIDE + ServerNameAndPort1) ;
			} catch (RemoteAccessException rae) {
				s_logger.debug("Expected exception: " + rae.getMessage()) ;
			} // catch
			s_logger.debug("-- Calling with hello 4") ;
			try {
				result = getTestObj().call("hello4") ;
			} catch (RemoteAccessException rae) {
				s_logger.debug("Expected exception: " + rae.getMessage()) ;
				// retry
				result = getTestObj().call("hello4") ;
				assertEquals(ServerNameAndPort2 + " should have executed this code.",
					ServerNameAndPort2,
					result);
			} // catch
			try {
				result = getTestObj().call(BusinessObject.COMMIT_SUICIDE + ServerNameAndPort2) ;
			} catch (RemoteAccessException rae) {
				s_logger.debug("Expected exception: " + rae.getMessage()) ;
			} // catch
			try {
				s_logger.debug("-- Calling with hello 5") ;
				result = getTestObj().call("hello5") ;
			} catch (RemoteAccessException rae) {
				s_logger.debug("Expected exception: " + rae.getMessage()) ;
				// Retry
				result = getTestObj().call("hello5") ;
				assertEquals(ServerNameAndPort3 + " should have executed this code.",
				ServerNameAndPort3,
				result);
			} // catch
			try {
				result = getTestObj().call(BusinessObject.COMMIT_SUICIDE + ServerNameAndPort3) ;
			} catch (RemoteAccessException rae) {
				s_logger.debug("Expected exception: " + rae.getMessage()) ;
			} // catch
			try {
				result = getTestObj().call("hello6") ;
				fail("No server should have executed this code.") ;
			} catch (NoProtocolAvailableRTException npae) {
				s_logger.debug("Expected exception: " + npae.getMessage()) ;
				assertTrue("Exception is correct", true) ;
			} // catch
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occurred: " + e.getMessage());
		} // catch
	} // testSecondAccess()

	/** {@inheritDoc} */
	protected void runTest() {
		testNextProtocol();
	} // runTest()

	/** {@inheritDoc} */
	protected String[] getInclusiveConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath:loadbalancing/client/redirectuponfailure/startup.xml",
			"classpath:loadbalancing/remoting/redirectuponfailure/loadbalancing-protocol-config.xml",
			"classpath:loadbalancing/remoting/loadbalancing-generic-protocol-config.xml",
			"classpath:remoting/loadbalancing/policy/redirectuponfailure-policy-config.xml"};
	} // getInclusiveConfigLocations()

	protected Logger getLog() {
		return s_logger;
	} // getLog()

	protected BusinessObject getTestObj() {
		if (m_obj == null) {
			BeanFactory factory = (BeanFactory) m_applicationContext;

			m_obj = (BusinessObject) factory.getBean("rmiBusinessObj");

		} // if
		return m_obj;
	} // getTestObj

	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory
		.getLogger(LbClientRedirectUponFailureTest.class);

	private ApplicationContext m_applicationContext;

	private BusinessObject m_obj;

} // CLASS ClientMultipleServerTestCase
