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
package ch.elca.el4j.tests.refdb.jpa;

/**
 * 
 * This class is a simple class to check the implementation of the HibernateWorkElementDao.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */

public class JpaHelperTest extends AbstractHelperTest {

	/**
	 * {@inheritDoc}
	 */
	protected String[] getIncludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/*.xml",
			"classpath*:scenarios/db/raw/*.xml",
			"classpath*:scenarios/dataaccess/jpa/*.xml",
			"classpath*:optional/refdb/trace-interceptor-config.xml",
			"classpath*:optional/interception/transactionJava5Annotations.xml"};
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getExcludeConfigLocations() {
		return new String[] {
			"classpath*:mandatory/refdb-core-config.xml",
			"classpath*:mandatory/keyword-core-config.xml"};
	}
		
}
