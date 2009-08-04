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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining;

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * Runs all offliner tests. This is required to get the tests to run under maven across module
 * boundaries.
 * <b>WARNING. All tests must be annotated with @Component to get them to run.</b>
 * <i>THIS FILE IS A COPY OF THE ONE IN TESTS-DB2. REMEMBER TO UPDATE BOTH ON ANY CHANGE.</i>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */

@RunWith(AllTests.class)
public class OfflinerOracleTest {
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		ClassPathScanningCandidateComponentProvider scan
			= new ClassPathScanningCandidateComponentProvider(true);
		Set<BeanDefinition> candidates = 
			scan.findCandidateComponents("ch.elca.el4j.tests.services.persistence.hibernate.offlining");
		for (BeanDefinition candidate : candidates) {
			String name = candidate.getBeanClassName();
			try {
				Class<?> cls = Class.forName(name);
				if (TestCase.class.isAssignableFrom(cls)) {
					// Will work as it's guarded by the if block.
					suite.addTestSuite((Class<? extends TestCase>) cls);
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return suite;
	}
}
