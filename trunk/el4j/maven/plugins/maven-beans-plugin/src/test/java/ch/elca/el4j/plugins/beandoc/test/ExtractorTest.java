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
package ch.elca.el4j.plugins.beandoc.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.plugins.beans.ConfigurationExtractor;

import junit.framework.TestCase;

/**
 * Tests for extracting configuration from files.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class ExtractorTest extends TestCase {

	/** Included files. */
	String[] m_expectedIncludes = {
		"classpath*:mandatory/*.xml",
		"classpath:demo/demo*.xml"
	};
	
	/** Excluded files. */
	String[] m_expectedExcludes = {
		"classpath*:exclude-*.xml"
	};
	
	/** Reading from java file. */
	public void testJava() throws Exception {
		doTest("SampleJavaFile.java");
	}
	
	/** Reading from xml file. */
	public void testXml() throws Exception {
		doTest("test.xml");
	}
	
	/** 
	 * Perform a check of the extractor.
	 * @param file The test file. 
	 */
	public void doTest(String file) throws Exception {
		
		Resource res = new ClassPathResource(file);
		
		ConfigurationExtractor ex
			= new ConfigurationExtractor(res.getFile()
				.getAbsoluteFile());
		
		assertEquals(m_expectedIncludes.length, ex.getInclusive().length);
		assertEquals(m_expectedExcludes.length, ex.getExclusive().length);
		
		for (int i = 0; i < m_expectedIncludes.length; i++) {
			assertEquals(m_expectedIncludes[i], ex.getInclusive()[i]);
		}
		
		for (int i = 0; i < m_expectedExcludes.length; i++) {
			assertEquals(m_expectedExcludes[i], ex.getExclusive()[i]);
		}
	}
}
