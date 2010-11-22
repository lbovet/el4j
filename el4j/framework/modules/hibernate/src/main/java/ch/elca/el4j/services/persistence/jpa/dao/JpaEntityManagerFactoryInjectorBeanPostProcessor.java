/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
 */package ch.elca.el4j.services.persistence.jpa.dao;

import java.lang.reflect.Method;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Inject the entity manager factory in GenericDaos (or other daos) if needed.
 *  It gets the entityManagerFactory from the spring context
 *   by using the default name {@link ENTITY_MANAGER_FACTORY_BEAN_DEFAULT_NAME} or
 *   via its setter method.
 *
 * @svnLink $Revision: 3875 $;$Date: 2009-08-04 14:35:53 +0200 (Di, 04 Aug 2009) $;$Author: swismer $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/HibernateSessionFactoryInjectorBeanPostProcessor.java $
 *
 * @author Simon Stelling (SST)
 */
public class JpaEntityManagerFactoryInjectorBeanPostProcessor
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware {

	private static final Logger s_logger= LoggerFactory.getLogger(JpaEntityManagerFactoryInjectorBeanPostProcessor.class);

	/**
	 * The default name for the property of the session factory.
	 */
	public static final String ENTITY_MANAGER_FACTORY_BEAN_DEFAULT_NAME = "entityManagerFactory";
	
	private int order = Ordered.LOWEST_PRECEDENCE;
		

	/**
	 * Initiates the real work.
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		s_logger.debug("Treating bean with name:" + beanName);
		if (ConvenienceGenericJpaDao.class.isAssignableFrom(bean.getClass())) {
			s_logger.debug("init dao with name:" + beanName);
			initDao((ConvenienceGenericJpaDao<?, ?>) bean);
		}
		return bean;
	}

	/** {@inheritDoc} */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	/**
	 * Try to init the sessionFactory of the bean.
	 * @param dao
	 */
	protected void initDao(ConvenienceGenericJpaDao<?, ?> dao) {
		if (getEntityManagerFactory() != null) {
			try {
				Method setter = dao.getClass().getMethod(
					"setEntityManagerFactory", EntityManagerFactory.class);
				setter.invoke(dao, getEntityManagerFactory());
				s_logger.debug("value set in dao");
				
			} catch (Exception e) {
				// ignore problems
				s_logger.info("problem when auto-setting entityManagerFactory", e);

			}
		}
	}

	/**
	 * Gets the session factory (from spring context if needed).
	 * @return
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		if ((m_entityManagerFactory == null) && (m_applicationContext != null)) {
			// try to locate the session factory
			if (m_applicationContext.containsBean(ENTITY_MANAGER_FACTORY_BEAN_DEFAULT_NAME)) {
				m_entityManagerFactory = (EntityManagerFactory)
					m_applicationContext.getBean(ENTITY_MANAGER_FACTORY_BEAN_DEFAULT_NAME);
				Reject.ifNull(m_entityManagerFactory, "session factory must not be null!");
			}
		}
		return m_entityManagerFactory;
	}

	/**
	 * You can either set the session factory explicitly or
	 *  have the factory load the session factory implicitly (by name).
	 * @param factory
	 */
	public void setEntityManagerFactory(EntityManagerFactory factory) {
		m_entityManagerFactory = factory;
	}

	protected EntityManagerFactory m_entityManagerFactory;

	private ApplicationContext m_applicationContext;

	public void setOrder(int order) {
		this.order = order;
	}

	/** {@inheritDoc} */
	public int getOrder() {
		return this.order;
	}

	/** {@inheritDoc} */
	public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
		
		m_applicationContext = applicationContext;
	}
}
