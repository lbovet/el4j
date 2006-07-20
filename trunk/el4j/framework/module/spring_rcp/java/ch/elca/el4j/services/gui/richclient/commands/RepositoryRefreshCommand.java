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

import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

import ch.elca.el4j.services.persistence.generic.LazyRepositoryWatcherRegistry;

import static ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.FUZZY_CHANGE;

/**
 * A command to request an refreshing data loaded using a lazy repository
 * watcher registry. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class RepositoryRefreshCommand extends ApplicationWindowAwareCommand {
    /**
     * The lazy repository watcher registry the components to be refreshed
     * use.
     */
    protected LazyRepositoryWatcherRegistry m_watcherRegistry;
    
    /**
     * Default constructor.
     */
    public RepositoryRefreshCommand(LazyRepositoryWatcherRegistry reg) {
        super("refreshCommand");
        m_watcherRegistry = reg;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecuteCommand() {
        m_watcherRegistry.process(FUZZY_CHANGE);
    }
}