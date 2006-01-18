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

package ch.elca.el4j.tests.core.context;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import ch.elca.el4j.core.context.ModuleApplicationContext;


/**
 * JUnit Test Class for the ModuleApplicationContext.
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
public class ModuleApplicationContextTest extends TestCase {

    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            ModuleApplicationContextTest.class);
    
    /** Test configuration. */
    String m_fileName1 = "classpath:/core/context/beans1.xml";

    /** Test configuration. */
    String m_fileName2 = "classpath:/core/context/beans2.xml";

    /** Test configuration. */
    String m_fileName3 = "classpath:/core/context/beans3.xml";

    /** Test configuration. */
    String m_allFileNamesInClasspath = "classpath:/core/context/*.xml";

    /** Test configuration. */
    String m_allFileNamesInBothClasspaths = "classpath:/core/context/**/*.xml";

    /** Mandatory configurations. */
    String m_mandatoryFiles = "classpath*:mandatory/*.xml";

    /**
     * This test takes a null String as inclusive configuration file and checks
     * if a NullPointerException is thrown.
     *  
     */
    public void testInclusiveFileNameNull() {
        try {
            new ModuleApplicationContext((String) null, false);
            fail("NullPointerException should have been thrown.");
        } catch (NullPointerException e) {
        }
    }

    /**
     * This test takes one inclusive configuration file with one bean defined,
     * loads it into one ApplicationContext with one bean and checks if the bean
     * was loaded.
     *  
     */
    public void testOneInclusiveFileName() {

        ModuleApplicationContext tac = new ModuleApplicationContext(m_fileName1,
                false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }
    }

    /**
     * This test takes two inclusive configuration files with one bean defined
     * each, loads them into one ApplicationContext and checks if the two beans
     * were loaded.
     *  
     */
    public void testTwoInclusiveFileNames() {

        ModuleApplicationContext tac = new ModuleApplicationContext(
                new String[] {m_fileName1, m_fileName2 }, false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }

        try {
            Foo foo2 = (Foo) tac.getBean("Bean2");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }
    }

    /**
     * This test takes one inclusive configuration file, a null String as
     * exclusive configuration file and checks if a NullPointerException is
     * thrown.
     *  
     */
    public void testOneInclusiveFileNameExclusiveFileNamesIsNull() {

        try {
            new ModuleApplicationContext(m_fileName1, (String) null, false);
            fail("NullPointerException should have been thrown.");
        } catch (NullPointerException e) {
        }

    }

    /**
     * This test takes one inclusive configuration file, an empty String as
     * exclusive configuration file, loads them into one ApplicationContext and
     * checks if the defined bean was loaded.
     *  
     */
    public void testOneInclusiveFileNameExclusiveFileNamesIsEmpty() {

        ModuleApplicationContext tac = new ModuleApplicationContext(m_fileName1,
                "", false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }
    }

    /**
     * This test takes an inclusive configuration files with one bean defined,
     * another exclusive configuration file, loads them into one
     * ApplicationContext and checks if the bean was loaded and no exception is
     * thrown since the exclusive configuration file defines a bean which is not
     * in an inclusive configuration file.
     *  
     */
    public void testOneInclusiveFileNamesAnotherExclusiveFileName() {

        ModuleApplicationContext tac = new ModuleApplicationContext(m_fileName1,
                m_fileName2, false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }

        try {
            Foo foo2 = (Foo) tac.getBean("Bean2");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    /**
     * This test takes two inclusive configuration files, one of them as
     * exclusive configuration file, loads them into one ApplicationContext and
     * checks if one bean was loaded and the other not.
     *  
     */
    public void testTwoInclusiveFileNamesOneExclusiveFileName() {

        ModuleApplicationContext tac = new ModuleApplicationContext(
                new String[] {m_fileName1, m_fileName2 }, m_fileName2, false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }

        try {
            Foo foo2 = (Foo) tac.getBean("Bean2");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    /**
     * This test takes all configuration files in the classpath via '*.xml'
     * ant-style pattern, two exclusive configuration files in this classpath,
     * loads them into one ApplicationContext and checks if the correct bean was
     * loaded and the three other beans were not loaded.
     *  
     */
    public void testAllInClasspathFileNamesMinusTwoExclusiveFileNames() {
        
        ModuleApplicationContext tac = new ModuleApplicationContext(
                m_allFileNamesInClasspath, new String[] {m_fileName1,
                    m_fileName3}, false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo2 = (Foo) tac.getBean("Bean2");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }

        try {
            Foo foo3 = (Foo) tac.getBean("Bean3");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo4 = (Foo) tac.getBean("Bean4");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    /**
     * This test takes all configuration files in the classpath via '*.xml'
     * ant-style pattern, all configuration files via '*.xml' ant-style pattern
     * in the classpath as exclusive configuration files, loads them into one
     * ApplicationContext and checks if no bean was loaded.
     *  
     */
    public void testAllInClasspathFileNamesMinusAllInClasspathFileNames() {
        
        ModuleApplicationContext tac = new ModuleApplicationContext(
                m_allFileNamesInClasspath, m_allFileNamesInClasspath, false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo2 = (Foo) tac.getBean("Bean2");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo3 = (Foo) tac.getBean("Bean3");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo4 = (Foo) tac.getBean("Bean4");
            fail("NoSuchBeanDefinitionException should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    /**
     * This test takes all configuration files in both classpaths via '*\*.xml'
     * ant-style pattern, all configuration files via '*.xml' ant-style pattern
     * in one of the filepaths as exclusive configuration files, loads them into
     * one ApplicationContext and checks if the correct bean was loaded.
     *  
     */
    public void testAllInBothClasspathsMinusAllInFilepath1() {
        
        ModuleApplicationContext tac = new ModuleApplicationContext(
                m_allFileNamesInBothClasspaths,
                m_allFileNamesInClasspath,
                false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
            fail("NoSuchBeanDefinitionExceptiona should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo2 = (Foo) tac.getBean("Bean2");
            fail("NoSuchBeanDefinitionExceptionb should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo3 = (Foo) tac.getBean("Bean3");
            fail("NoSuchBeanDefinitionExceptionc should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo4 = (Foo) tac.getBean("Bean4");
            fail("NoSuchBeanDefinitionExceptiond should have been thrown.");
        } catch (NoSuchBeanDefinitionException e) {
        }

        try {
            Foo foo5 = (Foo) tac.getBean("Bean5");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }
    }

    /**
     * This test takes an existing file and a non existing file, tries to load
     * them into one ApplicationContext and checks if the correct bean was
     * loaded and no exception was thrown. However, a logger warning should have
     * been displayed.
     *  
     */
    public void testOneExistingInclusiveFileNamePlusOneNonExistingFileName() {
        
        ModuleApplicationContext tac = new ModuleApplicationContext(
                new String[] {m_fileName1, "non existing file path"}, false);

        try {
            Foo foo1 = (Foo) tac.getBean("Bean1");
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        }
    }

    /**
     * This test takes twice the same configuration file, sets
     * allowBeanOverridingDefinition to false and checks if a
     * BeanDefinitionStoreException is thrown.
     *  
     */
    public void testBeanDefinitionOverridingIsFalse() {

        try {
            ModuleApplicationContext tac = new ModuleApplicationContext(
                    new String[] {m_fileName1, m_fileName1}, false);
            fail("BeanDefinitionStoreException should have been thrown.");
        } catch (BeanDefinitionStoreException e) {
        }
    }
    
    /**
     * This test has to be verified by the user. Between the two warn logger
     * outputs, no other warn logger should appear. Especially not the one
     * saying that 'classpath*:mandatory/*.xml' was not loaded.
     *  
     */
    public void testMandatoryWarning() {

        s_logger.warn("There should be no message with level at least 'WARN'"
                + "between THIS");
        
        new ModuleApplicationContext(
                    new String[] {m_mandatoryFiles, m_fileName1}, false);

        s_logger.warn("and THIS message.");
        
    }
}