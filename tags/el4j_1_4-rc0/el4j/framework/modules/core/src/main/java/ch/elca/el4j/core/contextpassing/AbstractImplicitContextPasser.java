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
package ch.elca.el4j.core.contextpassing;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Every bean requiring implicit context passing needs to have a context
 * passer bean that extends this class. The passer needs a reference to the
 * <code>ImplicitContextPassingRegistry</code> where it is should be registered.
 * 
 * <script type="text/javascript">printFileStatus 
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$", 
 *  "$Author$" ); </script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public abstract class AbstractImplicitContextPasser implements
        InitializingBean, BeanNameAware, ImplicitContextPasser {
    /**
     * Indicates if this class is initalized.
     */
    private boolean m_beanIsInitialized = false;

    /**
     * Name of bean.
     */
    private String m_beanName = null;

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (!m_beanIsInitialized) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                    "implicitContextPassingRegistry", this);
        }
    }

    /**
     * Sets the implicit context passing registry and registers this context
     * passer.
     * 
     * @param registry
     *            The implicit context passing registry.
     */
    public void setImplicitContextPassingRegistry(
            ImplicitContextPassingRegistry registry) {
        Reject.ifNull(registry,
                "Implicit context passing registry must not be null.");
        registry.registerImplicitContextPasser(this);
        m_beanIsInitialized = true;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanName(String beanName) {
        m_beanName = beanName;
    }

    /**
     * @return Returns the beanName.
     */
    public String getBeanName() {
        return m_beanName;
    }
}
