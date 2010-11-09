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
import ch.elca.el4j.services.persistence.jpa.dao.extentstrategies.ExtentFetcher;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Inject the ExtentFetcher in GenericDaos.
 * It gets the extentFetcher from the spring context
 * by using the default name {@link EXTENT_FETCHER_BEAN_DEFAULT_NAME}.
 *
 * @svnLink $Revision: 3875 $;$Date: 2009-08-04 14:35:53 +0200 (Di, 04 Aug 2009) $;$Author: swismer $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/HibernateSessionFactoryInjectorBeanPostProcessor.java $
 *
 * @author Simon Stelling (SST)
 */
public class JpaExtentFetcherInjectorBeanPostProcessor
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware {

	/**
	 * The default name for the property of the session factory.
	 */
	public static final String EXTENT_FETCHER_BEAN_DEFAULT_NAME = "extentFetcher";
	
	/**
	 * private logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(JpaExtentFetcherInjectorBeanPostProcessor.class);
	
	/**
	 * Caches the 'extentFetcher' bean from the application context. 
	 */
	protected ExtentFetcher extentFetcher;
	
	/**
	 * the order of this BeanPostProcessor.
	 */
	private int order = Ordered.LOWEST_PRECEDENCE;

	/**
	 * The ApplicationContext from which the extentFetcher bean is obtained.
	 */
	private ApplicationContext m_applicationContext;
	
	/**
	 * Initiates the real work.
	 * @param bean the bean to postprocess
	 * @param beanName the name of the bean
	 * @return the bean
	 * @throws BeansException
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		s_logger.debug("Treating bean with name:" + beanName);
		if (GenericJpaDao.class.isAssignableFrom(bean.getClass())) {
			s_logger.debug("init dao with name:" + beanName);
			initDao((GenericJpaDao<?, ?>) bean);
		}
		return bean;
	}

	/** {@inheritDoc} */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	/**
	 * Try to inject the extentFetcher into the given dao.
	 * @param dao the dao to inject the extentFetcher
	 */
	protected void initDao(GenericJpaDao<?, ?> dao) {
		if (getExtentFetcher() != null) {
			try {
				Method setter = dao.getClass().getMethod(
					"setExtentFetcher", ExtentFetcher.class);
				setter.invoke(dao, getExtentFetcher());
				s_logger.debug("value set in dao");
				
			} catch (Exception e) {
				// ignore problems
				s_logger.error("problem when auto-setting extentFetcher", e);

			}
		} else {
			s_logger.error("no extentFetcher available -- cannot use extent-based fetch methods");
		}
	}

	/**
	 * Gets the session factory (from spring context if needed).
	 * @return the extentFetcher
	 */
	public ExtentFetcher getExtentFetcher() {
		if ((extentFetcher == null) && (m_applicationContext != null)) {
			// try to locate the session factory
			if (m_applicationContext.containsBean(EXTENT_FETCHER_BEAN_DEFAULT_NAME)) {
				extentFetcher = (ExtentFetcher)
					m_applicationContext.getBean(EXTENT_FETCHER_BEAN_DEFAULT_NAME);
			} else {
				s_logger.error("failed to obtain extentFetcher from application context "
					+ "-- cannot use Extent-based fetching");
			}
		}
		return extentFetcher;
	}

	/**
	 * If you do not set the extent fetcher explicitly,
	 * it will be obtained from the application's context.
	 * @param extentFetcher the fetcher to use
	 */
	public void setExtentFetcher(ExtentFetcher extentFetcher) {
		this.extentFetcher = extentFetcher;
	}

	/** {@inheritDoc} */
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
