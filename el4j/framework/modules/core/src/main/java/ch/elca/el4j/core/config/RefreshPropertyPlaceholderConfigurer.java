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
package ch.elca.el4j.core.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * HACK!!!
 *
 * This property placeholder configurer is used to circumvent the problem with
 * the abstract application context. The application context instantiates the
 * ordered bean factory post processor at the same time, so further loaded
 * property placeholder configurers don't have any influence on later loaded
 * property placeholder configurers. See
 * <a href="http://opensource2.atlassian.com/projects/spring/browse/SPR-1657">
 * Spring JIRA entry
 * </a> for more details. This bean must be used as prototype and must have
 * attribute <code>id</code> set.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @deprecated This former hack is no longer required. It is solved in
 *             <code>ModuleApplicationContext</code> and
 *             <code>ModuleWebApplicationContext</code>.
 *
 * @author Alex Mathey (AMA)
 */
@Deprecated
public class RefreshPropertyPlaceholderConfigurer extends
	PropertyPlaceholderConfigurer {

	/**
	 * Flag to mark that this instance has been created by this class or a
	 * subclass of it.
	 */
	private boolean m_selfInstantiated = false;
	
	/**
	 * Is the name of this bean.
	 */
	private String m_beanName;
	
	/**
	 * {@inheritDoc}
	 *
	 * Creates a fresh instance of this class and invokes this method of the
	 * new instance.
	 */
	public void postProcessBeanFactory(
		ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (isSelfInstantiated()) {
			super.postProcessBeanFactory(beanFactory);
		} else {
			String beanName = getBeanName();
			if (!beanFactory.containsBean(beanName)) {
				CoreNotificationHelper.notifyMisconfiguration(
					"Refresh property placeholder configurer with name '"
						+ beanName + "' not found!");
			}
			if (beanFactory.isSingleton(beanName)) {
				CoreNotificationHelper.notifyMisconfiguration(
					"Refresh property placeholder configurer with name '"
						+ beanName + "' must be prototype!");
			}
			RefreshPropertyPlaceholderConfigurer configurer
				= (RefreshPropertyPlaceholderConfigurer) beanFactory.getBean(
					beanName);
			configurer.setSelfInstantiated(true);
			configurer.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * @return Returns the selfInstantiated.
	 */
	protected final boolean isSelfInstantiated() {
		return m_selfInstantiated;
	}

	/**
	 * @param selfInstantiated The selfInstantiated to set.
	 */
	protected final void setSelfInstantiated(boolean selfInstantiated) {
		m_selfInstantiated = selfInstantiated;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setBeanName(String beanName) {
		m_beanName = beanName;
	}

	/**
	 * @return Returns the beanName.
	 */
	public final String getBeanName() {
		return m_beanName;
	}
}
