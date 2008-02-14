package ch.elca.el4j.util.codingsupport;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

/**
 * Similar to Spring's AOPUtil class, with some more features:
 *  <ul>
 *    <li> convenience methods to easily add advice 
 *     (interceptors or mixins) programmatically to a class.
 *     This can be practical to make a quick test or for code that you would
 *     like not to require spring configuration. 
 * 
 *  </ul>
 *  
 *  Code sample:  <pre> <code> 
 *   DefaultPerson p = new DefaultPerson(); 
 *   p = addAdvice(p, 0, new AMixin(),
 *                       mySharedCInterceptor, 
 *                       new BMixin());
 *   
 * 	 // the next call now uses the 3 advices set up
 *   p.setAge(11);
 *  </code> </pre> 
 *  
 *  
 * Remark: We delegate sometimes to the AOPUtil of Spring 
 *    (we duplicated the methods here for simplicity).
 *    
 * @author Philipp Oser (POS)
 *
 */
public class AopHelper {

	/**
	 * Check whether the given object is a JDK dynamic proxy or a CGLIB proxy.
	 * @param object the object to check
	 * @see #isJdkDynamicProxy
	 * @see #isCglibProxy
	 * 
	 *  <br> delegates to the same method of Spring's AopUtil
	 */
	public static boolean isAopProxy(Object object) {
		return AopUtils.isAopProxy(object);
	}

	/**
	 * Check whether the given object is a JDK dynamic proxy.
	 * @param object the object to check
	 * @see java.lang.reflect.Proxy#isProxyClass
	 * 
	 *  <br> delegates to the same method of Spring's AopUtil
	 */
	public static boolean isJdkDynamicProxy(Object object) {
		return AopUtils.isJdkDynamicProxy(object);
	}

	/**
	 * Check whether the given object is a CGLIB proxy.
	 * @param object the object to check
	 * 
	 *  <br> delegates to the same method of Spring's AopUtil
	 */
	public static boolean isCglibProxy(Object object) {
		return AopUtils.isCglibProxy(object);
	}

	/**
	 * Check whether the specified class is a CGLIB-generated class.
	 * @param clazz the class to check
	 * 
	 *  <br> delegates to the same method of Spring's AopUtil
	 */
	public static boolean isCglibProxyClass(Class<?> clazz) {
		return AopUtils.isCglibProxyClass(clazz);
	}

	/**
	 * Determine the target class of the given bean instance,
	 * which might be an AOP proxy.
	 * <p>Returns the target class for an AOP proxy and the plain class else.
	 * @param candidate the instance to check (might be an AOP proxy)
	 * @return the target class (or the plain class of the given object as fallback)
	 * @see org.springframework.aop.TargetClassAware#getTargetClass()
	 * 
	 *  <br> delegates to the same method of Spring's AopUtil
	 */
	public static Class<?> getTargetClass(Object candidate) {
		return AopUtils.getTargetClass(candidate);
	}

	/**
	 * Add the advice(s) to the object. The method adds a Spring proxy if needed. 
	 * If there is already a Spring proxy, it repackages the object in a new 
	 * Spring proxy. The new advise is added in position 0 of the advice chain. <br> <br>
	 * 
	 *  CAVEAT: In case "object" is already wrapped (=proxied), the type of the 
	 *   target source must be assignable to T. <br>
	 *   
	 *  Take care that Mixins should typically be instanciated as Spring 
	 *  prototypes (i.e. 1 mixin instance per underlying object),  
	 *  Interceptors can typically be shared among all the objects they apply to.
	 *   When you add an advice that implement IntroductionInterceptor
	 *    (i.e. are Mixins) this class automatically wraps them in a
	 *     DefaultIntroductionAdvisor. <br> <br>
	 *
	 *  For sample code please refer to the javadoc of {@link AopHelper}. <br> <br>
	 *  
	 *  The method allows to add n advice at once (for performance: 
	 *   it does not require n times a re-proxying (for mixins)). <br> 
	 *     
	 * @param object the object to wrap
	 * @param position  where in the advice chain this new advice should be 
	 *    placed. 0 means at the beginning (=called first).
	 * @param advices the advise(s) (=the interceptors and mixins) to add 
	 * @return the same object wrapped with a spring proxy (if needed) that 
	 *  has in addition the given advice(s) <br> <br> 
	 */
	@SuppressWarnings("unchecked")	
	public static <T> T addAdvice(T object, int position, Advice... advices) {
		// idea to optimize: could we keep the same proxy in case
		//  we just want to add new interceptors?
		Advisor[] existingAdvisors = null;
		
		if (object instanceof Advised) {
			Advised advised = (Advised)object;
			existingAdvisors = advised.getAdvisors();
			
			try { // replace object by the wrapped object 
				object = (T)advised.getTargetSource().getTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ProxyFactory proxyFactory = new ProxyFactory (object);
				
		proxyFactory.setProxyTargetClass(true);
		proxyFactory.setExposeProxy(true);
		
		if (existingAdvisors != null) {
			for (Advisor a : existingAdvisors){
				proxyFactory.addAdvisor(a);
			}
		}
		
		//  inverse order of advices
		List<Advice> advicesAsList = Arrays.asList(advices);
		Collections.reverse(advicesAsList);

		for (Advice advice : advicesAsList) {
			if (advice instanceof IntroductionInterceptor) {
				IntroductionAdvisor ii = new DefaultIntroductionAdvisor(advice);
				proxyFactory.addAdvisor(position, ii);
			} else { // just an interceptor
				proxyFactory.addAdvice(position, advice);
			}
		}
	
		object = (T)proxyFactory.getProxy();
		return object;		
	}
	
	/**
	 * Convenience method. See {@link #addAdvice(Object, int, Advice)}
	 *  The position is always set to 0 (=first position)
	 */
	public static <T> T addAdvice(T object, Advice... advices) {
		return (T)addAdvice(object, 0, advices);
	}	
	
	/**
	 * Remove all advice (and the proxy) that were added via Spring AOP.
	 * 
	 * <br><br> CAVEAT: this method does NOT remove hibernate proxies! 
	 * @param object
	 * @return the object with all advice removed. <a>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T removeAllAdvice(T object) {
		if (object instanceof Advised) {
			Advised advised = (Advised)object;
						
			try { 
				return (T) advised.getTargetSource().getTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (T) object;
	}
	
	/**
	 * Does the given object have Spring AOP support (i.e.
	 *  a proxy with AOP methods on them)? <br>
	 * 
	 * @param object
	 * @return whether the object is proxied.
	 */
	public static boolean isProxied(Object object) {
		return (object instanceof Advised);
	}	

	
	// Remark: we could add here a remove advice method (it could
	//  take the class of the advices to remove)
}
