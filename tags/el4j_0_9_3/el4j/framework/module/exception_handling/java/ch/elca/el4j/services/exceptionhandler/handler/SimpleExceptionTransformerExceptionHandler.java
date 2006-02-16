/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.exceptionhandler.handler;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class transforms exceptions into other exceptions.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class SimpleExceptionTransformerExceptionHandler
        extends AbstractExceptionTransformerExceptionHandler
        implements InitializingBean {

    /** The transformed exception class. */
    private Class m_transformedExceptionClass;
    
    /**
     * Sets the class into which an exception has to be transformed.
     * 
     * @param transformedClass
     *      The transformation's target class.
     */
    public void setTransformedExceptionClass(Class transformedClass) {
        m_transformedExceptionClass = transformedClass;
    }

    /**
     * {@inheritDoc}
     */
    protected Exception transform(Throwable t, Log logger) {
        Exception e = createExceptionWithMessageAndThrowable(t);
        if (e == null) {
            e = createExceptionWithMessage(t);
        }
        if (e == null) {
            e = createException();
        }
        if (e == null) {
            logger.warn("Unable to transform Exception from ["
                    + t.getClass().getName() + "] to ["
                    + m_transformedExceptionClass.getName() + "].");
            return null;
        }
        e.setStackTrace(t.getStackTrace());
        
        return e;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_transformedExceptionClass, "transformedClass", this);
        if (!Exception.class.isAssignableFrom(m_transformedExceptionClass)) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The property 'transformedClass' has to be an instance of"
                    + " java.lang.Exception.");
        }
        // check whether a new instance can be created 
        m_transformedExceptionClass.newInstance();
    }

    /**
     * Tries to create a new exception with the given throwable's message
     * and the throwable itself as cause.
     * 
     * @param t
     *      The throwable to transform.
     *      
     * @return Returns the transformed exception or <code>null</code> if the
     *      class can't be instantiated.
     */
    private Exception createExceptionWithMessageAndThrowable(Throwable t) {
        Exception e = null;
        Class[] params = {String.class, Throwable.class};
        Object[] values = {t.getMessage(), t};
        // Checkstyle: EmptyBlock off
        try {
            Constructor c = m_transformedExceptionClass.getConstructor(params);
            e = (Exception) c.newInstance(values);
        } catch (Exception ex) { }
        return e;
        // Checkstyle: EmptyBlock on
    }
    
    /**
     * Tries to create a new exception with the given throwable's message.
     * 
     * @param t
     *      The throwable to transform.
     *      
     * @return Returns the transformed exception or <code>null</code> if the
     *      class can't be instantiated.
     */
    private Exception createExceptionWithMessage(Throwable t) {
        Exception e = null;
        Class[] params = {String.class};
        Object[] values = {t.getMessage()};
        // Checkstyle: EmptyBlock off
        try {
            Constructor c = m_transformedExceptionClass.getConstructor(params);
            e = (Exception) c.newInstance(values);
        } catch (Exception ex) { }
        // Checkstyle: EmptyBlock on
        return e;
    }
    
    /**
     * @return Returns a new instance of the target exception or
     *      <code>null</code> if it can't be created.
     */
    private Exception createException() {
        Exception e = null;
        // Checkstyle: EmptyBlock off
        try {
            e = (Exception) m_transformedExceptionClass.newInstance();
        } catch (Exception ex) { }
        // Checkstyle: EmptyBlock on
        return e;
    }
}
