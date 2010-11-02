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
 */
package ch.elca.el4j.core.context;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Listener used to intercept the creation of the module application context.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public interface ModuleApplicationContextCreationListener {
	/**
	 * Will be invoked right after the creation of the bean factory of the module application context.
	 * 
	 * @param beanFactory Is the module application context's bean factory.
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);

	/**
	 * Will be invoked as last step after refresh of the module application context.
	 * 
	 * @param context Is the refreshed module application context.
	 */
	public void finishRefresh(ModuleApplicationContext context);
}
