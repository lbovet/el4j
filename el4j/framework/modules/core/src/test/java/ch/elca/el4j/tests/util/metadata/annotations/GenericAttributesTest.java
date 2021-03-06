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

package ch.elca.el4j.tests.util.metadata.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

// Checkstyle: MagicNumber off

/**
 * JUnit test for the GenericMetaDataAdvisor class in combination with
 * Java 5 Annotations.
 *
 * Please be sure to compile the Commons Attributes before launching this test.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 */
public class GenericAttributesTest {
	/**
	 * Is the spring bean config location prefix for this test.
	 */
	public static final String SPRING_BEAN_CONFIG_LOCATION
		= "classpath:scenarios/util/metadata/annotations/";
	
	/**
	 * This test loads an application context with an AutoProxy, a
	 * GenericAttributeAdvisor and a class with declared attributes. The
	 * interceptor will be injected via contructor argument to the advisor. It
	 * calculates the result of a calculation which is only correct if the
	 * interception worked correctly.
	 */
	@Test
	public void testInterceptorInjectedViaConstructor() {

		ApplicationContext ac = new ModuleApplicationContext(
			SPRING_BEAN_CONFIG_LOCATION + "beansViaConstructor.xml", false);

		Foo foo = (Foo) ac.getBean("foo");

		// Although we multiply the FooImpl.BASE number with 0, the result is 15
		// since the method interceptor multiplies the FooImpl.BASE number with
		// 5 instead of 0.

		assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
			FooImpl.BASE * 5, foo.test(0));

	}

	/**
	 * This test loads an application context with an AutoProxy, a
	 * GenericAttributeAdvisor and a class with declared attributes. The
	 * interceptor will be injected via setter method to the advisor. It
	 * calculates the result of a calculation which is only correct if the
	 * interception worked correctly.
	 */
	@Test
	public void testInterceptorInjectedViaSetter() {

		ApplicationContext ac = new ModuleApplicationContext(
			SPRING_BEAN_CONFIG_LOCATION + "beansViaSetter.xml", false);

		Foo foo = (Foo) ac.getBean("foo");

		// Although we multiply the FooImpl.BASE number with 0, the result is 15
		// since the method interceptor multiplies the FooImpl.BASE number with
		// 5 instead of 0.
		assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
			FooImpl.BASE * 5, foo.test(0));
	}

	/**
	 * This test loads an application context with an AutoProxy, a
	 * GenericAttributeAdvisor and a class with declared attributes.
	 * Additionally, the AttributeSource and the Attributes implementation are
	 * injected via configuration file and not set automatically like in the
	 * other tests. It calculates the result of a calculation which is only
	 * correct if the interception worked correctly.
	 */
	@Test
	public void testConfiguredAttributeSource() {

		ApplicationContext ac = new ModuleApplicationContext(
			SPRING_BEAN_CONFIG_LOCATION
				+ "beansWithConfiguredMetaDataSource.xml", false);

		Foo foo = (Foo) ac.getBean("foo");

		// Although we multiply the FooImpl.BASE number with 0, the result is 15
		// since the method interceptor multiplies the FooImpl.BASE number with
		// 5 instead of 0.
		assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
			FooImpl.BASE * 5, foo.test(0));
	}

	/**
	 * This test loads an application context with a class having declared
	 * attributes, an AutoProxy and two GenericAttributeAdvisor with each
	 * defined a different ExampleAttribute and a different Interceptor. The
	 * order of the advisors can be adjusted by setting the property "order" in
	 * the configuration file. The advisor with the lower number will be invoked
	 * first. Therefore, in our case, "exampleInterceptorTwo" has to be the real
	 * interceptor for the target method.
	 */
	@Test
	public void testTwoAdvisors() {

		ApplicationContext ac = new ModuleApplicationContext(
			SPRING_BEAN_CONFIG_LOCATION + "beansTwoAdvisors.xml", false);

		Foo foo = (Foo) ac.getBean("foo");

		// Although we multiply the FooImpl.BASE number with 0, the result is
		// 270 since the method interceptor multiplies the FooImpl.BASE number
		// with 90 instead of 0.
		assertEquals("The result should be " + FooImpl.BASE * 9
			* ExampleAnnotationTwo.CONSTANT_FACTOR + ".", FooImpl.BASE * 9
			* ExampleAnnotationTwo.CONSTANT_FACTOR, foo.test(0));

	}

	/**
	 * This test loads an application context with an AutoProxy, one
	 * GenericAttributeAdvisor, a class with declared attributes, but no
	 * interceptor are defined. This test checks whether an exception is thrown.
	 */
	@Test
	public void testAdvisorWithNoInterceptor() {
		// Checkstyle: EmptyBlock off
		try {
			new ModuleApplicationContext(
				SPRING_BEAN_CONFIG_LOCATION + "beansNoInterceptor.xml", false);
			fail("A BaseException should have been thrown.");
		} catch (BeansException e) {
			// Expected behaviour
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test loads an application context with an AutoProxy, one
	 * GenericAttributeAdvisor, a class with declared attributes, but no
	 * intercepting Attributes are defined. This test checks whether an
	 * exception is thrown.
	 */
	@Test
	public void testAdvisorWithNoInterceptingAttributes() {
		// Checkstyle: EmptyBlock off
		try {
			new ModuleApplicationContext(
				SPRING_BEAN_CONFIG_LOCATION
					+ "beansNoInterceptingMetaData.xml", false);
			fail("A BaseRTException should have been thrown");
		} catch (Exception e) {
			// Expected behaviour
		}
		// Checkstyle: EmptyBlock on
	}
	
	/**
	 * This test does the same as method
	 * <code>testInterceptorInjectedViaConstructor</code> but it invokes the
	 * method with an inner class parameter. This should test if methods which
	 * has inner classes as parameter as also intercepted.
	 */
	@Test
	public void testInterceptorInjectedViaConstructorInnerClassAsParameter() {

		ApplicationContext ac = new ModuleApplicationContext(
			SPRING_BEAN_CONFIG_LOCATION + "beansViaConstructor.xml", false);

		Foo foo = (Foo) ac.getBean("foo");

		FooImpl.Bar fooBar = new FooImpl.Bar();
		
		// Although we multiply the FooImpl.BASE number with 0, the result is 15
		// since the method interceptor multiplies the FooImpl.BASE number with
		// 5 instead of 0.

		assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
			FooImpl.BASE * 5, foo.test(0, fooBar));

	}
}
//Checkstyle: MagicNumber on
