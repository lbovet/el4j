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
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.commons.collections.map.ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.PersistenceNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;
import ch.elca.el4j.services.persistence.jpa.dao.extentstrategies.ExtentFetcher;
import ch.elca.el4j.services.persistence.jpa.dao.extentstrategies.JpaHibernateExtentFetcher;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class is a JPA-specific implementation of the
 * ConvenienceGenericRepository interface.
 * 
 * Note: This class does not use the JpaTemplate. In order to remove supplementary layers, it was decided to not use 
 * ConvenienceJpaTemplate and JpaTemplate.
 * 
 * Note: At the moment, we use an ExtentFetcher that only works with Hibernate. 
 * (Maybe we later allow the ExtentFetcher to be injected.)
 * 
 * @svnLink $Revision: 4253 $;$Date: 2010-12-21 11:08:04 +0100 (Di, 21 Dez 2010)
 *          $;$Author: swismer $;$URL:
 *          https://el4j.svn.sourceforge.net/svnroot/el4j
 *          /trunk/el4j/framework/modules
 *          /hibernate/src/main/java/ch/elca/el4j/services
 *          /persistence/jpa/dao/GenericJpaDao.java $
 * 
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 * 
 * @author Simon Stelling (SST), Philipp Oser (POS)
 */
public class GenericJpaRepositoryImpl<T, ID extends Serializable> implements
		GenericJpaRepository<T, ID>, InitializingBean {

	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory
			.getLogger(GenericJpaRepository.class);

	/**
	 * The entity manager.
	 */
	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * The domain class this DAO is responsible for.
	 */
	private Class<T> persistentClass;

	/**
	 * The ExtentFetcher used to fetch extents. 
	 * Currently it only works with Hibernate (It could be injected 
	 * by JpaExtentFetcherInjectorBeanPostprocessor as well).
	 */
	private ExtentFetcher extentFetcher = new JpaHibernateExtentFetcher();

	/**
	 * Set up the Generic Dao. Auto-derive the parametrized type.
	 */
	@SuppressWarnings("unchecked")
	public GenericJpaRepositoryImpl() {
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
	 * figures the type out itself).
	 * 
	 * @param c
	 *            Mandatory. The domain class this DAO is responsible for.
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

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findById(ID id) throws DataAccessException,
			DataRetrievalFailureException {
		return (T) findByIdStrong(
				getPersistentClass(), id, getPersistentClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findByIdLazy(ID id) throws DataAccessException,
			DataRetrievalFailureException {
		return (T) findByIdStrongLazy(
				getPersistentClass(), id, getPersistentClassName());
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(final QueryBuilder criteria)
		throws DataAccessException {
		return criteria.applySelect(entityManager).getResultList(persistentClass);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(final QueryBuilder criteria,
			final int firstResult, final int maxResults)
		throws DataAccessException {

		return criteria.applySelect(entityManager)
				.getResultList(persistentClass, firstResult, maxResults);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int findCountByQuery(final QueryBuilder criteria)
		throws DataAccessException {

		return criteria.applyCount(entityManager).getCount();

	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T merge(T entity) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {

		return (T) mergeStrong(entity, getPersistentClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T persist(T entity) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {
		entityManager.persist(entity);
		return entity;
	}


	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(T entity) throws DataAccessException {
		T e = entityManager.merge(entity);
		entityManager.remove(e);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T refresh(T entity) throws DataAccessException,
			DataRetrievalFailureException {
		entityManager.refresh(entity);
		return entity;
	}

	/** {@inheritDoc} */
	@Deprecated
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ID id) throws DataAccessException {
		removeStrong(getPersistentClass(), id,
				getPersistentClassName());
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteById(ID id) throws DataAccessException {
		removeStrong(getPersistentClass(), id,
				getPersistentClassName());
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Collection<T> entities) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {
		removeAll(entities);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll() throws OptimisticLockingFailureException,
			DataAccessException {
		List<T> list = getAll();
		if (list.size() > 0) {
			delete(list);
		}
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void flush() {
		flush();
	}

	
	/** {@inheritDoc} */
	public CriteriaQuery<T> getOrderedCriteria() {
		CriteriaQuery<T> criteria = entityManager.getCriteriaBuilder()
				.createQuery(persistentClass);
		criteria.from(persistentClass);

		return makeDistinct(criteria);
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
	 * @param criteria
	 *            the criteria to modify
	 * @return the criteria enhanced with distinct restrictions
	 */
	protected CriteriaQuery<T> makeDistinct(CriteriaQuery<T> criteria) {
		return criteria.distinct(true);
	}

	// extent-related methods:

	/**
	 * @return Returns the extentFetcher.
	 */
	public ExtentFetcher getExtentFetcher() {
		return extentFetcher;
	}

	/**
	 * @param extentFetcher
	 *            Is the extentFetcher to set.
	 */
	public void setExtentFetcher(ExtentFetcher extentFetcher) {
		this.extentFetcher = extentFetcher;
	}

	/**
	 * Prototype of Extent-based fetching, steps through all the retrieved
	 * objects and calls the methods of the extent to ensure loading from db.
	 * 
	 * @param objects
	 *            list of objects to load in given extent
	 * @param extent
	 *            the fetch-extent
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
	 * Prototype of Extent-based fetching, steps through all the retrieved
	 * objects and calls the methods of the extent to ensure loading from db.
	 * 
	 * @param object
	 *            object to load in given extent
	 * @param extent
	 *            the fetch-extent
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
	 * Sub-method of the extent-based fetching, steps through the entities and
	 * calls the required methods.
	 * <p>
	 * 
	 * @param object
	 *            the object to load in given extent
	 * @param entity
	 *            the extent entity
	 * @param fetchedObjects
	 *            the HashMap with all the already fetched objects
	 * 
	 * @throws DataAccessException
	 */
	private void fetchExtentObject(Object object, ExtentEntity entity,
			ReferenceMap fetchedObjects) throws DataAccessException {
		s_logger.debug("using extent-fetcher " + extentFetcher.getClass());
		extentFetcher.fetchExtentObject(object, entity, fetchedObjects);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryBuilder criteria, DataExtent extent)
		throws DataAccessException {
		return fetchExtent(findByQuery(criteria), extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryBuilder criteria, int firstResult,
			int maxResults, DataExtent extent) throws DataAccessException {
		return fetchExtent(findByQuery(criteria, firstResult, maxResults),
				extent);
	}

	/** {@inheritDoc} */
	@Override
	public T findById(ID id, DataExtent extent)
		throws DataRetrievalFailureException, DataAccessException {
		return fetchExtent(
				(T) findByIdStrong(
						getPersistentClass(), id, getPersistentClassName()),
				extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> getAll(DataExtent extent) throws DataAccessException {
		return fetchExtent(
				findByCriteria(getOrderedCriteria()), 
				extent);
	}

	/** {@inheritDoc} */
	@Override
	public T refresh(T entity, DataExtent extent) throws DataAccessException,
			DataRetrievalFailureException {
		entityManager.refresh(entity);
		return fetchExtent(entity, extent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getByName(String name) throws DataAccessException,
		DataRetrievalFailureException {
		Reject.ifEmpty(name);
		
		QueryBuilder criteria = QueryBuilder.select("obj")
				.from(getPersistentClass() + " obj")
				.startAnd()
				.ifNotNull("name LIKE {p}", name)
				.end()
				.endBuilder();
				
		return findByQuery(criteria);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED/*propagation = Propagation.SUPPORTS, readOnly = true*/)
	public List<T> getByName(String name, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException {
		Reject.ifEmpty(name);
		
		QueryBuilder criteria = QueryBuilder.select("obj")
				.from(getPersistentClassName() + " obj")
				.startAnd()
				.ifNotNull("name = {p}", name)
				.end()
				.endBuilder();
		
		List<T> result = findByQuery(criteria);
		
		return fetchExtent(result, extent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getAll() throws DataAccessException {
		return findByCriteria(getOrderedCriteria());
	}
	
	//methods from the convenience template
	/**
	 * Retrieves the persistent instance given by its identifier in a strong
	 * way: does the same as the <code>find(Class, java.io.Serializable)</code>
	 * method, but throws a <code>DataRetrievalException</code> instead of
	 * <code>null</code> if the persistent instance could not be found.
	 *
	 * @param <T> entity type
	 * @param entityClass
	 *            The class of the object which should be returned.
	 * @param id
	 *            An identifier of the persistent instance
	 * @param objectName
	 *            Name of the persistent object type.
	 * @return the persistent instance
	 * @throws org.springframework.dao.DataAccessException
	 *             in case of Jpa persistence exceptions
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance is null
	 */
	public <T> T findByIdStrong(Class<T> entityClass, Serializable id, final String objectName)
		throws DataAccessException, DataRetrievalFailureException {

		Reject.ifNull(id, "The identifier must not be null.");
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		
		T result = entityManager.find(entityClass, id);
		
		if (result == null || !(entityClass.isInstance(result))) {
			PersistenceNotificationHelper.notifyObjectRetrievalFailure(entityClass, id, objectName);
		}
		return result;
	}
	
	/**
	 * Retrieves the persistent instance given by its identifier in a strong
	 * way: does the same as the <code>getReference(Class, java.io.Serializable)</code>
	 * method, but throws a <code>DataRetrievalException</code> instead of
	 * <code>null</code> if the persistent instance could not be found.
	 *
	 * @param <T> entity type
	 * @param entityClass
	 *            The class of the object which should be returned.
	 * @param id
	 *            An identifier of the persistent instance
	 * @param objectName
	 *            Name of the persistent object type.
	 * @return the persistent instance
	 * @throws org.springframework.dao.DataAccessException
	 *             in case of Jpa persistence exceptions
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance is null
	 */
	public <T> T findByIdStrongLazy(Class<T> entityClass, Serializable id, final String objectName)
		throws DataAccessException, DataRetrievalFailureException {

		Reject.ifNull(id, "The identifier must not be null.");
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		
		T result = entityManager.getReference(entityClass, id);
		
		if (result == null || !(entityClass.isInstance(result))) {
			PersistenceNotificationHelper.notifyObjectRetrievalFailure(entityClass, id, objectName);
		}
		return result;
	}

	/**
	 * Merges the given persistent instance in a strong way: does the
	 * same as the <code>saveOrUpdate(Object)</code> method, but throws a more
	 * specific <code>OptimisticLockingFailureException</code> in the case of
	 * an optimistic locking failure.
	 *
	 * @see HibernateTemplate#saveOrUpdate(Object)
	 * @param entity
	 *            the persistent entity to save or update
	 * @param objectName
	 *            Name of the persistent object type.
	 * @throws DataAccessException
	 *             in case of Hibernate errors
	 * @throws OptimisticLockingFailureException
	 *             in case optimistic locking fails
	 * @return the merged entity
	 */
	public Object mergeStrong(Object entity, final String objectName)
		throws DataAccessException, OptimisticLockingFailureException {
		
		Reject.ifNull(entity);
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		try {
			return entityManager.merge(entity);
		} catch (JpaOptimisticLockingFailureException holfe) {
			String message = "The current " + objectName + " was modified or"
				+ " deleted in the meantime.";
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
				message, objectName, holfe);
			throw holfe;
		}
	}
	
	/**
	 * Removes the persistent instance given by its identifier in a strong way:
	 * first, the persistent instance is retrieved with the help of the
	 * identifier. If it exists, it will be deleted, otherwise a
	 * <code>DataRetrievalFailureException</code> will be thrown.
	 *
	 * @see JpaTemplate#remove(Object)
	 * @param entityClass
	 *            The class of the object which should be deleted.
	 * @param id
	 *            The identifier of the persistent instance to delete
	 * @param objectName
	 *            Name of the persistent object type.
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance to delete is null
	 */
	public void removeStrong(Class<?> entityClass, Serializable id, final String objectName)
		throws DataRetrievalFailureException {
		
		Reject.ifEmpty(objectName, "The name of the persistent object type "
			+ "must not be empty.");
		Object toDelete = null;
		try {
			toDelete = findByIdStrong(entityClass, id, objectName);
		} catch (DataRetrievalFailureException e) {
			String message = "The current " + objectName + " was "
				+ "deleted already!";
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
				message, objectName, null);
		}
		entityManager.remove(toDelete);
	}
	
	/**
	 * removes all entities in the given collection.
	 * @param entities the collection of all entities.
	 */
	public void removeAll(final Collection<?> entities) {
	
		for (Object e : entities) {
			entityManager.remove(e);
		}
	}
		
	/**
	 * Finds entities matching the given criteria query.
	 * @param <T> the entity type
	 * @param criteria the criteria query to run against the database
	 * @return the list of found objects, which may be empty.
	 */
	public <T> List<T> findByCriteria(final CriteriaQuery<T> criteria) {
		Assert.notNull(criteria, "CriteriaQuery must not be null");
		return (List<T>) entityManager.createQuery(criteria).getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
