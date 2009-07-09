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
package ch.elca.el4j.tests.keyword;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;

import ch.elca.el4j.tests.core.AbstractTest;

/**
 * This class is a base class for tests in module <code>keyword-core</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractTestCaseBase extends AbstractTest {
	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(AbstractTestCaseBase.class);

	/**
	 * Data source. Created by application context.
	 */
	private DataSource m_dataSource;

	/**
	 * Hide default constructor.
	 */
	protected AbstractTestCaseBase() { }

	/**
	 * @return Returns the dataSource.
	 */
	protected DataSource getDataSource() {
		if (m_dataSource == null) {
			m_dataSource
				= (DataSource) getApplicationContext().getBean("dataSource");
		}
		return m_dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() throws Exception {
		Connection con = null;
		Statement stmt = null;
		try {
			con = getDataSource().getConnection();
			
			// try to delete REFERENCEKEYWORDRELATIONSHIPS
			try {
				stmt = con.createStatement();
				stmt.executeUpdate("DELETE FROM REFERENCEKEYWORDRELATIONSHIPS");
			} catch (SQLException e) {
				s_logger.info("There was a problem while deleting rows of "
					+ "table 'REFERENCEKEYWORDRELATIONSHIPS'. Maybe the table "
					+ "does not exist.");
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			
			// try to delete KEYWORDS
			stmt = con.createStatement();
			stmt.execute("DELETE FROM KEYWORDS");
			con.commit();
		} catch (SQLException e) {
			s_logger.info("There was a problem while deleting rows of "
				+ "table 'REFERENCEKEYWORDRELATIONSHIPS' or 'KEYWORDS'"
				+ ". Maybe a table does not exist.");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					s_logger.info("Connection could not be closed.");
				}
			}
		}
	}
}
