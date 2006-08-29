/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.apps.keyword.service;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;

/**
 * This interface provides all available business methods, which can be used in
 * presentation layer.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface KeywordService {
    
    /**
     * Get keyword by primary key.
     * 
     * @param key
     *            Is the primary key.
     * @return Returns desired keyword.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If keyword could not be retrieved.           
     */
    public KeywordDto getKeywordByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get keyword by name.
     * 
     * @param name
     *            Is the name of a keyword.
     * @return Returns desired keyword.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If keyword could not be retrieved.
     */
    public KeywordDto getKeywordByName(String name)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all keywords.
     * 
     * @return Returns all keywords. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAllKeywords() throws DataAccessException;

    /**
     * Search keywords.
     * 
     * @param query
     *            Is the search query object.
     * @return Returns a list with keywords. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List searchKeywords(QueryObject query)
        throws DataAccessException;

    /**
     * Save keyword. If keyword is new, viz is has no primary key, it will be
     * inserted. Otherwise, the keyword will be updated.
     * 
     * @param keyword
     *            Is the keyword to save.
     * @return Returns the saved keyword.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If keyword could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If keyword has been modificated in the meantime.
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove keyword. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the keyword that should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If keyword could not be deleted.
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public void removeKeyword(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;
    
    /**
     * Remove keywords. Primary key of each keyword will be used.
     * 
     * @param keys
     *            Are the primary keys of the keywords that should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If a keyword could not be deleted.
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public void removeKeywords(Collection<?> keys) 
        throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;
}