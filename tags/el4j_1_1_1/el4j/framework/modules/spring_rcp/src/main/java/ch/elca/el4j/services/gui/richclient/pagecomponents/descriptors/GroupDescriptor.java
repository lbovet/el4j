/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors;

/**
 * Interface for a group descriptor. The configured group is the one that will 
 * set by configuration, so it is the initial state. The preferred group will 
 * normally be taken if the configured group is not set, i.e. if the page 
 * component was not initially linked to a page.
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
public interface GroupDescriptor {
    /**
     * Is the name of the default group.
     */
    public static final String DEFAULT_GROUP = "rootGroup";

    /**
     * @return Returns the name of the preferred group.
     */
    public String getPreferredGroup();
    
    /**
     * @return Returns the name of the configured group.
     */
    public String getConfiguredGroup();
    
    /**
     * @param group Is the configured group name to set. 
     */
    public void setConfiguredGroup(String group);
}
