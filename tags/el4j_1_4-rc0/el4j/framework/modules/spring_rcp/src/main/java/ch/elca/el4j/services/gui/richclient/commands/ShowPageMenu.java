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
package ch.elca.el4j.services.gui.richclient.commands;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;

import ch.elca.el4j.services.gui.richclient.utils.WindowUtils;

/**
 * Menu to change the displayed page.
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
public class ShowPageMenu extends CommandGroup 
    implements ApplicationWindowAware {

    /**
     * Is the window the menu is made for.
     */
    private ApplicationWindow m_applicationWindow;

    /**
     * Default constructor.
     */
    public ShowPageMenu() {
        super("showPageMenu");
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationWindow(ApplicationWindow applicationWindow) {
        m_applicationWindow = applicationWindow;
    }

    /**
     * @return Returns the application window.
     */
    public ApplicationWindow getApplicationWindow() {
        return m_applicationWindow;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Populates menu after all properties are correctly set.
     */
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        populate();
    }

    /**
     * Populates this menu with commands.
     */
    protected void populate() {
        PageDescriptor[] pageDescriptors = WindowUtils.getPageDescriptors();
        for (int i = 0; i < pageDescriptors.length; i++) {
            PageDescriptor pageDescriptor = pageDescriptors[i];
            ActionCommand command = WindowUtils.createShowPageCommand(
                pageDescriptor, getApplicationWindow());
            addInternal(command);
        }
    }
}