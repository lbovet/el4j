/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.core.context.junit4;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.core.ModuleTestContextLoader;

/**
 * 
 * JUnit4ClassRunner which enables the use of the <code>ExtendedContextConfiguration</code>.
 * 
 * To be used in conjunction with the {@link ModuleTestContextLoader}.
 * It is an error to use this ClassRunner with a {@link ContextConfiguration#loader} not
 * set to a subtype of {@link ModuleTestContextLoader}.
 * 
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class EL4JJunit4ClassRunner extends SpringJUnit4ClassRunner {
	
	/**
	 * Constructor which places the tested class in a thread-local variable of ModuleTestContextLoader so
	 * the ContextLoader can investigate the <code>@ExtendedContextConfiguration</code> annotation and
	 * configure the {@link ModuleApplicationContext} appropriately.
	 * @param testClass the test class
	 * @throws InitializationError
	 */
	public EL4JJunit4ClassRunner(Class<?> testClass) throws InitializationError {
		super(testClass);

		// allow ModuleTestContextLoader to detect @ExtendedContextConfiguration annotations
		// on the test class.
		ContextConfiguration contextConfiguration = testClass.getAnnotation(ContextConfiguration.class);
		Class<? extends ContextLoader> loader = contextConfiguration.loader();
		if (!ModuleTestContextLoader.class.isAssignableFrom(loader)) {
			throw new InitializationError("EL4JJunit4ClassRunner used without ModuleTestContextLoader. "
				+ "Consider setting the 'loader' attribute of the @ContextConfiguration "
				+ "to ModuleTestContextLoader.class");
		}
		
		ModuleTestContextLoader.setTestedClass(testClass);
		
		/* Let Spring close the application context by marking it dirty.
		 * 
		 * There are two reasons why closing the context is wanted:
		 * 1. Be friendly to the database: release all db connections from the connection pool
		 * 2. There is an application context cache inside the TestContext that checks equality
		 *    only using the include and not the exclude locations. 
		 */
		getTestContextManager().registerTestExecutionListeners(new AbstractTestExecutionListener() {
			@Override
			public void afterTestClass(TestContext testContext) throws Exception {
				testContext.markApplicationContextDirty();
			}
		});
	}
}
