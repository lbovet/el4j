/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

import net.sf.cglib.proxy.Enhancer;

/**
 * A DaoRegistry where DAOs can be registered.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 * @author Alex Mathey (AMA)
 */
public class DefaultDaoRegistry implements DaoRegistry {

    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(DefaultDaoRegistry.class);    
    
    /** 
     * The map containing the registered DAOs.
     */
    private Map<Class<?>, ? extends GenericDao<?>> m_daos;
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> GenericDao<T> getFor(Class<T> entityType) {
        
        // ensure this works when the entityType is proxied by an cglib proxy:
        //  Thanks Ky (QKP) for the hint!
        if (Enhancer.isEnhanced(entityType)) {
            // "undo" cglib proxying:
            entityType = (Class<T>) entityType.getSuperclass(); 
        } 
       
        GenericDao<T> candidateReturn = (GenericDao<T>) m_daos.get(entityType);
        
        if (candidateReturn != null){
            return candidateReturn;            
        } else if (Proxy.isProxyClass(entityType)) {
            // if a jdk proxy and candidateReturn is null, try improving
            Class[] otherPossibilities = 
                AopProxyUtils.proxiedUserInterfaces(entityType);
            if (otherPossibilities != null){
                s_logger.info("Trying to unwrap JDK proxy to get DAO for type");                
                for (Class c :otherPossibilities){
                    candidateReturn = (GenericDao<T>) m_daos.get(c);
                    if (candidateReturn != null){
                        return candidateReturn;
                    }
                }
            }
        }
        // we give up
        return null;

    }

    /**
     * @return Returns the registered DAOs.
     */
    public Map<Class<?>, ? extends GenericDao<?>> getDaos() {
        return m_daos;
    }

    /**
     * @param daos Registers the DAOs.
     */
    public void setDaos(Map<Class<?>, ? extends GenericDao<?>> daos) {
        m_daos = daos;
    }
    
  
}
