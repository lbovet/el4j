/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.refdb.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.jpa.dao.GenericJpaDao;
import ch.elca.el4j.tests.core.ModuleTestContextLoader;
import ch.elca.el4j.tests.core.context.ExtendedContextConfiguration;
import ch.elca.el4j.tests.core.context.junit4.EL4JJunit4ClassRunner;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.refdb.jpa.dao.BrainJpaDao;
import ch.elca.el4j.tests.refdb.jpa.dao.FileJpaDao;
import ch.elca.el4j.tests.refdb.jpa.dao.LinkJpaDao;
import ch.elca.el4j.tests.refdb.jpa.dao.PersonJpaDao;
/**
 * 
 * Tests the functionality provided by {@link GenericJpaDao}.
 * 
 * Also serves as a running example of the <code>@ExtendedContextConfiguration</code>.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
@RunWith(EL4JJunit4ClassRunner.class)
@ExtendedContextConfiguration(exclusiveConfigLocations = {
		"classpath*:mandatory/refdb-core-config.xml",
		"classpath*:mandatory/keyword-core-config.xml" })
@ContextConfiguration(
	locations = {
		"classpath*:mandatory/*.xml",
		"classpath*:scenarios/db/raw/*.xml",
		"classpath*:scenarios/dataaccess/jpa/*.xml",
		"classpath*:optional/jpadao/*.xml",
		"classpath*:scenarios/dataaccess/jpa/refdb/*.xml"
		},
	loader = ModuleTestContextLoader.class)
@Transactional
public abstract class AbstractJpaDaoTest {

	/**
	 * JPA DAO for people.
	 */
	@Inject
	protected PersonJpaDao personJpaDao;
	
	/**
	 * JPA DAO for brain.
	 */
	@Inject
	protected BrainJpaDao brainJpaDao;
	
	/**
	 * JPA DAO for Link.
	 */
	@Inject
	protected LinkJpaDao linkJpaDao;
	
	/**
	 * JPA DAO for File.
	 */
	@Inject
	protected FileJpaDao fileJpaDao;
	
	/**
	 * The {@link EntityManagerFactory} used to retrieve a CriteriaBuilder.
	 */
	@Inject
	protected EntityManagerFactory entityManagerFactory;
	
	/**
	 * A proxy to the active EntityManager. We need this to explicitly detach
	 * entities.
	 */
	@PersistenceContext
	protected EntityManager entityManager;
	
	/**
	 * @return the personJpaDao
	 */
	protected PersonJpaDao getPersonDao() {
		return personJpaDao;
	}
	
	/**
	 * @return the brainJpaDao
	 */
	protected BrainJpaDao getBrainDao() {
		return brainJpaDao;
	}
	
	/**
	 * @return the LinkJpaDao
	 */
	protected LinkJpaDao getLinkDao() {
		return linkJpaDao;
	}
	
	/**
	 * @return the FileJpaDao
	 */
	protected FileJpaDao getFileDao() {
		return fileJpaDao;
	}
	
	/**
	 * @return a new Person
	 * @param name the name of the new person
	 */
	protected Person getNewPerson(String name) {
		Person p = new Person(name);
		Brain b = new Brain();
		b.setIq(99);
		p.setBrain(b);
		b.setOwner(p);
		
		return p;
	}
	
	/**
	 * Since other tests commit data to the DB, we have to wipe everything
	 * before we start our own tests. Note that the tests in this class
	 * are always rolled back.
	 */
	@Before
	public void wipeDB() {
		personJpaDao.deleteAll();
	}

}