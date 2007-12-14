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
package ch.elca.j4persist.generic.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.search.QueryObject;

/**
 *
 * This interface serves as generic access to DAOs. It is the interface for 
 * the DDD-Book's (http://www.domaindrivendesign.org/) Repository pattern. 
 * This interface is implemented generically and it can be extended in case 
 * you need more specific methods. Based on an idea from the Hibernate website.
 *
 * This is the canonical form of this interface. We recommend it when a generic
 * DAO is used in tools (to make the contract minimal). 
 * For direct programmer-usage we recommend to use the convenience subclasses. 
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
 * @author Martin Zeltner (MZE)
 */
public interface GenericDao {
    /**
     *  Needed because the Java generics throw away this type
     *  information.
     * @return Returns the domain class this DAO is responsible for.
     */
    public Class getPersistentClass();    
    
    /**
     * Executes a query based on a given query object.
     * 
     * @param q The search query object
     * @throws  DataAccessException
     *             If general data access problem occurred
     * @return A list containing 0 or more domain objects
     */
    List findByQuery(QueryObject q) throws DataAccessException;

    
    /**
     * Retrieves all the domain objects of type T.
     * 
     * @return The list containing all the domain objects of type T; if no such
     *         domain objects exist, an empty list will be returned
     * @throws DataAccessException
     *             If general data access problem occurred
     */
    List getAll() throws DataAccessException;   
    
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
    Object refresh(Object entity) throws DataAccessException, 
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
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            DataIntegrityViolationException.class,
            OptimisticLockingFailureException.class })
    Object saveOrUpdate(Object entity) throws DataAccessException,
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
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            OptimisticLockingFailureException.class })
    void delete(Collection entities)
        throws OptimisticLockingFailureException, DataAccessException;
}