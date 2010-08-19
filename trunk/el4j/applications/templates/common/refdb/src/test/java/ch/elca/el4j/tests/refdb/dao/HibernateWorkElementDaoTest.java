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
package ch.elca.el4j.tests.refdb.dao;

import static org.junit.Assert.assertTrue;

import org.hibernate.validator.InvalidStateException;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import ch.elca.el4j.apps.refdb.dao.WorkElementDao;
import ch.elca.el4j.apps.refdb.dom.WorkElement;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;


/**
 * 
 * This class is a simple class to check the implementation of the HibernateWorkElementDao.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */

public class HibernateWorkElementDaoTest extends AbstractTestCaseBase {
	
	
	/**
	 * Field to store the used workElementDao.
	 */
	private WorkElementDao m_workElementDao;
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getIncludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:mandatory/refdb/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/*.xml",
			"classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
			"classpath*:optional/refdb/trace-interceptor-config.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml"};
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getExcludeConfigLocations() {
		return null;
	}
	
	/**
	 * Saves a newly created WorkElement to the db and retrieves it.
	 */
	
	@Test
	public void saveAndFindWorkElement() {
		WorkElement work = new WorkElement();
		// Set date to 11.5.2009
		work.setDay(new LocalDate(2009, 11, 5));
		// set start time to 4 am
		work.setFrom(new LocalTime(4, 0, 0));
		// set end time to 6 am
		work.setTo(new LocalTime(6, 0 , 0));
		WorkElementDao dao = getWorkElementDao();
		dao.saveOrUpdate(work);
		assertTrue(dao.getAll().contains(work));
	}
	
	/**
	 * Saves a newly created WorkElement to the db, deletes it from the db and makes sure that its gone.
	 */
	
	@Test
	public void saveAndDeleteWorkElement() {
		WorkElementDao dao = getWorkElementDao();
		dao.deleteAll();
		WorkElement work = new WorkElement();
		// Set date to 11.5.2009
		work.setDay(new LocalDate(2009, 11, 5));
		// set start time to 4 am
		work.setFrom(new LocalTime(4, 0, 0));
		// set end time to 6 am
		work.setTo(new LocalTime(6, 0 , 0));
		dao.saveOrUpdate(work);
		assertTrue(dao.getAll().contains(work));
		dao.delete(work);
		assertTrue(dao.getAll().isEmpty());
	}
	
	
	/**
	 * Makes sure that an exception is thrown when trying to 
	 * persist a WorkElement for which the start of the work is 
	 * after it has finished.
	 */
	@Test (expected = InvalidStateException.class)
	public void testStartBeforeFinishValidator() {
		WorkElementDao dao = getWorkElementDao();
		WorkElement work = new WorkElement();
		// Set date to 11.5.2009
		work.setDay(new LocalDate(2009, 11, 5));
		// set start time to 6 am
		work.setFrom(new LocalTime(6, 0, 0));
		// set end time to 4 am, uuups, this is an invalid state
		work.setTo(new LocalTime(4, 0 , 0));
		dao.saveOrUpdate(work);
		
	}
	
	/**
	 * Makes sure that an exception is thrown when a WorkElement is persisted
	 * for which the date isn't set.
	 * 
	 */
	@Test (expected = DataIntegrityViolationException.class)
	public void testNotNullConstraint() {
		WorkElementDao dao = getWorkElementDao();
		WorkElement work = new WorkElement();
		// make sure the day is set to null
		work.setDay(null);
		// set start time to 4 am
		work.setFrom(new LocalTime(4, 0, 0));
		// set end time to 6 am
		work.setTo(new LocalTime(6, 0 , 0));
		dao.saveOrUpdate(work);
	}
	

	/**
	 * Simple helper method to get hold of a WorkElementDao from the ApplicationContext.
	 * 
	 * @return a WorkElementDao
	 */
	
	private WorkElementDao getWorkElementDao() {
		// get the dao
		if (m_workElementDao != null) {
			return m_workElementDao;
		}
		
		m_workElementDao = (WorkElementDao) getApplicationContext().getBean("workElementDao");
		return m_workElementDao;
	}
}
