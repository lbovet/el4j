/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.tests.util.metadata.attributes;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.tests.util.metadata.attributes.helper.ExampleAttributeTwo;
import ch.elca.el4j.tests.util.metadata.attributes.helper.Foo;
import ch.elca.el4j.tests.util.metadata.attributes.helper.FooImpl;

import junit.framework.TestCase;

// Checkstyle: MagicNumber off

/**
 * JUnit test for the GenericAttributeAdvisor class.
 * 
 * Please be sure to compile the Commons Attributes before launching this test.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
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

        ApplicationContext ac = new ModuleApplicationContext(
            "classpath:scenarios/util/metadata/attributes/beansViaConstructor.xml", 
            false);

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

        ApplicationContext ac = new ModuleApplicationContext(
            "classpath:scenarios/util/metadata/attributes/beansViaSetter.xml", false);

        Foo foo = (Foo) ac.getBean("foo");

        // Although we multiply the FooImpl.BASE number with 0, the result is 15
        // since the method interceptor multiplies the FooImpl.BASE number with
        // 5 instead of 0.
        assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
            FooImpl.BASE * 5, foo.test(0));
    }
    
    //TODO ADH | No longer supported

//    /**
//     * This test loads an application context with an AutoProxy, a
//     * GenericAttributeAdvisor and a class with declared attributes.
//     * Additionally, the AttributeSource and the Attributes implementation are
//     * injected via configuration file and not set automatically like in the
//     * other tests. It calculates the result of a calculation which is only
//     * correct if the interception worked correctly.
//     */
//    public void testConfiguredAttributeSource() {
//
//        ApplicationContext ac = new ModuleApplicationContext(
//            "classpath:scenarios/util/metadata/attributes/" 
//            + "beansWithConfiguredAttributeSource.xml", false);
//
//        Foo foo = (Foo) ac.getBean("foo");
//
//        // Although we multiply the FooImpl.BASE number with 0, the result is 15
//        // since the method interceptor multiplies the FooImpl.BASE number with
//        // 5 instead of 0.
//        assertEquals("The result should be " + FooImpl.BASE * 5 + ".",
//            FooImpl.BASE * 5, foo.test(0));
//    }

    //TODO ADH | Question: Problem: Probably GenericAttributeAdvisor does
    //not create two separate collector instances
    /**
     * This test loads an application context with a class having declared
     * attributes, an AutoProxy and two GenericAttributeAdvisor with each
     * defined a different ExampleAttribute and a different Interceptor. The
     * order of the advisors can be adjusted by setting the property "order" in
     * the configuration file. The advisor with the lower number will be invoked
     * first. Therefore, in our case, "exampleInterceptorTwo" has to be the real
     * interceptor for the target method.
     */
    public void twoAdvisors() {

        // TODO: This test must be corrected! It fails at the moment since the
        // returned result is 15, but the exprected result is 150.
        
        ApplicationContext ac = new ModuleApplicationContext(
            "classpath:scenarios/util/metadata/attributes/beansTwoAdvisors.xml", false);

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
        // Checkstyle: EmptyBlock off
        try {
            new ModuleApplicationContext(
                "classpath:scenarios/util/metadata/attributes/beansNoInterceptor.xml", 
                false);
            
        } catch (Exception e) {
            if (!(e instanceof BeanCreationException)) {
                fail("The thrown Exeception '" + e + "' is not of" 
                    + " type BaseException.");
            } else {
                
                if (!((e.getCause()) instanceof BaseException)) {
                    fail("The thrown Exeception '" + e + "' is not of" 
                        + " type BaseException.");
                    e.printStackTrace();
                } else {
                    //Expected behaviour
                }
            }
        }
        // Checkstyle: EmptyBlock on
    }

    //TODO ADH | No longer necessary
//    /**
//     * This test loads an application context with an AutoProxy, one
//     * GenericAttributeAdvisor, a class with declared attributes, but no
//     * intercepting Attributes are defined. This test checks whether an
//     * exception is thrown.
//     */
//    public void testAdvisorWithNoInterceptingAttributes() {
//        // Checkstyle: EmptyBlock off
//        try {
//            new ModuleApplicationContext(
//                "classpath:scenarios/util/metadata/attributes/" 
//                + "beansNoInterceptingAttributes.xml", false);
//            fail("A BaseRTException should have been thrown");
//        } catch (Exception e) {
//            // Expected behaviour
//        }
//        // Checkstyle: EmptyBlock on
//    }
    
    //TODO ADH | Test does not work
    /**
     * This test does the same as method 
     * <code>testInterceptorInjectedViaConstructor</code> but it invokes the 
     * method with an inner class parameter. This should test if methods which 
     * has inner classes as parameter as also intercepted.
     */
    public void xtestInterceptorInjectedViaConstructorInnerClassAsParameter() {

        ApplicationContext ac = new ModuleApplicationContext(
            "classpath:scenarios/util/metadata/attributes/beansViaConstructor.xml", 
            false);

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
