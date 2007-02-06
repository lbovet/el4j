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
package ch.elca.el4j.services.gui.richclient.utils;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.command.ActionCommand;

import ch.elca.el4j.services.gui.richclient.commands.ShowPageCommand;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;

/**
 * Util class for general spring rcp needs. 
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
public final class WindowUtils {
    /**
     * Hidden constructor.
     */
    private WindowUtils() { }
    
    /**
     * Creates a show page command.
     * 
     * @param pageDescriptor Is the page descriptor to create a command for.
     * @param window Is the window the command must be created for.
     * @return Returns the created show page command.
     */
    public static ActionCommand createShowPageCommand(
        PageDescriptor pageDescriptor, ApplicationWindow window) {
        ShowPageCommand command = new ShowPageCommand(pageDescriptor, window);
        return command;
    }
    
    /**
     * @return Returns an array of available page descriptors.
     */
    @ImplementationAssumption(
        "All page descriptors are contained in the application context.")
    public static PageDescriptor[] getPageDescriptors() {
        Map pageDescriptors = Services.get(
            ApplicationContext.class
        ).getBeansOfType(PageDescriptor.class, true, false);
        return (PageDescriptor[]) pageDescriptors.values().toArray(
            new PageDescriptor[pageDescriptors.size()]);
    }
}
