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
package ch.elca.el4j.core.aop;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.AopHelper;

/**
 * Intelligent autoproxy creator for advisors. Will not create a new proxy for a
 * given bean if this bean is already a proxy bean. All class members (plus
 * getter and setter for them) do just take place in this class, because class
 * {@link AbstractAutoProxyCreator} hides them.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class IntelligentAdvisorAutoProxyCreator extends DefaultAdvisorAutoProxyCreator implements InitializingBean {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8582646215764283797L;

	/**
	 * Private logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(IntelligentAdvisorAutoProxyCreator.class);
	
	/**
	 * COPYIED FROM SUPERCLASS!
	 *
	 * Names of common interceptors. We must use bean name rather than object
	 * references to handle prototype advisors/interceptors.
	 * Default is the empty array: no common interceptors.
	 */
	private String[] m_interceptorNames = new String[0];
	
	/**
	 * COPYIED FROM SUPERCLASS!
	 *
	 * Default is global AdvisorAdapterRegistry.
	 */
	private AdvisorAdapterRegistry m_advisorAdapterRegistry
		= GlobalAdvisorAdapterRegistry.getInstance();

	/**
	 * COPYIED FROM SUPERCLASS!
	 *
	 * Default is "true"; else, bean-specific interceptors will get applied first.
	 */
	private boolean m_applyCommonInterceptorsFirst = true;
	
	/**
	 * If <code>true</code> (default) the use of advisor name prefix is mandatory.
	 * @see #setUsePrefix(boolean)
	 */
	private boolean forceUseOfAdvisorNamePrefix = true;

	/**
	 * Will not create a new proxy for a given bean if this bean is already
	 * a proxy bean.
	 *
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object createProxy(Class beanClass, String beanName,
		Object[] specificInterceptors, TargetSource targetSource) {
		
		Object proxy = ProxyEnricher.enrichProxy(beanClass, beanName,
			specificInterceptors, targetSource, getInterceptorNames(),
			getBeanFactory(), getAdvisorAdapterRegistry(),
			isApplyCommonInterceptorsFirst());
		
		// If no proxy could be enriched create a new one.
		if (proxy == null) {
			proxy = super.createProxy(beanClass, beanName,
				specificInterceptors, targetSource);
		}
		return proxy;
	}

	/**
	 * Here we additionally de-proxy beans (to avoid that certain applications of interceptors fail).
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override	
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource targetSource) {
		Class deproxiedBeanClass = deproxyBeanClass(beanClass, beanName, getBeanFactory());
		return super.getAdvicesAndAdvisorsForBean(deproxiedBeanClass, beanName, targetSource);
	}

	/**
	 * Finds out if the given class is a generated one of a proxy. If yes, the original class will be returned.
	 * 
	 * @param beanClass Is the class of the bean.
	 * @param beanName Is the name opf the bean.
	 * @param beanFactory Is the beanFactory (to be able to keep this method static).
	 * @return Returns the original class if it is a proxy class. Else the given class will be returned.
	 */
	@SuppressWarnings("unchecked")
	protected static Class deproxyBeanClass(Class beanClass, String beanName, BeanFactory beanFactory) {
		Class deproxiedBeanClass = beanClass;
		if (AopUtils.isCglibProxyClass(beanClass)) {
			deproxiedBeanClass = AopHelper.getClassOfCglibProxyClass(beanClass);
		} else if (Proxy.isProxyClass(beanClass) && (beanFactory instanceof DefaultListableBeanFactory)) {
			DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
			BeanDefinition beanDefinition = factory.getBeanDefinition(beanName);
			if (!beanDefinition.isAbstract()) {
				String beanClassName = beanDefinition.getBeanClassName();
				try {
					// replace the beanClass (if it works - otherwise keep "old" beanClass)
					deproxiedBeanClass = beanClass.getClassLoader().loadClass(beanClassName);
				} catch (ClassNotFoundException e) {
					s_logger.debug("error deproxying beanClass:" + beanClass, e);
				} // ignore error in loading class, just return null
			}
		}
		return deproxiedBeanClass;
	}

	/**
	 * @return Returns the interceptorNames.
	 */
	protected String[] getInterceptorNames() {
		return m_interceptorNames;
	}

	/**
	 * COPYIED FROM SUPERCLASS!
	 * 
	 * Added to have access to the interceptor names.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setInterceptorNames(String[] interceptorNames) {
		m_interceptorNames = interceptorNames;
		super.setInterceptorNames(interceptorNames);
	}

	/**
	 * @return Returns the advisorAdapterRegistry.
	 */
	protected AdvisorAdapterRegistry getAdvisorAdapterRegistry() {
		return m_advisorAdapterRegistry;
	}

	/**
	 * COPYIED FROM SUPERCLASS!
	 * 
	 * Added to have access to the interceptor names.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setAdvisorAdapterRegistry(
		AdvisorAdapterRegistry advisorAdapterRegistry) {
		m_advisorAdapterRegistry = advisorAdapterRegistry;
		super.setAdvisorAdapterRegistry(advisorAdapterRegistry);
	}
	
	/**
	 * @return Returns the applyCommonInterceptorsFirst.
	 */
	protected boolean isApplyCommonInterceptorsFirst() {
		return m_applyCommonInterceptorsFirst;
	}

	/**
	 * COPYIED FROM SUPERCLASS!
	 *
	 * Set whether the common interceptors should be applied before
	 * bean-specific ones. Default is "true"; else, bean-specific interceptors
	 * will get applied first.
	 *
	 * @param applyCommonInterceptorsFirst See method description.
	 */
	@Override
	public void setApplyCommonInterceptorsFirst(
		boolean applyCommonInterceptorsFirst) {
		m_applyCommonInterceptorsFirst = applyCommonInterceptorsFirst;
		super.setApplyCommonInterceptorsFirst(applyCommonInterceptorsFirst);
	}

	/**
	 * @return Returns the forceUseOfAdvisorNamePrefix.
	 */
	public boolean isForceUseOfAdvisorNamePrefix() {
		return forceUseOfAdvisorNamePrefix;
	}

	/**
	 * @param forceUseOfAdvisorNamePrefix Is the forceUseOfAdvisorNamePrefix to set.
	 */
	public void setForceUseOfAdvisorNamePrefix(boolean forceUseOfAdvisorNamePrefix) {
		this.forceUseOfAdvisorNamePrefix = forceUseOfAdvisorNamePrefix;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(!isForceUseOfAdvisorNamePrefix() || isUsePrefix(),
			"Property 'forceUseOfAdvisorNamePrefix' is set to true, so property 'usePrefix' must be true too. "
			+ "This was made to eliminate duplicated used advisors, so interceptors are not applied twice or "
			+ "even more on one bean.");
	}
}
