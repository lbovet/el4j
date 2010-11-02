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

package ch.elca.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.map.ReferenceMap;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 * @author Jonas Hauenstein (JHN)
 */
public class GenericHibernateDao<T, ID extends Serializable>
	extends ConvenienceHibernateDaoSupport
	implements ConvenienceGenericHibernateDao<T, ID>, InitializingBean {
		
	/**
	 * Maximal number of entities which are deleted
	 * with a single HQL statement.
	 */
	private static final int MAX_BULK_DELETE = 100;

	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(GenericHibernateDao.class);
	
	/**
	 * The domain class this DAO is responsible for.
	 */
	private Class<T> m_persistentClass;
	
	/**
	 * The default hibernate {@link Order} to order results.
	 */
	private Order[] m_defaultOrder = null;
	
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
		return entity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public T findById(ID id, boolean lock, DataExtent extent)
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
		return fetchExtent(entity, extent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T findById(ID id)
		throws DataAccessException, DataRetrievalFailureException {
		return (T) getConvenienceHibernateTemplate().getByIdStrong(
			getPersistentClass(), id, getPersistentClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public T findById(ID id, DataExtent extent)
		throws DataAccessException, DataRetrievalFailureException {
		return fetchExtent((T) getConvenienceHibernateTemplate().getByIdStrong(
			getPersistentClass(), id, getPersistentClassName()), extent);
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
		return getConvenienceHibernateTemplate().findByCriteria(getOrderedCriteria());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> getAll(DataExtent extent) throws DataAccessException {
		return fetchExtent(getConvenienceHibernateTemplate().findByCriteria(getOrderedCriteria()), extent);
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
		
		return template.findByCriteria(hibernateCriteria, q.getFirstResult(), q.getMaxResults());
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * This method supports paging (see QueryObject for info on
	 *  how to use this).
	 *
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findByQuery(QueryObject q, DataExtent extent) throws DataAccessException {
		DetachedCriteria hibernateCriteria = getCriteria(q);
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return fetchExtent(template.findByCriteria(hibernateCriteria, q.getFirstResult(), 
			q.getMaxResults()), extent);
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
		
		return template.findByCriteria(hibernateCriteria);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, DataExtent extent)
		throws DataAccessException {
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return fetchExtent(template.findByCriteria(hibernateCriteria), extent);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult, int maxResults)
		throws DataAccessException {
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return template.findByCriteria(hibernateCriteria, firstResult, maxResults);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult, int maxResults,
			DataExtent extent) throws DataAccessException {
		
		ConvenienceHibernateTemplate template = getConvenienceHibernateTemplate();
		
		return fetchExtent(template.findByCriteria(hibernateCriteria, firstResult, maxResults), extent);
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
		return entity;
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public T refresh(T entity, DataExtent extent) throws DataAccessException,
	DataRetrievalFailureException {
		getConvenienceHibernateTemplate().refresh(entity);
		return fetchExtent(entity, extent);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public T reload(T entity) throws DataAccessException,
		DataRetrievalFailureException {
		ID id = (ID) getSessionFactory().getClassMetadata(entity.getClass())
			.getIdentifier(entity, EntityMode.POJO);
		return findById(id);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public T reload(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException {
		ID id = (ID) getSessionFactory().getClassMetadata(entity.getClass())
			.getIdentifier(entity, EntityMode.POJO);
		T refresh = (T) findById(id, extent);
		return refresh;

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
	public void delete(Collection<T> entities) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {
		getConvenienceHibernateTemplate().deleteAll(entities);
	}

	/** {@inheritDoc} */
	public void deleteNoCascade(Collection<T> entities) throws DataAccessException,
			DataIntegrityViolationException, OptimisticLockingFailureException {	

		//search for method with @Id annotation
		Method[] methods = getPersistentClass().getMethods();
		Method idMethod = null;
		for (Method m : methods) {
			if (m.isAnnotationPresent(javax.persistence.Id.class)) {
				idMethod = m;
			}
		}
		
		if (idMethod != null) {
			
			ArrayList<Object> hqlParameter = new ArrayList<Object>();
			StringBuilder hqlQuery = new StringBuilder("delete ");
			hqlQuery.append(getPersistentClassName());
			hqlQuery.append(" where ");
			
			Iterator<T> it = entities.iterator();
			T entity;
			boolean fallback;
			int querycount = 0;
			//creating hql bulk delete statement for all entities
			//by calling the annotated method to get @Id value
			while (it.hasNext()) {
				fallback = false;
				entity = it.next();
				Object o = null;
				try {
					o = idMethod.invoke(entity);
				} catch (IllegalArgumentException e) {
					fallback = true;
				} catch (IllegalAccessException e) {
					fallback = true;
				} catch (InvocationTargetException e) {
					fallback = true;
				}
				if (o == null) {
					fallback = true;
				}
				//if we encountered an error, use the given delete method for this entity object
				if (!fallback) {
					
					//check if maximal query length is reached
					if (querycount >= MAX_BULK_DELETE) {
						//execute present query
						getHibernateTemplate().bulkUpdate(hqlQuery.toString(), hqlParameter.toArray());
						//reinitialize new vars
						hqlParameter.clear();
						hqlQuery = new StringBuilder("delete ");
						hqlQuery.append(getPersistentClassName());
						hqlQuery.append(" where ");
						querycount = 0;
					}
					if (querycount > 0) {
						hqlQuery.append("or ");
					}
					hqlQuery.append("id = ? ");
					hqlParameter.add(o);
					querycount++;
				} else {
					//something went wrong (in the reflective method call)
					//use the given delete method to delete this entity
					s_logger.warn(idMethod.getName() + "could not be called in " + getPersistentClassName()
						+ ". Not using HQL bulk delete for this entity.");
					getConvenienceHibernateTemplate().delete(entity);
				}
			}
			
			//check if there is (still) something to do
			if (querycount > 0) {
				getHibernateTemplate().bulkUpdate(hqlQuery.toString(), hqlParameter.toArray());
			}
			
		} else {
			//if no method with @Id annotation found, delete
			//all entities with the given delete method
			s_logger.warn("No @Id annotation was found in " + getPersistentClassName()
				+ ". Not using HQL bulk delete for all entities.");
			getConvenienceHibernateTemplate().deleteAll(entities);
		}
	}
	

	/** {@inheritDoc} */
	public void deleteAll()
		throws OptimisticLockingFailureException, DataAccessException {
		List<T> list = getAll();
		if (list.size() > 0) {
			delete(list);
		}
	}
	

	/** {@inheritDoc} */
	public void deleteAllNoCascade()
		throws OptimisticLockingFailureException, DataAccessException {
		
		String hqlQuery = "delete " + getPersistentClassName();
		getHibernateTemplate().bulkUpdate(hqlQuery);
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
			// Fetch the base type fields, obsolete without byte-code instrumentation
			/*for (Method m : entity.getFields()) {
				m.invoke(object, nullArg);
			}*/
			// Fetch the child entities
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
			// Fetch the collections
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
