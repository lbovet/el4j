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

package ch.elca.el4j.tests.core.contextpassing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ch.elca.el4j.core.contextpassing.DefaultImplicitContextPassingRegistry;

/**
 * This class tests the context passing classes.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ContextPassingTest {

	/**
	 * This test simulates implicit context passing with two registered passers
	 * on server and client side.
	 */
	@Test
	public void testTwoRegisteredPassersOnBothSides() {
		ImplicitContextPasserA passerClientA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerClientB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryClient
			= new DefaultImplicitContextPassingRegistry();
		passerClientA.setImplicitContextPassingRegistry(registryClient);
		passerClientB.setImplicitContextPassingRegistry(registryClient);

		ImplicitContextPasserA passerServerA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerServerB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryServer
			= new DefaultImplicitContextPassingRegistry();
		passerServerA.setImplicitContextPassingRegistry(registryServer);
		passerServerB.setImplicitContextPassingRegistry(registryServer);

		Map contextClient = registryClient.getAssembledImplicitContext();
		Map contextServer = new HashMap(contextClient);
		registryServer.pushAssembledImplicitContext(contextServer);

		String sentDataA = passerClientA.getTestData();
		String receivedDataA = passerServerA.getReceivedData();
		assertEquals("Sent and received data of passer A is not the same.",
				sentDataA, receivedDataA);

		double sentDataB = passerClientB.getTestData();
		double receivedDataB = passerServerB.getReceivedData();
		assertEquals("Sent and received data of passer B is not the same.",
				sentDataB, receivedDataB, 0);
	}

	/**
	 * This test simulates implicit context passing with two registered passers
	 * on client and only one on server side.
	 */
	@Test
	public void testTwoRegisteredPassersOnClientSideAndOneOnServerSide() {
		ImplicitContextPasserA passerClientA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerClientB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryClient
			= new DefaultImplicitContextPassingRegistry();
		passerClientA.setImplicitContextPassingRegistry(registryClient);
		passerClientB.setImplicitContextPassingRegistry(registryClient);

		ImplicitContextPasserA passerServerA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerServerB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryServer
			= new DefaultImplicitContextPassingRegistry();
		//passerServerA.setImplicitContextPassingRegistry(registryServer);
		passerServerB.setImplicitContextPassingRegistry(registryServer);

		Map contextClient = registryClient.getAssembledImplicitContext();
		Map contextServer = new HashMap(contextClient);
		registryServer.pushAssembledImplicitContext(contextServer);

		String sentDataA = passerClientA.getTestData();
		String receivedDataA = passerServerA.getReceivedData();
		assertFalse("Sent and received data of passer A is the same!",
				sentDataA.equals(receivedDataA));

		double sentDataB = passerClientB.getTestData();
		double receivedDataB = passerServerB.getReceivedData();
		assertEquals("Sent and received data of passer B is not the same.",
				sentDataB, receivedDataB, 0);
	}

	/**
	 * This test simulates implicit context passing with one registered passer
	 * on client and two on server side.
	 */
	@Test
	public void testOneRegisteredPasserOnClientSideAndTwoOnServerSide() {
		ImplicitContextPasserA passerClientA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerClientB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryClient
			= new DefaultImplicitContextPassingRegistry();
		passerClientA.setImplicitContextPassingRegistry(registryClient);
		//passerClientB.setImplicitContextPassingRegistry(registryClient);

		ImplicitContextPasserA passerServerA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerServerB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryServer
			= new DefaultImplicitContextPassingRegistry();
		passerServerA.setImplicitContextPassingRegistry(registryServer);
		passerServerB.setImplicitContextPassingRegistry(registryServer);

		Map contextClient = registryClient.getAssembledImplicitContext();
		Map contextServer = new HashMap(contextClient);
		registryServer.pushAssembledImplicitContext(contextServer);

		String sentDataA = passerClientA.getTestData();
		String receivedDataA = passerServerA.getReceivedData();
		assertEquals("Sent and received data of passer A is not the same.",
				sentDataA, receivedDataA);

		double sentDataB = passerClientB.getTestData();
		double receivedDataB = passerServerB.getReceivedData();
		assertFalse("Sent and received data of passer B is the same!",
				sentDataB == receivedDataB);
	}

	/**
	 * This test uses the <code>DefaultImplicitContextPassingRegistry</code>
	 * without any registered passers.
	 */
	@Test
	public void testDefaultImplicitContextPassingRegistryStandalone() {
		ImplicitContextPasserA passerClientA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerClientB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryClient
			= new DefaultImplicitContextPassingRegistry();
		//passerClientA.setImplicitContextPassingRegistry(registryClient);
		//passerClientB.setImplicitContextPassingRegistry(registryClient);

		ImplicitContextPasserA passerServerA = new ImplicitContextPasserA();
		ImplicitContextPasserB passerServerB = new ImplicitContextPasserB();
		DefaultImplicitContextPassingRegistry registryServer
			= new DefaultImplicitContextPassingRegistry();
		//passerServerA.setImplicitContextPassingRegistry(registryServer);
		//passerServerB.setImplicitContextPassingRegistry(registryServer);

		Map contextClient = registryClient.getAssembledImplicitContext();
		Map contextServer = new HashMap(contextClient);
		registryServer.pushAssembledImplicitContext(contextServer);

		String sentDataA = passerClientA.getTestData();
		String receivedDataA = passerServerA.getReceivedData();
		assertFalse("Sent and received data of passer A is the same!",
				sentDataA.equals(receivedDataA));

		double sentDataB = passerClientB.getTestData();
		double receivedDataB = passerServerB.getReceivedData();
		assertFalse("Sent and received data of passer B is the same!",
				sentDataB == receivedDataB);
	}
}