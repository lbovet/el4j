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

package ch.elca.el4j.services.monitoring.jmx;

import javax.management.ObjectName;

/**
 * The interface of the proxy class for an ApplicationContext.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public interface ApplicationContextMBMBean {

    /**
     * Returns the display name of the ApplicationContext.
     * 
     * @return display name
     */
    String getName();

    /**
     * Returns the resolved config locations of this ApplicationContext, i.e.
     * the inclusive config locations minus the ones defined as exclusive config
     * locations. These resolved config locations are the ones that were loaded
     * by the ApplicationContext.
     * 
     * @return Resolved config locations
     */
    String[] getResolvedConfigLocations();

    /**
     * Returns the inclusive config locations of this ApplicationContext. The
     * inclusive config locations are the xml configuration files which are
     * loaded into the ApplicationContext.
     * 
     * @return Inclusive config locations
     */
    String[] getInclusiveConfigLocations();

    /**
     * Returns the exclusive config locations of this ApplicationContext. The
     * exclusive config locations are the xml configuration files which are
     * explicitly excluded from loading, even if they appear in inclusive config
     * locations.
     * 
     * @return Exclusive config locations
     */
    String[] getExclusiveConfigLocations();

    /**
     * Returns the JvmMB of this ApplicationContext.
     * 
     * @return JvmMB
     */
    ObjectName getJvmMB();

    /**
     * Returns the SpringBeans created by this ApplicationContext.
     * 
     * @return links to all SpringBeans created by this ApplicationContext
     */
    ObjectName[] getSpringBeansMB();
}
