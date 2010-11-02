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
package ch.elca.el4j.tests.refdb.hibernate.jpasupport;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.tests.core.AbstractTest;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;

/**
 * Test class for JpaFullSupport feature.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Rueedlinger (ARR)
 */
public class HibernateJpaSupportTest extends AbstractTest {

	/** See corresponding getter for information. */
	private GenericDao<Person> m_personDao;

	@Override
	protected String[] getIncludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:mandatory/refdb/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/jpasupport/test/*.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml"};
	}
	
	/**
	 * @return Returns the person DAO.
	 */
	protected GenericDao<Person> getPersonDao() {
		if (m_personDao == null) {
			DefaultDaoRegistry daoRegistry
				= (DefaultDaoRegistry) getApplicationContext()
					.getBean("daoRegistry");
			m_personDao = daoRegistry.getFor(Person.class);
		}
		return m_personDao;
	}
	
	/**
	 * Test the JPA functionality of the sessionFactory.
	 */
	@Test
	public void testJPAEntension() {
		Person p1 = new Person("name");
		Brain b = new Brain();
		b.setIq(99);
		p1.setBrain(b);
		b.setOwner(p1);
		Person p = getPersonDao().saveOrUpdate(p1);
		
		assertEquals("@PrePersist callback was not executed", "name(modified when persisted)", p.getName());
	}

}
