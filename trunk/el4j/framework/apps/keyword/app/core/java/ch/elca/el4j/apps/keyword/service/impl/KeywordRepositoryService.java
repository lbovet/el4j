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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.repository.KeywordRepository;
import ch.elca.el4j.apps.keyword.repository.KeywordRepositoryRegistry;
import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;

/**
 * 
 * This class is the repository-specific implementation of the keyword service.
 * 
 * @param <RR> The RepositoryRegistry's type.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 * @author Adrian Moos (AMS)
 */
public class KeywordRepositoryService<RR extends KeywordRepositoryRegistry>
    implements KeywordService, InitializingBean {
    
    /**
     * Hibernate repository factory.
     */
    private RR m_repositoryRegistry;
    
    /**
     * @return The repository registry
     */
    public RR getRepositoryRegistry() {
        return m_repositoryRegistry;
    }
    
    /**
     * @param reg
     *            The repositoryRegistry to set
     */
    public void setRepositoryRegistry(RR reg) {
        m_repositoryRegistry = reg;
    }
    
    /**
     * Returns the repository for keywords.
     */
    protected KeywordRepository getKeywordRepository() {
        return getRepositoryRegistry().getForKeyword();
    }
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getKeywordRepository(), "keywordRepository",
            this);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public KeywordDto getKeywordByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return getKeywordRepository().findById(key,
            false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public KeywordDto getKeywordByName(String name) throws DataAccessException,
        DataRetrievalFailureException {
        return getKeywordRepository().getKeywordByName(name);    
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<KeywordDto> getAllKeywords() throws DataAccessException {
        return getKeywordRepository().findAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List searchKeywords(QueryObject query) throws DataAccessException {
        return getKeywordRepository().findByQuery(query);
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException,
        OptimisticLockingFailureException {
        return getKeywordRepository().saveOrUpdate(keyword);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeKeyword(KeywordDto keyword) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        
        getKeywordRepository().delete(keyword);
    }
    
    /**
     * {@inheritDoc}
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
