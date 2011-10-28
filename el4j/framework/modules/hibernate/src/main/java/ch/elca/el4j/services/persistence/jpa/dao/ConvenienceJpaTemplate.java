/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.PersistenceNotificationHelper;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This is a convenience class for the Jpa template.
 *  Features:
 *   <ul>
 *      <li> improved paging support: allows to specify id of 1st element of a
 *            query
 *      <li> methods that signal an error if no element is found
 *            (they use the <em>Strong</em> suffixes)
 *   </ul>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class ConvenienceJpaTemplate extends JpaTemplate {
	
	/**
	 * Constructor.
	 * @param emf EntityManagerFactory used to create EntityManager
	 */
	public ConvenienceJpaTemplate(EntityManagerFactory emf) {
		super(emf);
	}

	/**
	 * Constructor.
	 * @param emf EntityManagerFactory used to create EntityManager
	 */
	public ConvenienceJpaTemplate(EntityManager em) {
		super(em);
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
			return merge(entity);
		} catch (JpaOptimisticLockingFailureException holfe) {
			String message = "The current " + objectName + " was modified or"
				+ " deleted in the meantime.";
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
				message, objectName, holfe);
			throw holfe;
		}
	}
	
	//// paging support /////
	
	/**
	 * for paging: what is the id of the first result to return?
	 *  NO_CONSTRAINT means we do not constrain anything
	 */
	int m_firstResult = QueryObject.NO_CONSTRAINT;
	
	/**
	 * Gets the id of the first result to return.
	 * @return The id of the first result to return.
	 */
	public int getFirstResult() {
		return m_firstResult;
	}

	/**
	 * Sets the id of the first result to return.
	 * @param firstResult The id of the first result to return.
	 */
	public void setFirstResult(int firstResult) {
		m_firstResult = firstResult;
	}

	/**
	 * Finds entities matching the given criteria query.
	 * @param <T> the entity type
	 * @param criteria the criteria query to run against the database
	 * @return the list of found objects, which may be empty.
	 */
	public <T> List<T> findByCriteria(final CriteriaQuery<T> criteria) {
		Assert.notNull(criteria, "CriteriaQuery must not be null");
		return execute(new JpaCallback<List<T>>() {

			@Override
			public List<T> doInJpa(EntityManager em) throws PersistenceException {
				return em.createQuery(criteria).getResultList();
			}
		});
	}
	
	/**
	 * Finds entities matching the given criteria query, with paging support.
	 * @param <T> the entity type
	 * @param criteria the criteria query to run against the database
	 * @param firstResult the index of the first row to return
	 * @param maxResults the maximum number of rows to return
	 * @return the list of found objects, which may be empty.
	 */
	public <T> List<T> findByCriteria(final CriteriaQuery<T> criteria, final int firstResult, final int maxResults) {
		Assert.notNull(criteria, "CriteriaQuery must not be null");
		return execute(new JpaCallback<List<T>>() {

			@Override
			public List<T> doInJpa(EntityManager em) throws PersistenceException {
				TypedQuery<T> query = em.createQuery(criteria);
				query.setFirstResult(firstResult);
				query.setMaxResults(maxResults);
				return query.getResultList();
			}
		});

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
		
		T result = find(entityClass, id);
		
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
		
		T result = getReference(entityClass, id);
		
		if (result == null || !(entityClass.isInstance(result))) {
			PersistenceNotificationHelper.notifyObjectRetrievalFailure(entityClass, id, objectName);
		}
		return result;
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
		remove(toDelete);
	}
	
	/**
	 * removes all entities in the given collection.
	 * @param entities the collection of all entities.
	 */
	public void removeAll(final Collection<?> entities) {
		execute(new JpaCallback<Void>() {

			@Override
			public Void doInJpa(EntityManager em) throws PersistenceException {
				for (Object e : entities) {
					em.remove(e);
				}
				return null;
			}
			
		});
	}
}
