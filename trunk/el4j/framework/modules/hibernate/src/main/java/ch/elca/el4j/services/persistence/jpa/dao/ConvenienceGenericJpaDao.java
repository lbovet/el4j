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
package ch.elca.el4j.services.persistence.jpa.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;

/**
 * This interface extends {@link ConvenienceGenericDao} with query methods using
 * {@link CriteriaQuery}s.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @param <T>     the domain object type
 * @param <ID>    the id of the domain object to find
 *
 * @author Simon Stelling (SST)
 */
// TODO: javadoc review.
public interface ConvenienceGenericJpaDao<T, ID extends Serializable> {
	
	// begin GenericDao part
	
	/**
	 *  Needed because the Java generics throw away this type
	 *  information.
	 * @return Returns the domain class this DAO is responsible for.
	 */
	public Class<T> getPersistentClass();
	
	/**
	 * New: this callback is in general no longer required (the constructor
	 *  should figure the type out itself).
	 *
	 * @param c    Mandatory. The domain class this DAO is responsible for.
	 */
	public void setPersistentClass(Class<T> c);
	
	/**
	 * Re-reads the state of the given domain object from the underlying
	 * store.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity) throws DataAccessException,
		DataRetrievalFailureException;
	
	/**
	 * Re-reads the state of the given domain object from the undermost
	 * store (eg. the database).
	 *
	 * @param entity
	 *            The domain object to re-load the state of
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-loaded
	 * @return The reloaded entity
	 */
	T reload(T entity) throws DataAccessException,
		DataRetrievalFailureException;

	/**
	 * Saves or updates the given domain object.
	 *
	 * @param entity
	 *            The domain object to save or update
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataIntegrityViolationException
	 *             If domain object could not be inserted due to a data
	 *             integrity violation
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @return The saved or updated domain object
	 */
	@ReturnsUnchangedParameter
	T saveOrUpdate(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;

	/**
	 * Deletes the given domain objects. This method executed in a single
	 * transaction (by default with the Required semantics).
	 *
	 * @param entities
	 *             The domain objects to delete.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 */
	void delete(Collection<T> entities)
		throws OptimisticLockingFailureException, DataAccessException;
	
	// end GenericDao
	// begin ConvenienceGenericDao part
	
	/**
	 * Retrieves a domain object by identifier. This method gets the object from
	 * the hibernate cache. It might be that you don't get the actual version
	 * that is in the database. If you want the actual version do a refresh()
	 * after this method call.
	 *
	 * @param id
	 *            The id of the domain object to find
	 * @return Returns the found domain object.
	 * @throws DataRetrievalFailureException
	 *             If no domain object could be found with given id.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	T findById(ID id) throws DataRetrievalFailureException, DataAccessException;
	
	/**
	 * Deletes the domain object with the given id, disregarding any
	 * concurrent modifications that may have occurred.
	 *
	 * @param id
	 *             The id of the domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	void deleteById(ID id)
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 *
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	List<T> getAll() throws DataAccessException;
	
	/**
	 * Deletes the given domain object.
	 *
	 * @param entity
	 *             The domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	void delete(T entity)
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Deletes all available <code>T</code>.
	 *
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	public void deleteAll()
		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Sometimes, the way Hibernate handles all the actions in a session is
	 * very unbelievable. For example, we call
	 * <code>
	 *  delete(project);
	 *  project.setId(null) <= to insert new one
	 *  insert(project);
	 * </code>
	 *
	 * It could cause java.sql.BatchUpdateException:
	 * ORA-00001: unique constraint BECAUSE Hibernate doesn't flush
	 * the previous action first.
	 *
	 * This method provides a way to flush manually some action.
	 * Note that this method is only used in an extremely rare case.
	 */
	void flush();
	
	// end ConvenienceGenericDao part
	
	/**
	 * Convenience method: Executes saveOrUpdate() and flush() on that entity.
	 * 
	 * @param entity    The domain object to save or update
	 * @return          The saved or updated object
	 * @throws DataAccessException
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	@Deprecated
	public T saveOrUpdateAndFlush(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;
	
	/**
	 * merge the given entity.
	 * @param entity the entity to merge.
	 * @return the merged entity
	 * @throws DataAccessException
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T merge(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;
	
	/**
	 * persist the given entity.
	 * @param entity the entity to persist.
	 * @return the persisted entity
	 * @throws DataAccessException
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T persist(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;
	
	/** 
	 * Deletes all available <code>T</code> using a JPQL query.
	 * 
	 * This has the benefit of a significant performance improvement
	 * in comparison to {@link deleteAll}. The tradeoff is that this
	 * method does no cascade deletion. 
	 *
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * */
//	public void deleteAllNoCascade()
//		throws OptimisticLockingFailureException, DataAccessException;
	
	/**
	 * Deletes the given domain objects using a JPQL query. 
	 * 
	 * This has the benefit of a significant performance improvement
	 * in comparison to {@link delete}. The tradeoff is that this
	 * method does no cascade deletion. 
	 * 
	 * @param entities The domain objects to delete.
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
//	public void deleteNoCascade(Collection<T> entities) throws DataAccessException,
//		DataIntegrityViolationException, OptimisticLockingFailureException;

	/**
	 * Retrieves all the domain objects matching the JPA criteria.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByQuery(QueryBuilder criteria)
		throws DataAccessException;
	
	/**
	 * Retrieves all the domain objects matching the JPA criteria.
	 * Loads at least the given extent.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @param extent               the extent in which objects get loaded.
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByQuery(QueryBuilder criteria,
		DataExtent extent) throws DataAccessException;
	
	/**
	 * Retrieves a range of domain objects matching the JPA criteria.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByQuery(QueryBuilder criteria,
		int firstResult, int maxResults) throws DataAccessException;
	
	/**
	 * Retrieves a range of domain objects matching the JPA criteria.
	 * Loads at least the given extent.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @param extent               the extent in which objects get loaded.
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByQuery(QueryBuilder criteria, int firstResult,
		int maxResults, DataExtent extent) throws DataAccessException;
	
	/**
	 * Retrieves the number of domain objects matching the JPA criteria.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @return                     the number of objects that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceJpaTemplate#findCountByCriteria(DetachedCriteria)
	 */
	public int findCountByQuery(QueryBuilder criteria)
		throws DataAccessException;
	
	/**
	 * Retrieves a domain object by identifier. This method gets the object from
	 * the hibernate cache. It might be that you don't get the actual version
	 * that is in the database. If you want the actual version do a refresh()
	 * after this method call.
	 * Loads at least the given extent.
	 *
	 * @param id        The id of the domain object to find
	 * @param extent    the extent in which objects get loaded.
	 * @return Returns the found domain object.
	 * @throws DataRetrievalFailureException
	 *             If no domain object could be found with given id.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	public T findById(ID id, DataExtent extent) 
		throws DataRetrievalFailureException, DataAccessException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 * Loads at least the given extent.
	 *
	 * @param extent    the extent in which objects get loaded.
	 * 
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	List<T> getAll(DataExtent extent) throws DataAccessException;

	/**
	 * Re-reads the state of the given domain object from the underlying
	 * store.
	 * Loads at least the given extent.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @param extent
	 *            the extent in which objects get loaded.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException;
	
	/**
	 * Re-reads the state of the given domain object from the undermost
	 * store (eg. the database).
	 * Loads at least the given extent.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @param extent
	 *            the extent in which objects get loaded.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T reload(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException;
	
}
