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

import javax.persistence.OptimisticLockException;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.persistence.jpa.criteria.QueryBuilder;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Generic Repository (former name DAO). Use as parent class for you own DAOs.
 *
 * @svnLink $Revision: 4253 $;$Date: 2010-12-21 11:08:04 +0100 (Tue, 21 Dec 2010) $;$Author: swismer $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/jpa/dao/ConvenienceGenericJpaDao.java $
 *
 * @param <T>     the domain object type
 * @param <ID>    the id of the domain object to find
 *
 * @author Simon Stelling (SST), Philipp Oser (POS)
 */
public interface GenericJpaRepository<T, ID extends Serializable> {
	
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
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity) throws DataRetrievalFailureException;

	/**
	 * Deletes the given domain objects. This method executed in a single
	 * transaction (by default with the Required semantics).
	 *
	 * @param entities
	 *             The domain objects to delete.
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 */
	void delete(Collection<T> entities)
		throws OptimisticLockException;
	
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
	 */
	T findById(ID id) throws DataRetrievalFailureException;
	
	/**
	 * Deletes the domain object with the given id, disregarding any
	 * concurrent modifications that may have occurred.
	 *
	 * @param id
	 *             The id of the domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been deleted in the meantime
	 */
	void deleteById(ID id)
		throws OptimisticLockException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 *
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 */
	List<T> getAll();
	
	/**
	 * Deletes the given domain object.
	 *
	 * @param entity
	 *             The domain object to delete
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 */
	void delete(T entity) throws OptimisticLockException;
	
	/**
	 * Deletes all available <code>T</code>.
	 *
	 * @throws OptimisticLockingFailureException
	 *             If domain object has been modified/deleted in the meantime
	 */
	public void deleteAll() throws OptimisticLockException;
	
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
	 * merge the given entity.
	 * @param entity the entity to merge.
	 * @return the merged entity
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T merge(T entity) throws ConstraintViolationException, OptimisticLockException;
	
	/**
	 * persist the given entity.
	 * @param entity the entity to persist.
	 * @return the persisted entity
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T persist(T entity) throws ConstraintViolationException, OptimisticLockException;
	
	/**
	 * Retrieves all the domain objects matching the JPA criteria.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @return                     all object that fulfill the criteria
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByQuery(QueryBuilder criteria);
	
	/**
	 * Retrieves all the domain objects matching the JPA criteria.
	 * Loads at least the given extent.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @param extent               the extent in which objects get loaded.
	 * @return                     all object that fulfill the criteria
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByQuery(QueryBuilder criteria,
		DataExtent extent);
	
	/**
	 * Retrieves a range of domain objects matching the JPA criteria.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByQuery(QueryBuilder criteria,
		int firstResult, int maxResults);
	
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
	 *
	 * @see ConvenienceJpaTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByQuery(QueryBuilder criteria, int firstResult,
		int maxResults, DataExtent extent);
	
	/**
	 * Retrieves the number of domain objects matching the JPA criteria.
	 * 
	 * @param criteria             the criteria that the result has to fulfill
	 * @return                     the number of objects that fulfill
	 *                             the criteria
	 *
	 * @see ConvenienceJpaTemplate#findCountByCriteria(DetachedCriteria)
	 */
	public int findCountByQuery(QueryBuilder criteria);
	
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
	 */
	public T findById(ID id, DataExtent extent) 
		throws DataRetrievalFailureException;
	
	/**
	 *  Lazily retrieves a domain object by identifier.
	 *  
	 * @param id        The id of the domain object to find
	 * @return Returns the found domain object.
	 * @throws DataRetrievalFailureException
	 *             If no domain object could be found with given id.
	 */

	public T findByIdLazy(ID id) throws DataRetrievalFailureException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 * Loads at least the given extent.
	 *
	 * @param extent    the extent in which objects get loaded.
	 * 
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 */
	List<T> getAll(DataExtent extent);

	/**
	 * Re-reads the state of the given domain object from the underlying
	 * store.
	 * Loads at least the given extent.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @param extent
	 *            the extent in which objects get loaded.
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity, DataExtent extent) throws DataRetrievalFailureException;
	
	/**
	 * Finds entities matching the given criteria query.
	 * @param <T> the entity type
	 * @param criteria the criteria query to run against the database
	 * @return the list of found objects, which may be empty.
	 */
	public <T> List<T> findByCriteria(final CriteriaQuery<T> criteria);
}
