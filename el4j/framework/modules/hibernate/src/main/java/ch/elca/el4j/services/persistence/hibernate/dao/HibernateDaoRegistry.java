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
package ch.elca.el4j.services.persistence.hibernate.dao;

import java.io.Serializable;

import org.hibernate.SessionFactory;

import ch.elca.el4j.services.persistence.generic.dao.impl.SettableDaoRegistry;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyOptimisticLockingObject;

/**
 * A DAO registry for Hibernate DAOs. DAOs are configured upon registration
 * using this registry's session factory.
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
public class HibernateDaoRegistry 
    extends SettableDaoRegistry<GenericHibernateDao<?, ?>> {
    
    /** The session factory to configure DAOs with. */
    private SessionFactory m_sessionFactory;

    /**
     * Sets {@link #m_sessionFactory} to {@code sf}.
     * 
     * @param sf The session factory to set
     */
    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }
    
    /**
     * Returns the session factory that DAOs are configured with upon
     * registration.
     * 
     * @return The session factory that DAOs are configured with upon
     *         registration
     */
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }
    
    /** {@inheritDoc} */
    @Override
    public void injectInto(GenericHibernateDao<?, ?> dao) {
        dao.setSessionFactory(m_sessionFactory);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends PrimaryKeyOptimisticLockingObject> 
    GenericHibernateDao<T, ?> getFor(Class<T> entityType) {

        GenericHibernateDao<T, ?> hd 
            = (GenericHibernateDao<T, ?>)
                super.getFor(entityType);
        if (hd == null) {
            hd = new GenericHibernateDao<T, Serializable>();
            hd.setPersistentClass(entityType);
            register(hd);
        }
        return hd;
    }
}
