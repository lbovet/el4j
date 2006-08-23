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

package ch.elca.el4j.services.persistence.dao;

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
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ch.elca.el4j.services.persistence.generic.repo.ConvenientGenericRepository;
import ch.elca.el4j.services.persistence.generic.repo.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * This class is a Hibernate-specific implementation of the GenericRepository
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
 *            The domain class the repository is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 * 
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateRepository<T, ID extends Serializable>
    extends HibernateDaoSupport
    implements ConvenientGenericRepository<T, ID>, InitializingBean {
    
    /**
     * The domain class this repository is responsible for.
     */
    private Class<T> m_persistentClass;
    
    /** 
     * @param c
     *           Mandatory. The domain class this repository is responsible for.
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
     * @return Returns the domain class this repository is responsible for.
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
                
        T entity;
        if (lock) {
            entity = (T) getHibernateTemplate().get(getPersistentClass(), id,
                LockMode.UPGRADE);
        } else {
            entity = (T) getHibernateTemplate().get(getPersistentClass(), id);
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
    public List<T> findAll() throws DataAccessException {
        DetachedCriteria criteria = DetachedCriteria
            .forClass(getPersistentClass());
        return getHibernateTemplate().findByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance) throws DataAccessException {
        return getHibernateTemplate().findByExample(exampleInstance);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByQuery(QueryObject q) throws DataAccessException {
        DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(q,
            getPersistentClass());
        return getHibernateTemplate().findByCriteria(hibernateCriteria);
    }

    /**
     * {@inheritDoc}
     */
    @ReturnsUnchangedParameter
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(T entity) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T entity) throws DataAccessException {
        getHibernateTemplate().delete(entity);
    }
    
    /**
     * {@inheritDoc}
     */
    public T refresh(T entity) {
        getHibernateTemplate().refresh(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ID id) throws DataAccessException {
        getHibernateTemplate().delete(
            findById(id, false)
        );
    }

    /** {@inheritDoc} */
    public void delete(Collection<T> entities) throws DataAccessException,
            DataIntegrityViolationException, OptimisticLockingFailureException {
        getHibernateTemplate().deleteAll(entities);
    }
}