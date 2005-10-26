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

package ch.elca.el4j.core.aop;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;

/**
 * Auto proxy creator that identifies beans to proxy via a list of classes.
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
