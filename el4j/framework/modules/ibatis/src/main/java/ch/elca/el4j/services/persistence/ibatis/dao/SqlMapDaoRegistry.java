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
package ch.elca.el4j.services.persistence.ibatis.dao;

import java.io.Serializable;

import ch.elca.el4j.services.persistence.generic.dao.impl.SettableDaoRegistry;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;

/**
 * 
 * A DAO registry for SqlMap DAOs. DAOs are configured upon registration.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class SqlMapDaoRegistry extends
    SettableDaoRegistry<GenericSqlMapDao<?, ?>> {
     
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends PrimaryKeyOptimisticLockingObject> GenericSqlMapDao<T, ?>
    getFor(Class<T> entityType) {

        GenericSqlMapDao<T, ?> sd = (GenericSqlMapDao<T, ?>) super
            .getFor(entityType);

        if (sd == null) {
            sd = new GenericSqlMapDao<T, Serializable>();
            sd.setPersistentClass(entityType);
            register(sd);
        }

        return sd;
    }
  
}
