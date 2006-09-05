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
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dao.KeywordDaoRegistry;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;

/**
 * 
 * This is the default implementation of the keyword service.
 * 
 * @param <DR> The DaoRegistry's type.
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
public class DefaultKeywordService<DR extends KeywordDaoRegistry>
    implements KeywordService, InitializingBean {
    
    /**
     * Hibernate DAO factory.
     */
    private DR m_daoRegistry;

    /** 
     * Constructor.
     */
    public DefaultKeywordService() { }
    
    /**
     * @return The DAO registry
     */
    public DR getDaoRegistry() {
        return m_daoRegistry;
    }
    
    /**
     * @param reg
     *            The DaoRegistry to set
     */
    public void setDaoRegistry(DR reg) {
        m_daoRegistry = reg;
    }
    
    /**
     * Returns the DAO for keywords.
     * 
     * @return The DAO for keywords
     */
    protected KeywordDao getKeywordDao() {
        return getDaoRegistry().getForKeyword();
    }
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getKeywordDao(), "keywordDao",
            this);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public KeywordDto getKeywordByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return getKeywordDao().findById(key,
            false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public KeywordDto getKeywordByName(String name) throws DataAccessException,
        DataRetrievalFailureException {
        return getKeywordDao().getKeywordByName(name);    
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<KeywordDto> getAllKeywords() throws DataAccessException {
        return getKeywordDao().findAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List searchKeywords(QueryObject query) throws DataAccessException {
        return getKeywordDao().findByQuery(query);
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordDto saveKeyword(KeywordDto keyword)
        throws DataAccessException, InsertionFailureException,
        OptimisticLockingFailureException {
        return getKeywordDao().saveOrUpdate(keyword);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeKeyword(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        
        getKeywordDao().delete(
            getKeywordDao().findById(key, true)
        );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeKeywords(Collection<?> keys) throws DataAccessException, 
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        if (keys != null) {
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                Object element = it.next();
                if (element instanceof Number) {
                    int key = ((Number) element).intValue();
                    removeKeyword(key);
                } else if (element instanceof String) {
                    int key = Integer.parseInt((String) element);
                    removeKeyword(key);
                } else {
                    CoreNotificationHelper.notifyMisconfiguration(
                        "Given keys must be of type number or string. "
                        + "Given key element is of type " 
                        + element.getClass() + ".");
                }
            }
        }    
    }
}
