/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.context;

import org.springframework.core.Ordered;

/**
 * Holder for ordered beans. Hold is just the name and its order.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philipp Oser (POS)
 */
public class OrderedBeanNameHolder implements Ordered {
    /**
     * Is the order of the bean.
     */
    protected final int m_order;
    
    /**
     * Is the name of the bean.
     */
    protected final String m_beanName;
    
    /**
     * Initializes the bean holder with the given name and order.
     * 
     * @param order Is the bean order.
     * @param beanName Is the name of the bean.
     */
    public OrderedBeanNameHolder(int order, String beanName) {
        m_order = order;
        m_beanName = beanName;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOrder() {
        return m_order;
    }

    /**
     * @return Returns the name of the bean.
     */
    public String getBeanName() {
        return m_beanName;
    }
}
