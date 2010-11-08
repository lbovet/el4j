/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.services.persistence.jpa.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;

import org.apache.commons.collections.map.ReferenceMap;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentCollection;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.services.persistence.jpa.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class is a JPA-specific implementation of the
 * ConvenienceGenericDao interface.
 *
 * @svnLink $Revision: 4173 $;$Date: 2010-09-20 15:55:50 +0200 (Mo, 20 Sep 2010) $;$Author: sstelca $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/GenericHibernateDao.java $
 *
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 *
 * @author Simon Stelling (SST)
 */
public class GenericJpaDao<T, ID extends Serializable>
	extends ConvenienceJpaDaoSupport
	implements ConvenienceGenericJpaDao<T, ID>, InitializingBean {
	
	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(GenericJpaDao.class);
	
	/**
	 * The domain class this DAO is responsible for.
	 */
	private Class<T> persistentClass;
	
	/**
	 * The default JPA {@link Order} to order results.
	 */
	private Order[] defaultOrder = null;
	
	/**
	 * Set up the Generic Dao. Auto-derive the parametrized type.
	 */
	@SuppressWarnings("unchecked")
	public GenericJpaDao() {
		try {
			this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
			// Checkstyle: EmptyBlock off
		} catch (Exception e) {
			// ignore issues (e.g. when the subclass is not a parametrized type)
			// in that case, one needs to set the persistencClass otherwise.
			// Checkstyle: EmptyBlock on
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
		persistentClass = c;
	}

	/**
	 * @return Returns the domain class this DAO is responsible for.
	 */
	public Class<T> getPersistentClass() {
		assert persistentClass != null;
		return persistentClass;
	}
	
	/** {@inheritDoc} */
	public Order[] getDefaultOrder() {
		return defaultOrder;
	}
	
	/** {@inheritDoc} */
	public void setDefaultOrder(Order... defaultOrder) {
		this.defaultOrder = defaultOrder;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findById(ID id)
		throws DataAccessException, DataRetrievalFailureException {
		return (T) getConvenienceJpaTemplate().findByIdStrong(
			getPersistentClass(), id, getPersistentClassName());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findByIdLazy(ID id)
		throws DataAccessException, DataRetrievalFailureException {
		return (T) getConvenienceJpaTemplate().findByIdStrongLazy(
			getPersistentClass(), id, getPersistentClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getAll() throws DataAccessException {
		return getConvenienceJpaTemplate().findByCriteria(getOrderedCriteria());
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * This method supports paging (see QueryObject for info on
	 *  how to use this).
	 *
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(QueryObject q) throws DataAccessException {
		CriteriaQuery<T> criteria = getCriteria(q);
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();
		
		return template.findByCriteria(criteria, q.getFirstResult(), q.getMaxResults());
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
		CriteriaQuery<T> criteria = getCriteria(q);
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();

		return template.findCountByCriteria(criteria);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByCriteria(CriteriaQuery<T> criteria)
		throws DataAccessException {
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();
		
		return template.findByCriteria(criteria);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByCriteria(CriteriaQuery<T> criteria, int firstResult, int maxResults)
		throws DataAccessException {
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();
		
		return template.findByCriteria(criteria, firstResult, maxResults);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int findCountByCriteria(CriteriaQuery<T> criteria)
		throws DataAccessException {
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();

		return template.findCountByCriteria(criteria);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T merge(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		
		return (T) getConvenienceJpaTemplate().mergeStrong(entity, getPersistentClassName());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T persist(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException {
		
		getConvenienceJpaTemplate().persist(entity);
		return entity;
	}
	
	/**
	 * Deprecated: Use merge instead.
	 * 
	 * Note: this method is NOT equivalent to Hibernate's <code>saveOrUpdate</code> but to
	 * <code>saveOrUpdateCopy</code>, i.e. you need to use the return value:
	 * 
	 * <pre>
	 * dom = dao.saveOrUpdate(dom);
	 * </pre>
	 */
	@Deprecated
	public T saveOrUpdate(T entity) {
		return merge(entity);
	}
	
	/** {@inheritDoc} */
	@Deprecated
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
		T e = entity;
		getConvenienceJpaTemplate().remove(e);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T refresh(T entity) throws DataAccessException,
	DataRetrievalFailureException {
		T e = entity;
		getConvenienceJpaTemplate().refresh(e);
		return e;
	}
		
	/** {@inheritDoc} */
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ID id) throws DataAccessException {
		getConvenienceJpaTemplate().removeStrong(getPersistentClass(),
			id, getPersistentClassName());
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteById(ID id) throws DataAccessException {
		getConvenienceJpaTemplate().removeStrong(getPersistentClass(),
			id, getPersistentClassName());
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Collection<T> entities) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {
		getConvenienceJpaTemplate().removeAll(entities);
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
		getConvenienceJpaTemplate().flush();
	}
	
	/** {@inheritDoc} */
	public CriteriaQuery<T> getOrderedCriteria() {
		CriteriaQuery<T> criteria 
			= getJpaTemplate().execute(new JpaCallback<CriteriaQuery<T>>() {

				@Override
				public CriteriaQuery<T> doInJpa(EntityManager em) throws PersistenceException {
					CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(persistentClass);
					criteria.from(persistentClass);
					return criteria;
				}
			});
		
		return addOrder(makeDistinct(criteria));
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
	 * @return               a suitable {@link CriteriaQuery}
	 */
	protected CriteriaQuery<T> getCriteria(QueryObject queryObject) {
		
		CriteriaTransformer<T> ct = new CriteriaTransformer<T>(getPersistentClass(), 
				getConvenienceJpaTemplate().getEntityManagerFactory().getCriteriaBuilder());
		CriteriaQuery<T> criteria = ct.transform(queryObject);
		
		if (queryObject.getOrderConstraints().size() == 0) {
			criteria = addOrder(criteria);
		}
		
		return makeDistinct(criteria);
	}
	
	
	/**
	 * @param criteria    the criteria to modify
	 * @return            the criteria enhanced with order constraints (if set using setDefaultOrder)
	 */
	protected CriteriaQuery<T> addOrder(CriteriaQuery<T> criteria) {
		if (defaultOrder != null) {
			criteria.orderBy(defaultOrder);
		}
		return criteria;
	}
	
	/**
	 * @param criteria    the criteria to modify
	 * @return            the criteria enhanced with distinct restrictions
	 */
	protected CriteriaQuery<T> makeDistinct(CriteriaQuery<T> criteria) {
		return criteria.distinct(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T reload(T entity) throws DataAccessException, DataRetrievalFailureException {
		PersistenceUnitUtil util = getConvenienceJpaTemplate().getEntityManagerFactory().getPersistenceUnitUtil();
		return findById((ID) util.getIdentifier(entity));
	}

	// extent-using methods:
	
	/** 
	 * Prototype of Extent-based fetching,
	 * steps through all the retrieved objects and calls
	 * the methods of the extent to ensure loading from db.
	 * 
	 * @param objects	list of objects to load in given extent
	 * @param extent	the fetch-extent
	 * @return returns the new list of objects.
	 * 
	 * @throws DataAccessException 
	 */
	protected List<T> fetchExtent(List<T> objects, DataExtent extent)
		throws DataAccessException {
		
		if (extent != null) {
			ReferenceMap fetchedObjects = new ReferenceMap();
			for (Object obj : objects) {
				fetchExtentObject(obj, extent.getRootEntity(), fetchedObjects);
			}
		}
		return objects;
	}
	
	/** 
	 * Prototype of Extent-based fetching,
	 * steps through all the retrieved objects and calls
	 * the methods of the extent to ensure loading from db.
	 * 
	 * @param object	object to load in given extent
	 * @param extent	the fetch-extent
	 * @return returns the new object.
	 * 
	 * @throws DataAccessException 
	 */
	protected T fetchExtent(T object, DataExtent extent)
		throws DataAccessException {
		
		if (extent != null) {
			ReferenceMap fetchedObjects = new ReferenceMap();
			fetchExtentObject(object, extent.getRootEntity(), fetchedObjects);
		}
		return object;
	}
	
	/**
	 * Sub-method of the extent-based fetching, steps
	 * through the entities and calls the required methods.
	 * <p>
	 * <strong>Note:</strong> This implementation assumes that the underlying JPA implementation
	 * is Hibernate. If another implementation is used, and that implemenation returns proxy
	 * objects when a entity is requested, less than the requested extent might be loaded and
	 * LazyInitializationExceptions may occur.
	 * 
	 * @param object			the object to load in given extent
	 * @param entity			the extent entity
	 * @param fetchedObjects	the HashMap with all the already fetched objects
	 * 
	 * @throws DataAccessException
	 */
	private void fetchExtentObject(Object object, ExtentEntity entity, ReferenceMap fetchedObjects)
		throws DataAccessException {
		
		Object[] nullArg = null;
		if (object == null || entity == null || fetchedObjects == null) {
			return;
		}
		fetchedObjects.put(object, entity);
		try {
			for (ExtentEntity ent : entity.getChildEntities()) {
				Object obj = ent.getMethod().invoke(object, nullArg);
				// Initialize the object if it is a proxy
				if (obj instanceof HibernateProxy && !Hibernate.isInitialized(obj)) {
					Hibernate.initialize(obj);
				}
				if (!fetchedObjects.containsKey(obj) || !fetchedObjects.get(obj).equals(ent)) {
					fetchExtentObject(obj, ent, fetchedObjects);
				}
			}
			
			// Fetch the collections. Since we assume batch fetching for collections
			for (ExtentCollection c : entity.getCollections()) {
				Collection<?> coll = (Collection<?>) c.getMethod().invoke(object, nullArg);
				if (coll != null) {
					for (Object o : coll) {
						// Initialize the object if it is a proxy
						if (o instanceof HibernateProxy && !Hibernate.isInitialized(o)) {
							Hibernate.initialize(o);
						}
						if (!fetchedObjects.containsKey(o) || !fetchedObjects.get(o).equals(c.getContainedEntity())) {
							fetchExtentObject(o, c.getContainedEntity(), fetchedObjects);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByCriteria(CriteriaQuery<T> criteria, DataExtent extent) throws DataAccessException {
		return fetchExtent(getConvenienceJpaTemplate().findByCriteria(criteria), extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByCriteria(CriteriaQuery<T> criteria, int firstResult, int maxResults, DataExtent extent)
		throws DataAccessException {
		return fetchExtent(getConvenienceJpaTemplate().findByCriteria(criteria, firstResult, maxResults), extent);
	}

	/** {@inheritDoc} */
	@Override
	public T findById(ID id, DataExtent extent) throws DataRetrievalFailureException, DataAccessException {
		return fetchExtent((T) getConvenienceJpaTemplate().findByIdStrong(
			getPersistentClass(), id, getPersistentClassName()), extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryObject q, DataExtent extent) throws DataAccessException {
		CriteriaQuery<T> criteria = getCriteria(q);
		
		return fetchExtent(getConvenienceJpaTemplate().findByCriteria(criteria, q.getFirstResult(), 
			q.getMaxResults()), extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> getAll(DataExtent extent) throws DataAccessException {
		return fetchExtent(getConvenienceJpaTemplate().findByCriteria(getOrderedCriteria()), extent);
	}

	/** {@inheritDoc} */
	@Override
	public T refresh(T entity, DataExtent extent) throws DataAccessException, DataRetrievalFailureException {
		getConvenienceJpaTemplate().refresh(entity);
		return fetchExtent(entity, extent);
	}

	/** {@inheritDoc} */
	@Override
	public T reload(T entity, DataExtent extent) throws DataAccessException, DataRetrievalFailureException {
		return fetchExtent(reload(entity), extent);
	}

}
