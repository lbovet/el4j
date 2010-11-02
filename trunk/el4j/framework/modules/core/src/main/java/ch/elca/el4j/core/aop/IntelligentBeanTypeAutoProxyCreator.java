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

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;

/**
 * Intelligent bean type autoproxy creator. Will not create a new
 * proxy for a given bean if this bean is already a proxy bean. All class
 * members (plus getter and setter for them) do just take place in this class,
 * because class {@link AbstractAutoProxyCreator} hides them.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class IntelligentBeanTypeAutoProxyCreator extends BeanTypeAutoProxyCreator {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2417610783882720300L;

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
		Class deproxiedBeanClass = IntelligentAdvisorAutoProxyCreator.deproxyBeanClass(
			beanClass, beanName, getBeanFactory());
		return super.getAdvicesAndAdvisorsForBean(deproxiedBeanClass, beanName, targetSource);
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
	 * Here we additionally de-proxy beans (to avoid that certain applications of interceptors fail).
	 * {@inheritDoc}
	 */
	@Override	
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource targetSource) {
		Class bClass = IntelligentAdvisorAutoProxyCreator.deproxyBeanClass(beanClass, beanName, getBeanFactory());

		return super.getAdvicesAndAdvisorsForBean(bClass, beanName, targetSource);
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
}
