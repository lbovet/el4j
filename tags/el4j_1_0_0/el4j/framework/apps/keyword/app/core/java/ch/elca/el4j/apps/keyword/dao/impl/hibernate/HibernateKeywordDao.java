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
package ch.elca.el4j.apps.keyword.dao.impl.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.Constants;
import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.services.persistence.dao.ConvenienceHibernateDaoSupport;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;

/**
 * 
 * Implementation of the keyword dao using Hibernate.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class HibernateKeywordDao extends ConvenienceHibernateDaoSupport 
    implements KeywordDao {

    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByKey(int key) throws DataAccessException,
        DataRetrievalFailureException {
    
        return (KeywordDto) getConvenienceHibernateTemplate()
            .getByIdStrong(KeywordDto.class, key, Constants.KEYWORD);
    }

    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByName(String name) throws DataAccessException,
        DataRetrievalFailureException {
      
        String queryString = "from KeywordDto keyword where name = :name";
        
        return (KeywordDto) getConvenienceHibernateTemplate()
            .findByNamedParamStrong(queryString, "name", name,
                Constants.KEYWORD);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllKeywords() throws DataAccessException {
        return getConvenienceHibernateTemplate().find("from KeywordDto");
    }

    /**
     * {@inheritDoc}
     */
    public List searchKeywords(QueryObject query) throws DataAccessException {
       
        DetachedCriteria hibernateCriteria = CriteriaTransformer
            .transform(query, KeywordDto.class); 
            
        // Execute Hibernate criteria query and return the list of KeywordDto
        // objects returned by the query.
        return getConvenienceHibernateTemplate().
            findByCriteria(hibernateCriteria);
    }

    /**
     * {@inheritDoc}
     */
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException,
        OptimisticLockingFailureException {
        
        getConvenienceHibernateTemplate().saveOrUpdateStrong(keyword,
            Constants.KEYWORD);
        return keyword;
    }

    /**
     * {@inheritDoc}
     */
    public void removeKeyword(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        
        getConvenienceHibernateTemplate().deleteStrong(KeywordDto.class, key,
            Constants.KEYWORD);
    }

}
