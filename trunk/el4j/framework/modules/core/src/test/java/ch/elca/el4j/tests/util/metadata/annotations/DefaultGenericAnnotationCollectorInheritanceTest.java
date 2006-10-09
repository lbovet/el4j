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
package ch.elca.el4j.tests.util.metadata.annotations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleAbstractClassAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleClassAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleClassAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationFive;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationFour;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationSix;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleInterfaceAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExampleMethodAnnotationOne;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationFive;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationFour;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationThree;
import ch.elca.el4j.tests.util.metadata.annotations.definitions.ExamplePackageAnnotationTwo;
import ch.elca.el4j.tests.util.metadata.annotations.helper.Foo;

import junit.framework.TestCase;

/**
 * This test case tests if the {@link DefaultGenericAnnotationCollector} 
 * collects the annotations like it is configured 
 * (cf. {@link DefaultInheritanceConfiguration}.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Häfeli (ADH)
 */
public class DefaultGenericAnnotationCollectorInheritanceTest extends TestCase {
    
    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(DefaultGenericAnnotationCollectorInheritanceTest.class);
    
    /**
     * TODO
     */
    private enum expectedAnnotations { DEFAULT_INHERITANCE , 
        DEFAULT_INHERITANCE_METHOD};
    
    /**
     * TODO ADH
     */
    private String[] m_testInput;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        m_testInput = new String[1];
        m_testInput[0] = "testInput";
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    /**
     * Tests if the {@link DefaultGenericAnnotationCollector} inherites the
     * annotations from classes as defined in the default configuration 
     * (cf. {@link DefaultInheritanceConfiguration}).
     */
    public void testDefaultInheritanceConfigurationFromClass() {
        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:scenarios/util/metadata/annotations/" 
            + "testDefaultInheritanceConfiguration.xml");

        Foo foo = (Foo) ac.getBean("foo");
        Object[] testResults = foo.metaDataClassInheritance(m_testInput);
        assertTrue(runTest(testResults, 14, expectedAnnotations.DEFAULT_INHERITANCE, foo));   
    }
    
    /**
     * Tests if the {@link DefaultGenericAnnotationCollector} inherites the
     * annotations from methods and classes as defined in the default configuration 
     * (cf. {@link DefaultInheritanceConfiguration}).
     */
    public void testDefaultInheritanceConfigurationFromMethod() {
        ApplicationContext ac = new ClassPathXmlApplicationContext(
            "classpath:scenarios/util/metadata/annotations/" 
            + "testDefaultInheritanceConfiguration.xml");

        Foo foo = (Foo) ac.getBean("foo");
        Object[] testResults = foo.metaDataMethodInheritance(m_testInput);
        assertTrue(runTest(testResults, 12, expectedAnnotations.DEFAULT_INHERITANCE_METHOD, foo));
    }
    
