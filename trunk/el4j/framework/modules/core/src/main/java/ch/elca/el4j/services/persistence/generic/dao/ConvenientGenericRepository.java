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
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Extends the SimpleGenericRepository with a few convenience methods.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The domain class the repository is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public interface ConvenientGenericRepository<T, ID extends Serializable> 
         extends SimpleGenericRepository<T> {
    
    /**
     * Retrieves a domain object by identifier.
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
    T findById(ID id, boolean lock)
        throws DataAccessException, DataRetrievalFailureException;


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
    List<T> findByExample(T exampleInstance) throws DataAccessException;
    
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
    void delete(T entity)  throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException;
}