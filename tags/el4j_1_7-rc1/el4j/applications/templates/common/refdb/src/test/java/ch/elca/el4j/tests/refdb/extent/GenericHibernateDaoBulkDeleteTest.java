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
package ch.elca.el4j.tests.refdb.extent;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.dao.DataRetrievalFailureException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.tests.person.dao.impl.hibernate.GenericHibernateBrainDaoInterface;
import ch.elca.el4j.tests.person.dao.impl.hibernate.GenericHibernatePersonDaoInterface;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

/**
 * Test case for <code>GenericHibernateDao</code> to test
 * the bulk delete functionality using a hql statement.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class GenericHibernateDaoBulkDeleteTest extends AbstractTestCaseBase {

	/**
	 * Person DAO. Created by application context.
	 */
	private GenericHibernatePersonDaoInterface m_personDao;
	
	/**
	 * Brain DAO. Created by application context.
	 */
	private GenericHibernateBrainDaoInterface brainDao;
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getIncludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:mandatory/refdb/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml"};
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getExcludeConfigLocations() {
		// use extent-test-hibernate-config.xml instead
		return new String[] {
			"classpath*:scenarios/dataaccess/hibernate/refdb/refdb-core-hibernate-config.xml"};
	}
	
	
	/**
	 * This test checks if cascading delete DOES NOT work.
	 */
	@Test
	public void testCascadingDelete() {
		
		GenericHibernatePersonDaoInterface pdao = getPersonDao();
		GenericHibernateBrainDaoInterface bdao = getBrainDao();

		int personcount = pdao.getAll().size();
		int braincount = bdao.getAll().size();

		Person person1 = new Person("Peter Muster");
		Brain brain1 = new Brain();
		brain1.setIq(99);
		person1.setBrain(brain1);
		person1 = pdao.saveOrUpdate(person1);
		int brainkey1 = person1.getBrain().getKey();
		brain1 = person1.getBrain();
		int personkey1 = person1.getKey();

		Person person2 = new Person("Hans Muster");
		Brain brain2 = new Brain();
		brain2.setIq(7);
		person2.setBrain(brain2);
		person2 = pdao.saveOrUpdate(person2);
		int brainkey2 = person2.getBrain().getKey();
		brain2 = person2.getBrain();
		int personkey2 = person2.getKey();
		
		//check if brains and persons are stored. 
		assertEquals("Not both persons were stored.", personcount + 2, pdao.getAll().size());
		assertEquals("Person 1 was not stored.", pdao.findById(personkey1), person1);
		assertEquals("Person 2 was not stored.", pdao.findById(personkey2), person2);
		assertEquals("Not all brains were stored.", braincount + 2, bdao.getAll().size());
		assertEquals("Brain 1 was not stored.", bdao.findById(brainkey1), brain1);
		assertEquals("Brain 2 was not stored.", bdao.findById(brainkey2), brain2);
		
		//delete persons
		ArrayList<Person> personlist = new ArrayList<Person>();
		personlist.add(person1);
		personlist.add(person2);
		
		pdao.deleteNoCascade(personlist);

		//check if persons were deleted
		assertEquals("Not all persons were deleted.", personcount, pdao.getAll().size());
		try {
			person1 = pdao.findById(personkey1);
			fail("Person 1 was not deleted.");
		} catch (DataRetrievalFailureException e) {
			//do nothing
		}
		try {
			person2 = pdao.findById(personkey2);
			fail("Person 2 was not deleted.");
		} catch (DataRetrievalFailureException e) {
			//do nothing
		}
		
		//check if brains were NOT deleted
		//if brains were deleted, then something in the hql cascade handling
		//of hibernate has changed and the corresponding methods need an update
		try {
			brain1 = bdao.findById(brainkey1);
		} catch (DataRetrievalFailureException e) {
			fail("Brain 1 was deleted by method deleteNoCascade.");
		}
		try {
			brain2 = bdao.findById(brainkey2);
		} catch (DataRetrievalFailureException e) {
			fail("Brain 2 was not deleted by method deleteNoCascade.");
		}
		
		//delete brains now
		bdao.delete(brain1);
		bdao.delete(brain2);
		assertEquals("Brains were not deleted.", braincount, bdao.getAll().size());
		
	}
	
	/**
	 * @return Returns the person DAO.
	 */
	protected GenericHibernatePersonDaoInterface getPersonDao() {
		if (m_personDao == null) {
			DefaultDaoRegistry daoRegistry
				= (DefaultDaoRegistry) getApplicationContext()
					.getBean("daoRegistry");
			m_personDao = (GenericHibernatePersonDaoInterface) daoRegistry
				.getFor(Person.class);
		}
		return m_personDao;
	}
	
	/**
	 * @return Returns the brain DAO.
	 */
	protected GenericHibernateBrainDaoInterface getBrainDao() {
		if (brainDao == null) {
			DefaultDaoRegistry daoRegistry
				= (DefaultDaoRegistry) getApplicationContext()
					.getBean("daoRegistry");
			brainDao = (GenericHibernateBrainDaoInterface) daoRegistry
				.getFor(Brain.class);
		}
		return brainDao;
	}
	
}
