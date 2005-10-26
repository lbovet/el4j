/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

import ch.elca.el4j.services.exceptionhandler.handler.ExceptionHandler;

/**
 * The exception configuration is used to determine, whether a exception handler
 * is able to handle a given exception that was thrown in a given method
 * invocation.
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
public interface ExceptionConfiguration {

    /**
     * Requests whether this exception configuration is able to handle the
     * given exception that was thrown in the given method invocation.
     * 
     * @param t
     *      The exception to handle.
     *      
     * @param invocation
     *      The invocation in which the exception was thrown.
     *      
     * @return Returns <code>true</code> if this exception configuratin is able
     *      to handle the exception, <code>false</code> otherwise.
     */
    public boolean handlesExceptions(Throwable t, MethodInvocation invocation);
    
    /**
     * @return Returns the exception handler.
     */
    public ExceptionHandler getExceptionHandler();
}
