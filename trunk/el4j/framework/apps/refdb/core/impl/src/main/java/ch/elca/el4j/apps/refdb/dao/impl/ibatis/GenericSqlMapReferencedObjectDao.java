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
package ch.elca.el4j.apps.refdb.dao.impl.ibatis;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.GenericReferencedObjectDao;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;
import ch.elca.el4j.services.persistence.ibatis.dao.GenericSqlMapDao;
import ch.elca.el4j.util.codingsupport.CollectionUtils;

/**
 * 
 * Generic DAO for referenced objects which is using iBatis SQL Maps.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 * @param <ID>
 *            The generic type of the domain class' identifier
 *            
 * @author Alex Mathey (AMA)
 */
public class GenericSqlMapReferencedObjectDao<T extends
    PrimaryKeyOptimisticLockingObject, ID extends Serializable>
    extends GenericSqlMapDao<T, ID>
    implements GenericReferencedObjectDao<T, ID> {
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<T> getByReference(ID id) throws DataAccessException {
        
        List<T> result = getConvenienceSqlMapClientTemplate().queryForList(
            "get" + getPersistentClassName() + "sByReference", id);
        return CollectionUtils.asList(result);
    }

}