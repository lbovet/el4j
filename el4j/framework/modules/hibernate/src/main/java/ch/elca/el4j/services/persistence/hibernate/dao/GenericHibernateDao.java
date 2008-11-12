/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class is a Hibernate-specific implementation of the
 * ConvenienceGenericDao interface.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateDao<T, ID extends Serializable>
	extends ConvenienceHibernateDaoSupport
	implements ConvenienceGenericHibernateDao<T, ID>, InitializingBean {
	
	/**
	 * The domain class this DAO is responsible for.
	 */
	private Class<T> m_persistentClass;
	
	/**
	 * The default hibernate {@link Order} to order results.
	 */
	private Order[] m_defaultOrder = null;
	
	/** 
	 * The least extent of the graph of objects in which 
	 * objects should be loaded.
	 */
	private DataExtent m_extent;
	
	/**
	 * Map indicating which objects have already been fetched.
	 */
	private Map<Object, WeakReference<ExtentEntity>> m_fetchedObjects 
		= new HashMap<Object, WeakReference<ExtentEntity>>();
	
	/**
	 * Set up the Generic Dao. Auto-derive the parametrized type.
	 */
	@SuppressWarnings("unchecked")
	public GenericHibernateDao() {
		try {
			this.m_persistentClass = (Class<T>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
		} catch (Exception e) {
			// ignore issues (e.g. when the subclass is not a parametrized type)
			// in that case, one needs to set the persistencClass otherwise.
		}
	}
	
	/**
	 * New: this callback is in general no longer required (the constructor
	 *  figures the type out itself).
	 *
	 * @param c
	 *           Mandatory. The domain class this DAO is responsible for.
	 */
	public void setPersistentClass(Class<T> c) {
		Reject.ifNull(c);
		m_persistentClass = c;
	}

	/**
	 * @return Returns the domain class this DAO is responsible for.
	 */
	public Class<T> getPersistentClass() {
		assert m_persistentClass != null;
		return m_persistentClass;
	}
	
	/** {@inheritDoc} */
	public Order[] getDefaultOrder() {
		return m_defaultOrder;
	}
	
	/** {@inheritDoc} */
	public void setDefaultOrder(Order... defaultOrder) {
		m_defaultOrder = defaultOrder;
	}

	/**
	 * Retrieves a domain object by identifier, optionally obtaining a database
	 * lock for this operation.  <br>
	 *
	 * (For hibernate specialists: we do a "get()"
	 * in this method. In case you require only a "load()" (e.g. for lazy
	 * loading to work) we recommend that you write your own find method in the
	 * interface's subclass.)
	 *
	 * @param id
	 *            The id of a domain object
	 * @param lock
	 *            Indicates whether a database lock should be obtained for this
	 *            operation
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be retrieved
	 * @return The desired domain object
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findById(ID id, boolean lock)
		throws DataAccessException, DataRetrievalFailureException {
		
		T entity;
		if (lock) {
			entity = (T) getConvenienceHibernateTemplate().get(getPersistentClass(), id, LockMode.UPGRADE);
		} else {
			entity = (T) getConvenienceHibernateTemplate().get(getPersistentClass(), id);
		}
		if (entity == null) {
			throw new DataRetrievalFailureException("The desired domain object could not be retrieved.");
		}
		return fetchExtent(entity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findById(ID id)
		throws DataAccessException, DataRetrievalFailureException {
		return fetchExtent((T) getConvenienceHibernateTemplate().getByIdStrong(
			getPersistentClass(), id, getPersistentClassName()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findByIdLazy(ID id)
		throws DataAccessException, DataRetrievalFailureException {
		return (T) getConvenienceHibernateTemplate().getByIdStrongLazy(
			getPersistentClass(), id, getPersistentClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getAll() throws DataAccessException {
		return fetchExtent(getConvenienceHibernateTemplate().findByCriteria(getOrderedCriteria()));
	}

	
	/**
	 * {@inheritDoc}
	 *
	 * This method supports paging (see QueryObject for info on
	 *  how to use this).
	 *
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(QueryObject q) throws DataAccessException {
		DetachedCriteria hibernateCriteria = getCriteria(q);
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return fetchExtent(template.findByCriteria(hibernateCriteria, q.getFirstResult(), q.getMaxResults()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * This method supports paging (see QueryObject for info on
	 *  how to use this).
	 *
	 * @return how many elements do we find with the given query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int findCountByQuery(QueryObject q) throws DataAccessException {
		DetachedCriteria hibernateCriteria = getCriteria(q);
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();

		return template.findCountByCriteria(hibernateCriteria);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException {
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return fetchExtent(template.findByCriteria(hibernateCriteria));
	}
	
	/** 
	 * Prototype of Extent-based fetching,
	 * steps through all the retrieved objects and calls
	 * the methods of the extent to ensure loading from db.
	 * 
	 * @param objects	list of objects to load in given extent
	 * @return returns the new list of objects.
	 * 
	 * @throws DataAccessException 
	 */
	protected List<T> fetchExtent(List<T> objects)
		throws DataAccessException {
		
		if (m_extent != null) {
			m_fetchedObjects.clear();
			for (Object obj : objects) {
				fetchExtentObject(obj, m_extent.getRootEntity());
			}
			m_fetchedObjects.clear();
		}
		return objects;
	}
	
	/** 
	 * Prototype of Extent-based fetching,
	 * steps through all the retrieved objects and calls
	 * the methods of the extent to ensure loading from db.
	 * 
	 * @param object	object to load in given extent
	 * @return returns the new object.
	 * 
	 * @throws DataAccessException 
	 */
	protected T fetchExtent(T object)
		throws DataAccessException {
		
		if (m_extent != null) {
			m_fetchedObjects.clear();
			fetchExtentObject(object, m_extent.getRootEntity());
			m_fetchedObjects.clear();
		}
		return object;
	}
	
	/**
	 * Sub-method of the extent-based fetching, steps
	 * through the entities and calls the required methods.
	 * @param object	the object to load in given extent
	 * @param entity	the extent entity
	 * 
	 * @throws DataAccessException
	 */
	private void fetchExtentObject(Object object, ExtentEntity entity)
		throws DataAccessException {
		
		Object[] nullArg = null;
		m_fetchedObjects.put(object, new WeakReference<ExtentEntity>(entity));
		
		try {
			// Fetch the base type fields
			/* Obsolete for the moment: properties not lazy
			for (String field : entity.getGetters()) {
				Method fieldGetter = entity.getEntityClass().getMethod(field, nullClass);
				fieldGetter.invoke(object, nullArg);
			}*/
			// Fetch the child entities
			for (ExtentEntity ent : entity.getChildEntities()) {
				Object obj = ent.getMethod().invoke(object, nullArg);
				// Call one method of the object to guarantee its existence
				obj.toString();
				if (!m_fetchedObjects.containsKey(obj) || !m_fetchedObjects.get(obj).get().equals(ent)) {
					fetchExtentObject(obj, ent);
				}
			}
			// Fetch the collections
			for (ExtentCollection c : entity.getCollections()) {
				Collection<?> coll = (Collection<?>) c.getMethod().invoke(object, nullArg);
				for (Object o : coll) {
					// Call one method of the object to guarantee its existence
					o.toString();
					if (!m_fetchedObjects.containsKey(o) 
						|| !m_fetchedObjects.get(o).get().equals(c.getContainedEntity())) {
						fetchExtentObject(o, c.getContainedEntity());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult, int maxResults)
		throws DataAccessException {
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return fetchExtent(template.findByCriteria(hibernateCriteria, firstResult, maxResults));
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int findCountByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException {
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();

		return template.findCountByCriteria(hibernateCriteria);
	}
	
	/** {@inheritDoc} */
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T saveOrUpdate(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		
		getConvenienceHibernateTemplate().saveOrUpdateStrong(entity, getPersistentClassName());
		return entity;
	}
	
	
	/** {@inheritDoc} */
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T saveOrUpdateAndFlush(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		
		T tmp = saveOrUpdate(entity);
		flush();
		return tmp;
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(T entity) throws DataAccessException {
		getConvenienceHibernateTemplate().delete(entity);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T refresh(T entity) throws DataAccessException,
	DataRetrievalFailureException {
		getConvenienceHibernateTemplate().refresh(entity);
		return fetchExtent(entity);
	}

	/** {@inheritDoc} */
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ID id) throws DataAccessException {
		getConvenienceHibernateTemplate().deleteStrong(getPersistentClass(),
			id, getPersistentClassName());
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteById(ID id) throws DataAccessException {
		getConvenienceHibernateTemplate().deleteStrong(getPersistentClass(),
			id, getPersistentClassName());
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Collection<T> entities) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {
		getConvenienceHibernateTemplate().deleteAll(entities);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll()
		throws OptimisticLockingFailureException, DataAccessException {
		List<T> list = getAll();
		if (list.size() > 0) {
			delete(list);
		}
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void flush() {
		getConvenienceHibernateTemplate().flush();
	}
	
	/** {@inheritDoc} */
	public DetachedCriteria getOrderedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		
		return addOrder(makeDistinct(criteria));
	}
	
	/** {@inheritDoc} */
	public DataExtent getExtent() {
		return m_extent;
	}
	
	/** {@inheritDoc} */
	public boolean setExtent(DataExtent extent) {
		if (extent == null) {
			m_extent = null;
			return true;
		} else {
			m_extent = extent;
			return true;
		}
	}
	
	/**
	 * Returns the simple name of the persistent class this DAO is responsible
	 * for.
	 *
	 * @return The simple name of the persistent class this DAO is responsible
	 *         for.
	 */
	protected String getPersistentClassName() {
		return getPersistentClass().getSimpleName();
	}
	
	/**
	 * @param queryObject    an EL4J {@link QueryObject} that should be converted to a {@link DetachedCriteria}
	 * @return               a suitable {@link DetachedCriteria}
	 */
	protected DetachedCriteria getCriteria(QueryObject queryObject) {
		DetachedCriteria criteria = CriteriaTransformer.transform(queryObject, getPersistentClass());
		
		if (queryObject.getOrderConstraints().size() == 0) {
			criteria = addOrder(criteria);
		}
		
		return makeDistinct(criteria);
	}
	
	
	/**
	 * @param criteria    the criteria to modify
	 * @return            the criteria enhanced with order constraints (if set using setDefaultOrder)
	 */
	protected DetachedCriteria addOrder(DetachedCriteria criteria) {
		if (m_defaultOrder != null) {
			for (Order order : m_defaultOrder) {
				criteria.addOrder(order);
			}
		}
		return criteria;
	}
	
	/**
	 * @param criteria    the criteria to modify
	 * @return            the criteria enhanced with distinct restrictions
	 */
	protected DetachedCriteria makeDistinct(DetachedCriteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		
		return criteria;
	}

}
