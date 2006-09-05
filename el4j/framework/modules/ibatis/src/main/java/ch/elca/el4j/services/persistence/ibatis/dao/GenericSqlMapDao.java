/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
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
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.ConvenientGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * This class is an iBatis-specific implementation of the ConvenientGenericDao
 * interface.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 * 
 * @author Alex Mathey (AMA)
 */
public class GenericSqlMapDao<T extends PrimaryKeyOptimisticLockingObject,
    ID extends Serializable> extends ConvenienceSqlMapClientDaoSupport
    implements ConvenientGenericDao<T, ID>, InitializingBean {
    
    /**
     * The domain class this DAO is responsible for.
     */
    private Class<T> m_persistentClass;
    
    /**
     * @param c
     *            Mandatory. The domain class this DAO is responsible for.
     */
    // Since it is impossible to determine the actual type of a type 
    // parameter (!), we resort to requiring the caller to provide the
    // actual type as parameter, too.
    public void setPersistentClass(Class<T> c) {
        assert m_persistentClass == null;
        Reject.ifNull(c);
        m_persistentClass = c;
    }

    /**
     * @return Returns the domain class this DAO is responsible for.
     */
    public Class<T> getPersistentClass() {
        assert m_persistentClass != null;
        return m_persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findById(ID id, boolean lock) 
        throws DataAccessException, DataRetrievalFailureException {
        return (T) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("get" + getPersistentClassName() + "ByKey",
                id, getPersistentClassName());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() throws DataAccessException {
        List<T> result = getConvenienceSqlMapClientTemplate().queryForList(
            "getAll" + getPersistentClassName() + "s", null);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance) throws DataAccessException {
        //return getHibernateTemplate().findByExample(exampleInstance);
        // TODO: Currently not implemented.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByQuery(QueryObject q) throws DataAccessException {
        Reject.ifNull(q);
        List<T> result = getConvenienceSqlMapClientTemplate().queryForList(
            "search" + getPersistentClassName() + "s", q.getCriteriaList());
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    @ReturnsUnchangedParameter
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(T entity) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException {
        Reject.ifNull(entity);
        getConvenienceSqlMapClientTemplate().insertOrUpdate(entity,
            getPersistentClassName());
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T entity) throws DataAccessException {
        getConvenienceSqlMapClientTemplate().delete("delete" 
            + getPersistentClassName(), entity.getKeyAsObject());
    }
    
    /**
     * {@inheritDoc}
     */
    public T refresh(T entity) {
        //getHibernateTemplate().refresh(entity);
        // TODO: Currently not implemented.
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ID id) throws DataAccessException {
        getConvenienceSqlMapClientTemplate().delete(
            "delete" + getPersistentClassName(), id);
    }

    /** {@inheritDoc} */
    public void delete(Collection<T> entities) throws DataAccessException,
            DataIntegrityViolationException, OptimisticLockingFailureException {
        for (T entity : entities) {
            getConvenienceSqlMapClientTemplate().delete("delete" 
                + getPersistentClassName(), entity.getKeyAsObject());
        }
    }
    
    /**
     * Returns the simple name of the persistent class this DAO is responsible
     * for.
     * 
     * @return The simple name of the persistent class this DAO is responsible
     *         for.
     */
    private String getPersistentClassName() {
        return getPersistentClass().getSimpleName();
    }
}