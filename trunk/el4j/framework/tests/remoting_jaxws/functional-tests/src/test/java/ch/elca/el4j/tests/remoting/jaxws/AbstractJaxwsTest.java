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
package ch.elca.el4j.tests.remoting.jaxws;

import ch.elca.el4j.tests.core.AbstractTest;

/**
 * The abstract test base class for JAX-WS tests.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractJaxwsTest extends AbstractTest {

	@Override
	protected String[] getExcludeConfigLocations() {
		// LazyInitializationTest introduces dependencies to keyword and refdb, therefore
		// some exclude config locations have to be added
		return new String[] {
			"classpath*:mandatory/keyword-core-service-config.xml",
			"classpath*:mandatory/refdb-core-service-config.xml",
			"classpath*:scenarios/dataaccess/extent-test-hibernate-config.xml"
		};
	}
}
