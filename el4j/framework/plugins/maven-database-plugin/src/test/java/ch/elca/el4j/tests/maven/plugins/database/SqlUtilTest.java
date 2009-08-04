/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.maven.plugins.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ch.elca.el4j.maven.plugins.database.util.SqlUtils;

/**
 * A Test class to test {@link SqlUtils}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SqlUtilTest {

	@Test
	public void testExtractStmtsFromFile() {
		checkResult("/test-comments.sql", 2);
		checkResult("/test-create.sql", 5);
		checkResult("/test-alter.sql", 5);
		checkResult("/test-index.sql", 2);
		checkResult("/test-trigger.sql", 6);
		
		List<String> original = SqlUtils.extractStmtsFromFile(
			getClass().getResource("/test-comments-original.sql"), ";", "/");
		List<String> commented = SqlUtils.extractStmtsFromFile(
			getClass().getResource("/test-comments.sql"), ";", "/");
		
		assertTrue(commented.size() == original.size());
		assertEquals(original.get(0), commented.get(0));
	}
	
	/**
	 * Check that a given SQL files contains exactly the given number of statements.
	 * @param sqlFile          the SQL file name
	 * @param numStatements    the number of contained statements
	 */
	private void checkResult(String sqlFile, int numStatements) {
		List<String> stmts = SqlUtils.extractStmtsFromFile(getClass().getResource(sqlFile), ";", "/");
		assertEquals(numStatements, stmts.size());
		for (String stmt : stmts) {
			assertTrue(!stmt.contains("--"));
			assertTrue(!stmt.contains("/*"));
			assertTrue(!stmt.contains("*/"));
		}
		
	}
}
