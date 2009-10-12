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
package ch.elca.el4j.core.context.annotations;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;


/**
 * This {@link AnnotationScopeMetadataResolver} searches for {@link LazyInit} annotations on the class and modifies
 * the bean definition according to that. This is a bit hacky because lazy initialization actually has nothing to do
 * with the scope, but no better extension point could be found in Spring 2.5.5. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class LazyInitAwareScopeMetadataResolver extends AnnotationScopeMetadataResolver {

	/** {@inheritDoc} */
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		ScopeMetadata result = super.resolveScopeMetadata(definition);
		if (definition instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition beanDef = (AbstractBeanDefinition) definition;
			
			try {
				beanDef.setLazyInit(Class.forName(beanDef.getBeanClassName()).getAnnotation(LazyInit.class) != null);
			} catch (ClassNotFoundException e) {
				// ignore class
			}
		}
		return result;
	}

}
