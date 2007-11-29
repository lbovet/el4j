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

package ch.elca.j4persist.generic.dao.impl;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ch.elca.j4persist.generic.dao.ConvenienceGenericDao;
import ch.elca.j4persist.generic.dao.DaoRegistry;
import ch.elca.j4persist.generic.dao.GenericDao;
import ch.elca.j4persist.hibernate.dao.ConvenienceHibernateDao;

/**
 * A DaoRegistry where DAOs can be registered.
 * 
 * This version has been enhanced with the DAO auto-creation feature:
 * If no DAO is found in the current mapping, the DaoPrototype prototype bean
 * is used as a basis for an automatically created DAO. The created DAO
 * will have its persistentClass property set to the class that a DAO was
 * initially requested for.
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
 * @author Christoph Baeni (CBA)
 */
public class DefaultDaoRegistry implements DaoRegistry, ApplicationContextAware {

    /** 
     * The map containing the registered DAOs.
     */
    private Map<Class, GenericDao> m_daos;
    
    /**
     * The SessionFactory to use for DAO auto creation 
     */
    private SessionFactory m_SessionFactory;

	private ApplicationContext m_ApplicationContext;
    
	public SessionFactory getSessionFactory() {
		return m_SessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		m_SessionFactory = sessionFactory;
	}
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public GenericDao getFor(Class entityType) {
    	GenericDao dao = (GenericDao) m_daos.get(entityType);
    	
    	/* if no dao was found, try to create one automatically! */
    	if (dao == null) {
    		dao = createFor(entityType);
        	m_daos.put(entityType, dao);
    	}
    	
        return  dao;
    }

    /**
     * @return Returns the registered DAOs.
     */
    public Map<Class, GenericDao> getDaos() {
        return m_daos;
    }

    /**
     * @param daos Registers the DAOs.
     */
    public void setDaos(Map<Class, GenericDao> daos) {
        m_daos = daos;
    }
    
    private ConvenienceGenericDao createFor(Class entityType) {
    	ConvenienceHibernateDao dao = (ConvenienceHibernateDao) m_ApplicationContext.getBean("DaoPrototype");	
    	
    	dao.setPersistentClass(entityType);
    	
    	return dao;	
    }

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		m_ApplicationContext = applicationContext;
	}
}
