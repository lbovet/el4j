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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.maven.plugins.database.util.derby.DerbyController;
import ch.elca.el4j.maven.plugins.database.util.h2.H2Controller;

/**
 * A factory for {@link DbController}s.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public final class DbControllerFactory {
	/**
	 * The logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(DbControllerFactory.class);
	
	/**
	 * The hidden constructor.
	 */
	private DbControllerFactory() { }
	
	/**
	 * Create the DB controller.
	 * @param db    the db name
	 * @return      a matching DB controller
	 */
	public static DbController create(String db) {
		if (db.equalsIgnoreCase("db2")) {
			return new DerbyController();
		} else if (db.equalsIgnoreCase("h2")) {
			return new H2Controller();
		} else {
			s_logger.warn("Database " + db + " can not be started by this plugin.");
			return new NopDbController(db);
		}
	}
}
