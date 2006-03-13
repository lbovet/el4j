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
package ch.elca.el4j.services.daemonmanager;

import java.util.List;

/**
 * Factory for daemons. The given number of daemons will be instantiated by
 * using the given bean name.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface DaemonFactory {
    /**
     * @return Returns the requested number of daemons in a list.
     */
    public List getDaemons();
    
    /**
     * @param daemonBeanName Is the bean name to instantiate daemons.
     */
    public void setDaemonBeanName(String daemonBeanName);
    
    /**
     * @param numberOfDaemons Is the number daemons to instantiate.
     */
    public void setNumberOfDaemons(int numberOfDaemons);
}
