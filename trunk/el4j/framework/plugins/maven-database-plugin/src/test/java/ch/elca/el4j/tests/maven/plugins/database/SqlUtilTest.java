package ch.elca.el4j.tests.maven.plugins.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ch.elca.el4j.maven.plugins.database.util.SqlUtils;

/**
 * A Test class to test {@link SqlUtils}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
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
