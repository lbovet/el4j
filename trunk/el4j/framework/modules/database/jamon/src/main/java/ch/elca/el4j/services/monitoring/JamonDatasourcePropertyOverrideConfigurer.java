/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.monitoring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;

/**
 * A property override configurer that makes the data source bean use the JAMon JDBC interceptor.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class JamonDatasourcePropertyOverrideConfigurer extends PropertyOverrideConfigurer {
	
	private static final String DATA_SOURCE_BEAN_NAME = "dataSource";
	private static final String DRIVER_CLASS = "driverClass";
	private static final String JDBC_URL = "jdbcUrl";
	
	/** {@inheritDoc} */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition bd = beanFactory.getBeanDefinition(DATA_SOURCE_BEAN_NAME);
		while (bd.getOriginatingBeanDefinition() != null) {
			bd = bd.getOriginatingBeanDefinition();
		}
		
		String oldDriver = getPropertyValue(bd, DRIVER_CLASS);
		String oldJdbcUrl = getPropertyValue(bd, JDBC_URL);
		
		String newJdbcUrl = oldJdbcUrl.replace("jdbc:", "jdbc:jamon:") + "jamonrealdriver=" + oldDriver;
		
		bd.getPropertyValues().addPropertyValue(DRIVER_CLASS, new TypedStringValue("com.jamonapi.proxy.JAMonDriver"));
		bd.getPropertyValues().addPropertyValue(JDBC_URL, new TypedStringValue(newJdbcUrl));
	}

	/**
	 * @param bd          the beanDefinition
	 * @param property    the property name
	 * @return            the property value
	 */
	private String getPropertyValue(BeanDefinition bd, String property) {
		Object valueObject = bd.getPropertyValues().getPropertyValue(property).getValue();
		if (valueObject instanceof TypedStringValue) {
			return ((TypedStringValue) valueObject).getValue();
		} else {
			return valueObject.toString();
		}
	}
}
