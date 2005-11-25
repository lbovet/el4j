/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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
package ch.elca.el4j.apps.keyword.dao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.Constants;
import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.persistence.generic.dao.ConvenienceSqlMapClientDaoSupport;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.ObjectUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Implementation of the keyword dao which is using iBatis SqlMaps.
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
public class SqlMapKeywordDao extends ConvenienceSqlMapClientDaoSupport 
    implements KeywordDao {

    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return (KeywordDto) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("getKeywordByKey", new Integer(key), 
                Constants.KEYWORD);
    }

    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByName(String name)
        throws DataAccessException, DataRetrievalFailureException {
        Reject.ifEmpty(name);
        return (KeywordDto) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("getKeywordByName", name, Constants.KEYWORD);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllKeywords() throws DataAccessException {
        List result = getConvenienceSqlMapClientTemplate().queryForList(
            "getAllKeywords", null);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public List searchKeywords(String name, String description) 
        throws DataAccessException {
        String searchName = ObjectUtils.asString(name).toLowerCase();
        String searchDescription 
            = ObjectUtils.asString(description).toLowerCase();
        KeywordDto keyword = new KeywordDto();
        keyword.setName(searchName);
        keyword.setDescription(searchDescription);
        List result = getConvenienceSqlMapClientTemplate().queryForList(
            "searchKeywords", keyword);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(keyword);
        getConvenienceSqlMapClientTemplate().insertOrUpdate(
            keyword, Constants.KEYWORD);
        return keyword;
    }

    /**
     * {@inheritDoc}
     */
    public void removeKeyword(int key)
        throws DataAccessException,
            JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceSqlMapClientTemplate().delete(
            new Integer(key), 1, Constants.KEYWORD);
    }
}