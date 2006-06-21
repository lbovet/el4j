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
package ch.elca.el4j.services.gui.richclient.commands;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

import ch.elca.el4j.services.gui.event.RefreshEvent;
import ch.elca.el4j.services.gui.richclient.utils.Services;

/**
 * Command to publish a refresh event.
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
public class RefreshCommand extends ApplicationWindowAwareCommand {
    /**
     * Default constructor.
     */
    public RefreshCommand() {
        super("refreshCommand");
    }
    
    /**
     * {@inheritDoc}
     * 
     * Publish refresh event.
     */
    protected void doExecuteCommand() {
        RefreshEvent event = new RefreshEvent(getApplicationWindow());
        ApplicationEventPublisher applicationEventPublisher 
            = Services.get(ApplicationContext.class);
        applicationEventPublisher.publishEvent(event);
    }
}
