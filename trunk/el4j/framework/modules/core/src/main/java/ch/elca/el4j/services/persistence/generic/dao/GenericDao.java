/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.generic.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;
import ch.elca.el4j.services.search.QueryObject;

/**
 *
 * This interface serves as generic access to storage DAOs. It is the
 * interface for the DDD-Book's Repository pattern. The repository pattern is
 * similar to the DAO pattern, but a bit more generic. This interface can be
 * implemented in a generic way and can be extended in case a user needs more
 * specific methods. Based on an idea from the Hibernate website.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 * @author Adrian Moos (AMS)
 */
public interface GenericDao<T extends PrimaryKeyOptimisticLockingObject> {
    /**
     * @return Returns the domain class this DAO is responsible for.
     */
    public Class<T> getPersistentClass();    
    
    /**
     * Executes a query based on a given query object.
     * 
     * @param q
     *            The search query object
     * @throws DataAccessException
     *             If general data access problem occurred
     * @return A list containing 0 or more domain objects
     */
    List<T> findByQuery(QueryObject q) throws DataAccessException;

    
    /**
     * Retrieves all the domain objects of type T.
     * 
     * @return The list containing all the domain objects of type T; if no such
     *         domain objects exist, an empty list will be returned
     * @throws DataAccessException
     *             If general data access problem occurred
     */
    List<T> findAll() throws DataAccessException;   
    
    /**
     * Re-reads the state of the given domain object from the underlying
     * database.
     * 
     * @param entity
     *            The domain object to re-read the state of
     * @return The refreshed entity
     */
    T refresh(T entity);

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
     *             If domain object has been modified in the meantime   
     * @return The saved or updated domain object
     */
    @ReturnsUnchangedParameter
    T saveOrUpdate(T entity) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException;

    /**
     * Deletes the given domain objects. This method executed in a single
     * transaction.
     * 
     * @param entities
     *             The domain objects to delete.
     * @throws DataAccessException
     *             If general data access problem occurred
     * @throws DataIntegrityViolationException
     *             If domain object could not be deleted due to a data
     *             integrity violation 
     * @throws OptimisticLockingFailureException
     *             If domain object has been modified in the meantime   
     */
    void delete(Collection<T> entities)  throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException;
}