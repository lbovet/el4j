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

package ch.elca.el4j.services.persistence.ibatis.dao; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Inject the sqlMapClientTemplate in DAOs if needed and possible
 *  (it requires the {@link SqlMapClientDaoSupport} interface). <a>
 *  
 *  It gets the sqlMapClientTemplate from the spring context 
 *   by using the default name {@link SQL_MAP_CLIENT_TEMPLATE_NAME} or 
 *   via its setter method.
 *    
 * @author pos
 *
 */
public class IbatisSqlMapClientTemplateInjectorBeanPostProcessor 
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware {

	protected static Log s_logger= LogFactory.getLog(IbatisSqlMapClientTemplateInjectorBeanPostProcessor.class);	
	
	/** 
	 * The default name under which we look for the sql map client template
	 */
	public static final String SQL_MAP_CLIENT_TEMPLATE_NAME = "convenienceSqlMapClientTemplate";	
	
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
		if (getSqlMapClientTemplate() != null) {
			try {
				if (dao instanceof SqlMapClientDaoSupport) {
					SqlMapClientTemplate template = ((SqlMapClientDaoSupport)dao).getSqlMapClientTemplate(); 
					if ((template != null) && (template.getSqlMapClient() == null)){
						((SqlMapClientDaoSupport)dao).setSqlMapClientTemplate(m_sqlmapClientTemplate);
					}
				}
				s_logger.debug("2value set in dao set"); 
			} catch (Exception e) {
				// ignore problems			
				s_logger.warn("problem when auto-setting sessionFactory ",e);

			}
		}
	} 

	
	public SqlMapClientTemplate getSqlMapClientTemplate() {
		if ((m_sqlmapClientTemplate == null) && (m_applicationContext != null)){
			// try to locate the session factory 
			if (m_applicationContext.containsBean(SQL_MAP_CLIENT_TEMPLATE_NAME)) {
				m_sqlmapClientTemplate = (SqlMapClientTemplate) 
				  m_applicationContext.getBean(SQL_MAP_CLIENT_TEMPLATE_NAME);
				 Reject.ifNull(m_sqlmapClientTemplate, "sql map template must not be null!");
				 Reject.ifNull(m_sqlmapClientTemplate.getSqlMapClient(), "sql map client must not be null!");
			}
		}
		return m_sqlmapClientTemplate;
	}

	/**
	 * You can either set the session factory explicitly or 
	 *  have the factory load the session factory implicitly (by name).
	 * @param factory
	 */
	public void setSessionFactory(SqlMapClientTemplate factory) {
		m_sqlmapClientTemplate = factory;
	}

	protected SqlMapClientTemplate m_sqlmapClientTemplate;

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
