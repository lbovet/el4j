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
package ch.elca.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.search.QueryObject;

/**
 * This interface extends {@link ConvenienceGenericDao} with query methods using
 * {@link DetachedCriteria}s.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>     the domain object type
 * @param <ID>    the id of the domain object to find
 *
 * @author Stefan Wismer (SWI)
 */
public interface ConvenienceGenericHibernateDao<T, ID extends Serializable>
	extends ConvenienceGenericDao<T, ID> {
	
	/**
	 * Convenience method: Executes saveOrUpdate() and flush() on that entity.
	 * 
	 * @param entity    The domain object to save or update
	 * @return          The saved or updated object
	 * @throws DataAccessException
	 * @throws DataIntegrityViolationException
	 * @throws OptimisticLockingFailureException
	 */
	public T saveOrUpdateAndFlush(T entity) throws DataAccessException,
		DataIntegrityViolationException, OptimisticLockingFailureException;

	/**
	 * Retrieves all the domain objects matching the Hibernate criteria.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException;
	
	/**
	 * Retrieves all the domain objects matching the Hibernate criteria.
	 * Loads at least the given extent.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @param extent			   the extent in which objects get loaded.
	 * @return                     all object that fulfill the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria,
		DataExtent extent) throws DataAccessException;
	
	/**
	 * Retrieves a range of domain objects matching the Hibernate criteria.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria,
		int firstResult, int maxResults) throws DataAccessException;
	
	/**
	 * Retrieves a range of domain objects matching the Hibernate criteria.
	 * Loads at least the given extent.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @param firstResult          the index of the first result to return
	 * @param maxResults           the maximum number of results to return
	 * @param extent			   the extent in which objects get loaded.
	 * @return                     the specified subset of object that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findByCriteria(DetachedCriteria, int, int)
	 */
	public List<T> findByCriteria(DetachedCriteria hibernateCriteria, int firstResult,
		int maxResults, DataExtent extent) throws DataAccessException;
	
	/**
	 * Retrieves the number of domain objects matching the Hibernate criteria.
	 * 
	 * @param hibernateCriteria    the criteria that the result has to fulfill
	 * @return                     the number of objects that fulfill
	 *                             the criteria
	 * @throws DataAccessException
	 *
	 * @see ConvenienceHibernateTemplate#findCountByCriteria(DetachedCriteria)
	 */
	public int findCountByCriteria(DetachedCriteria hibernateCriteria)
		throws DataAccessException;
	
	/**
	 * Retrieves a domain object by identifier. This method gets the object from
	 * the hibernate cache. It might be that you don't get the actual version
	 * that is in the database. If you want the actual version do a refresh()
	 * after this method call.
	 * Loads at least the given extent.
	 *
	 * @param id			The id of the domain object to find
	 * @param extent		the extent in which objects get loaded.
	 * @return Returns the found domain object.
	 * @throws DataRetrievalFailureException
	 *             If no domain object could be found with given id.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	public T findById(ID id, DataExtent extent) 
		throws DataRetrievalFailureException, DataAccessException;
	
	/**
	 * Retrieves a domain object by identifier, optionally obtaining a database
	 * lock for this operation.  <br>
	 *
	 * (For hibernate specialists: we do a "get()"
	 * in this method. In case you require only a "load()" (e.g. for lazy
	 * loading to work) we recommend that you write your own find method in the
	 * interface's subclass.)
	 * Loads at least the given extent.
	 *
	 * @param id
	 *            The id of a domain object
	 * @param lock
	 *            Indicates whether a database lock should be obtained for this
	 *            operation
	 * @param extent
	 * 			  the extent in which objects get loaded.
	 * 
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be retrieved
	 * @return The desired domain object
	 */
	public T findById(ID id, boolean lock, DataExtent extent)
		throws DataAccessException, DataRetrievalFailureException;
	
	/**
	 * Retrieves all the domain objects of type T.
	 * Loads at least the given extent.
	 *
	 * @param extent	the extent in which objects get loaded.
	 * 
	 * @return The list containing all the domain objects of type T; if no such
	 *         domain objects exist, an empty list will be returned
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 */
	List<T> getAll(DataExtent extent) throws DataAccessException;
	
	/**
	 * Executes a query based on a given query object.
	 *  This method may also support paging (see javadoc
	 *   of implementing class).
	 * Loads at least the given extent.
	 *
	 * @param q 		The search query object
	 * @param extent	the extent in which objects get loaded.
	 * @throws  DataAccessException
	 *             If general data access problem occurred
	 * @return A list containing 0 or more domain objects
	 */
	List<T> findByQuery(QueryObject q, DataExtent extent) throws DataAccessException;

	/**
	 * Re-reads the state of the given domain object from the underlying
	 * store.
	 * Loads at least the given extent.
	 *
	 * @param entity
	 *            The domain object to re-read the state of
	 * @param extent	
	 * 			  the extent in which objects get loaded.
	 * @throws DataAccessException
	 *             If general data access problem occurred
	 * @throws DataRetrievalFailureException
	 *             If domain object could not be re-read
	 * @return The refreshed entity
	 */
	T refresh(T entity, DataExtent extent) throws DataAccessException,
		DataRetrievalFailureException;
	
	/**
	 * @return    the convenience Hibernate template
	 */
	public ConvenienceHibernateTemplate getConvenienceHibernateTemplate();
	
	/**
	 * @return    the default {@link Order} to order the results
	 */
	public Order[] getDefaultOrder();

	/**
	 * Set default order of results returned by getAll and findByQuery (not findByCriteria!).
	 * If defaultOrder is <code>null</code> then default ordering is deactivated.
	 * 
	 * @param defaultOrder    the default {@link Order} to order the results
	 */
	public void setDefaultOrder(Order... defaultOrder);
	
	/**
	 * Create a {@link DetachedCriteria} what contains default ordering and distinct constraints.
	 * 
	 * @return    a {@link DetachedCriteria}
	 */
	public DetachedCriteria getOrderedCriteria();
	
}
