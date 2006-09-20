/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.generic.dao;

import java.io.Serializable;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Extends the SimpleGenericDao with a few convenience methods.
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
 * @param <ID>
 *            The generic type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public interface ConvenientGenericDao<T, ID extends Serializable>
    extends GenericDao<T> {
    
    /**
     * Retrieves a domain object by identifier.
     * 
     * @param id
     * @return
     * @throws DataAccessException
     * @throws DataRetrievalFailureException
     */
    T findById(ID id) throws DataAccessException, DataRetrievalFailureException;
    

    /**
     * Executes a query based on a given example domain object.
     * 
     * @param exampleInstance
     *            An instance of the desired domain object, serving as example
     *            for "query-by-example"
     * @throws DataAccessException
     *             If general data access problem occurred           
     * @return A list containing 0 or more domain objects
     */
    /*List<T> findByExample(T exampleInstance) throws DataAccessException;*/
    
    /**
     * Deletes the domain object with the given id, disregarding any 
     * concurrent modifications that may have occurred.
     * 
     * @param id
     *             The id of the domain object to delete
     * @throws DataIntegrityViolationException
     *             If domain object could not be deleted due to a data
     *             integrity violation 
     * @throws DataAccessException
     *             If general data access problem occurred
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    void delete(ID id) throws DataIntegrityViolationException, 
                              DataAccessException;
    
    /**
     * Deletes the given domain object.
     * 
     * @param entity
     *             The domain object to delete
     * @throws DataAccessException
     *             If general data access problem occurred
     * @throws DataIntegrityViolationException
     *             If domain object could not be deleted due to a data
     *             integrity violation 
     * @throws OptimisticLockingFailureException
     *             If domain object has been modified in the meantime   
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    void delete(T entity)  throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException;
}