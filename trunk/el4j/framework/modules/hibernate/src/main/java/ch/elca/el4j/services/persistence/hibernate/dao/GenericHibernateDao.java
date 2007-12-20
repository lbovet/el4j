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

package ch.elca.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * This class is a Hibernate-specific implementation of the 
 * ConvenienceGenericDao interface.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL:https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/GenericHibernateDao.java $",
 *    "$Revision:1059 $",
 *    "$Date:2006-09-04 13:33:11 +0000 (Mo, 04 Sep 2006) $",
 *    "$Author:mathey $"
 * );</script>
 *
 * @param <T>
 *            The domain class the DAO is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 * 
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateDao<T, ID extends Serializable>
    extends ConvenienceHibernateDaoSupport
    implements ConvenienceGenericDao<T, ID>, InitializingBean {
    
    /**
     * The domain class this DAO is responsible for.
     */
    private Class<T> m_persistentClass;
    
    /** 
     * @param c
     *           Mandatory. The domain class this DAO is responsible for.
     */
    // Since it is impossible to determine the actual type of a type 
    // parameter (!), we resort to requiring the caller to provide the
    // actual type as parameter, too.
    // Not set in a constructor to enable easy CGLIB-proxying (passing 
    // constructor arguments to Spring AOP proxies is quite cumbersome).
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
     * Retrieves a domain object by identifier, optionally obtaining a database
     * lock for this operation.  <br>
     * 
     * (For hibernate specialists: we do a "get()"
     * in this method. In case you require only a "load()" (e.g. for lazy 
     * loading to work) we recommend that you write your own find method in the
     * interface's subclass.)
     * 
     * @param id
     *            The id of a domain object
     * @param lock
     *            Indicates whether a database lock should be obtained for this
     *            operation        
     * @throws DataAccessException
     *             If general data access problem occurred
     * @throws DataRetrievalFailureException
     *             If domain object could not be retrieved           
     * @return The desired domain object
     */  
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    T findById(ID id, boolean lock)
        throws DataAccessException, DataRetrievalFailureException {
        
        T entity;
        if (lock) {
            entity = (T) getConvenienceHibernateTemplate()
                .get(getPersistentClass(), id, LockMode.UPGRADE);
        } else {
            entity = (T) getConvenienceHibernateTemplate()
                .get(getPersistentClass(), id);
        }
        if (entity == null) {
            throw new DataRetrievalFailureException("The desired domain object"
                   + " could not be retrieved.");
        }
        return entity;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public T findById(ID id) 
        throws DataAccessException, DataRetrievalFailureException {
        return (T) getConvenienceHibernateTemplate().getByIdStrong(
            getPersistentClass(), id, getPersistentClassName());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<T> getAll() throws DataAccessException {
        return getConvenienceHibernateTemplate().loadAll(getPersistentClass());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<T> findByQuery(QueryObject q) throws DataAccessException {
        DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(q,
            getPersistentClass());
        return getConvenienceHibernateTemplate()
            .findByCriteria(hibernateCriteria);
    }

    /**
     * {@inheritDoc}
     */
    @ReturnsUnchangedParameter
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public T saveOrUpdate(T entity) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException {
        getConvenienceHibernateTemplate().saveOrUpdateStrong(entity, 
            getPersistentClassName());
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(T entity) throws DataAccessException {
        getConvenienceHibernateTemplate().delete(entity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public T refresh(T entity) throws DataAccessException, 
    DataRetrievalFailureException {
        getConvenienceHibernateTemplate().refresh(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ID id) throws DataAccessException {
        getConvenienceHibernateTemplate().deleteStrong(getPersistentClass(),
            id, getPersistentClassName());
    }

    /** {@inheritDoc} */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<T> entities) throws DataAccessException,
            DataIntegrityViolationException, OptimisticLockingFailureException {
        getConvenienceHibernateTemplate().deleteAll(entities);
    }
    
    /**
     * Returns the simple name of the persistent class this DAO is responsible
     * for.
     * 
     * @return The simple name of the persistent class this DAO is responsible
     *         for.
     */
    protected String getPersistentClassName() {
        return getPersistentClass().getSimpleName();
    }
}