//    /**
//     * TODO
//     * Inheritance Configuration: Get annotations from
//     * - class
//     * - superclass
//     * - interface
//     * - interface on superclass
//     */ 
//    public void testInheritanceTwo() {
//        ApplicationContext ac = new ClassPathXmlApplicationContext(
//           "classpath:scenarios/util/metadata/annotations/testMethodInheritanceTwo.xml");
//        
//        m_foo = (Foo) ac.getBean("foo");
//        assertTrue(runTest(15, 2));
//    }
//    
//    /**
//     * TODO Auto-Generated comment
//     * 
//     * Testing also the configuration.
//     *
//     */
//    public void testNoAnnotation() {
//        ApplicationContext ac = new ClassPathXmlApplicationContext(
//            "classpath:scenarios/util/metadata/annotations/testMethodNoAnnotation.xml");
//        
//        m_foo = (Foo) ac.getBean("foo");
//        Object[] result = ((FooSimpleImpl) m_foo).noAnnotation(m_testInput);
//       
//        assertTrue(result == null);
//    }
//    
//    /**
//     * Tests if just the specified metaData will be collected.     *
//     */
//    public void testSpecificAnnotationSearch() {
//        ApplicationContext ac = new ClassPathXmlApplicationContext(
//            "classpath:scenarios/util/metadata/annotations/testMethodSpecificAnnotationSearch.xml");
//        m_foo = (Foo) ac.getBean("foo");
//        assertTrue(runTest(1, 8));
//    }
//    
//
//    /**
//     * TODO Auto-Generated comment
//     *
//     */
//    public void testGetClassAnnotations() {
//        ApplicationContext ac = new ClassPathXmlApplicationContext(
//                "classpath:scenarios/util/metadata/annotations/testClassInheritanceOne.xml");
//    
//        m_foo = (Foo) ac.getBean("foo");
//        assertTrue(runTest(6, 11));
//    }

    /**
     * Gets annotations from the specified test object (e.g. from the test 
     * framework {@link ch.elca.el4j.tests.util.metadata.annotations.helper}) 
     * and check if the right annotation types and the correct number are
     * returned. The test object has a the method 
     * {@link Foo#metaDataClassInheritance(Object[])} which returns the annotations
     * that the Interceptor got.
     * 
     * @param numberOfExpectedAnnotations
     *                          Count of annotation which has to be returned.
     * @param id
     *                          Id of methods wich checks if the right 
     *                          annotation types are returned.
     * @param foo
     *                          Object which returns the annotations to check.
     * @return <code>true</code> if the collector considered the inheritance 
     * correct, <code>false</code> otherwise.
     */
    private boolean runTest(Object[] testResults, int numberOfExpectedAnnotations, 
        expectedAnnotations id, Foo foo) {
        boolean testResult = true;
        int counter = 0;
        
        
        if (testResults != null) {
            s_logger.info("Test Hierarchie: Method have " 
                + testResults.length + " annotations.");
            
            for (int i = 0; i < testResults.length; i++) {
                /*
                 * Based on the log output, can be checked which annotations are
                 * on the method
                 */
                s_logger.info("Annotation on "
                        + "Method metaDataTester(String[]) found: "
                        + testResults[i].toString());
    
                /*
                 * Check if the correct annotations are on the method. The
                 * correct overwridding is checked by the annotation value.
                 */
                try {
                    switch(id) {
                        
                        case DEFAULT_INHERITANCE:     
                            counter = checkDefaultInheritance(counter, 
                                testResults, i); 
                            break;
                            
                        case DEFAULT_INHERITANCE_METHOD:
                            counter = checkDefaultInheritanceConfigurationFromMethod(counter,
                                testResults, i);
                            break;
                    
//                        case 2:     counter = configurationTwo(counter, testResults, i);
//                                    break;
//                                
//                        case 8:     counter = configurationEight(counter, testResults, i);
//                                    break;
//                                
//                        case 11:    counter = configurationEleven(counter, testResults, i);
//                                    break;
                        
                        default: throw new UnsupportedOperationException("The defined configuration " 
                                + id + " does not exists");
                       
                    }
                    
                    
                } catch (BaseException e) {
                    testResult = false;
                    s_logger.info(e.getMessage());
                }
            }
            
            /* Check if the expected annotations are on the test method (the counter have in this cas the correct number of annotations).
             */
            if (counter != numberOfExpectedAnnotations) {
                testResult = false;
                s_logger.info("Method metaDataTester(String[]) has some wrong annotation types.");
            }
            
            if (testResults.length != numberOfExpectedAnnotations) {
                testResult = false;
                s_logger.info("Method metaDataTester(String[]) has not the correct count of annotations. \n"
                        + "Count of method: " + testResults.length + " Expected: " + numberOfExpectedAnnotations);
            }  
            
        } else {
            testResult = false;
            s_logger.info("Method metaDataTester(String[]) has no annotations.");
        }
    
        return testResult;
    }

    /**
     * TODO ADH
     * 
     * @param result
     * @param counter
     * @param annotationType
     * @param expectedAnnotationValue
     * @return
     * @throws Exception
     */
    private int checkResult(Object result, int counter, Class annotationType, 
            String expectedAnnotationValue) throws BaseException {
        
        
        if (annotationType.isInstance(result)) {
            String value = result.toString();
            value = value.substring(value.indexOf("=") + 1, value.lastIndexOf(")"));
            
            if (value.equals(expectedAnnotationValue)) {
                //do not modify testResult if it is ok, just increase the couter
                //counter must at the and have the same value, as the expected number of annotations
                counter++;
            } else {
                throw new BaseException("Annotation " + annotationType + " does not have the right value. \n" 
                        + "Expected: \t \t" + expectedAnnotationValue + "\n"
                        + "Value of annotation: \t" + value + "\n"
                        + "Probably the overwridding has been failed.");
            }
        }
        return counter;
    }

    /**
     * Inheritance Configuration: Get annotations from
     * - class
     * - interface
     * 
     * TODO ADH | Describe parameters
     * 
     * @param counter
     * @param testResults
     * @param i
     * @return
     * @throws BaseException
     */
    private int checkDefaultInheritance(int counter, Object[] testResults, 
        int i) throws BaseException {
        
        /* Annotations set on method */
        counter = checkResult(testResults[i], counter, 
            ExampleMethodAnnotationOne.class, 
            "ExampleMethodAnnotationOne");

        counter = checkResult(testResults[i], counter, 
            ExampleClassAnnotationTwo.class, 
            "ExampleClassAnnotationTwo: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, 
            ExampleAbstractClassAnnotationThree.class,
            "ExampleAbstractClassAnnotationThree: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, 
            ExampleInterfaceAnnotationFour.class,
            "ExampleInterfaceAnnotationFour: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, 
            ExamplePackageAnnotationFive.class,
            "ExamplePackageAnnotationFive: Overwritten by Method metaDataTester");
        
        /* Annotations inherited from class */

        counter = checkResult(testResults[i], counter, 
            ExampleClassAnnotationOne.class, 
            "ExampleClassAnnotationOne");

        counter = checkResult(testResults[i], counter, 
            ExampleAbstractClassAnnotationTwo.class,
            "ExampleAbstractClassAnnotationTwo: Overwritten by Class FooImpl");

        counter = checkResult(testResults[i], counter, 
            ExampleInterfaceAnnotationThree.class,
            "ExampleInterfaceAnnotationThree: Overwritten by Class FooImpl");

        counter = checkResult(testResults[i], counter, 
            ExamplePackageAnnotationFour.class,
            "ExamplePackageAnnotationFour: Overwritten by Class FooImpl");
        
        /* Annotations inherited from direct interfaces */
        
        counter = checkResult(testResults[i], counter,
            ExampleInterfaceAnnotationFive.class,
            "ExampleInterfaceAnnotationFive: Set from Interface FooBase");
        
        /* Annotations inherited from interfaces on superclasses */
        
        counter = checkResult(testResults[i], counter,
            ExamplePackageAnnotationTwo.class,
            "ExamplePackageAnnotationTwo: Overwritten by Interface Foo");
        
        counter = checkResult(testResults[i], counter,
            ExampleInterfaceAnnotationOne.class,
            "ExampleInterfaceAnnotationOne");
        
        counter = checkResult(testResults[i], counter,
            ExampleInterfaceAnnotationTwo.class,
            "ExampleInterfaceAnnotationTwo");
        
        
        counter = checkResult(testResults[i], counter,
            ExampleInterfaceAnnotationSix.class,
            "ExampleInterfaceAnnotationSix: Set from Interface Base");
        
        return counter;
    }
    
    
    private int checkDefaultInheritanceConfigurationFromMethod(int counter, 
        Object[] testResults, int i) throws BaseException {
        
        /* Annotations set on method */
        counter = checkResult(testResults[i], counter, 
            ExampleMethodAnnotationOne.class, 
            "ExampleMethodAnnotationOne");
        
        counter = checkResult(testResults[i], counter, 
            ExampleInterfaceAnnotationFour.class, 
            "ExampleInterfaceAnnotationFour: "
            + "Overwritten by Method metaDataMethodInheritance");
        
        /* Annotations inherited from method in super class TODO not correct*/
        counter = checkResult(testResults[i], counter, 
            ExampleInterfaceAnnotationThree.class, 
            "ExampleInterfaceAnnotationThree: "
            + "Set from Method metaDataMethodInheritance in Interface Foo");
            
            
           // TODO ADH | Should be - "ExampleInterfaceAnnotationThree: "
           // + "Overwritten by Method metaDataMethodInheritance in "
           // + "Class FooAbstract");
        
        /* Annotations inherited from method in interfaces 
         * implemented by super class (direct and indirect) */
        counter = checkResult(testResults[i], counter, 
            ExampleInterfaceAnnotationTwo.class, 
            "ExampleInterfaceAnnotationTwo: "
            + "Set from Method metaDataMethodInheritance in Interface Foo");
        
        counter = checkResult(testResults[i], counter, 
            ExampleInterfaceAnnotationOne.class, 
            "ExampleInterfaceAnnotationOne: "
            + "Set from Method metaDataMethodInheritance in Interface Base");
        
        /* Annotations inherited from class */
        
        counter = checkResult(testResults[i], counter, 
            ExampleClassAnnotationOne.class, 
            "ExampleClassAnnotationOne");
        
        counter = checkResult(testResults[i], counter, 
            ExampleClassAnnotationTwo.class, 
            "ExampleClassAnnotationTwo");

        counter = checkResult(testResults[i], counter, 
            ExampleAbstractClassAnnotationTwo.class,
            "ExampleAbstractClassAnnotationTwo: Overwritten by Class FooImpl");

        counter = checkResult(testResults[i], counter, 
            ExamplePackageAnnotationFour.class,
            "ExamplePackageAnnotationFour: Overwritten by Class FooImpl");       
               
        /* Annotations inherited from direct interfaces */
        
        counter = checkResult(testResults[i], counter,
            ExampleInterfaceAnnotationFive.class,
            "ExampleInterfaceAnnotationFive: Set from Interface FooBase");
        
        /* Annotations inherited from interfaces on superclasses */
        
        counter = checkResult(testResults[i], counter, 
            ExamplePackageAnnotationTwo.class,
            "ExamplePackageAnnotationTwo: Overwritten by Interface Foo");
        
        counter = checkResult(testResults[i], counter,
            ExampleInterfaceAnnotationSix.class,
            "ExampleInterfaceAnnotationSix: Set from Interface Base");
        
        
        
        
        
        return counter;
    }
    


    /**
     * <p>
     * Configuration of {@link #testInheritanceTwo()}.</p>
     * 
     * <p><b>Parameter</b><br />
     * Same parameters as {@link #checkDefaultInheritance(int, Object[], int)}.</p>
     */
    private int configurationTwo(int counter, Object[] testResults, int i) throws BaseException {
        
        counter = checkResult(testResults[i], counter, ExampleMethodAnnotationOne.class, 
                "ExampleMethodAnnotationOne");

        counter = checkResult(testResults[i], counter, ExampleClassAnnotationTwo.class, 
                "ExampleClassAnnotationTwo: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, ExampleAbstractClassAnnotationThree.class,
                "ExampleAbstractClassAnnotationThree: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationFour.class,
                "ExampleInterfaceAnnotationFour: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, ExamplePackageAnnotationFive.class,
                "ExamplePackageAnnotationFive: Overwritten by Method metaDataTester");

        counter = checkResult(testResults[i], counter, ExampleClassAnnotationOne.class, 
                "ExampleClassAnnotationOne");

        counter = checkResult(testResults[i], counter, ExampleAbstractClassAnnotationTwo.class,
                "ExampleAbstractClassAnnotationTwo: Overwritten by Class FooImpl");
        
        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationThree.class,
                "ExampleInterfaceAnnotationThree: Overwritten by Class FooImpl");

        counter = checkResult(testResults[i], counter, ExamplePackageAnnotationFour.class,
                "ExamplePackageAnnotationFour: Overwritten by Class FooImpl");

        counter = checkResult(testResults[i], counter, ExampleAbstractClassAnnotationOne.class, 
                "ExampleAbstractClassAnnotationOne");

        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationTwo.class,
                "ExampleInterfaceAnnotationTwo: Overwritten by Class FooAbstract");

        counter = checkResult(testResults[i], counter, ExamplePackageAnnotationThree.class,
                "ExamplePackageAnnotationThree: Overwritten by Class FooAbstract");
        
        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationOne.class,
                "ExampleInterfaceAnnotationOne");
        
        counter = checkResult(testResults[i], counter, ExamplePackageAnnotationTwo.class,
                "ExamplePackageAnnotationTwo: Overwritten by Interface Foo");
        
        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationFive.class,
                "ExampleInterfaceAnnotationFive: Set from Interface FooBase");
        
        return counter;
    }
    
    
    /**
     * <p>
     * Configuration of {@link #testGetClassAnnotations()}.</p>
     * 
     * <p><b>Parameter</b><br />
     * Same parameters as {@link #checkDefaultInheritance(int, Object[], int)}.</p>
     */
    private int configurationEleven(int counter, Object[] testResults, int i) throws BaseException {
        
        counter = checkResult(testResults[i], counter, ExampleClassAnnotationOne.class, 
                        "ExampleClassAnnotationOne");
        
        counter = checkResult(testResults[i], counter, ExampleClassAnnotationTwo.class, 
                        "ExampleClassAnnotationTwo");
        
        counter = checkResult(testResults[i], counter, ExampleAbstractClassAnnotationTwo.class, 
                        "ExampleAbstractClassAnnotationTwo: Overwritten by Class FooImpl");
        
        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationThree.class, 
                        "ExampleInterfaceAnnotationThree: Overwritten by Class FooImpl");
        
        counter = checkResult(testResults[i], counter, ExamplePackageAnnotationFour.class, 
                        "ExamplePackageAnnotationFour: Overwritten by Class FooImpl");
        
        counter = checkResult(testResults[i], counter, ExampleInterfaceAnnotationFive.class, 
                        "ExampleInterfaceAnnotationFive: Set from Interface FooBase");
        
        return counter;
    }

    /**
     * <p>
     * Configuration of {@link #testSpecificAnnotationSearch()}.</p>
     * 
     * <p><b>Parameter</b><br />
     * Same parameters as {@link #checkDefaultInheritance(int, Object[], int)}.</p>
     */
    private int configurationEight(int counter, Object[] testResults, int i) throws BaseException {
        counter = checkResult(testResults[i], counter, ExampleMethodAnnotationOne.class, 
               "ExampleMethodAnnotationOne");
        return counter;
    }
    


}
