/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.core.aop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.BeanFactory;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Used to enrich an existing proxy.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ProxyEnricher {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(ProxyEnricher.class);
    
    /**
     * Hide default constructor.
     */
    protected ProxyEnricher() { }

    /**
     * If the target of the given target source is already an advised object, 
     * then this proxy will be enriched with the known advisors. If the 
     * enrichment failed, this method will return <code>null</code>.
     * 
     * @param beanClass
     *            the class of the bean
     * @param beanName
     *            the name of the bean
     * @param specificInterceptors
     *            the set of interceptors that is specific to this bean (may be
     *            empty, but not null)
     * @param targetSource
     *            the TargetSource for the proxy, already pre-configured to
     *            access the bean
     * @param interceptorNames
     *            Are the names of interceptors to add to proxy too.
     * @param beanFactory Is the bean factory.
     * @param advisorAdapterRegistry
     *            Is the advisor adapter registry.
     * @param applyCommonInterceptorsFirst
     *            Flag if common interceptors should be applied before the
     *            specific interceptors.
     * @return Returns the enriched proxy or <code>null</code> if enrichment 
     *         failed for some reason.
     */
    public static Object enrichProxy(Class beanClass, String beanName, 
        Object[] specificInterceptors, TargetSource targetSource,
        String[] interceptorNames, BeanFactory beanFactory,
        AdvisorAdapterRegistry advisorAdapterRegistry,
        boolean applyCommonInterceptorsFirst) {
        boolean shouldCreateNewProxy = shouldCreateNewProxy(
            beanClass, beanName, specificInterceptors, targetSource);
        
        
        // Trying to get the advised object. 
        Advised proxy = null;
        if (!shouldCreateNewProxy) {
            try {
                proxy = (Advised) targetSource.getTarget();
            } catch (Exception e) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Exception while getting target of target source of bean"
                    + " with name " + beanName + " of type " + beanClass 
                    + ".", e);
                shouldCreateNewProxy = true;
            }
        }
        
        // If the given target is already an advised object we enrich this
        // proxy.
        Object result = null;
        if (!shouldCreateNewProxy && proxy != null) {
            result = enrichProxyInternal(beanClass, beanName, 
                specificInterceptors, targetSource, interceptorNames, 
                beanFactory, advisorAdapterRegistry, 
                applyCommonInterceptorsFirst, proxy);
        }
        return result;
    }
    
    /**
     * Dectects if a new proxy should be created for the given bean.
     * 
     * @param beanClass
     *            the class of the bean
     * @param beanName
     *            the name of the bean
     * @param specificInterceptors
     *            the set of interceptors that is specific to this bean (may be
     *            empty, but not null)
     * @param targetSource
     *            the TargetSource for the proxy, already pre-configured to
     *            access the bean
     * @return Returns <code>true</code> if a new proxy should be created for
     *         the given bean.
     */
    public static boolean shouldCreateNewProxy(Class beanClass, 
        String beanName, Object[] specificInterceptors, 
        TargetSource targetSource) {
        boolean result = true;
        try {
            Object targetBean = targetSource.getTarget();
            if (targetBean != null) {
                // test if given target bean is a proxy
                if (targetBean instanceof Advised) {
                    result = false;
                }
            }
        } catch (Exception e) {
            s_logger.warn("Exception occured while trying to detect if bean"
                + " with name " + beanName + " of type " + beanClass 
                + " is already a proxy.", e);
            result = true;
        }
        
        return result;
    }

    /**
     * Enriches the given proxy instead of creating a new proxy.
     * 
     * @param beanClass
     *            the class of the bean
     * @param beanName
     *            the name of the bean
     * @param specificInterceptors
     *            the set of interceptors that is specific to this bean (may be
     *            empty, but not null)
     * @param targetSource
     *            the TargetSource for the proxy, already pre-configured to
     *            access the bean
     * @param interceptorNames
     *            Are the names of interceptors to add to given proxy too.
     * @param beanFactory Is the bean factory.
     * @param advisorAdapterRegistry
     *            Is the advisor adapter registry.
     * @param applyCommonInterceptorsFirst
     *            Flag if common interceptors should be applied before the
     *            common interceptors.
     * @param proxy
     *            Is the proxy (advised) to enrich.
     * @return Returns the enriched proxy.
     * @see #createProxy(Class, String, Object[], TargetSource)
     */
    @SuppressWarnings("unchecked")
    protected static Object enrichProxyInternal(Class beanClass, 
        String beanName, Object[] specificInterceptors,
        TargetSource targetSource, String[] interceptorNames,
        BeanFactory beanFactory, AdvisorAdapterRegistry advisorAdapterRegistry,
        boolean applyCommonInterceptorsFirst, Advised proxy) {
        
        // Collect all interceptors.
        Advisor[] commonInterceptors = resolveInterceptorNames(
            interceptorNames, beanFactory, advisorAdapterRegistry);
        List allInterceptors = new ArrayList();
        if (specificInterceptors != null) {
            allInterceptors.addAll(Arrays.asList(specificInterceptors));
            if (commonInterceptors != null) {
                if (applyCommonInterceptorsFirst) {
                    allInterceptors.addAll(0, 
                        Arrays.asList(commonInterceptors));
                } else {
                    allInterceptors.addAll(Arrays.asList(commonInterceptors));
                }
            }
        }
        if (s_logger.isDebugEnabled()) {
            int nrOfCommonInterceptors = commonInterceptors != null 
                ? commonInterceptors.length : 0;
            int nrOfSpecificInterceptors = specificInterceptors != null 
                ? specificInterceptors.length : 0;
            s_logger.debug("Prepending " + nrOfCommonInterceptors
                + " common interceptors and " + nrOfSpecificInterceptors 
                + " specific interceptors on existing proxy bean with name '" 
                + beanName + "'");
        }
        
        // Reverse order of interceptor list so interceptors will be added 
        // to existing proxy in correct order.
        Collections.reverse(allInterceptors);
        for (Object advice : allInterceptors) {
            Advisor advisor = advisorAdapterRegistry.wrap(advice);
            proxy.addAdvisor(0, advisor);
        }
        
        return proxy;
    }
    
    /**
     * @param interceptorNames Are the interceptor names to resolve.
     * @param beanFactory Is the used bean factory.
     * @param advisorAdapterRegistry Is the used advisor adapter registry.
     * @return Resolves the specified interceptor names to Advisor objects.
     * @see AbstractAutoProxyCreator#resolveInterceptorNames
     */
    protected static Advisor[] resolveInterceptorNames(
        String[] interceptorNames, BeanFactory beanFactory,
        AdvisorAdapterRegistry advisorAdapterRegistry) {
        Advisor[] advisors = new Advisor[interceptorNames.length];
        for (int i = 0; i < interceptorNames.length; i++) {
            Object advice = beanFactory.getBean(interceptorNames[i]);
            advisors[i] = advisorAdapterRegistry.wrap(advice);
        }
        return advisors;
    }
}
