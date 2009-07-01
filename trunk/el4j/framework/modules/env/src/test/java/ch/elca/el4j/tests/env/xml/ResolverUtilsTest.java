/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.env.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import static org.junit.Assert.*;

import ch.elca.el4j.env.xml.ResolverUtils;

/**
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 */
public class ResolverUtilsTest {
	@Test
	public void testResolve() {
		Properties values = new Properties();
		values.setProperty("varA", "valueA");
		values.setProperty("varB", "valueB");
		
		Map<String, String> tests = new HashMap<String, String>();
		tests.put("", "");
		tests.put("varA", "varA");
		tests.put("${varA}", "valueA");
		tests.put("${varA}${varB}", "valueAvalueB");
		tests.put("${varA} bla ${varB}", "valueA bla valueB");
		tests.put("xx${varB}zz", "xxvalueBzz");
		
		tests.put("${notFound}", "${notFound}");
		
		tests.put("$$", "$$");
		tests.put("$}{$", "$}{$");
		tests.put("${", "${");
		tests.put("${}", "${}");
		
		for (String input : tests.keySet()) {
			String expected = tests.get(input);
			assertEquals(expected, ResolverUtils.resolve(input, values));
		}
	}
}
