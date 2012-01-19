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
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections.map.ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
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
 * This class is a JPA-specific implementation of the ConvenienceGenericRepository interface.
 * 
 * Note: This class does not use the JpaTemplate. In order to remove supplementary layers, it was decided to not use 
 * ConvenienceJpaTemplate and JpaTemplate.
 * 
 * Note: At the moment, we use an ExtentFetcher that only works with Hibernate. 
 * (Maybe we later allow the ExtentFetcher to be injected.)
 * 
 * @svnLink $Revision: 4253 $;$Date: 2010-12-21 11:08:04 +0100 (Di, 21 Dez 2010)$;$Author: swismer $;$URL: $
 * 
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 * 
 * @author Simon Stelling (SST), Philipp Oser (POS)
 */
public class GenericJpaRepositoryImpl<T, ID extends Serializable> implements
GenericJpaRepository<T, ID> {

	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(GenericJpaRepository.class);

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
	 * {@inheritDoc}
	 */
	public void setPersistentClass(Class<T> c) {
		Reject.ifNull(c);
		persistentClass = c;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<T> getPersistentClass() {
		assert persistentClass != null;
		return persistentClass;
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
	 * {@inheritDoc}
	 */
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T persist(T entity) throws ConstraintViolationException, 
		OptimisticLockException {
		entityManager.persist(entity);
		return entity;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@ReturnsUnchangedParameter
	@Transactional(propagation = Propagation.REQUIRED)
	public T merge(T entity) throws ConstraintViolationException, 
		OptimisticLockException {

		return (T) mergeStrong(entity, getPersistentClassName());
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
	 * @throws OptimisticLockingFailureException
	 *             in case optimistic locking fails
	 * @return the merged entity
	 */
	@Transactional
	private Object mergeStrong(Object entity, final String objectName)
			throws OptimisticLockException {

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

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T refresh(T entity) throws DataRetrievalFailureException {
		entityManager.refresh(entity);
		return entity;
	}
	
	/** {@inheritDoc} */
	@Override
	public T refresh(T entity, DataExtent extent) throws DataRetrievalFailureException {
		entityManager.refresh(entity);
		return fetchExtent(entity, extent);
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(T entity) {
		T e = entityManager.merge(entity);
		entityManager.remove(e);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Collection<T> entities) throws ConstraintViolationException, 
		OptimisticLockException {
		for (Object e : entities) {
			e = entityManager.merge(e);
			entityManager.remove(e);
		}
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteById(ID id) {	
		Reject.ifEmpty(getPersistentClassName(), "The name of the persistent object type "
				+ "must not be empty.");
		Object toDelete = null;
		try {
			toDelete = findByIdStrong(getPersistentClass(), id, getPersistentClassName());
		} catch (DataRetrievalFailureException e) {
			String message = "The current " + getPersistentClassName() + " was "
					+ "deleted already!";
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
					message, getPersistentClassName(), null);
		}
		entityManager.remove(toDelete);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll() throws OptimisticLockException {
		List<T> list = getAll();
		if (list.size() > 0) {
			delete(list);
		}
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void flush() {
		entityManager.flush();
	}


	/** {@inheritDoc} */
	public CriteriaQuery<T> getOrderedCriteria() {
		CriteriaQuery<T> criteria = entityManager.getCriteriaBuilder()
				.createQuery(persistentClass);
		criteria.from(persistentClass);

		return criteria;
	}

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
	 */
	protected List<T> fetchExtent(List<T> objects, DataExtent extent) {

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
	 */
	protected T fetchExtent(T object, DataExtent extent) {

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
	 */
	private void fetchExtentObject(Object object, ExtentEntity entity,
			ReferenceMap fetchedObjects) {
		s_logger.debug("using extent-fetcher " + extentFetcher.getClass());
		extentFetcher.fetchExtentObject(object, entity, fetchedObjects);
	}
	
	/** {@inheritDoc} */
	@Override
	public List<T> getAll(DataExtent extent) {
		return fetchExtent(
				findByCriteria(getOrderedCriteria()), 
				extent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> getAll() {
		return findByCriteria(getOrderedCriteria());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findById(ID id) throws DataRetrievalFailureException {
		return (T) findByIdStrong(
				getPersistentClass(), id, getPersistentClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findByIdLazy(ID id) throws DataRetrievalFailureException {
		return (T) findByIdStrongLazy(
				getPersistentClass(), id, getPersistentClassName());
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(final QueryBuilder criteria) {
		return criteria.applySelect(entityManager).getResultList(persistentClass);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(final QueryBuilder criteria,
			final int firstResult, final int maxResults) {

		return criteria.applySelect(entityManager)
				.getResultList(persistentClass, firstResult, maxResults);
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int findCountByQuery(final QueryBuilder criteria) {

		return criteria.applyCount(entityManager).getCount();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> findByCriteria(final CriteriaQuery<T> criteria) {
		Assert.notNull(criteria, "CriteriaQuery must not be null");
		return (List<T>) entityManager.createQuery(criteria).getResultList();
	}
	
	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryBuilder criteria, DataExtent extent) {
		return fetchExtent(findByQuery(criteria), extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryBuilder criteria, int firstResult,
			int maxResults, DataExtent extent) {
		return fetchExtent(findByQuery(criteria, firstResult, maxResults),
				extent);
	}
	
	/** {@inheritDoc} */
	@Transactional
	@Override
	public T findById(ID id, DataExtent extent)
			throws DataRetrievalFailureException {
		return fetchExtent(
				(T) findByIdStrong(
						getPersistentClass(), id, getPersistentClassName()),
						extent);
	}

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
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance is null
	 */
	@Transactional
	private <T> T findByIdStrong(Class<T> entityClass, Serializable id, final String objectName)
			throws DataRetrievalFailureException {

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
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             in case the persistent instance is null
	 */
	@Transactional
	private <T> T findByIdStrongLazy(Class<T> entityClass, Serializable id, final String objectName)
			throws DataRetrievalFailureException {

		Reject.ifNull(id, "The identifier must not be null.");
		Reject.ifEmpty(objectName, "The name of the persistent object type "
				+ "must not be empty.");

		T result = entityManager.getReference(entityClass, id);

		if (result == null || !(entityClass.isInstance(result))) {
			PersistenceNotificationHelper.notifyObjectRetrievalFailure(entityClass, id, objectName);
		}
		return result;
	}

}
