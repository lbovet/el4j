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

import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.Constants;
import ch.elca.el4j.apps.refdb.dao.GenericReferenceDao;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.dom.ReferenceKeywordRelationship;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.ibatis.dao.GenericSqlMapDao;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * Generic DAO for references which is using iBatis SQL Maps.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 *            
 * @author Alex Mathey (AMA)
 */
public class GenericSqlMapReferenceDao<T extends Reference> 
    extends GenericSqlMapDao<T, Integer>
    implements GenericReferenceDao<T, Integer> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public T saveOrUpdate(T entity)
        throws DataAccessException, DataIntegrityViolationException, 
            OptimisticLockingFailureException {
        Reject.ifNull(entity);
        boolean mustIncreaseOptimisticLockingVersion = false;
        if (entity.isKeyNew()) {
            Object keyObject = getSqlMapClientTemplate().insert(
                "insertReference", entity);
            if (keyObject == null) {
                CoreNotificationHelper.notifyDataIntegrityViolationFailure(
                    Constants.REFERENCE);
            }
            if (entity.isKeyNew()) {
                entity.setKey(keyObject);
            }
            int count = getSqlMapClientTemplate().update(
                "insert" + getPersistentClassName(), entity);
            if (count != 1) {
                CoreNotificationHelper.notifyInsertionFailure(
                    getPersistentClassName());
            }
        } else {
            int count = getSqlMapClientTemplate().update(
                "updateReference", entity);
            if (count != 1) {
                CoreNotificationHelper.notifyOptimisticLockingFailure(
                    Constants.REFERENCE);
            }
            count = getSqlMapClientTemplate().update(
                "update" + getPersistentClassName(), entity);
            if (count != 1) {
                CoreNotificationHelper.notifyOptimisticLockingFailure(
                    getPersistentClassName());
            }
            mustIncreaseOptimisticLockingVersion = true;
        }

        /**
         * Remove all old keyword references to the saved reference and set them
         * new.
         */
        removeAllReferenceKeywordRelationshipsByReference(entity.getKey());
        Set<Keyword> set = entity.getKeywords();
        if (set != null) {
            for (Keyword k : set) {
                addReferenceKeywordRelationship(
                    entity.getKey(), k.getKey());
            }
        }

        /**
         * Increase optimistic locking version if necessary.
         */
        if (mustIncreaseOptimisticLockingVersion) {
            entity.increaseOptimisticLockingVersion();
        }
        return entity;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Integer id) throws DataAccessException {
        removeAllReferenceKeywordRelationshipsByReference(id);
        super.delete(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @SuppressWarnings("unchecked")
    public List<T> getByName(String name) throws DataAccessException,
    DataRetrievalFailureException {
        Reject.ifEmpty(name);
        List<T> result = getConvenienceSqlMapClientTemplate()
            .queryForList("get" + getPersistentClassName() + "sByName", name);
        return CollectionUtils.asList(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean referenceExists(Integer id) {
        List<Integer> idList = getConvenienceSqlMapClientTemplate()
            .queryForList("getAll" + getPersistentClassName() + "Keys", null);
        if (idList.contains(id)) {
            return true;
        }
        return false;               
    }
    
    /**
     * This method adds a relation between the given reference and keyword.
     * 
     * @param referenceKey
     *            To relate with the given keyword
     * @param keywordKey
     *            To relate with the given reference
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataIntegrityViolationException
     *             If reference could not be inserted.
     */
    private void addReferenceKeywordRelationship(
        int referenceKey, int keywordKey) 
        throws DataAccessException, DataIntegrityViolationException {
        ReferenceKeywordRelationship ref
            = new ReferenceKeywordRelationship();
        ref.setKeyReference(referenceKey);
        ref.setKeyKeyword(keywordKey);

        int count = getConvenienceSqlMapClientTemplate().update(
            "addReferenceKeywordRelationship", ref);
        if (count != 1) {
            CoreNotificationHelper.notifyDataIntegrityViolationFailure(
                Constants.REFERENCE_KEYWORD_RELATIONSHIP);
        }
    }
    
    /**
     * This method removes all relationships between the given reference and a
     * keyword.
     * 
     * @param referenceKey
     *            Is the reference key where keyword relations must be removed.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    private void removeAllReferenceKeywordRelationshipsByReference(
        int referenceKey) throws DataAccessException {
        getConvenienceSqlMapClientTemplate().delete(
            "deleteAllReferenceKeywordRelationshipsByReference",
            referenceKey);
    }
    
}
