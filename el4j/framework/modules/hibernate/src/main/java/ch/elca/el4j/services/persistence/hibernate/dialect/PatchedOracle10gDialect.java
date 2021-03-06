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
package ch.elca.el4j.services.persistence.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.Oracle10gDialect;

/**
 * Fix hibernate column type validation on double precision values. Oracle
 * accepts the type "double precision", but stores it as "float(126)". Whenever
 * hibernate validates such a column, oracle returns "float(126)" and hibernate
 * therefore reports a mismatch.
 * 
 * See http://opensource.atlassian.com/projects/hibernate/browse/HHH-1961
 * or http://opensource.atlassian.com/projects/hibernate/browse/HHH-2315
 * 
 * Registers type for timestamps (see http://opensource.atlassian.com/projects/hibernate/browse/HHH-3193)
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class PatchedOracle10gDialect extends Oracle10gDialect {
	/**
	 * Is the sql type for timestamps.
	 */
	private static final int TIMESTAMP_CODE = -101;
	
	/**
	 * Default constructor.
	 */
	public PatchedOracle10gDialect() {
		super();
		registerColumnType(Types.DOUBLE, "float(126)");
		registerHibernateType(TIMESTAMP_CODE, "timestamp");
	}
}
