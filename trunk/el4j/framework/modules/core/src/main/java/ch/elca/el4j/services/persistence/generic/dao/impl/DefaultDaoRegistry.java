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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.PatternMatchUtils;

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

import net.sf.cglib.proxy.Enhancer;

/**
 * A DaoRegistry where DAOs can be registered either explicitly 
 * (via its map configuration) or implicitly
 * (by collecting all beans that have the GenericDao interface). <br>
 *
 *  This can be used together with
 *   <ul>
 *	  <li> context:component-scan configuration setting and the 
 *		   @AutocollectedGenericDao annotation to load all DAOs with this
 *         annotation into the spring application context.
 *    <li> {@link HibernateSessionFactoryInjectorPostProcessor} or
 *         {@link IbatisSqlMapClientTemplateInjectorBeanPostProcessor}
 *         to automatically set the session factory/
 *        sql map client template.
 *   </ul>
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
public class DefaultDaoRegistry implements DaoRegistry,
	ApplicationContextAware {

	/**
	 * Private logger of this class.
	 */
	private static Log s_logger
		= LogFactory.getLog(DefaultDaoRegistry.class);
	
	/**
	 * The map containing the registered DAOs.
	 */
	protected Map<Class<?>, GenericDao<?>> m_daos
		= new HashMap<Class<?>, GenericDao<?>>();

	/** 
	 * The application context. 
	 */
	protected ApplicationContext m_applicationContext;
	
	/**
	 * The dao matching pattern. All GenericDaos whose names match this pattern
	 * are collected. 
	 */
	protected String m_daoNamePattern = "*";
	
	/** was {@link initDaosFromSpringBeans} already called? */
	protected boolean m_initialized = false;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> GenericDao<T> getFor(Class<T> entityType) {
		
		if ((!m_initialized) && m_collectDaos) {
			m_initialized = true;
			initDaosFromSpringBeans();
		}
		
		// ensure this works when the entityType is proxied by an cglib proxy:
		//  Thanks Ky (QKP) for the hint!
		if (Enhancer.isEnhanced(entityType)) {
			// "undo" cglib proxying:
			entityType = (Class<T>) entityType.getSuperclass();
		}
		
		GenericDao<T> candidateReturn = (GenericDao<T>) m_daos.get(entityType);
		
		if (candidateReturn != null) {
			return candidateReturn;
		} else if (Proxy.isProxyClass(entityType)) {
			// if a jdk proxy and candidateReturn is null, try improving
			Class[] otherPossibilities
				= AopProxyUtils.proxiedUserInterfaces(entityType);
			if (otherPossibilities != null) {
				s_logger.info("Trying to unwrap JDK proxy to get DAO for type");
				for (Class c : otherPossibilities) {
					candidateReturn = (GenericDao<T>) m_daos.get(c);
					if (candidateReturn != null) {
						return candidateReturn;
					}
				}
			}
		}
		// we give up
		return null;

	}

	/**
	 * Set a new DAO name pattern. Only DAOs whose bean names match this pattern
	 * are collected. Allowed wildcards are '*' which match any characters,
	 * the default is {@code "*"} which matches all DAOs.
	 * @param namePattern The name pattern to set.
	 */
	public void setNamePattern(String namePattern) {
		m_daoNamePattern = namePattern;
		
	}
	
	/**
	 * Load all GenericDaos from this spring bean's bean factory.
	 */
	protected void initDaosFromSpringBeans() {
		String[] beanNamesToLoad
			= m_applicationContext.getBeanNamesForType(GenericDao.class);
		for (String name : beanNamesToLoad) {
			if (!PatternMatchUtils.simpleMatch(m_daoNamePattern, name)) {
				// Doesn't match - so skip it.
				continue;
			}
			
			GenericDao<?> dao = (GenericDao<?>)
				m_applicationContext.getBean(name);
			
			// avoid adding a DAO again
			if (!m_daos.values().contains(dao)) {
				initDao(dao);
				m_daos.put(dao.getPersistentClass(), dao);
			}
		}
	}
	
	/**
	 * This method can be overridden by child classes to initialize
	 *  all DAOs even further.
	 *  @param dao The dao to initialize.
	 */
	protected void initDao(GenericDao<?> dao) {
	}
	
	/** {@inheritDoc} */
	public Map<Class<?>, ? extends GenericDao<?>> getDaos() {
		return m_daos;
	}

	/**
	 * @param daos Registers the DAOs.
	 */
	public void setDaos(Map<Class<?>, GenericDao<?>> daos) {
		m_daos = daos;
		for (GenericDao<?> dao : daos.values()) {
			initDao(dao);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
		m_applicationContext = applicationContext;
	}
	
	/**
	 * Whether to collect DAOs automatically.
	 */
	protected boolean m_collectDaos = true;

	/**
	 * See {@link setCollectDaos}.
	 * @return Whether to collect DAOs automatically.
	 */
	public boolean isCollectDaos() {
		return m_collectDaos;
	}
	
	/**
	 * By default we automatically collect here all generic DAOs from the spring
	 *  application context (all DAOs that implement the GenericDao interface).
	 *  This setter method allows to change this default.
	 * @param collectDaos The new value for collecting daos.
	 */
	public void setCollectDaos(boolean collectDaos) {
		m_collectDaos = collectDaos;
	}
}
