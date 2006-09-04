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
package ch.elca.el4j.services.persistence.generic.impl;

import ch.elca.el4j.services.persistence.generic.HierarchyDaoChangeNotifier;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoChangeNotifier;

/**
 * A default implementation with no notable features.
 * @param <T> see supertype
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
public class DefaultHierarchyDaoChangeNotifier<T>
        extends DefaultDaoChangeNotifier
        implements HierarchyDaoChangeNotifier {
    
    /** The class of entities this notifier is responsible for. */
    Class<T> m_responsibility;
    
    /**
     * Constructor.
     * @param responsibility see {@link #m_responsibility} 
     */
    public DefaultHierarchyDaoChangeNotifier(Class<T> responsibility) {
        m_responsibility = responsibility;
    }
    
    /** 
     * Returns whether this notifier is responsible for announcing 
     * {@code change}.
     */
    private boolean isResponsibleFor(Change change) {
        if (change instanceof EntityChange) {
            return m_responsibility.isInstance(
                ((EntityChange) change).getChangee()
            );
        } else {
            return true;
        }
    }

    /** {@inheritDoc} */
    public void announceIfResponsible(Change change) {
        if (isResponsibleFor(change)) {
            announce(change);
        }
    }
}
