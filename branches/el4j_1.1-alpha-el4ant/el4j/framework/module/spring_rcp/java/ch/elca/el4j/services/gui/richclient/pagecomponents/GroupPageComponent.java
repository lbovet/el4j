/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.pagecomponents;

import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponent;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.GroupDescriptor;
import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.LayoutDescriptor;

/**
 * Interface for a page component group.
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
public interface GroupPageComponent extends PageComponent, LayoutDescriptor, 
    GroupDescriptor {
    /**
     * Adds the given page component to the group page component.
     * 
     * @param pageComponent Is the page component to add.
     * @return Returns the really added <code>JComponent</code>.
     */
    public JComponent addPageComponent(PageComponent pageComponent);
    
    /**
     * Checks if the group contains the given page component.
     * 
     * @param pageComponent
     *            Is the page component that could be in group.
     * @return Returns <code>true</code> if the given page component is in
     *         group.
     */
    public boolean containsPageComponent(PageComponent pageComponent);
    
    /**
     * Removes the given page component from the group page component.
     * 
     * @param pageComponent Is the page component to remove.
     * @return Returns the really removed <code>JComponent</code>.
     */
    public JComponent removePageComponent(PageComponent pageComponent);
    
    /**
     * @return Returns the number containing page components.
     */
    public int getNumberOfPageComponents();
}
