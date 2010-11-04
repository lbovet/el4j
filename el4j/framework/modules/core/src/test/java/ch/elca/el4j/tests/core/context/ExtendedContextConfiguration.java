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
package ch.elca.el4j.tests.core.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.ContextConfiguration;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;

/**
 * In addition Spring's {@link @ContextConfiguration}, provides 
 * settings for the ModuleApplicationContext to be created.
 * <p>
 * <strong>WARNING</strong>: For the settings to be applied to the ApplicationContext,
 * the contextLoader attribute of the <code>@ContextConfiguration</code>
 * annotation <em>MUST BE A SUBTYPE</em> OF ModuleTestContextLoader.
 * <p>  
 * Typically, you would annotate your test classes as follows:
 * <pre>
 * &#64;RunWith(EL4JJunit4ClassRunner.class)
 * &#64;ExtendedContextConfiguration(exclusiveConfigLocations = {
 * 	   "classpath*:mandatory/refdb-core-config.xml",
 * 	   "classpath*:mandatory/keyword-core-config.xml" },
 *     allowBeanDefinitionOverriding = "true")
 * &#64;ContextConfiguration(
 *     locations = {
 * 	       "classpath*:mandatory/*.xml",
 *	       "classpath*:scenarios/db/raw/*.xml",
 *         ...
 *         },
 *     loader = ModuleTestContextLoader.class)
 * &#64;Transactional
 * public abstract class AbstractJpaDaoTest
 * </pre>
 * The <code>exclusiveConfigLocations</code> parameter is affected by <code>@ContextConfiguration</code>'s
 * <code>inheritLocations</code> attribute, i.e. the paths to be excluded are overridden or merged depending
 * on the value of <code>inheritLocations</code>.
 * <p>
 * The boolean properties of {@link ModuleApplicationContextConfiguration} have to be specified as String
 * values which allows one to only specify the properties whose values vary from the values specified in
 * the superclasses' <code>@ExtendedContextConfiguration</code> specification. 
 * 
 * @see ModuleApplicationContextConfiguration
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExtendedContextConfiguration {

	/**
	 * Exclusive config locations.
	 * The default is an empty string array.
	 * 
	 * Note that the {@link ContextConfiguration#inheritLocations} property
	 * affects the behaviour of exclusiveConfigLocations in the same way
	 * it affects the {@link ContextConfiguration#locations}.
	 */
	String[] exclusiveConfigLocations() default { };

	/**
	 * Indicates if bean definition overriding is enabled.
	 * Must be <code>"true"</code> or <code>"false"</code>.
	 */
	String allowBeanDefinitionOverriding() default "";
	
	/**
	 * Indicates if unordered/unknown resources should be used.
	 * Must be <code>"true"</code> or <code>"false"</code>.
	 */
	String mergeWithOuterResources() default "";
	
	/**
	 * Indicates if the most specific resource should be the last resource
	 * in the fetched resource array. If its value is set to <code>true</code>
	 * and only one resource is requested the least specific resource will be
	 * returned. 
	 * Must be <code>"true"</code> or <code>"false"</code>.
	 */
	String mostSpecificResourceLast() default "";
	
	/**
	 * Indicates if the most specific bean definition counts.
	 * Must be <code>"true"</code> or <code>"false"</code>.
	 */
	String mostSpecificBeanDefinitionCounts() default "";
	
}
