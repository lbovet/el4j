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
package ch.elca.el4j.services.persistence.generic.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ch.elca.el4j.services.persistence.generic.dao.RepositoryChangeListener;
import ch.elca.el4j.services.persistence.generic.dao.RepositoryChangeNotifier;

/**
 * A default implementation with no notable features.
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
public class DefaultRepositoryChangeNotifier 
           implements RepositoryChangeNotifier {

    /** The presently subscribed listeners. */
    protected List<RepositoryChangeListener> m_listeners
        = new ArrayList<RepositoryChangeListener>();
    
    /** 
     * Causes {@code cl} to receive future change notifications.
     */
    public void subscribe(RepositoryChangeListener cl) {
        m_listeners.add(cl);
    }
    
    /** 
     * Causes {@code cl} not to receive future change notifications.
     */
    public void unsubscribe(RepositoryChangeListener cl) {
        m_listeners.remove(cl);
    }
    
    /** {@inheritDoc} */
    public void announce(Change change) {
        List<RepositoryChangeListener> snapshot
            = new ArrayList<RepositoryChangeListener>(m_listeners);
        for (RepositoryChangeListener cl : snapshot) {
            cl.changed(change);
        }
    }
}
