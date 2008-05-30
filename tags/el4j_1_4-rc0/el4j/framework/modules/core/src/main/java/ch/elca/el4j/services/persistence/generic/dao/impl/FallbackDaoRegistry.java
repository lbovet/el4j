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
package ch.elca.el4j.services.persistence.generic.dao.impl;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

/**
 * This class extends the {@link DefaultDaoRegistry} with a fallback function:
 * If no DAO can be found for an entityType, a generic DAO is created.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class FallbackDaoRegistry extends DefaultDaoRegistry {
    /**
     * The bean name of the fallback DAO prototype.
     */
    private String m_daoPrototypeBeanName;
    
    /** {@inheritDoc} */
    @Override
    public <T> GenericDao<T> getFor(Class<T> entityType) {
        GenericDao<T> dao = super.getFor(entityType);
        
        /* if no dao was found, try to create one automatically! */
        if (dao == null) {
            dao = createFor(entityType);
            m_daos.put(entityType, dao);
        }
        
        return dao;
    }
    
    /**
     * @param <T>   The class of entityType
     * @param entityType
     *              The domain class for which a generic DAO should be returned.
     * @return      A new generic DAO for this entityType
     */
    @SuppressWarnings("unchecked")
    private <T> GenericDao<T> createFor(Class<T> entityType) {
        GenericDao<T> dao = null;
        if (m_daoPrototypeBeanName != null) {
            dao = (GenericDao<T>) m_applicationContext.getBean(
                m_daoPrototypeBeanName);
            if (dao != null) {
                dao.setPersistentClass(entityType);
            }
        }
        
        return dao; 
    }

    /**
     * @return    The bean name of the fallback DAO prototype.
     */
    public String getDaoPrototypeBeanName() {
        return m_daoPrototypeBeanName;
    }

    /**
     * @param daoPrototypeBeanName
     *                             The bean name of the fallback DAO prototype.
     */
    public void setDaoPrototypeBeanName(String daoPrototypeBeanName) {
        m_daoPrototypeBeanName = daoPrototypeBeanName;
    }
}
