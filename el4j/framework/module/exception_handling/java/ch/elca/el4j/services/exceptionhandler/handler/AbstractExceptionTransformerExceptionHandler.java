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

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import ch.elca.el4j.services.exceptionhandler.AbstractExceptionHandlerInterceptor;

/**
 * This is a convenience abstract class to create exception transformer
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
public abstract class AbstractExceptionTransformerExceptionHandler extends
        AbstractExceptionHandler {

    /**
     * {@inheritDoc}
     */
    protected Object handleException(Throwable t,
            AbstractExceptionHandlerInterceptor exceptionInvoker,
            MethodInvocation invocation, Log logger) throws Throwable {
        
        Exception transformed = transform(t, logger);
        if (transformed == null) {
            return null;
        } else {
            throw transformed;
        }
    }

    /**
     * Transforms the given throwable into an appropriate exception.
     * 
     * @param t
     *      The throwable to transform.
     *      
     * @param logger
     *      The logger that is configured properly.
     *      
     * @return Returns the transformed exception or <code>null</code> if no
     *      exceptions has to be thrown.
     */
    protected abstract Exception transform(Throwable t, Log logger);
}
