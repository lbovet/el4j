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
package ch.elca.el4j.tests.aspects;

import static junit.framework.Assert.assertEquals;

import javax.annotation.Resource;

import org.aopalliance.aop.Advice;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.elca.el4j.tests.aspects.interceptor.ServiceLoggerInterceptor;
import ch.elca.el4j.tests.aspects.services.Service;
import ch.elca.el4j.tests.aspects.util.InvocationMonitor;
import ch.elca.el4j.tests.core.ModuleTestContextLoader;

/**
 * Abstract test case for aspects tests.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @author Reynald Borer (RBR)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
	locations = {
		"classpath*:mandatory/*.xml",
		"classpath*:scenarios/db/raw/*.xml",
		"classpath*:scenarios/dataaccess/hibernate/*.xml" },
	loader = ModuleTestContextLoader.class)
public abstract class AbstractInvokationTests {
	/**
	 * Private logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(InvokationWithoutTrJ5ATest.class);
	
	/**
	 * Test service.
	 */
	@Resource
	protected Service myService;
	
	/**
	 * @param o Is the object to report aspect infos of it.
	 */
	protected void reportAspectInformation(Object o) {
		if (o instanceof Advised) {
			Advised myAdvisedService = (Advised) o;
			for (Advisor adv : myAdvisedService.getAdvisors()) {
				Advice advice = adv.getAdvice();
				if (advice instanceof AspectJAroundAdvice) {
					AspectJAroundAdvice around = (AspectJAroundAdvice) advice;
					s_logger.info("- around advice " + around.getAspectName() + ": " + adv.getAdvice());
				} else {
					s_logger.info("- " + adv.getAdvice());
				}
			}
		}
	}
	
	/**
	 * Tests the double invokation problem.
	 */
	protected void commonDoubleInvokationTest() {
		InvocationMonitor.initCounter(ServiceLoggerInterceptor.class);
		reportAspectInformation(myService);
		myService.oneMethod();
		assertEquals("Service logger interceptor called not exactly once!",
			1, InvocationMonitor.getCounter(ServiceLoggerInterceptor.class));
	}
}
