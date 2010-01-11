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
package ch.elca.el4j.util.codingsupport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * This class provides means of breaking the Java rules through reflection.
 * Essentially, these are ugly hacks and should be used with extreme caution. 
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public final class ReflectionUtils {
	
	/**
	 * The logger for this class.
	 */
	private static Logger s_logger 
		= LoggerFactory.getLogger(ReflectionUtils.class);
	
	/**
	 * Hide default constructor.
	 */
	private ReflectionUtils() {
		// do nothing, will never be called
	}

	
	/**
	 * Reflectively calls internalExceptionThrower which throws 
	 * the given exception. As the method is called via reflection,
	 * it doesn't need to be decleared or caugt by the caller of this method.
	 * Important: This is an ugly hack and undermines the general exception mechanism
	 * defined by Java, so, use with caution. 
	 * 
	 * @param e is the exception that should be thrown.
	 */
	public static void throwException(Exception e) {
		Class<?> reflectionUtils = null;
		Method thrower = null;
		
		try {
			reflectionUtils = Class.forName("ch.elca.el4j.util.codingsupport.ReflectionUtils");
		} catch (ClassNotFoundException e1) {
			s_logger.error("Internal error in class ReflectionUtils: couldn't find class ReflectionUtils");
		}
		try {
			thrower = reflectionUtils.getDeclaredMethod("internalExceptionThrower",  java.lang.Exception.class);
		} catch (SecurityException e1) {
			s_logger.error("Internal error in class ReflectionUtils: " 
									+ "couldn't access decleared method internalExceptionThrower");
		} catch (NoSuchMethodException e1) {
			s_logger.error("Internal error in class ReflectionUtils: " 
				+ "couldn't find decleared method internalExceptionThrower");
		}
		try {
			thrower.invoke(null, e);
		} catch (IllegalArgumentException e1) {
			s_logger.error("Internal error in class ReflectionUtils: " 
				+ "couldn't access decleared method internalExceptionThrower");
		} catch (IllegalAccessException e1) {
			s_logger.error("Internal error in class ReflectionUtils: " 
				+ "couldn't access decleared method internalExceptionThrower");
		} catch (InvocationTargetException e1) {
			s_logger.error("Internal error in class ReflectionUtils: " 
				+ "couldn't access decleared method internalExceptionThrower");
		}
		
	}

	
	/**
	 * Simple method that just throws the exception that it receives.
	 * This method is meant to be called by reflection, see throwException for details.
	 * 
	 * @param e is the exception that we want to throw
	 * @throws Exception is equal to e
	 */
	@SuppressWarnings("unused")
	private static void internalExceptionThrower(Exception e) throws Exception {
		throw e;
		
	}
	
	
}
