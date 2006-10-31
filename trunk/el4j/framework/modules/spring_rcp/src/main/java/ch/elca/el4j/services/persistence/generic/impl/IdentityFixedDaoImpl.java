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
package ch.elca.el4j.services.persistence.generic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.search.QueryObject;

/**
 * Identity-fixed view on a (possibly remote) DAO. 
 * 
 * @param <T> see supertype.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 * @deprecated use {@link ch.elca.el4j.services.persistence.generic.dao
 * .AbstractIdentityFixer.GenericInterceptor} instead.
 * @see AbstractIdentityFixer
 */
@Deprecated
public class IdentityFixedDaoImpl<T> 
    implements GenericDao<T> {
    /** The fixer to be used. */
    AbstractIdentityFixer m_fixer;
    
    /** The backing DAO (may be remote). */
    GenericDao<T> m_backing;
    
    /**
     * Constructor.
     * @param backing See {@link #m_backing}
     * @param fixer See {@link #m_fixer}
     */
    public IdentityFixedDaoImpl(AbstractIdentityFixer fixer,
                                   GenericDao<T> backing) {
        m_backing = backing;
        m_fixer = fixer;
    }
    
    /** {@inheritDoc} */
    public void delete(Collection<T> entities) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException {
        m_backing.delete(entities);
    }

    /** 
     * Fixes the identities of list's elements.
     * @param list the list containing the mangled identities
     * @return the resulting list
     */
    protected List<T> fixed(List<T> list) {
        List<T> r = new ArrayList<T>();
        for (T t : list) {
            r.add(m_fixer.merge(null, t));
        }
        return r;
    }
    
    /** {@inheritDoc} */
    public List<T> findAll() throws DataAccessException {
        return fixed(m_backing.findAll());        
    }

    /** {@inheritDoc} */
    public List<T> findByQuery(QueryObject q) throws DataAccessException {
        return fixed(m_backing.findByQuery(q));        
    }

    /** {@inheritDoc} */
    public Class<T> getPersistentClass() {
        return m_backing.getPersistentClass();
    }

    /** {@inheritDoc} */
    public T refresh(T entity) {
        return m_fixer.merge(entity, m_backing.refresh(entity));
    }

    /** {@inheritDoc} */
    public T saveOrUpdate(T entity) throws DataAccessException,
        DataIntegrityViolationException, OptimisticLockingFailureException {
        // assumes: entity is a representative or there is no representative yet
        return m_fixer.merge(entity, m_backing.saveOrUpdate(entity));
        // ensures that return value is a representative
    }
}