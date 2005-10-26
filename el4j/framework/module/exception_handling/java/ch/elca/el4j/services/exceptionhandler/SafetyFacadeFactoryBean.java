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

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Convenience proxy factory for creating safety facades.
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
public class SafetyFacadeFactoryBean extends
        AbstractExceptionHandlerFactoryBean {

    /** The exception configurations to use. */
    private ExceptionConfiguration[] m_exceptionConfigurations;

    /**
     * Default constructor. Configures the proxy to handle only those exceptions
     * that are <b>not</b> defined in the signature (excluding unchecked
     * exceptions, which are handled always).
     */
    public SafetyFacadeFactoryBean() {
        super();
        // change this behaviour in the SafetyFacadeInterceptor too
        // (to be done manually since Java doesn't support multi inheritance).
        setForwardSignatureExceptions(true);
        setHandleRTSignatureExceptions(true);
    }
    
    /**
     * @see SafetyFacadeInterceptor#setExceptionConfigurations(ExceptionConfiguration[])
     */
    public void setExceptionConfigurations(
            ExceptionConfiguration[] exceptionConfigurations) {
        m_exceptionConfigurations = exceptionConfigurations;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                m_exceptionConfigurations, "exceptionConfigurations", this);
        
        super.afterPropertiesSet();
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractExceptionHandlerInterceptor
    createExceptionHandlerInterceptor() {
        
        SafetyFacadeInterceptor interceptor = new SafetyFacadeInterceptor();
        interceptor.setExceptionConfigurations(m_exceptionConfigurations);
        return interceptor;
    }
}
