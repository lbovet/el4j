/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.exceptionhandler;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.exceptionhandler.handler.ExceptionHandler;

/**
 * This class configures an exception handler. It maps exception types to
 * exception handlers.
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
public class ClassExceptionConfiguration
    implements ExceptionConfiguration, InitializingBean {

    /** The exception types which the handler is responsible for. */
    private Class[] m_exceptionTypes;
    
    /** The exception handler. */
    private ExceptionHandler m_exceptionHandler;
    
    /**
     * @return Returns the exception types which the handler is responsible for.
     */
    public Class[] getExceptionTypes() {
        return m_exceptionTypes;
    }

    /**
     * Sets the exception types which the hander is responsible for.
     * 
     * @param exceptionTypes
     *      The exception types to set.
     */
    public void setExceptionTypes(Class[] exceptionTypes) {
        m_exceptionTypes = exceptionTypes;
    }

    /**
     * {@inheritDoc}
     */
    public ExceptionHandler getExceptionHandler() {
        return m_exceptionHandler;
    }
    
    /**
     * Sets the exception handler.
     * 
     * @param exceptionHandler
     *      The exception handler to set.
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handlesExceptions(Throwable t, MethodInvocation invocation) {
        for (int i = 0; i < m_exceptionTypes.length; i++) {
            if (m_exceptionTypes[i].isInstance(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_exceptionTypes == null || m_exceptionTypes.length == 0) {
            throw new IllegalAccessException(
                    "The property 'exceptionTypes' is required.");
        }
        if (m_exceptionHandler == null) {
            throw new IllegalAccessException(
                    "The property 'exceptionHandler' is required.");
        }
    }
}
