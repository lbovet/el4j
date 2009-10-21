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
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;


/**
 * 
 * This class is a simple class to check the implementation of the HibernateWorkElementDao.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Daniel Thomas (DTH)
 */

public class HibernateWorkElementDaoTest extends AbstractTestCaseBase{
	
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
		work.setDay(new LocalDate());
		work.setFrom(new LocalTime());
		work.setTo(new LocalTime().plusHours(2));
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
		// current Date
		work.setDay(new LocalDate());
		// current time
		work.setFrom(new LocalTime());
		// current time + 2h
		work.setTo(new LocalTime().plusHours(2));
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
	@Test (expected=InvalidStateException.class)
	public void testStartBeforeFinishValidator () {
		WorkElementDao dao = getWorkElementDao();
		WorkElement work = new WorkElement();
		work.setDay(new LocalDate());
		work.setFrom(new LocalTime(12, 10, 0));
		work.setTo(new LocalTime(8, 10, 0));
		
		dao.saveOrUpdate(work);
		
	}
	
	/**
	 * Makes sure that an exception is thrown when a WorkElement is persisted
	 * for which the date isn't set.
	 * 
	 */
	@Test (expected=DataIntegrityViolationException.class)
	public void testNotNullConstraint() {
		WorkElementDao dao = getWorkElementDao();
		WorkElement work = new WorkElement();
		work.setDay(null);
		work.setFrom(new LocalTime().plusHours(10));
		work.setTo(new LocalTime());
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
		
		DefaultDaoRegistry daoRegistry
		= (DefaultDaoRegistry) getApplicationContext()
			.getBean("daoRegistry");
		WorkElementDao workDao = (WorkElementDao) daoRegistry.getFor(WorkElement.class);
		return workDao;
	}
}
