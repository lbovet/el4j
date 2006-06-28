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
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ch.elca.el4j.services.persistence.generic.dao.GenericRepository;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;

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
 * @param <RepositoryImpl>
 *            The repository interface that is implemented by this class
 * 
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateRepository<T, ID extends Serializable,
    RepositoryImpl extends GenericRepository<T, ID>> extends HibernateDaoSupport
    implements GenericRepository<T, ID> {
    
    /**
     * The domain class this repository is responsible for.
     */
    private Class<T> m_persistentClass;

    /**
     * Creates a new Hibernate-specific repository.
     * 
     * @param c
     *            The domain class this repository is responsible for.
     */
    public GenericHibernateRepository(Class<T> c) {
        // Since it is impossible to determine the actual type of a type 
        // parameter (!), we resort to requiring the caller to provide the
        // actual type as parameter, too. 
        m_persistentClass = c;
    }

    /**
     * @return Returns the domain class this repository is responsible for.
     */
    public Class<T> getPersistentClass() {
        return m_persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findById(ID id, boolean lock) {
                
        T entity;
        if (lock) {
            entity = (T) getHibernateTemplate().load(getPersistentClass(), id,
                LockMode.UPGRADE);
        } else {
            entity = (T) getHibernateTemplate().load(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        DetachedCriteria criteria = DetachedCriteria
            .forClass(getPersistentClass());
        return getHibernateTemplate().findByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance) {
        return getHibernateTemplate().findByExample(exampleInstance);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByQuery(QueryObject q) {
        DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(q,
            getPersistentClass());
        return getHibernateTemplate().findByCriteria(hibernateCriteria);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(T entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ID id) {
        T entity = findById(id, false);
        getHibernateTemplate().delete(entity);
    }

}