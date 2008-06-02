/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.elca.el4j.services.persistence.hibernate.dao; 

import java.beans.PropertyDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Inject the session factory in GenericDaos (or other daos) if needed.
 *  It gets the sessionFactory from the spring context 
 *   by using the default name {@link SESSION_FACTORY_BEAN_DEFAULT_NAME} or 
 *   via its settor method.
 *    
 * @author pos
 *
 */
public class HibernateSessionFactoryInjectorBeanPostProcessor 
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware {

	protected static Log s_logger= LogFactory.getLog(HibernateSessionFactoryInjectorBeanPostProcessor.class);	

	/** 
	 * The default name for the property of the session factory
	 */
	public static final String SESSION_FACTORY_BEAN_DEFAULT_NAME = "sessionFactory";	
	
	private int order = Ordered.LOWEST_PRECEDENCE; 
		

	/**
	 * Initiates the real work
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		s_logger.debug("Treating bean with name:"+beanName);
		if (GenericDao.class.isAssignableFrom(bean.getClass())) {
			s_logger.debug("init dao with name:"+beanName);			
			initDao((GenericDao<?>)bean);
		}
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	/** 
	 * Try to init the sessionFactory of the bean
	 * @param dao
	 */
	protected void initDao(GenericDao<?> dao) {
		if (getSessionFactory() != null) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor(SESSION_FACTORY_BEAN_DEFAULT_NAME,
															   dao.getClass());
				Object value = pd.getReadMethod().invoke(dao);
				if (value == null) {
					pd.getWriteMethod().invoke(dao,m_sessionFactory);
				}
				s_logger.debug("value set in dao set");
			} catch (Exception e) {
				// ignore problems			
				s_logger.info("problem when auto-setting sessionFactory",e);

			}
		}
	} 

	
	
	/**
	 * Gets the session factory (from spring context if needed)
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		if ((m_sessionFactory == null) && (m_applicationContext != null)){
			// try to locate the session factory 
			if (m_applicationContext.containsBean(SESSION_FACTORY_BEAN_DEFAULT_NAME)) {
				m_sessionFactory = (SessionFactory) 
				  m_applicationContext.getBean(SESSION_FACTORY_BEAN_DEFAULT_NAME);
				 Reject.ifNull(m_sessionFactory, "session factory must not be null!");
			}
		}
		return m_sessionFactory;
	}

	/**
	 * You can either set the session factory explicitly or 
	 *  have the factory load the session factory implicitly (by name).
	 * @param factory
	 */
	public void setSessionFactory(SessionFactory factory) {
		m_sessionFactory = factory;
	}

	protected SessionFactory m_sessionFactory;

	private ApplicationContext m_applicationContext;	

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}	

	public void setApplicationContext(ApplicationContext applicationContext)
	throws BeansException {
		m_applicationContext = applicationContext;
	}
}
