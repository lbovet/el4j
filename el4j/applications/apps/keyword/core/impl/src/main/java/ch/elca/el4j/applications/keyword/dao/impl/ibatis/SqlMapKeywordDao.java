/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.applications.keyword.dao.impl.ibatis;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.applications.keyword.dao.KeywordDao;
import ch.elca.el4j.applications.keyword.dom.Keyword;
import ch.elca.el4j.services.persistence.ibatis.dao.GenericSqlMapDao;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Implementation of the keyword DAO which is using iBatis sql maps.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author alex Mathey (AMA)
 */
public class SqlMapKeywordDao extends GenericSqlMapDao<Keyword, Integer> 
    implements KeywordDao {

    /**
     * Creates a new SqlMapKeywordDao instance.
     */
    public SqlMapKeywordDao() {
        setPersistentClass(Keyword.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Keyword getKeywordByName(String name)
        throws DataAccessException, DataRetrievalFailureException {
        Reject.ifEmpty(name);
        return (Keyword) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("getKeywordByName", name,
                getPersistentClassName());
    }
    
    
}