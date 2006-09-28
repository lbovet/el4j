/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.apps.keyword.service;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;

/**
 * This interface provides the business methods which can be used in the
 * presentation layer and which are not already present in the underlying DAO.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 */
public interface KeywordService {
       
    /**
     * Remove keywords. Primary key of each keyword will be used.
     * 
     * @param keys
     *            Are the primary keys of the keywords that should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws OptimisticLockingFailureException
     *             If a keyword could not be deleted.
     */
    @RollbackConstraint(rollbackFor = {
            OptimisticLockingFailureException.class, 
            DataAccessException.class })
    public void removeKeywords(Collection<?> keys) 
        throws OptimisticLockingFailureException, DataAccessException;
}
