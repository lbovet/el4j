/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This exception configuration uses the exception class as well as the invoked
 * method's name to specify the exception handler.
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
public class MethodNameExceptionConfiguration extends
        ClassExceptionConfiguration {

    /** The method names which this configuration is responsible for. */
    private String[] m_methodNames;

    /**
     * Sets the method names which this exception configuration is responsible
     * for.
     * 
     * @param methodNames
     *      The method names to set.
     */
    public void setMethodNames(String[] methodNames) {
        m_methodNames = methodNames;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handlesExceptions(Throwable t, MethodInvocation invocation) {
        if (super.handlesExceptions(t, invocation)) {
            for (int i = 0; i < m_methodNames.length; i++) {
                if (m_methodNames[i].equals(invocation.getMethod().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_methodNames, "methodNames", this);
    }
}
