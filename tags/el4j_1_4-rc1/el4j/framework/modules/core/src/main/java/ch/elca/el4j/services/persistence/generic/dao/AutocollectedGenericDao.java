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
package ch.elca.el4j.services.persistence.generic.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;

/**
 * Indicates that a GenericDao implementation should be added as singleton bean to spring
 *  (and in a second step (by the @link {@link DefaultDaoRegistry}) to the DAO Registry). <a>
 *  
 *  You can indicate as parameter the desired <code>id</code> for the spring bean. <a>
 *  
 *  Requires config that looks something like the following (see sample for more details): <a>
 *  
 *  <pre>
 *  1) collect GenericDao implementations with this annotation <br>
 *  &lt;!--  This section scans for DAOs annotated with @AutocollectedGenericDao that should be 
	       added to the spring application context (as beans). Later, the DAO Registry
	       automatically collects these DAOs. --&gt;
       &lt;!-- 	The attribute base-packages indicates the packages where we look for DAOs  --&gt;	       
	&lt;context:component-scan use-default-filters="false"
		annotation-config="false"	
		base-package="ch.elca.el4j.apps.keyword.dao, ch.elca.el4j.apps.refdb.dao"&gt;
		&lt;context:include-filter type="annotation"
			expression="ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao" /&gt;
	&lt;/context:component-scan&gt;

   2) set up dao registry <br>
	&lt;!-- Allows to register DAOs. Automatically collects all GenericDaos from the application context --&gt;		
	&lt;bean id="daoRegistry"
		class="ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry"/&gt;
	

   3) init the session factory on the DAOs <br>
	&lt;!-- Inits the session factory in all the GenericDaos registered in the spring application context--&gt;
	&lt;bean id="injectionPostProcessor"
		class="ch.elca.el4j.services.persistence.hibernate.dao.HibernateSessionFactoryInjectorBeanPostProcessor" /&gt;  
 * </pre>
 *
 * @author Philipp Oser (POS)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AutocollectedGenericDao {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     */
    String value() default "";    
    
}
