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

package ch.elca.el4j.tests.remoting.jaxws.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import ch.elca.el4j.core.contextpassing.ImplicitContextPasser;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This class is used to test if the implicit context passing works.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class TestImplicitContextPassingRegistry implements
		ImplicitContextPassingRegistry {
	
	/**
	 * The test message.
	 */
	private static final String MESSAGE = "Hello everybody, I am THE test message.";
	
	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(
		TestImplicitContextPassingRegistry.class);

	/**
	 * {@inheritDoc}
	 */
	public void registerImplicitContextPasser(
		ImplicitContextPasser passer) {
		// Do nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregisterImplicitContextPasser(
		ImplicitContextPasser passer) {
		// Do nothing.
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Map getAssembledImplicitContext() {
		Map map = new HashMap();
		map.put("testMessage", MESSAGE);
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void pushAssembledImplicitContext(Map contexts) {
		s_logger.info("Test message: " + contexts.get("testMessage"));
		Assert.isTrue(MESSAGE.equals(contexts.get("testMessage")));
	}
}
