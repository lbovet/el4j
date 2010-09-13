package ch.elca.el4j.tests.aspects.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.tests.aspects.interceptor.Logged;

public class Service {
	Log logger = LogFactory.getLog(Service.class);
	private static volatile int counter = 0;
	
	public static synchronized void resetCounter() {
		counter = 0;
	}

	public static synchronized int getCounter() {
		return counter;
	}
	
	public static synchronized void incrementCounter() {
		counter++;
	}

	public Service() {
		logger.warn("MyService is instanciated");
	}

	@Logged
	public void oneMethod() {
		logger.warn("I run this method once");
	}
}
