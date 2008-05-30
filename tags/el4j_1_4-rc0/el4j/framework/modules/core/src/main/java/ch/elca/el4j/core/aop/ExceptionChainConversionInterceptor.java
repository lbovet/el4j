/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.core.aop;

import java.lang.reflect.Field;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interceptor that converts the cause of an Exception thrown 
 * by the intercepted method into a {@link Throwable} instance. 
 * Nested causes are processed as well.<br>
 * 
 * The purpose of this interceptor is to assure that a client using the 
 * intercepted service can deserialize (unmarshall) a possible Exception 
 * thrown by the service in any case. 
 * Due to exception chaining, the exception could contain instances of 
 * (arbitrary) subclasses of {@link Throwable} as cause that are not known 
 * by the client and can therefore not be deserialized.
 * 
 * This class was contributed to EL4J by Reto Fankhauser.
 *
 * @author Reto Fankhauser (RFA) 
 */
public class ExceptionChainConversionInterceptor implements MethodInterceptor {

    /** 
     * Name of the field of {@link Throwable} that holds the cause. 
     * */
    private static final String CAUSE_FIELD_NAME = "cause";

    /** Log channel. */
    private static Log s_logger  
        = LogFactory.getLog(ExceptionChainConversionInterceptor.class);

    /**
     * Default constructor.
     */
    public ExceptionChainConversionInterceptor() {
        s_logger.debug("Interceptor instantiated.");
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        try {
            // just invoke the underlying 'layer', if there is no exception,
            // no handling by this interceptor is required.
            Object retVal = methodInvocation.proceed();
            return retVal;
        } catch (Exception e) {
            // an exception has occured during the method invocation.
            // - keep the toplevel exception (must be known by the  
            //   client that uses the service interface)
            // - convert all nested exceptions to java.lang.Throwable
            //     (can certainly be unmarshalled by a client)
            try {
                // get access to private 'cause' field (using getCause is 
                //  not sufficient as we need write access)
                Field f = Throwable.class.getDeclaredField(CAUSE_FIELD_NAME);
                f.setAccessible(true);
                
                // get the 'old cause'
                Throwable oldCause = (Throwable) f.get(e);
                
                // convert 'old cause' and it's 'subcauses' 
                //  to java.lang.Throwable instances
                Throwable convertedCause = convertCause(oldCause);
                
                // set the converted 'cause hierarchy' on the original 
                //  exception 
                f.set(e, convertedCause);
            } catch (Exception ex) {
                // if there was a problem, do nothing except s_logging the  
                // event. the original exception will be forwarded to the  
                // client (as if this interceptor had not been present).
                s_logger.error("There was a problem when processing the exception.", e);
            }
            // rethrow processed exception
            throw e;
        }
    }

    /**
     * Converts the {@link Throwable} passed in (can be any subclass of 
     * {@link Throwable}) into a {@link Throwable} (not a subclass of it).
     * The converted exception is known in any JVM. This means that the 
     * converted {@link Throwable} can be deserialized in any JVM.
     * This method also recursively converts nested causes of exception causes.
     *  
     * @param original The {@link Throwable} to convert.
     * @return The converted {@link Throwable}. Instance of {@link Throwable} 
     * (no subclass).
     * 
     */
    // visibility moved to protected for easier testing
    protected Throwable convertCause(Throwable original) {

        if (original == null)  { 
            return null;
        }
        
        // message to add to the cause message
        String additionalMsg = "Converted from " 
            + original.getClass().getName() + " to "
                + Throwable.class.getName() + ". ";

        // new cause instance
        Throwable converted = new Throwable(additionalMsg + original.getMessage());

        // if there is a cause, convert it by calling this method recursively
        // and init the converted throwable with the converted cause
        Throwable cause = original.getCause();
        if (cause != null) {
            Throwable convertedCause = convertCause(cause);
            converted.initCause(convertedCause);
        }
        // set stack trace of original throwable on converted throwable
        converted.setStackTrace(original.getStackTrace());
        // return converted throwable
        return converted;
    }
}