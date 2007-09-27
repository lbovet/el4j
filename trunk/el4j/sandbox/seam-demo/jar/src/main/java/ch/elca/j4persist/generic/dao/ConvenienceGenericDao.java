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
package ch.elca.j4persist.generic.dao;

import java.io.Serializable;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;

/**
 * Extends the GenericDao with a few convenience methods.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/core/src/main/java/ch/elca/el4j/services/persistence/generic/dao/ConvenienceGenericDao.java $",
 *    "$Revision: 1359 $",
 *    "$Date: 2006-10-13 15:32:37 +0200 (Fri, 13 Oct 2006) $",
 *    "$Author: mathey $"
 * );</script>
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 * @param <ID>
 *            The generic type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 * @author Martin Zeltner (MZE)
 */
public interface ConvenienceGenericDao
    extends GenericDao {
    
    /**
     * Retrieves a domain object by identifier.
     * 
     * @param id
     *             The id of the domain object to delete
     * @return Returns the found domain object.
     * @throws DataRetrievalFailureException
     *             If no domain object could be found with given id.
     * @throws DataAccessException
     *             If general data access problem occurred
     */
    Object findById(Serializable id) throws DataRetrievalFailureException, DataAccessException;
    
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
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            OptimisticLockingFailureException.class })
    void delete(Serializable id) 
        throws OptimisticLockingFailureException, DataAccessException;
    
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
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            OptimisticLockingFailureException.class })
    void deleteObject(Object entity)
        throws OptimisticLockingFailureException, DataAccessException;
}