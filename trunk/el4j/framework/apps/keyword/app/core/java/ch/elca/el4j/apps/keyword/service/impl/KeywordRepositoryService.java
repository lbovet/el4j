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
package ch.elca.el4j.apps.keyword.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.repository.RepositoryFactory;
import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;

/**
 * 
 * This class is the repository-specific implementation of the keyword service.
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
public class KeywordRepositoryService implements KeywordService,
    InitializingBean {
    
    /**
     * Hibernate repository factory.
     */
    private RepositoryFactory m_repositoryFactory;
    
    /**
     * @return The repository factory
     */
    public RepositoryFactory getRepositoryFactory() {
        return m_repositoryFactory;
    }
    
    /**
     * @param repositoryFactory
     *            The repositoryFactory to set
     */
    public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
        m_repositoryFactory = repositoryFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getRepositoryFactory().getKeywordRepository(), "keywordRepository",
            this);
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return getRepositoryFactory().getKeywordRepository().findById(key,
            false);
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByName(String name) throws DataAccessException,
        DataRetrievalFailureException {
        return getRepositoryFactory().getKeywordRepository()
            .getKeywordByName(name);    
    }
    
    /**
     * {@inheritDoc}
     */
    public List<KeywordDto> getAllKeywords() throws DataAccessException {
        return getRepositoryFactory().getKeywordRepository().findAll();
    }
    
    /**
     * {@inheritDoc}
     */
    public List searchKeywords(QueryObject query) throws DataAccessException {
        return getRepositoryFactory().getKeywordRepository().findByQuery(query);
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException,
        OptimisticLockingFailureException {
        return getRepositoryFactory().getKeywordRepository()
            .saveOrUpdate(keyword);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeKeyword(KeywordDto keyword) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        
        getRepositoryFactory().getKeywordRepository().delete(keyword);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @@attrib.transaction.RequiredRuleBased()
     */
    public void removeKeywords(Collection<KeywordDto> keywords)
        throws DataAccessException, 
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        if (keywords != null) {
            for (KeywordDto kw : keywords) {
                removeKeyword(kw);
            }
        }    
    }
}
