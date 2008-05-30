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

import java.awt.LayoutManager;

import org.springframework.richclient.application.PageComponentDescriptor;

/**
 * Group of page component descriptors.
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
public interface GroupPageComponentDescriptor 
    extends LayoutDescriptor, GroupDescriptor, ExtendedPageComponentDescriptor {
    
    /**
     * @return Returns the page component descriptors of this group.
     */
    public PageComponentDescriptor[] getPageComponentDescriptors();
    
    /**
     * @return Retruns the layout manager of this group.
     */
    public LayoutManager getLayoutManager();
}
