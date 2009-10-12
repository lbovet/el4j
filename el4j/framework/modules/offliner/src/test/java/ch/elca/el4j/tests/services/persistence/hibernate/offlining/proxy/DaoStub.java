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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceHibernateTemplate;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.tests.services.persistence.hibernate.offlining.notifications.Notification;
import ch.elca.el4j.tests.services.persistence.hibernate.offlining.notifications.NotificationProcessor;


/**
 * Simulate a dao.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class DaoStub implements ConvenienceGenericHibernateDao<Object, Long>, FooDeleter {

	/** Callback for notifications. */
	private NotificationProcessor m_processor;
	
	/**
	 * Default constructor.
	 * @param processor The callback.
	 */
	public DaoStub(NotificationProcessor processor) {
		m_processor = processor;
	}
	
	/**
	 * CGLIB only.
	 */
	@SuppressWarnings("unused")
	protected DaoStub() {	}

	/**
	 * Unaccounted for delete method - must cause an exception in proxy.
	 */
	public void deleteFoo() { }
	
	/*
	 * Dao methods.
	 */
	
	/** {@inheritDoc} */
	public List<Object> findByCriteria(DetachedCriteria hibernateCriteria,
		int firstResult, int maxResults) throws DataAccessException {
		return null;
	}
	
	/** {@inheritDoc} */
	public List<Object> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult,
		int maxResults, DataExtent extent) throws DataAccessException {
		return null;
	}

	/** {@inheritDoc} */
	public List<Object> findByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException {
		return null;
	}
	
	/** {@inheritDoc} */
	public List<Object> findByCriteria(DetachedCriteria hibernateCriteria,
		DataExtent extent) throws DataAccessException {
		return null;
	}

	/** {@inheritDoc} */
	public int findCountByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException {
		return 0;
	}

	/** {@inheritDoc} */
	public ConvenienceHibernateTemplate getConvenienceHibernateTemplate() {
		return null;
	}

	/** {@inheritDoc} */
	public Order[] getDefaultOrder() {
		return null;
	}

	/** {@inheritDoc} */
	public DetachedCriteria getOrderedCriteria() {
		return null;
	}

	/** {@inheritDoc} */
	public Object saveOrUpdateAndFlush(Object entity)
		throws DataAccessException, DataIntegrityViolationException,
		OptimisticLockingFailureException {
		return null;
	}

	/** {@inheritDoc} */
	public void setDefaultOrder(Order... defaultOrder) { }

	/** {@inheritDoc} */
	@Deprecated
	public void delete(Long id) throws OptimisticLockingFailureException,
		DataAccessException {
		m_processor.call(new Notification("deprecatedDelete", id));
	}

	/** {@inheritDoc} */
	public void delete(Object entity) throws OptimisticLockingFailureException,
		DataAccessException {
		m_processor.call(new Notification("delete", entity));
		if ("EXCEPTION".equals(entity)) {
			throw new DataAccessException("Testing exception.", null) { };
		}
	}

	/** {@inheritDoc} */
	public void deleteAll() throws OptimisticLockingFailureException,
		DataAccessException {
		m_processor.call(new Notification("deleteAll"));
	}

	/** {@inheritDoc} */
	public void deleteById(Long id) throws OptimisticLockingFailureException,
		DataAccessException {
		m_processor.call(new Notification("deleteById", id));
	}

	/** {@inheritDoc} */
	public Object findById(Long id) throws DataRetrievalFailureException,
		DataAccessException {
		m_processor.call(new Notification("findById", id));
		return "Object " + id.toString();
	}
	
	/** {@inheritDoc} */
	public Object findById(Long id, DataExtent extent) throws DataRetrievalFailureException,
		DataAccessException {
		m_processor.call(new Notification("findById", id));
		return "Object " + id.toString();
	}
	
	/** {@inheritDoc} */
	public Object findById(Long id, boolean lock, DataExtent extent)
		throws DataRetrievalFailureException, DataAccessException {
		m_processor.call(new Notification("findById", id));
		return "Object " + id.toString();
	}

	/** {@inheritDoc} */
	public Object findByIdLazy(Long id) throws DataRetrievalFailureException,
		DataAccessException {
		return null;
	}

	/** {@inheritDoc} */
	public void flush() { }

	/** {@inheritDoc} */
	public List<Object> getAll() throws DataAccessException {
		m_processor.call(new Notification("getAll"));
		List<Object> data = new ArrayList<Object>();
		String[] strings = {"first", "second", "third"};
		for (String string : strings) {
			data.add((Object) string); 
		}
		return data;
	}
	
	/** {@inheritDoc} */
	public List<Object> getAll(DataExtent extent) throws DataAccessException {
		m_processor.call(new Notification("getAll"));
		List<Object> data = new ArrayList<Object>();
		String[] strings = {"first", "second", "third"};
		for (String string : strings) {
			data.add((Object) string); 
		}
		return data;
	}

	/** {@inheritDoc} */
	public void delete(Collection<Object> entities)
		throws OptimisticLockingFailureException, DataAccessException {
		m_processor.call(new Notification("deleteCollection", entities));
	}

	/** {@inheritDoc} */
	public List<Object> findByQuery(QueryObject q) throws DataAccessException {
		return null;
	}
	
	/** {@inheritDoc} */
	public List<Object> findByQuery(QueryObject q, DataExtent extent) 
		throws DataAccessException {
		return null;
	}

	/** {@inheritDoc} */
	public int findCountByQuery(QueryObject query) throws DataAccessException {
		return 0;
	}

	/** {@inheritDoc} */
	public Class<Object> getPersistentClass() {
		return null;
	}

	/** {@inheritDoc} */
	public Object refresh(Object entity) throws DataAccessException,
		DataRetrievalFailureException {
		return null;
	}
	
	/** {@inheritDoc} */
	public Object refresh(Object entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException {
		return null;
	}

	/** {@inheritDoc} */
	public Object saveOrUpdate(Object entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		return null;
	}

	/** {@inheritDoc} */
	public void setPersistentClass(Class<Object> c) { }

	/** {@inheritDoc} */
	public DataExtent getExtent() {
		return null;
	}
	
	/** {@inheritDoc} */
	public boolean setExtent(DataExtent extent) {
		return false;
	}

	/** {@inheritDoc} */
	public Object reload(Object entity, DataExtent extent) throws DataAccessException, DataRetrievalFailureException {
		return null;
	}

	/** {@inheritDoc} */
	public Object reload(Object entity) throws DataAccessException, DataRetrievalFailureException {
		return null;
	}
}