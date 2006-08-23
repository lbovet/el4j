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

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.keyword.repository.KeywordRepository;
import ch.elca.el4j.services.persistence.dao.GenericHibernateRepository;

/**
 * 
 * This class is a Hibernate-specific implementation of the KeywordRepository
 * interface.
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
public class HibernateKeywordRepository
    extends GenericHibernateRepository<KeywordDto, Integer>
    implements KeywordRepository {
    
    /**
     * Creates a new HibernateKeywordRepository instance.
     */
    public HibernateKeywordRepository() {
        setPersistentClass(KeywordDto.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public KeywordDto getKeywordByName(String name)
        throws DataAccessException, DataRetrievalFailureException {
        
        String queryString = "from KeywordDto keyword where name = :name";
                
        List keywordList = getHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
        if (keywordList.isEmpty()) {
            throw new DataRetrievalFailureException("The desired keyword could"
                + " not be retrieved.");
        } else {
            return (KeywordDto) keywordList.get(0);
        }
    }
}
