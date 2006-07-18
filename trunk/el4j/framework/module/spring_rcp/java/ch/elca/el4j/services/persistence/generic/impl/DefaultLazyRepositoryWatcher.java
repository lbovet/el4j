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
package ch.elca.el4j.services.persistence.generic.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.LazyRepositoryWatcher;
import ch.elca.el4j.services.persistence.generic.RepositoryChangeListener;
import ch.elca.el4j.services.persistence.generic.dao.SimpleGenericRepository;
import ch.elca.el4j.services.search.QueryObject;

/**
 * A LazyRepositoryWatcher that automatically reloads beans if a concurrent
 * modification is detected.
 * 
 * @param <T> see supertype
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
// TODO: include IdentityFixer
public class DefaultLazyRepositoryWatcher<T> 
     extends DefaultRepositoryChangeNotifier
     implements LazyRepositoryWatcher<T> {
    
    /** The listener non-local changes should be escalated to. */
    RepositoryChangeListener m_escalator; 
    
    /** The backing repository. */
    SimpleGenericRepository<T> m_backing;
    
    /** 
     * Constructor.
     * @param backing see {@link #m_backing}
     * @param escalator see {@link #m_escalator}
     */
    public DefaultLazyRepositoryWatcher(RepositoryChangeListener escalator,
                                        SimpleGenericRepository<T> backing) {
        m_escalator = escalator;
        m_backing = backing;
    }
    
    /** {@inheritDoc} */
    public void delete(T entity) throws DataAccessException {
        try {
            m_backing.delete(entity);
            
            EntityDeletion ed = new EntityDeletion();
            ed.changee = entity;
            announce(ed);
        } catch (OptimisticLockingFailureException e) {
            escalate(FUZZY_CHANGE);
            throw e;
        }
    }

    /** {@inheritDoc} */
    public List<T> findAll() throws DataAccessException {
        // TODO
        return m_backing.findAll();
    }

    /** {@inheritDoc} */
    public List<T> findByQuery(QueryObject q) throws DataAccessException {
        // TODO
        return m_backing.findByQuery(q);
    }

    /** {@inheritDoc} */
    public Class<T> getPersistentClass() {
        return m_backing.getPersistentClass();
    }

    /** {@inheritDoc} */
    public T saveOrUpdate(T entity) throws DataAccessException,
                                           DataIntegrityViolationException, 
                                           OptimisticLockingFailureException {
        try {
            m_backing.saveOrUpdate(entity);
            return entity;
        } catch (OptimisticLockingFailureException e) {
            escalate(FUZZY_CHANGE);
            throw e;            
        }
    }
    
    /** {@inheritDoc} */
    public void refresh(T entity) {
        m_backing.refresh(entity);
        // TODO fire nested change notifications
        
        EntityChange ec = new EntityChange();
        ec.changee = entity;
        announce(ec);
    }
    
    /** 
     * Returns whether this notifier is responsible for announcing 
     * {@code change}.
     */
    private boolean isResponsibleFor(Change change) {
        if (change instanceof EntityChange) {
            return m_backing.getPersistentClass().isInstance(
                ((EntityChange) change).changee
            );
        } else {
            return true;
        }
    }

    /** {@inheritDoc} */
    public void announceIfResponsible(Change change) {
        if (isResponsibleFor(change)) {
            super.announce(change);
        }
    }
    
    /** Escalates the change to the configured escalator. */
    private void escalate(Change c) {
        m_escalator.process(c);
    }
}