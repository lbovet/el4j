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

package ch.elca.el4j.tests.core.context;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import ch.elca.el4j.core.context.ModuleApplicationContext;


/**
 * JUnit Test Class for the ModuleApplicationContext.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 */
public class ModuleApplicationContextTest {

	/** The static logger. */
	private static Logger s_logger
		= LoggerFactory.getLogger(ModuleApplicationContextTest.class);
	
	/** Test configuration. */
	String m_fileName1 = "classpath:scenarios/core/context/beans1.xml";

	/** Test configuration. */
	String m_fileName2 = "classpath:scenarios/core/context/beans2.xml";

	/** Test configuration. */
	String m_fileName3 = "classpath:scenarios/core/context/beans3.xml";

	/** Test configuration. */
	String m_allFileNamesInClasspath = "classpath:scenarios/core/context/*.xml";

	/** Test configuration. */
	String m_allFileNamesInBothClasspaths
		= "classpath:scenarios/core/context/**/*.xml";

	/** Mandatory configurations. */
	String m_mandatoryFiles = "classpath*:mandatory/*.xml";

	/**
	 * This test takes a null String as inclusive configuration file and checks
	 * if a NullPointerException is thrown.
	 *
	 */
	@Test
	public void testInclusiveFileNameNull() {
		// Checkstyle: EmptyBlock off
		try {
			new ModuleApplicationContext((String) null, false);
			fail("NullPointerException should have been thrown.");
		} catch (NullPointerException e) {
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes one inclusive configuration file with one bean defined,
	 * loads it into one ApplicationContext with one bean and checks if the bean
	 * was loaded.
	 */
	@Test
	public void testOneInclusiveFileName() {
		ModuleApplicationContext tac
			= new ModuleApplicationContext(m_fileName1, false);
		tac.getBean("Bean1");
	}

	/**
	 * This test takes two inclusive configuration files with one bean defined
	 * each, loads them into one ApplicationContext and checks if the two beans
	 * were loaded.
	 */
	@Test
	public void testTwoInclusiveFileNames() {
		ModuleApplicationContext tac = new ModuleApplicationContext(
				new String[] {m_fileName1, m_fileName2 }, false);

		tac.getBean("Bean1");
		tac.getBean("Bean2");
	}

	/**
	 * This test takes one inclusive configuration file, a null String as
	 * exclusive configuration file and checks if a NullPointerException is
	 * thrown.
	 */
	@Test
	public void testOneInclusiveFileNameExclusiveFileNamesIsNull() {
		// Checkstyle: EmptyBlock off
		try {
			new ModuleApplicationContext(m_fileName1, (String) null, false);
			fail("NullPointerException should have been thrown.");
		} catch (NullPointerException e) {
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes one inclusive configuration file, an empty String as
	 * exclusive configuration file, loads them into one ApplicationContext and
	 * checks if the defined bean was loaded.
	 */
	@Test
	public void testOneInclusiveFileNameExclusiveFileNamesIsEmpty() {
		ModuleApplicationContext tac = new ModuleApplicationContext(m_fileName1,
				"", false);
		tac.getBean("Bean1");
	}

	/**
	 * This test takes an inclusive configuration files with one bean defined,
	 * another exclusive configuration file, loads them into one
	 * ApplicationContext and checks if the bean was loaded and no exception is
	 * thrown since the exclusive configuration file defines a bean which is not
	 * in an inclusive configuration file.
	 */
	@Test
	public void testOneInclusiveFileNamesAnotherExclusiveFileName() {
		ModuleApplicationContext tac
			= new ModuleApplicationContext(m_fileName1, m_fileName2, false);
		tac.getBean("Bean1");
		// Checkstyle: EmptyBlock off
		try {
			tac.getBean("Bean2");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes two inclusive configuration files, one of them as
	 * exclusive configuration file, loads them into one ApplicationContext and
	 * checks if one bean was loaded and the other not.
	 */
	@Test
	public void testTwoInclusiveFileNamesOneExclusiveFileName() {
		ModuleApplicationContext tac = new ModuleApplicationContext(
				new String[] {m_fileName1, m_fileName2 }, m_fileName2, false);
		tac.getBean("Bean1");
		// Checkstyle: EmptyBlock off
		try {
			tac.getBean("Bean2");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes all configuration files in the classpath via '*.xml'
	 * ant-style pattern, two exclusive configuration files in this classpath,
	 * loads them into one ApplicationContext and checks if the correct bean was
	 * loaded and the three other beans were not loaded.
	 */
	@Test
	public void testAllInClasspathFileNamesMinusTwoExclusiveFileNames() {
		ModuleApplicationContext tac = new ModuleApplicationContext(
			m_allFileNamesInClasspath,
			new String[] {m_fileName1, m_fileName3}, false);
		// Checkstyle: EmptyBlock off
		try {
			tac.getBean("Bean1");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		tac.getBean("Bean2");

		try {
			tac.getBean("Bean3");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean4");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes all configuration files in the classpath via '*.xml'
	 * ant-style pattern, all configuration files via '*.xml' ant-style pattern
	 * in the classpath as exclusive configuration files, loads them into one
	 * ApplicationContext and checks if no bean was loaded.
	 */
	@Test
	public void testAllInClasspathFileNamesMinusAllInClasspathFileNames() {
		ModuleApplicationContext tac = new ModuleApplicationContext(
				m_allFileNamesInClasspath, m_allFileNamesInClasspath, false);
		// Checkstyle: EmptyBlock off
		try {
			tac.getBean("Bean1");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean2");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean3");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean4");
			fail("NoSuchBeanDefinitionException should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes all configuration files in both classpaths via '*\*.xml'
	 * ant-style pattern, all configuration files via '*.xml' ant-style pattern
	 * in one of the filepaths as exclusive configuration files, loads them into
	 * one ApplicationContext and checks if the correct bean was loaded.
	 */
	@Test
	public void testAllInBothClasspathsMinusAllInFilepath1() {
		ModuleApplicationContext tac = new ModuleApplicationContext(
			m_allFileNamesInBothClasspaths, m_allFileNamesInClasspath, false);
		// Checkstyle: EmptyBlock off
		try {
			tac.getBean("Bean1");
			fail("NoSuchBeanDefinitionExceptiona should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean2");
			fail("NoSuchBeanDefinitionExceptionb should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean3");
			fail("NoSuchBeanDefinitionExceptionc should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		try {
			tac.getBean("Bean4");
			fail("NoSuchBeanDefinitionExceptiond should have been thrown.");
		} catch (NoSuchBeanDefinitionException e) {
		}

		tac.getBean("Bean5");
		// Checkstyle: EmptyBlock on
	}

	/**
	 * This test takes an existing file and a non existing file, tries to load
	 * them into one ApplicationContext and checks if the correct bean was
	 * loaded and no exception was thrown. However, a logger warning should have
	 * been displayed.
	 */
	@Test
	public void testOneExistingInclusiveFileNamePlusOneNonExistingFileName() {
		ModuleApplicationContext tac = new ModuleApplicationContext(
			new String[] {m_fileName1, "non existing file path"}, false);
		tac.getBean("Bean1");
	}

	/**
	 * This test takes twice the same configuration file, sets
	 * allowBeanOverridingDefinition to false and checks if a
	 * BeanDefinitionStoreException is thrown.
	 */
	@Test
	public void testBeanDefinitionOverridingIsFalse() {
		// Checkstyle: EmptyBlock off
		try {
			new ModuleApplicationContext(
				new String[] {m_fileName1, m_fileName1}, false);
			fail("BeanDefinitionStoreException should have been thrown.");
		} catch (BeanDefinitionStoreException e) {
		}
		// Checkstyle: EmptyBlock on
	}
	
	/**
	 * This test has to be verified by the user. Between the two warn logger
	 * outputs, no other warn logger should appear. Especially not the one
	 * saying that 'classpath*:mandatory/*.xml' was not loaded.
	 */
	@Test
	public void testMandatoryWarning() {
		s_logger.warn("There should be no message with level at least 'WARN'"
				+ "between THIS");
		new ModuleApplicationContext(
					new String[] {m_mandatoryFiles, m_fileName1}, false);
		s_logger.warn("and THIS message.");
	}
}
