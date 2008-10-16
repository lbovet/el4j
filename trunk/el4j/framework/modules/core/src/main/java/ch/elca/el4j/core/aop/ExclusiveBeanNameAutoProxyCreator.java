/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * Auto proxy creator that identifies beans to proxy via a list of names.
 * Additionally, it allows you to specify a list of names that must not be
 * proxied. Both list, <code>beanNames</code> and
 * <code>exclusiveBeanNames</code> check for direct, "xxx*", and "*xxx" matches.
 *
 * <p><b>Note</b> if you don't specify an include pattern (i.e. not setting the
 * <code>beanNames</code> property) and you have specified some beans to
 * exclude, then all beans except the excluding ones will be auto-proxied.
 *
 * <p>Exclusion has higher precedence than inclusions.
 *
 * Aditional features of this auto proxy creator:
 *  <ul>
 *   <li>Do not add a second proxy around a bean (if already one exists). This is the
 *        feature of the Intelligent*AutoProxyCreator
 *   <li>Deproxy underlying class, before looking for Advices and Advisors (to avoid that
 *        proxies hide the fact that certain beans should be intercepted).
 *  </ul> 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator
 */
public class ExclusiveBeanNameAutoProxyCreator
	extends BeanNameAutoProxyCreator implements InitializingBean {

	/** Bean names to autoproxy all beans. */
	public static final String[] AUTOPROXY_ALL_BEANS = {"*"};
	
	/** List of bean names that don't have to be advised. */
	private List<String> m_exclusiveBeanNames;
	
	/** Whether there have been inclusive patterns set. */
	private boolean m_hasBeanNames = false;
	
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
	 * */
	private AdvisorAdapterRegistry m_advisorAdapterRegistry
		= GlobalAdvisorAdapterRegistry.getInstance();

	/**
	 * @see #setApplyCommonInterceptorsFirst(boolean)
	 */
	private boolean m_applyCommonInterceptorsFirst;	
	
	/**
	 * Should the output of a {@link FactoryBean} be proxied instead of the factory itself?
	 */
	private boolean m_proxyFactoryBeanOutput = true;
	
	/**
	 * Set the names of the beans that must not automatically get wrapped with
	 * proxies. A name can specify a prefix to match by ending with "*",
	 * e.g. "myBean,tx*" will match the bean named "myBean" and all beans whose
	 * name start with "tx".
	 *
	 * @param exclusiveBeanNames
	 *      The bean names to exclude.
	 */
	public void setExclusiveBeanNames(String[] exclusiveBeanNames) {
		m_exclusiveBeanNames = Arrays.asList(exclusiveBeanNames);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBeanNames(String[] beanNames) {
		if (beanNames.length > 0) {
			m_hasBeanNames = true;
		}
		super.setBeanNames(beanNames);
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws Exception {
		if (!m_hasBeanNames) {
			super.setBeanNames(AUTOPROXY_ALL_BEANS);
		}
	}
	
	/**
	 * Wrap a bean if necessary. If bean is actually a {@link FactoryBean} then wrap it
	 * using {@link GenericProxiedFactoryBean}.
	 * 
	 * {@inheritDoc}
	 */
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
		if (m_proxyFactoryBeanOutput && bean instanceof FactoryBean) {
			return new GenericProxiedFactoryBean((FactoryBean) bean, resolveInterceptorNames());
		} else {
			return super.wrapIfNecessary(bean, beanName, cacheKey);
		}
	}
	
	/**
	 * COPYIED FROM SUPERCLASS!
	 * 
	 * @return    a list of {@link Advisor}s to use for auto proxying
	 */
	private Advisor[] resolveInterceptorNames() {
		ConfigurableBeanFactory cbf = (getBeanFactory() instanceof ConfigurableBeanFactory
			? (ConfigurableBeanFactory) getBeanFactory() : null);
		List<Advisor> advisors = new ArrayList<Advisor>();
		for (int i = 0; i < getInterceptorNames().length; i++) {
			String beanName = getInterceptorNames()[i];
			if (cbf == null || !cbf.isCurrentlyInCreation(beanName)) {
				Object next = getBeanFactory().getBean(beanName);
				advisors.add(getAdvisorAdapterRegistry().wrap(next));
			}
		}
		return advisors.toArray(new Advisor[advisors.size()]);
	}

	/**
	 * Will not create a new proxy for a given bean if this bean is already
	 * a proxy bean.
	 *
	 * {@inheritDoc}
	 */
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
	 * {@inheritDoc}
	 */
	protected Object[] getAdvicesAndAdvisorsForBean(
			Class beanClass, String beanName, TargetSource targetSource) {
		boolean doNotProxy = false;
		if (m_exclusiveBeanNames != null) {
			if (m_exclusiveBeanNames.contains(beanName)) {
				doNotProxy = true;
			} else {
				for (String mappedName : m_exclusiveBeanNames) {
					if (isMatch(beanName, mappedName)) {
						doNotProxy = true;
						break;
					}
				}
			}
		}
		
		if (doNotProxy) {
			return DO_NOT_PROXY;
		} else {
			beanClass = IntelligentAdvisorAutoProxyCreator.
				deproxyBeanClass(beanClass, beanName, getBeanFactory());
			
			return super.getAdvicesAndAdvisorsForBean(
				beanClass, beanName, targetSource);
		}
	}
	
	/**
	 * @return Returns the interceptorNames.
	 */
	protected String[] getInterceptorNames() {
		return m_interceptorNames;
	}

	/**
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
	public void setApplyCommonInterceptorsFirst(
		boolean applyCommonInterceptorsFirst) {
		m_applyCommonInterceptorsFirst = applyCommonInterceptorsFirst;
		super.setApplyCommonInterceptorsFirst(applyCommonInterceptorsFirst);
	}
	
	/**
	 * @return Should the output of a {@link FactoryBean} be proxied instead of the factory itself?
	 */
	public boolean isProxyFactoryBeanOutput() {
		return m_proxyFactoryBeanOutput;
	}

	/**
	 * @param proxyFactoryBeanOutput
	 *            Should the output of a {@link FactoryBean} be proxied instead of the factory itself?
	 */
	public void setProxyFactoryBeanOutput(boolean proxyFactoryBeanOutput) {
		m_proxyFactoryBeanOutput = proxyFactoryBeanOutput;
	}
}
