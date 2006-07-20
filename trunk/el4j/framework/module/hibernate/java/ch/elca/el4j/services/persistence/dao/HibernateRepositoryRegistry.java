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
package ch.elca.el4j.services.persistence.dao;

import java.io.Serializable;

import org.hibernate.SessionFactory;

import ch.elca.el4j.services.persistence.generic.repo.impl.SettableRepositoryRegistry;

/**
 * A repository registry for hibernate repositories. Repositories are configured
 * upon registration using this registry's session factory.
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
public class HibernateRepositoryRegistry 
    extends SettableRepositoryRegistry<GenericHibernateRepository<?, ?>> {
    
    /** The session factory to configure repositories with. */
    private SessionFactory m_sessionFactory;

    /**
     * Sets {@link #m_sessionFactory} to {@code sf}.
     */
    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }
    
    /**
     * Returns the session factory that repositories are configured with upon
     * registration. 
     */
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }
    
    /** {@inheritDoc} */
    @Override
    public void injectInto(GenericHibernateRepository<?, ?> rep) {
        rep.setSessionFactory(m_sessionFactory);
    }

    /** {@inheritDoc} */
    @Override
    public <T> GenericHibernateRepository<T, ?> getFor(Class<T> entityType) {

        GenericHibernateRepository<T, ?> hr 
            = (GenericHibernateRepository<T, ?>)
                super.getFor(entityType);
        if (hr == null) {
            hr = new GenericHibernateRepository<T, Serializable>(entityType);
            register(hr);
        }
        return hr;
    }
}
