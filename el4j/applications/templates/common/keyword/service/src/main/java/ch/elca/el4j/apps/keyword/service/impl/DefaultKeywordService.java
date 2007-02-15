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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;

/**
 * 
 * This is the default implementation of the keyword service.
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
public class DefaultKeywordService
    implements KeywordService, InitializingBean {
    
    /**
     * Hibernate DAO registry.
     */
    protected DaoRegistry m_daoRegistry;

    /** 
     * Constructor.
     */
    public DefaultKeywordService() { }
    
    /**
     * @return The DAO registry
     */
    public DaoRegistry getDaoRegistry() {
        return m_daoRegistry;
    }
    
    /**
     * @param reg
     *            The DaoRegistry to set
     */
    public void setDaoRegistry(DaoRegistry reg) {
        m_daoRegistry = reg;
    }
    
    /**
     * Returns the DAO for keywords.
     * 
     * @return The DAO for keywords
     */
    protected KeywordDao getKeywordDao() {
        return (KeywordDao) getDaoRegistry().getFor(Keyword.class);
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
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteKeywords(Collection<?> keys)
        throws OptimisticLockingFailureException, DataAccessException {
        if (keys != null) {
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                Object element = it.next();
                if (element instanceof Number) {
                    int key = ((Number) element).intValue();
                    getKeywordDao().delete(key);
                } else if (element instanceof String) {
                    int key = Integer.parseInt((String) element);
                    getKeywordDao().delete(key);
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
