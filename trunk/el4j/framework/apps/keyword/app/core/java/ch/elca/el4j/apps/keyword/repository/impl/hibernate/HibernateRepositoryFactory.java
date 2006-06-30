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
package ch.elca.el4j.apps.keyword.repository.impl.hibernate;

import org.hibernate.SessionFactory;

import ch.elca.el4j.apps.keyword.repository.KeywordRepository;
import ch.elca.el4j.apps.keyword.repository.RepositoryFactory;

/**
 * 
 * This class is a Hibernate-specific implementation of the RepositoryFactory.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class HibernateRepositoryFactory extends RepositoryFactory {
    
    /**
     * Hibernate session factory.
     */
    private SessionFactory m_sessionFactory;
    
    /**
     * @return Returns the sessionFactory.
     */
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    /**
     * @param sessionFactory Is the sessionFactory to set.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        m_sessionFactory = sessionFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordRepository getKeywordRepository() {
        HibernateKeywordRepository keywordRepository
            = new HibernateKeywordRepository();
        keywordRepository.setSessionFactory(m_sessionFactory);
        return keywordRepository;
    }

} 
