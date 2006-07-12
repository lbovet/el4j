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
package ch.elca.el4j.apps.keyword.repository.impl.hibernate;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.repository.KeywordRepositoryRegistry;
import ch.elca.el4j.services.persistence.dao.GenericHibernateRepository;
import ch.elca.el4j.services.persistence.dao.HibernateRepositoryRegistry;

/**
 * Repository registry for the keyword-application with hibernate-backed
 * persistence.
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
public class HibernateKeywordRepositoryRegistry 
    extends HibernateRepositoryRegistry 
    implements KeywordRepositoryRegistry {

    /**
     * Constructor.
     */
    protected HibernateKeywordRepositoryRegistry() {
        super.register(
            new HibernateKeywordRepository()
        );
    }
    
    /**
     * Returns the keyword repository.
     */
    @SuppressWarnings("unchecked")
    public HibernateKeywordRepository getForKeyword() {
        return (HibernateKeywordRepository) 
            (GenericHibernateRepository<KeywordDto, Integer>)
            getFor(KeywordDto.class);
    }
}
