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
package ch.elca.el4j.services.gui.richclient.commands;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.core.LabeledObjectSupport;

/**
 * Is the show command for pages.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ShowPageCommand extends ApplicationWindowAwareCommand {
    /**
     * Is the page descriptor for this command.
     */
    private PageDescriptor m_pageDescriptor;

    /**
     * Constructor.
     * 
     * @param pageDescriptor Is the page descriptor for this command. 
     * @param window Is the window the command is made for.
     */
    public ShowPageCommand(PageDescriptor pageDescriptor, 
        ApplicationWindow window) {
        super("showPageCommand");
        setApplicationWindow(window);
        m_pageDescriptor = pageDescriptor;
        init();
        setEnabled(true);
    }

    /**
     * Initializes this command.
     */
    private void init() {
        setId(m_pageDescriptor.getId());
        setIcon(m_pageDescriptor.getIcon());
        setCaption(m_pageDescriptor.getCaption());
        if (m_pageDescriptor instanceof LabeledObjectSupport) {
            LabeledObjectSupport labeledObject 
                = (LabeledObjectSupport) m_pageDescriptor;
            setLabel(labeledObject.getLabel());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Shows the page by using the page descriptor id.
     */
    protected void doExecuteCommand() {
        getApplicationWindow().showPage(m_pageDescriptor.getId());
    }
}