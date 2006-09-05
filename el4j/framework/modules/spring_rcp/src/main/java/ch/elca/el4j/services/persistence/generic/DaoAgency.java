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
package ch.elca.el4j.services.persistence.generic;

import ch.elca.el4j.services.persistence.generic.dao.DaoChangeListener;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;

/**
 * A registry for {@link DaoAgent}s relaying change notifications
 * to the appropriate notifiers.
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
public interface DaoAgency 
         extends DaoRegistry,
                 DaoChangeListener {
    
    /** 
     * Returns the DAO agent responsible for entities of type 
     * {@code entityType}.
     * 
     * @param entityType The entity type for which a DAO agent will be returned
     * @return A DAO agent for the given entity type  
     */
    <T extends PrimaryKeyOptimisticLockingObject> DaoAgent<T> getFor(Class<T> entityType);
}