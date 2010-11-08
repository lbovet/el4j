/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.elca.el4j.tests.core.context.transaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionConfigurationAttributes;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.DelegatingTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAspectUtils;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 
 * Modified version of Spring's {@link TransactionalTestExecutionListener} (3.0.4.RELEASE) with the only difference
 * that {@link ModuleTransactionalTestExecutionListener#isDefaultRollback(TestContext)} reads the
 * system property specified in <code>SYSTEM_PROPERTY_NAME</code> (<code>= "test.transaction.behaviour"</code>)
 * which can override the default rollback behaviour. Valid property values are <code>"commit"</code>
 * and <code>"rollback"</code>.
 * <p>
 * Use this Listener as follows:
 * <pre>
 * &#64;RunWith(SpringJUnit4ClassRunner.class)
 * &#64;ContextConfiguration
 * &#64;Transactional
 * &#64;TestExecutionListeners({
	org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
	org.springframework.test.context.support.DirtiesContextTestExecutionListener.class,
	ch.elca.el4j.tests.core.context.transaction.ModuleTransactionalTestExecutionListener.class })
 * public class TestClass { ...
 * </pre>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Simon Stelling (SST)
 */
public class ModuleTransactionalTestExecutionListener extends AbstractTestExecutionListener {

	private static final Logger s_logger = LoggerFactory.getLogger(ModuleTransactionalTestExecutionListener.class);

	private static final String SYSTEM_PROPERTY_NAME = "test.transaction.behaviour";
	
	protected final TransactionAttributeSource attributeSource = new AnnotationTransactionAttributeSource();

	private TransactionConfigurationAttributes configurationAttributes;

	private volatile int transactionsStarted = 0;

	private final Map<Method, TransactionContext> transactionContextCache =
			Collections.synchronizedMap(new IdentityHashMap<Method, TransactionContext>());

	/**
	 * If the test method of the supplied {@link TestContext test context} is
	 * configured to run within a transaction, this method will run
	 * {@link BeforeTransaction @BeforeTransaction methods} and start a new
	 * transaction.
	 * <p>Note that if a {@link BeforeTransaction @BeforeTransaction method} fails,
	 * remaining {@link BeforeTransaction @BeforeTransaction methods} will not
	 * be invoked, and a transaction will not be started.
	 * @see org.springframework.transaction.annotation.Transactional
	 * @see org.springframework.test.annotation.NotTransactional
	 */
	@SuppressWarnings("serial")
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		final Method testMethod = testContext.getTestMethod();
		Assert.notNull(testMethod, "The test method of the supplied TestContext must not be null");

		if (this.transactionContextCache.remove(testMethod) != null) {
			throw new IllegalStateException("Cannot start new transaction without ending existing transaction: " +
					"Invoke endTransaction() before startNewTransaction().");
		}

		if (testMethod.isAnnotationPresent(NotTransactional.class)) {
			return;
		}

		TransactionAttribute transactionAttribute =
				this.attributeSource.getTransactionAttribute(testMethod, testContext.getTestClass());
		TransactionDefinition transactionDefinition = null;
		if (transactionAttribute != null) {
			transactionDefinition = new DelegatingTransactionAttribute(transactionAttribute) {
				public String getName() {
					return testMethod.getName();
				}
			};
		}

		if (transactionDefinition != null) {
			if (s_logger.isDebugEnabled()) {
				s_logger.debug("Explicit transaction definition [" + transactionDefinition +
						"] found for test context [" + testContext + "]");
			}
			String qualifier = transactionAttribute.getQualifier();
			PlatformTransactionManager tm;
			if (StringUtils.hasLength(qualifier)) {
				// Use autowire-capable factory in order to support extended qualifier matching
				// (only exposed on the internal BeanFactory, not on the ApplicationContext).
				BeanFactory bf = testContext.getApplicationContext().getAutowireCapableBeanFactory();
				tm = TransactionAspectUtils.getTransactionManager(bf, qualifier);
			}
			else {
				tm = getTransactionManager(testContext);
			}
			TransactionContext txContext = new TransactionContext(tm, transactionDefinition);
			runBeforeTransactionMethods(testContext);
			startNewTransaction(testContext, txContext);
			this.transactionContextCache.put(testMethod, txContext);
		}
	}

	/**
	 * If a transaction is currently active for the test method of the supplied
	 * {@link TestContext test context}, this method will end the transaction
	 * and run {@link AfterTransaction @AfterTransaction methods}.
	 * <p>{@link AfterTransaction @AfterTransaction methods} are guaranteed to be
	 * invoked even if an error occurs while ending the transaction.
	 */
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		Method testMethod = testContext.getTestMethod();
		Assert.notNull(testMethod, "The test method of the supplied TestContext must not be null");

		// If the transaction is still active...
		TransactionContext txContext = this.transactionContextCache.remove(testMethod);
		if (txContext != null && !txContext.transactionStatus.isCompleted()) {
			try {
				endTransaction(testContext, txContext);
			}
			finally {
				runAfterTransactionMethods(testContext);
			}
		}
	}

	/**
	 * Run all {@link BeforeTransaction @BeforeTransaction methods} for the
	 * specified {@link TestContext test context}. If one of the methods fails,
	 * however, the caught exception will be rethrown in a wrapped
	 * {@link RuntimeException}, and the remaining methods will <strong>not</strong>
	 * be given a chance to execute.
	 * @param testContext the current test context
	 */
	protected void runBeforeTransactionMethods(TestContext testContext) throws Exception {
		try {
			List<Method> methods = getAnnotatedMethods(testContext.getTestClass(), BeforeTransaction.class);
			Collections.reverse(methods);
			for (Method method : methods) {
				if (s_logger.isDebugEnabled()) {
					s_logger.debug("Executing @BeforeTransaction method [" + method + "] for test context ["
							+ testContext + "]");
				}
				method.invoke(testContext.getTestInstance());
			}
		}
		catch (InvocationTargetException ex) {
			s_logger.error("Exception encountered while executing @BeforeTransaction methods for test context ["
					+ testContext + "]", ex.getTargetException());
			ReflectionUtils.rethrowException(ex.getTargetException());
		}
	}

	/**
	 * Run all {@link AfterTransaction @AfterTransaction methods} for the
	 * specified {@link TestContext test context}. If one of the methods fails,
	 * the caught exception will be logged as an error, and the remaining
	 * methods will be given a chance to execute. After all methods have
	 * executed, the first caught exception, if any, will be rethrown.
	 * @param testContext the current test context
	 */
	protected void runAfterTransactionMethods(TestContext testContext) throws Exception {
		Throwable afterTransactionException = null;

		List<Method> methods = getAnnotatedMethods(testContext.getTestClass(), AfterTransaction.class);
		for (Method method : methods) {
			try {
				if (s_logger.isDebugEnabled()) {
					s_logger.debug("Executing @AfterTransaction method [" + method + "] for test context [" +
							testContext + "]");
				}
				method.invoke(testContext.getTestInstance());
			}
			catch (InvocationTargetException ex) {
				Throwable targetException = ex.getTargetException();
				if (afterTransactionException == null) {
					afterTransactionException = targetException;
				}
				s_logger.error("Exception encountered while executing @AfterTransaction method [" + method +
						"] for test context [" + testContext + "]", targetException);
			}
			catch (Exception ex) {
				if (afterTransactionException == null) {
					afterTransactionException = ex;
				}
				s_logger.error("Exception encountered while executing @AfterTransaction method [" + method +
						"] for test context [" + testContext + "]", ex);
			}
		}

		if (afterTransactionException != null) {
			ReflectionUtils.rethrowException(afterTransactionException);
		}
	}

	/**
	 * Start a new transaction for the supplied {@link TestContext test context}.
	 * <p>Only call this method if {@link #endTransaction} has been called or if no
	 * transaction has been previously started.
	 * @param testContext the current test context
	 * @throws TransactionException if starting the transaction fails
	 * @throws Exception if an error occurs while retrieving the transaction manager
	 */
	private void startNewTransaction(TestContext testContext, TransactionContext txContext) throws Exception {
		txContext.startTransaction();
		++this.transactionsStarted;
		if (s_logger.isInfoEnabled()) {
			s_logger.info("Began transaction (" + this.transactionsStarted + "): transaction manager [" +
					txContext.transactionManager + "]; rollback [" + isRollback(testContext) + "]");
		}
	}

	/**
	 * Immediately force a <em>commit</em> or <em>rollback</em> of the
	 * transaction for the supplied {@link TestContext test context}, according
	 * to the commit and rollback flags.
	 * @param testContext the current test context
	 * @throws Exception if an error occurs while retrieving the transaction manager
	 */
	private void endTransaction(TestContext testContext, TransactionContext txContext) throws Exception {
		boolean rollback = isRollback(testContext);
		if (s_logger.isTraceEnabled()) {
			s_logger.trace("Ending transaction for test context [" + testContext + "]; transaction manager [" +
					txContext.transactionStatus + "]; rollback [" + rollback + "]");
		}
		txContext.endTransaction(rollback);
		if (s_logger.isInfoEnabled()) {
			s_logger.info((rollback ? "Rolled back" : "Committed") +
					" transaction after test execution for test context [" + testContext + "]");
		}
	}

	/**
	 * Get the {@link PlatformTransactionManager transaction manager} to use
	 * for the supplied {@link TestContext test context}.
	 * @param testContext the test context for which the transaction manager
	 * should be retrieved
	 * @return the transaction manager to use, or <code>null</code> if not found
	 * @throws BeansException if an error occurs while retrieving the transaction manager
	 */
	protected final PlatformTransactionManager getTransactionManager(TestContext testContext) {
		String tmName = retrieveConfigurationAttributes(testContext).getTransactionManagerName();
		try {
			return testContext.getApplicationContext().getBean(tmName, PlatformTransactionManager.class);
		}
		catch (BeansException ex) {
			if (s_logger.isWarnEnabled()) {
				s_logger.warn("Caught exception while retrieving transaction manager with bean name [" +
						tmName + "] for test context [" + testContext + "]", ex);
			}
			throw ex;
		}
	}

	/**
	 * Determine whether or not to rollback transactions by default for the
	 * supplied {@link TestContext test context}.
	 * @param testContext the test context for which the default rollback flag
	 * should be retrieved
	 * @return the <em>default rollback</em> flag for the supplied test context
	 * @throws Exception if an error occurs while determining the default rollback flag
	 */
	protected final boolean isDefaultRollback(TestContext testContext) throws Exception {
		String action = System.getProperty(SYSTEM_PROPERTY_NAME);
		if ("commit".equals(action)) {
			return false;
		} else if ("rollback".equals(action)) {
			return true;
		} else {
			return retrieveConfigurationAttributes(testContext).isDefaultRollback();
		}
	}

	/**
	 * Determine whether or not to rollback transactions for the supplied
	 * {@link TestContext test context} by taking into consideration the
	 * {@link #isDefaultRollback(TestContext) default rollback} flag and a
	 * possible method-level override via the {@link Rollback} annotation.
	 * @param testContext the test context for which the rollback flag
	 * should be retrieved
	 * @return the <em>rollback</em> flag for the supplied test context
	 * @throws Exception if an error occurs while determining the rollback flag
	 */
	protected final boolean isRollback(TestContext testContext) throws Exception {
		boolean rollback = isDefaultRollback(testContext);
		Rollback rollbackAnnotation = testContext.getTestMethod().getAnnotation(Rollback.class);
		if (rollbackAnnotation != null) {
			boolean rollbackOverride = rollbackAnnotation.value();
			if (s_logger.isDebugEnabled()) {
				s_logger.debug("Method-level @Rollback(" + rollbackOverride + ") overrides default rollback [" +
						rollback + "] for test context [" + testContext + "]");
			}
			rollback = rollbackOverride;
		}
		else {
			if (s_logger.isDebugEnabled()) {
				s_logger.debug("No method-level @Rollback override: using default rollback [" +
						rollback + "] for test context [" + testContext + "]");
			}
		}
		return rollback;
	}

	/**
	 * Gets all superclasses of the supplied {@link Class class}, including the
	 * class itself. The ordering of the returned list will begin with the
	 * supplied class and continue up the class hierarchy.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass#getSuperClasses(Class)} and
	 * adapted.
	 * @param clazz the class for which to retrieve the superclasses.
	 * @return all superclasses of the supplied class.
	 */
	private List<Class<?>> getSuperClasses(Class<?> clazz) {
		ArrayList<Class<?>> results = new ArrayList<Class<?>>();
		Class<?> current = clazz;
		while (current != null) {
			results.add(current);
			current = current.getSuperclass();
		}
		return results;
	}

	/**
	 * Gets all methods in the supplied {@link Class class} and its superclasses
	 * which are annotated with the supplied <code>annotationType</code> but
	 * which are not <em>shadowed</em> by methods overridden in subclasses.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass#getAnnotatedMethods(Class)}
	 * and adapted.
	 * @param clazz the class for which to retrieve the annotated methods
	 * @param annotationType the annotation type for which to search
	 * @return all annotated methods in the supplied class and its superclasses
	 */
	private List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
		List<Method> results = new ArrayList<Method>();
		for (Class<?> eachClass : getSuperClasses(clazz)) {
			Method[] methods = eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation = eachMethod.getAnnotation(annotationType);
				if (annotation != null && !isShadowed(eachMethod, results)) {
					results.add(eachMethod);
				}
			}
		}
		return results;
	}

	/**
	 * Determines if the supplied {@link Method method} is <em>shadowed</em>
	 * by a method in supplied {@link List list} of previous methods.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass#isShadowed(Method,List)}.
	 * @param method the method to check for shadowing
	 * @param previousMethods the list of methods which have previously been processed
	 * @return <code>true</code> if the supplied method is shadowed by a
	 * method in the <code>previousMethods</code> list
	 */
	private boolean isShadowed(Method method, List<Method> previousMethods) {
		for (Method each : previousMethods) {
			if (isShadowed(method, each)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the supplied {@link Method current method} is
	 * <em>shadowed</em> by a {@link Method previous method}.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass#isShadowed(Method,Method)}.
	 * @param current the current method
	 * @param previous the previous method
	 * @return <code>true</code> if the previous method shadows the current one
	 */
	private boolean isShadowed(Method current, Method previous) {
		if (!previous.getName().equals(current.getName())) {
			return false;
		}
		if (previous.getParameterTypes().length != current.getParameterTypes().length) {
			return false;
		}
		for (int i = 0; i < previous.getParameterTypes().length; i++) {
			if (!previous.getParameterTypes()[i].equals(current.getParameterTypes()[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieves the {@link TransactionConfigurationAttributes} for the
	 * specified {@link Class class} which may optionally declare or inherit a
	 * {@link TransactionConfiguration @TransactionConfiguration}. If a
	 * {@link TransactionConfiguration} annotation is not present for the
	 * supplied class, the <em>default values</em> for attributes defined in
	 * {@link TransactionConfiguration} will be used instead.
	 * @param clazz the Class object corresponding to the test class for which
	 * the configuration attributes should be retrieved
	 * @return a new TransactionConfigurationAttributes instance
	 */
	private TransactionConfigurationAttributes retrieveConfigurationAttributes(TestContext testContext) {
		if (this.configurationAttributes == null) {
			Class<?> clazz = testContext.getTestClass();
			Class<TransactionConfiguration> annotationType = TransactionConfiguration.class;
			TransactionConfiguration config = clazz.getAnnotation(annotationType);
			if (s_logger.isDebugEnabled()) {
				s_logger.debug("Retrieved @TransactionConfiguration [" + config + "] for test class [" + clazz + "]");
			}

			String transactionManagerName;
			boolean defaultRollback;
			if (config != null) {
				transactionManagerName = config.transactionManager();
				defaultRollback = config.defaultRollback();
			}
			else {
				transactionManagerName = (String) AnnotationUtils.getDefaultValue(annotationType, "transactionManager");
				defaultRollback = (Boolean) AnnotationUtils.getDefaultValue(annotationType, "defaultRollback");
			}

			TransactionConfigurationAttributes configAttributes =
					new TransactionConfigurationAttributes(transactionManagerName, defaultRollback);
			if (s_logger.isDebugEnabled()) {
				s_logger.debug("Retrieved TransactionConfigurationAttributes [" + configAttributes + "] for class [" + clazz + "]");
			}
			this.configurationAttributes = configAttributes;
		}
		return this.configurationAttributes;
	}

	/**
	 * Internal context holder for a specific test method.
	 */
	private static class TransactionContext {

		private final PlatformTransactionManager transactionManager;

		private final TransactionDefinition transactionDefinition;

		private TransactionStatus transactionStatus;

		public TransactionContext(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
			this.transactionManager = transactionManager;
			this.transactionDefinition = transactionDefinition;
		}

		public void startTransaction() {
			this.transactionStatus = this.transactionManager.getTransaction(this.transactionDefinition);
		}

		public void endTransaction(boolean rollback) {
			if (rollback) {
				this.transactionManager.rollback(this.transactionStatus);
			}
			else {
				this.transactionManager.commit(this.transactionStatus);
			}
		}
	}

}
