/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.generic.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

/**
 * A DaoRegistry where DAOs can be registered. This class also provides an
 * infrastructure to inject depedencies and initialize the registered DAOs.
 * 
 * @param <D> The type of DAOs managed by this class.
 * 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/core/src/main/java/ch/elca/el4j/services/persistence/generic/dao/impl/SettableRepositoryRegistry.java $",
 *    "$Revision:1040 $",
 *    "$Date:2006-09-04 09:11:56 +0000 (Mo, 04 Sep 2006) $",
 *    "$Author:mathey $"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class SettableDaoRegistry<D extends GenericDao<?>> 
    implements DaoRegistry, InitializingBean {
    
    /** The preinstantiated DAOs. */
    private D[] m_presetDaos;
    
    /** The already created DAOs. */
    private Map<Class<?>, D> m_daos
        = new HashMap<Class<?>, D>();
    
    /** 
     * Injects depedencies into the given DAO.
     * 
     * @param dao The DAO to inject dependencies into
     */
    protected void injectInto(D dao) { }
    
    /**
     * Registers the passed DAO.
     * 
     * @param dao The DAO to register
     */
    public void register(D dao) {
        injectInto(dao);
        m_daos.put(dao.getPersistentClass(), dao);        
    }
    
    /**
     * Registers the passed DAOs.
     * 
     * @param daos The DAOs to register
     */
    public void register(D... daos) {
        for (D dao : daos) {
            register(dao);
        }
    }
    
    /**
     * Schedules the passed DAOs for registration once this registry's
     * initialization is complete.
     * 
     *  @param daos The DAOs to be scheduled for registration
     */
    public void setDaos(D... daos) {
        m_presetDaos = daos;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> GenericDao<T> getFor(Class<T> entityType) {
        return (GenericDao<T>) m_daos.get(entityType);
    }

    /** {@inheritDoc} */
    public void afterPropertiesSet() throws Exception {
        if (m_presetDaos != null) {
            for (D dao : m_presetDaos) {
                register(dao);
                
                // TODO provide better BeanFactory illusion
                if (dao instanceof InitializingBean) {
                    ((InitializingBean) dao).afterPropertiesSet();
                }
            }
        }
    }
}