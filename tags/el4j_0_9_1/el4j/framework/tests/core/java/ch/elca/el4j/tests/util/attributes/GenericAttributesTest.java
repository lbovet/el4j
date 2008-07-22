/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.util.attributes;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * JUnit test for the GenericAttributeAdvisor class.
 * 
 * Please be sure to compile the Commons Attributes before launching this test.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class GenericAttributesTest extends TestCase {

    /**
     * This test loads an application context with an AutoProxy, a
     * GenericAttributeAdvisor and a class with declared attributes. The
     * interceptor will be injected via contructor argument to the advisor. It
     * calculates the result of a calculation which is only correct if the
     * interception worked correctly.
     */
    public void testInterceptorInjectedViaConstructor() {

        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:util/attributes/beansViaConstructor.xml");

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
    public void testInterceptorInjectedViaSetter() {

        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:util/attributes/beansViaSetter.xml");

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
    public void testConfiguredAttributeSource() {

        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:util/attributes/beansWithConfiguredAttributeSource.xml");

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
    public void testTwoAdvisors() {

        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:util/attributes/beansTwoAdvisors.xml");

        Foo foo = (Foo) ac.getBean("foo");

        // Although we multiply the FooImpl.BASE number with 0, the result is
        // 150 since the method interceptor multiplies the FooImpl.BASE number
        // with 50 instead of 0.
        assertEquals("The result should be " + FooImpl.BASE * 5
            * ExampleAttributeTwo.CONSTANT_FACTOR + ".", FooImpl.BASE * 5
            * ExampleAttributeTwo.CONSTANT_FACTOR, foo.test(0));

    }

    /**
     * This test loads an application context with an AutoProxy, one
     * GenericAttributeAdvisor, a class with declared attributes, but no
     * interceptor are defined. This test checks whether an exception is thrown.
     */
    public void testAdvisorWithNoInterceptor() {

        try {
            ApplicationContext ac = new ClassPathXmlApplicationContext(
                "classpath:util/attributes/beansNoInterceptor.xml");
            fail("A BaseException should have been thrown.");
        } catch (BeansException e) {
            // Expected behaviour
        }
    }

    /**
     * This test loads an application context with an AutoProxy, one
     * GenericAttributeAdvisor, a class with declared attributes, but no
     * intercepting Attributes are defined. This test checks whether an
     * exception is thrown.
     */
    public void testAdvisorWithNoInterceptingAttributes() {

        try {
            ApplicationContext ac = new ClassPathXmlApplicationContext(
                "classpath:util/attributes/beansNoInterceptingAttributes.xml");
            fail("A BaseRTException should have been thrown");
        } catch (Exception e) {
            // Expected behaviour
        }
    }
    
    /**
     * This test does the same as method 
     * <code>testInterceptorInjectedViaConstructor</code> but it invokes the 
     * method with an inner class parameter. This should test if methods which 
     * has inner classes as parameter as also intercepted.
     */
    public void testInterceptorInjectedViaConstructorInnerClassAsParameter() {

        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:util/attributes/beansViaConstructor.xml");

        Foo foo = (Foo) ac.getBean("foo");

        FooImpl.Bar fooBar = new FooImpl.Bar();
        
        // Although we multiply the FooImpl.BASE number with 0, the result is 15
        // since the method interceptor multiplies the FooImpl.BASE number with
        // 5 instead of 0.

        assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
            FooImpl.BASE * 5, foo.test(0, fooBar));

    }
}