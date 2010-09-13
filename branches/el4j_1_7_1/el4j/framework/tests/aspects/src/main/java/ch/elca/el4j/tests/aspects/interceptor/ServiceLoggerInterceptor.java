package ch.elca.el4j.tests.aspects.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;

import ch.elca.el4j.tests.aspects.services.Service;





@SuppressWarnings("serial")
public class ServiceLoggerInterceptor extends AbstractTraceInterceptor {
	Log logger = LogFactory.getLog(ServiceLoggerInterceptor.class);
	boolean useInfoLevel = true;

	public ServiceLoggerInterceptor() {
		logger.warn("ServiceLoggerInterceptor is now instanciated");
	}

	protected Object test(ProceedingJoinPoint pjp) throws Throwable {
		Service.incrementCounter();
		int count = Service.getCounter();
		logger.warn("Passed here : " + count + " times");
		Object result = pjp.proceed();
		logger.warn("Ended here : " + count + " times");
		return result;
	}

	@Override
	protected Object invokeUnderTrace(MethodInvocation arg0, Log arg1) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}
}
