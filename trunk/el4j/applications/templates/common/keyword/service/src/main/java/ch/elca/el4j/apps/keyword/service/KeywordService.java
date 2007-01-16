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
package ch.elca.el4j.apps.keyword.service;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;

/**
 * This interface provides the business methods which can be used in the
 * presentation layer and which are not already present in the underlying DAO.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 */
public interface KeywordService {
       
    /**
     * Delete keywords. Primary key of each keyword will be used.
     * 
     * @param keys
     *            Are the primary keys of the keywords that should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws OptimisticLockingFailureException
     *             If a keyword could not be deleted.
     */
    @RollbackConstraint(rollbackFor = {
            OptimisticLockingFailureException.class, 
            DataAccessException.class })
    public void deleteKeywords(Collection<?> keys) 
        throws OptimisticLockingFailureException, DataAccessException;
   
    /**
     * @return A list of all keywords.
     */
    public List<Keyword> getAllKeywords();
    
    /**
     * Create and store a keyword.
     * 
     * @param name Name of the keyword
     * @param description Description of the keyword
     * @throws OptimisticLockingFailureException
     * @throws DataAccessException
     */
    @RollbackConstraint(rollbackFor = {
            OptimisticLockingFailureException.class,
            DataAccessException.class })
    public void createAndStore(String name, String description)
        throws OptimisticLockingFailureException, DataAccessException;
    
    /**
     * Delete a list of keywords.
     * 
     * @param keywords
     *            Keywords to delete
     * @throws OptimisticLockingFailureException
     *             If general data access problem occurred.
     * @throws DataAccessException
     *             If a keyword could not be deleted.
     */
    @RollbackConstraint(rollbackFor = {
            OptimisticLockingFailureException.class,
            DataAccessException.class })
    public void deleteKeywords(List<Keyword> keywords)
        throws OptimisticLockingFailureException, DataAccessException;
}
