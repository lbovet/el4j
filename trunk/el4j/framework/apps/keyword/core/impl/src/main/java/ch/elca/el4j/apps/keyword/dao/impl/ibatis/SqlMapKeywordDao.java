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
package ch.elca.el4j.apps.keyword.dao.impl.ibatis;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.Constants;
import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.persistence.ibatis.dao.ConvenienceSqlMapClientDaoSupport;
import ch.elca.el4j.services.persistence.ibatis.dao.GenericSqlMapDao;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Implementation of the keyword dao which is using iBatis SqlMaps.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author alex Mathey (AMA)
 */
public class SqlMapKeywordDao extends GenericSqlMapDao<KeywordDto, Integer> 
    implements KeywordDao {

    /**
     * Creates a new SqlMapKeywordDao instance.
     */
    public SqlMapKeywordDao() {
        setPersistentClass(KeywordDto.class);
    }
    
    /**
     * {@inheritDoc}
     */
    /*public KeywordDto getKeywordByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return (KeywordDto) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("getKeywordByKey", new Integer(key), 
                Constants.KEYWORD);
    }*/

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
    /*public List getAllKeywords() throws DataAccessException {
        List result = getConvenienceSqlMapClientTemplate().queryForList(
            "getAllKeywords", null);
        return CollectionUtils.asList(result);
    }*/

    /**
     * {@inheritDoc}
     */
    /*public List searchKeywords(QueryObject query) 
        throws DataAccessException {
        Reject.ifNull(query);
        List result = getConvenienceSqlMapClientTemplate().queryForList(
            "searchKeywords", query.getCriteriaList());
        return CollectionUtils.asList(result);
    }*/

    /**
     * {@inheritDoc}
     */
    /*public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(keyword);
        getConvenienceSqlMapClientTemplate().insertOrUpdate(
            keyword, Constants.KEYWORD);
        return keyword;
    }*/

    /**
     * {@inheritDoc}
     */
    /*public void removeKeyword(int key)
        throws DataAccessException,
            JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceSqlMapClientTemplate().delete(
            new Integer(key), 1, Constants.KEYWORD);
    }*/
}