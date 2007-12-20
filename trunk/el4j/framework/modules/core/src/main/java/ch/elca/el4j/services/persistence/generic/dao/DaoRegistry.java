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
package ch.elca.el4j.services.persistence.generic.dao;

/**
 * A registry for DAOs.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 * @author Alex Mathey (AMA)
 */
public interface DaoRegistry {
    
    /**
     * Returns the generic DAO for entities of type {@code entityType}.
     * 
     * @param entityType
     *            The domain class for which a generic DAO should be returned.
     *            The class does some basic handling to tolerate (i.e. unwrap) 
     *            Spring proxies.
     * @return A fully generic or partially specific DAO for the given type, 
     *            null if none was found.
     */
    public <T> GenericDao<T> getFor(Class<T> entityType);
    
}
