/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.apps.keyword.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;

/**
 * This interface provides all available business methods, which can be used in
 * presentation layer.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
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
     * Search keywords whose name and description contains given substrings.
     * This search is case-insensitive.
     * 
     * @param name
     *            Is the name to search for.
     * @param description
     *            Is the description to search for.
     * @return Returns a list with keywords. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List searchKeywords(String name, String description)
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
     * 
     * @@attrib.transaction.RollbackRule(DataAccessException.class)
     * @@attrib.transaction.RollbackRuleOnRuntimeException()
     * @@attrib.transaction.RollbackRuleOnError()
     */
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove keyword. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the keyword, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If keyword could not be deleted.
     * 
     * @@attrib.transaction.RollbackRule(DataAccessException.class)
     * @@attrib.transaction.RollbackRuleOnRuntimeException()
     * @@attrib.transaction.RollbackRuleOnError()
     */
    public void removeKeyword(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;
}