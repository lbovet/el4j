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
package ch.elca.j4persist.generic.dao.impl;

import java.util.ArrayList;
import java.util.List;

import ch.elca.j4persist.generic.dao.DaoChangeListener;
import ch.elca.j4persist.generic.dao.DaoChangeNotifier;

/**
 * A default implementation with no notable features.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/core/src/main/java/ch/elca/el4j/services/persistence/generic/dao/impl/DefaultRepositoryChangeNotifier.java $",
 *    "$Revision:1040 $",
 *    "$Date:2006-09-04 09:11:56 +0000 (Mo, 04 Sep 2006) $",
 *    "$Author:mathey $"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class DefaultDaoChangeNotifier 
           implements DaoChangeNotifier {

    /**
     * The presently subscribed listeners.
     */
    protected List<DaoChangeListener> m_listeners
        = new ArrayList<DaoChangeListener>();
    
    /**
     * {@inheritDoc}
     */
    public void subscribe(DaoChangeListener cl) {
        m_listeners.add(cl);
    }
    
    /**
     * {@inheritDoc}
     */
    public void unsubscribe(DaoChangeListener cl) {
        m_listeners.remove(cl);
    }
    
    /**
     * {@inheritDoc}
     */
    public void announce(Change change) {
        List<DaoChangeListener> snapshot
            = new ArrayList<DaoChangeListener>(m_listeners);
        for (DaoChangeListener cl : snapshot) {
            cl.changed(change);
        }
    }
}
