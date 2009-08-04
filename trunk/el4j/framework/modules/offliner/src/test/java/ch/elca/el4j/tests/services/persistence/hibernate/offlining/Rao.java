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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceHibernateTemplate;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.search.QueryObject;

/**
 * Dao proxy to simulate remoting.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 * @param <T> The entity type.
 * @param <ID> The key type. 
 */
public class Rao<T, ID extends Serializable>
	implements ConvenienceGenericHibernateDao<T, ID> {

	/** The dao we proxy. */
	private ConvenienceGenericHibernateDao<T, ID> m_dao;
	
	/**
	 * Empty Constructor. Users MUST set m_dao with setter. 
	 */
	public Rao() { }
	
	/**
	 * Constructor taking a DAO.
	 * @param dao The dao to wrap.
	 */
	public Rao(ConvenienceGenericHibernateDao<T, ID> dao) {
		m_dao = dao;
	}
	
	/** 
	 * Set the proxy target. This is initialized by spring injection.
	 * @param dao The target dao.
	 */
	public void setTarget(ConvenienceGenericHibernateDao<T, ID> dao) {
		m_dao = dao;
	}

	/** {@inheritDoc} */
	public void delete(Collection<T> entities)
		throws OptimisticLockingFailureException, DataAccessException {
		m_dao.delete(entities);
	}
	
	/** {@inheritDoc} */
	@Deprecated
	public void delete(ID id) throws OptimisticLockingFailureException,
		DataAccessException {
		m_dao.delete(id);
	}

	/** {@inheritDoc} */
	public void deleteById(ID id) throws OptimisticLockingFailureException,
		DataAccessException {
		m_dao.deleteById(id);
	}
	
	/** {@inheritDoc} */
	public void delete(T entity) throws OptimisticLockingFailureException,
		DataAccessException {
		m_dao.delete(entity);
	}

	/** {@inheritDoc} */
	public void deleteAll() throws OptimisticLockingFailureException,
		DataAccessException {
		m_dao.deleteAll();
	}

	/** {@inheritDoc} */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria,
		int firstResult, int maxResults) throws DataAccessException {
		return m_dao.findByCriteria(hibernateCriteria, firstResult, maxResults);
	}
	
	/** {@inheritDoc} */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult,
		int maxResults, DataExtent extent) throws DataAccessException {
		return m_dao.findByCriteria(hibernateCriteria, firstResult, maxResults, extent);
	}

	/** {@inheritDoc} */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException {
		return m_dao.findByCriteria(hibernateCriteria);
	}
	
	/** {@inheritDoc} */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, 
		DataExtent extent) throws DataAccessException {
		return m_dao.findByCriteria(hibernateCriteria, extent);
	}

	/** {@inheritDoc} */
	public T findById(ID id) throws DataRetrievalFailureException,
		DataAccessException {
		return m_dao.findById(id);
	}
	
	/** {@inheritDoc} */
	public T findById(ID id, boolean lock, DataExtent extent) 
		throws DataRetrievalFailureException, DataAccessException {
		return m_dao.findById(id, lock, extent);
	}
	
	/** {@inheritDoc} */
	public T findById(ID id, DataExtent extent) throws DataRetrievalFailureException,
		DataAccessException {
		return m_dao.findById(id, extent);
	}

	/** {@inheritDoc} */
	public T findByIdLazy(ID id) throws DataRetrievalFailureException,
		DataAccessException {
		return m_dao.findByIdLazy(id);
	}

	/** {@inheritDoc} */
	public List<T> findByQuery(QueryObject q) throws DataAccessException {
		return m_dao.findByQuery(q);
	}
	
	/** {@inheritDoc} */
	public List<T> findByQuery(QueryObject q, DataExtent extent) throws DataAccessException {
		return m_dao.findByQuery(q, extent);
	}

	/** {@inheritDoc} */
	public int findCountByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException {
		return m_dao.findCountByCriteria(hibernateCriteria);
	}

	/** {@inheritDoc} */
	public int findCountByQuery(QueryObject query) throws DataAccessException {
		return m_dao.findCountByQuery(query);
	}

	/** {@inheritDoc} */
	public void flush() {
		m_dao.flush();
	}

	/** {@inheritDoc} */
	public List<T> getAll() throws DataAccessException {
		return m_dao.getAll();
	}
	
	/** {@inheritDoc} */
	public List<T> getAll(DataExtent extent) throws DataAccessException {
		return m_dao.getAll(extent);
	}

	/** {@inheritDoc} */
	public ConvenienceHibernateTemplate getConvenienceHibernateTemplate() {
		return m_dao.getConvenienceHibernateTemplate();
	}

	/** {@inheritDoc} */
	public Order[] getDefaultOrder() {
		return m_dao.getDefaultOrder();
	}

	/** {@inheritDoc} */
	public Class<T> getPersistentClass() {
		return m_dao.getPersistentClass();
	}

	/** {@inheritDoc} */
	@ReturnsUnchangedParameter
	public T refresh(T entity) throws DataAccessException,
		DataRetrievalFailureException {
		return m_dao.refresh(entity);
	}
	
	/** {@inheritDoc} */
	@ReturnsUnchangedParameter
	public T refresh(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException {
		return m_dao.refresh(entity, extent);
	}

	/** {@inheritDoc} */
	@ReturnsUnchangedParameter
	public T saveOrUpdate(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		return m_dao.saveOrUpdate(entity);
	}

	/** {@inheritDoc} */
	@ReturnsUnchangedParameter
	public T saveOrUpdateAndFlush(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		return m_dao.saveOrUpdateAndFlush(entity);
	}

	/** {@inheritDoc} */
	public void setDefaultOrder(Order... defaultOrder) {
		m_dao.setDefaultOrder(defaultOrder);
	}

	/** {@inheritDoc} */
	public void setDefaultOrder(Order defaultOrder) {
		m_dao.setDefaultOrder(defaultOrder);
	}

	/** {@inheritDoc} */
	public void setPersistentClass(Class<T> c) {
		m_dao.setPersistentClass(c);
	}

	/** {@inheritDoc} */
	public DetachedCriteria getOrderedCriteria() {
		return m_dao.getOrderedCriteria();
	}

	/** {@inheritDoc} */
	public T reload(T entity, DataExtent extent) throws DataAccessException, DataRetrievalFailureException {
		return m_dao.reload(entity, extent);
	}

	/** {@inheritDoc} */
	public T reload(T entity) throws DataAccessException, DataRetrievalFailureException {
		return m_dao.reload(entity);
	}
}
