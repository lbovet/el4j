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

import org.apache.commons.collections.map.ReferenceMap;
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
import ch.elca.el4j.services.persistence.hibernate.dao.extent.ExtentEntity;
import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;
import ch.elca.el4j.services.persistence.jpa.dao.extentstrategies.ExtentFetcher;
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
	 * The ExtentFetcher used to fetch extents. 
	 * Injected by JpaExtentFetcherInjectorBeanPostprocessor.
	 */
	private ExtentFetcher extentFetcher;

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
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(final QueryBuilder criteria)
		throws DataAccessException {
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();
		
		return template.execute(new JpaCallback<List<T>>() {

			@Override
			public List<T> doInJpa(EntityManager em) throws PersistenceException {
				return criteria.applySelect(em).getResultList(persistentClass);
			}
			
		});
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByQuery(final QueryBuilder criteria, final int firstResult, final int maxResults)
		throws DataAccessException {
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();
		
		return template.execute(new JpaCallback<List<T>>() {

			@Override
			public List<T> doInJpa(EntityManager em) throws PersistenceException {
				return criteria.applySelect(em).getResultList(persistentClass, firstResult, maxResults);
			}
			
		});
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int findCountByQuery(final QueryBuilder criteria)
		throws DataAccessException {
		
		ConvenienceJpaTemplate template = getConvenienceJpaTemplate();

		return template.execute(new JpaCallback<Integer>() {

			@Override
			public Integer doInJpa(EntityManager em) throws PersistenceException {
				return criteria.applyCount(em).getCount();
			}
			
		});
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

	// extent-related methods:
	
	/**
	 * @return Returns the extentFetcher.
	 */
	public ExtentFetcher getExtentFetcher() {
		return extentFetcher;
	}

	/**
	 * @param extentFetcher Is the extentFetcher to set.
	 */
	public void setExtentFetcher(ExtentFetcher extentFetcher) {
		this.extentFetcher = extentFetcher;
	}
	
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
	 * @param object			the object to load in given extent
	 * @param entity			the extent entity
	 * @param fetchedObjects	the HashMap with all the already fetched objects
	 * 
	 * @throws DataAccessException
	 */
	private void fetchExtentObject(Object object, ExtentEntity entity, ReferenceMap fetchedObjects)
		throws DataAccessException {
		s_logger.debug("using extent-fetcher " + extentFetcher.getClass());
		extentFetcher.fetchExtentObject(object, entity, fetchedObjects);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryBuilder criteria, DataExtent extent) throws DataAccessException {
		return fetchExtent(findByQuery(criteria), extent);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> findByQuery(QueryBuilder criteria, int firstResult, int maxResults, DataExtent extent)
		throws DataAccessException {
		return fetchExtent(findByQuery(criteria, firstResult, maxResults), extent);
	}

	/** {@inheritDoc} */
	@Override
	public T findById(ID id, DataExtent extent) throws DataRetrievalFailureException, DataAccessException {
		return fetchExtent((T) getConvenienceJpaTemplate().findByIdStrong(
			getPersistentClass(), id, getPersistentClassName()), extent);
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
