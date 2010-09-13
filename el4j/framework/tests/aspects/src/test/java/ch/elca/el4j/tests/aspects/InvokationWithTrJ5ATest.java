package ch.elca.el4j.tests.aspects;

import javax.annotation.Resource;

import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

import ch.elca.el4j.tests.aspects.services.Service;

@ContextConfiguration(locations = { "classpath*:mandatory/aspect-test-config.xml",
		"classpath*:optional/interception/transactionJava5Annotations.xml" })
public class InvokationWithTrJ5ATest extends AbstractJUnit38SpringContextTests {
	Log logger = LogFactory.getLog(InvokationWithTrJ5ATest.class);

	@Resource
	protected Service myService;

	public void testDoubleInvokation() {
		Service.resetCounter();
		if (myService instanceof Advised) {
			Advised myAdvisedService = (Advised) myService;
			for (Advisor adv : myAdvisedService.getAdvisors()) {
				Advice advice = adv.getAdvice();
				if (advice instanceof AspectJAroundAdvice) {
					AspectJAroundAdvice around = (AspectJAroundAdvice) advice;
					System.out.println("- around advice " + around.getAspectName() + ": " + adv.getAdvice());
				} else {
					System.out.println("- " + adv.getAdvice());
				}

			}
		}
		myService.oneMethod();
		assertEquals(1, Service.getCounter());
	}
}
