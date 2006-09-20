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
package ch.elca.el4j.apps.refdb.dao.impl.ibatis;

import org.springframework.dao.DataAccessException;

import ch.elca.el4j.apps.keyword.dao.impl.ibatis.SqlMapKeywordDao;

/**
 * 
 * This class is an extended version of the SqlMapsKeywordDao for the
 * refdb application.
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
public class ExtendedSqlMapKeywordDao extends SqlMapKeywordDao {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        removeAllReferenceKeywordRelationshipsByKeyword(id);
        super.delete(id);
    }
 
    /**
     * This method removes all relationships between a reference and the given
     * keyword.
     * 
     * @param keywordKey
     *            Is the keyword key where reference relations must be removed.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    private void removeAllReferenceKeywordRelationshipsByKeyword(int keywordKey)
        throws DataAccessException {
        getConvenienceSqlMapClientTemplate().delete(
            "deleteAllReferenceKeywordRelationshipsByKeyword",
            new Integer(keywordKey));
    }
    
}
