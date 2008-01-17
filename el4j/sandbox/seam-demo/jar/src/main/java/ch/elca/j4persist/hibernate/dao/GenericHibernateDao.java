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

package ch.elca.j4persist.hibernate.dao;

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

import ch.elca.j4persist.generic.dao.ConvenienceGenericDao;
import ch.elca.el4j.services.persistence.generic.dao.annotations.ReturnsUnchangedParameter;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceHibernateTemplate;
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
public class GenericHibernateDao 
    extends ConvenienceHibernateDaoSupport
    implements ConvenienceHibernateDao{
    
    /**
     * The domain class this DAO is responsible for.
     */
    private Class m_persistentClass;
    
    /** 
     * @param c 
     *           Mandatory. The domain class this DAO is responsible for.
     */
    // Since it is impossible to determine the actual type of a type 
    // parameter (!), we resort to requiring the caller to provide the
    // actual type as parameter, too.
    // Not set in a constructor to enable easy CGLIB-proxying (passing 
    // constructor arguments to Spring AOP proxies is quite cumbersome).
    public void setPersistentClass(Class c) {
        assert m_persistentClass == null;
        Reject.ifNull(c);
        m_persistentClass = c;
    }

    /**
     * @return Returns the domain class this DAO is responsible for.
     */
    public Class getPersistentClass() {
        assert m_persistentClass != null;
        return m_persistentClass;
    }

    /**
     * Retrieves a domain object by identifier, optionally obtaining a database
     * lock for this operation.
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
    Object findById(Serializable id, boolean lock)
        throws DataAccessException, DataRetrievalFailureException {
        
        Object entity;
        if (lock) {
            entity = getConvenienceHibernateTemplate()
                .get(getPersistentClass(), id, LockMode.UPGRADE);
        } else {
            entity = getConvenienceHibernateTemplate()
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
    public Object findById(Serializable id) 
        throws DataAccessException, DataRetrievalFailureException {
        return getConvenienceHibernateTemplate().getByIdStrong(
            getPersistentClass(), id, getPersistentClassName());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getAll() throws DataAccessException {
        DetachedCriteria criteria = DetachedCriteria
            .forClass(getPersistentClass());
        return getConvenienceHibernateTemplate().findByCriteria(criteria);
    }

    
    /**
     * {@inheritDoc}
     * 
     * 
     * @return how many elements do we find with the given query 
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public int findCountByQuery(QueryObject q) throws DataAccessException {
        DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(q,
            getPersistentClass());
        
        ch.elca.j4persist.hibernate.dao.ConvenienceHibernateTemplate template 
        = getConvenienceHibernateTemplate();
        
        template.setMaxResults(q.getMaxResults());
        
        if (q.getFirstResult() != QueryObject.NO_CONSTRAINT){
            template.setFirstResult(q.getFirstResult());
        }      
        
        return template.findCountByCriteria(hibernateCriteria);
    }    
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List findByQuery(QueryObject q) throws DataAccessException {
        DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(q,
            getPersistentClass());
        
        List result;
        if (q.getFirstResult() == QueryObject.NO_CONSTRAINT) {
            
            result = getConvenienceHibernateTemplate()
            .findByCriteria(hibernateCriteria);
            
        } else {
            
            result = getConvenienceHibernateTemplate()
            .findByCriteria(hibernateCriteria,q.getFirstResult(),
                q.getMaxResults());
        }
        
        return result;
    }
    
    

    /**
     * {@inheritDoc}
     */
    @ReturnsUnchangedParameter
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public Object saveOrUpdate(Object entity) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException {
        getConvenienceHibernateTemplate().saveOrUpdateStrong(entity, 
            getPersistentClassName());
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteObject(Object entity) throws DataAccessException {
        getConvenienceHibernateTemplate().delete(entity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Object refresh(Object entity) throws DataAccessException, 
    DataRetrievalFailureException {
        getConvenienceHibernateTemplate().refresh(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Serializable id) throws DataAccessException {
        getConvenienceHibernateTemplate().deleteStrong(getPersistentClass(),
            id, getPersistentClassName());
    }

    /** {@inheritDoc} */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection entities) throws DataAccessException,
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