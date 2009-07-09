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
package ch.elca.el4j.tests.maven.plugins.springide;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.maven.plugins.springide.BeanPathResolver;

import junit.framework.TestCase;

/**
 * Test of BeanPathResolver.
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
public class BeanPathResolverTest extends TestCase {

	/** Classpath. */
	private List<URL> m_classpath;
	
	/** Inclusive locations. */
	private String[] m_inclusive;
	
	/** Exclusive locations. */
	private String[] m_exclusive;
	
	/** {@inheritDoc} */
	public void setUp() throws Exception {
		m_inclusive = new String[] {
			"classpath:file/beansInAFile.xml",
			"classpath:beansInAJar.xml"
		};
		m_exclusive = new String[] {};
		m_classpath = new ArrayList<URL>();

		m_classpath.add(new File("target/test-classes/jar/beanJar.jar")
			.getAbsoluteFile().toURL());
		m_classpath.add(new File("target/test-classes/")
			.getAbsoluteFile().toURL());

	}
	
	
	/**
	 * Resolve a sample bean path.
	 * @throws MalformedURLException
	 */
	public void testBeanPath() throws MalformedURLException {
				
		BeanPathResolver r = new BeanPathResolver();
		String[] result = r.resolve(m_inclusive, m_exclusive,
			m_classpath.toArray(new URL[0]),
			new File("target/test-classes").getAbsolutePath());

		/*
		 * Test files:
		 * test-classes/
		 * -- beansInAJar.xml
		 * -- file/
		 *    -- beansInAFile.xml
		 * -- jar/
		 *    -- beanJar.jar
		 *       -- beansInAJar.xml
		 *
		 * The first file (in test-classes/ directly) is in the classpath of the
		 * test runner but is not added because it is not in the classpath we
		 * pass to the resolver. Spring finds it when searching "classpath*:"
		 * but it is filtered out again.
		 */
		assertEquals(2, result.length);
		assertTrue(result[0].startsWith("file:/"));
		assertTrue(result[0].endsWith("beansInAFile.xml"));
		assertTrue(result[1].startsWith("jar:"));
		assertTrue(result[1].endsWith("beansInAJar.xml"));
	}
}
