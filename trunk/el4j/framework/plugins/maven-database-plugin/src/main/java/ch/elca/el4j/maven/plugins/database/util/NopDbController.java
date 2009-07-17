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
package ch.elca.el4j.maven.plugins.database.util;

/**
 * A {@link DbController} that does nothing (for externally started DBs like oracle).
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
public class NopDbController implements DbController {
	
	/**
	 * The DB name.
	 */
	private String m_dbName;
	
	/**
	 * @param dbName    the DB name
	 */
	public NopDbController(String dbName) {
		m_dbName = dbName;
	}
	
	/** {@inheritDoc} */
	public void setHomeDir(String homeDir) { }
	
	/** {@inheritDoc} */
	public void setPort(int port) { }
	
	/** {@inheritDoc} */
	public void setUsername(String username) { }
	
	/** {@inheritDoc} */
	public void setPassword(String password) { }
	
	/** {@inheritDoc} */
	public void start() throws Exception { }

	/** {@inheritDoc} */
	public void stop() throws Exception { }
	
	/** {@inheritDoc} */
	public String getDbName() {
		return m_dbName;
	}
}
