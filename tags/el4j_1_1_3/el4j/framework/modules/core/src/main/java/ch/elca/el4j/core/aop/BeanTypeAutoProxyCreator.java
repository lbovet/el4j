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

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;

/**
 * Auto proxy creator that identifies beans to proxy via a list of classes.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class BeanTypeAutoProxyCreator extends AbstractAutoProxyCreator {

    /** The classes which subtypes have to be proxied. */
    private Class[] m_interfaces;
    
    /**
     * Sets the classes to be proxied.
     * 
     * @param interfaceNames
     *      Classes to proxy.
     */
    public void setInterfaceNames(Class[] interfaceNames) {
        m_interfaces = interfaceNames;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass,
            String beanName, TargetSource customTargetSource) {
        
        if (this.m_interfaces != null) {
            for (int i = 0; i < m_interfaces.length; i++) {
                if (m_interfaces[i].isAssignableFrom(beanClass)) {
                    return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                }
            }
        }

        return DO_NOT_PROXY;
    }
}
