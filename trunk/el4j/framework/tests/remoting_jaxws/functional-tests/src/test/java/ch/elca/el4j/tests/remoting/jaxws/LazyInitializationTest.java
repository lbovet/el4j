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
package ch.elca.el4j.tests.remoting.jaxws;

import junit.framework.Assert;

import org.junit.Test;

import ch.elca.el4j.tests.core.AbstractTest;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.remoting.jaxws.service.LazyPerson;

public class LazyInitializationTest extends AbstractJaxwsTest {
	@Override
	protected String[] getIncludeConfigLocations() {
		return new String[] {"classpath*:mandatory/*.xml",
			"scenarios/client/remotingtests-jaxws-hibernate-client-config.xml"};
	}
	
	@Test
	public void testLazyPersonService() {
		LazyPerson lazyPerson = getLazyPerson();
		
		Person person = lazyPerson.getPerson(true);
		Assert.assertNotNull(person.getBrain());
		
		person = lazyPerson.getPerson(false);
		Assert.assertNull(person.getBrain());
		
		Brain b = new Brain();
		b.setIq(100);
		person.setBrain(b);
		lazyPerson.setPerson(person);
		
		person = lazyPerson.getPerson(false);
		Assert.assertNull(person.getBrain());
		
		person = lazyPerson.getPerson(true);
		Assert.assertEquals(b.getIq(), person.getBrain().getIq());
	}
	
	/**
	 * Get the LazyPerson to use.
	 * @return LazyPerson to use
	 */
	public LazyPerson getLazyPerson() {
		return (LazyPerson) getApplicationContext().getBean("lazyPerson");
	}
	
}
