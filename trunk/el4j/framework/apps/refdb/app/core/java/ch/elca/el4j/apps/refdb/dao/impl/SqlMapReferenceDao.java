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
package ch.elca.el4j.apps.refdb.dao.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.dao.impl.ibatis.SqlMapKeywordDao;
import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.Constants;
import ch.elca.el4j.apps.refdb.dao.ReferenceDao;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.FormalPublicationDto;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceKeywordRelationshipDto;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Implementation of the reference DAO using iBatis.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
public class SqlMapReferenceDao extends SqlMapKeywordDao 
    implements ReferenceDao {

    /**
     * {@inheritDoc}
     */
    public void removeKeyword(int key)
        throws DataAccessException, DataRetrievalFailureException {
        removeAllReferenceKeywordRelationshipsByKeyword(key);
        super.removeKeyword(key);
    }

    /**
     * {@inheritDoc}
     */
    public AnnotationDto getAnnotationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return (AnnotationDto) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("getAnnotationByKey", new Integer(key), 
                Constants.ANNOTATION);
    }

    /**
     * {@inheritDoc}
     */
    public List getAnnotationsByAnnotator(String annotator)
        throws DataAccessException {
        Reject.ifEmpty(annotator);
        List result = getSqlMapClientTemplate().queryForList(
            "getAnnotationsByAnnotator", annotator);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public List getAnnotationsByReference(int key) throws DataAccessException {
        List result = getSqlMapClientTemplate().queryForList(
            "getAnnotationsByReference", new Integer(key));
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllAnnotations() throws DataAccessException {
        List result = getSqlMapClientTemplate().queryForList(
            "getAllAnnotations", null);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public AnnotationDto saveAnnotation(AnnotationDto annotation)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(annotation);
        getConvenienceSqlMapClientTemplate().insertOrUpdate(
            annotation, Constants.ANNOTATION);
        return annotation;
    }

    /**
     * {@inheritDoc}
     */
    public void removeAnnotation(int key) 
        throws DataAccessException,
            JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceSqlMapClientTemplate().delete(
            new Integer(key), 1, Constants.ANNOTATION);
    }

    /**
     * {@inheritDoc}
     */
    public FileDto getFileByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return (FileDto) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("getFileByKey", new Integer(key), 
                Constants.FILE);
    }

    /**
     * {@inheritDoc}
     */
    public List getFilesByName(String name) throws DataAccessException {
        Reject.ifEmpty(name);
        List result = getSqlMapClientTemplate().queryForList(
            "getFilesByName", name);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public List getFilesByReference(int key) throws DataAccessException {
        List result = getSqlMapClientTemplate().queryForList(
            "getFilesByReference", new Integer(key));
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllFiles() throws DataAccessException {
        List result = getSqlMapClientTemplate().queryForList(
            "getAllFiles", null);
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public FileDto saveFile(FileDto file) 
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(file);
        getConvenienceSqlMapClientTemplate().insertOrUpdate(
            file, Constants.FILE);
        return file;
    }

    /**
     * {@inheritDoc}
     */
    public void removeFile(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceSqlMapClientTemplate().delete(
            new Integer(key), 1, Constants.FILE);
    }

    /**
     * {@inheritDoc}
     */
    public List getFileDescriptorViewsByReference(int key)
        throws DataAccessException {
        List result = getSqlMapClientTemplate().queryForList(
            "getFileDescriptorViewsByReference", new Integer(key));
        return CollectionUtils.asList(result);
    }

    /**
     * {@inheritDoc}
     */
    public FileDescriptorView modifyFileDescriptorView(
        FileDescriptorView fileView) throws DataAccessException, 
        OptimisticLockingFailureException, DataRetrievalFailureException {
        Reject.ifNull(fileView);
        if (fileView.isKeyNew()) {
            // File must not be new!
            CoreNotificationHelper.notifyDataRetrievalFailure(
                Constants.FILE_DESCRIPTOR_VIEW);
        }
        getConvenienceSqlMapClientTemplate().insertOrUpdate(
            fileView, Constants.FILE_DESCRIPTOR_VIEW);
        return fileView;
    }

    /**
     * Get a reference by key.
     * 
     * @param type
     *            Is one of the reference types.
     * @param key
     *            Is the key, for which should be searched.
     * @return Returns the desired reference.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If reference could not be retrieved.
     */
    private ReferenceDto getReferenceByKey(String type, int key)
        throws DataAccessException, DataRetrievalFailureException {
        Reject.ifEmpty(type);
        return (ReferenceDto) getConvenienceSqlMapClientTemplate()
            .queryForObjectStrong("get" + type + "ByKey", new Integer(key), 
                type);
    }

    /**
     * Get references by name.
     * 
     * @param type
     *            Is one of the reference types.
     * @param name
     *            Is the name, for which should be searched.
     * @return Returns a list with references.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    private List getReferencesByName(String type, String name)
        throws DataAccessException {
        Reject.ifEmpty(type);
        Reject.ifEmpty(name);
        List result = getSqlMapClientTemplate().queryForList(
            "get" + type + "sByName", name);
        return CollectionUtils.asList(result);
    }

    /**
     * Get all references.
     * 
     * @param type
     *            Is one of the reference types.
     * @return Returns a list with references.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    private List getAllReferences(String type) throws DataAccessException {
        Reject.ifEmpty(type);
        List result = getSqlMapClientTemplate().queryForList(
            "getAll" + type + "s", null);
        return CollectionUtils.asList(result);
    }

    /**
     * Search for references.
     * 
     * @param type
     *            Is one of the reference types.
     * @param query
     *            Is the search query object.
     * @return Returns a list with references.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    private List searchReferences(String type, QueryObject query) 
        throws DataAccessException {
        Reject.ifEmpty(type);
        Reject.ifNull(query);
        List result = getSqlMapClientTemplate().queryForList(
                "search" + type + "s", query.getCriteriaList());
        return CollectionUtils.asList(result);
    }

    /**
     * Save reference. If reference is new, viz is has no primary key, it will
     * be inserted. Otherwise, the reference will be updated.
     * 
     * @param type
     *            Is one of the reference types.
     * @param reference
     *            Is the reference to save.
     * @return Returns the saved reference.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If link could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If link has been modificated in the meantime.
     */
    private ReferenceDto saveReference(String type, ReferenceDto reference)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifEmpty(type);
        Reject.ifNull(reference);
        boolean mustIncreaseOptimisticLockingVersion = false;
        if (reference.isKeyNew()) {
            Object keyObject = getSqlMapClientTemplate().insert(
                "insertReference", reference);
            if (keyObject == null) {
                CoreNotificationHelper.notifyInsertionFailure(
                    Constants.REFERENCE);
            }
            if (reference.isKeyNew()) {
                reference.setKey(keyObject);
            }
            int count = getSqlMapClientTemplate().update(
                "insert" + type, reference);
            if (count != 1) {
                CoreNotificationHelper.notifyInsertionFailure(type);
            }
        } else {
            int count = getSqlMapClientTemplate().update(
                "updateReference", reference);
            if (count != 1) {
                CoreNotificationHelper.notifyOptimisticLockingFailure(
                    Constants.REFERENCE);
            }
            count = getSqlMapClientTemplate().update(
                "update" + type, reference);
            if (count != 1) {
                CoreNotificationHelper.notifyOptimisticLockingFailure(type);
            }
            mustIncreaseOptimisticLockingVersion = true;
        }

        /**
         * Remove all old keyword references to the saved reference and set them
         * new.
         */
        removeAllReferenceKeywordRelationshipsByReference(reference.getKey());
        List list = reference.getKeywords();
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                KeywordDto keyword = (KeywordDto) it.next();
                addReferenceKeywordRelationship(
                    reference.getKey(), keyword.getKey());
            }
        }

        /**
         * Increase optimistic locking version if necessary.
         */
        if (mustIncreaseOptimisticLockingVersion) {
            reference.increaseOptimisticLockingVersion();
        }
        return reference;
    }

    /**
     * Remove reference.
     * 
     * @param type
     *            Is one of the reference types.
     * @param key
     *            Is the key of the reference to remove.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If link could not be deleted.
     */
    private void removeReference(String type, int key)
        throws DataAccessException,
            JdbcUpdateAffectedIncorrectNumberOfRowsException {
        Reject.ifEmpty(type);
        removeAllReferenceKeywordRelationshipsByReference(key);
        getConvenienceSqlMapClientTemplate().delete(
            new Integer(key), 1, type);
        getConvenienceSqlMapClientTemplate().delete(
            new Integer(key), 1, Constants.REFERENCE);
    }

    /**
     * {@inheritDoc}
     */
    public LinkDto getLinkByKey(int key) throws DataAccessException, 
        DataRetrievalFailureException {
        return (LinkDto) getReferenceByKey(Constants.LINK, key);
    }

    /**
     * {@inheritDoc}
     */
    public List getLinksByName(String name) throws DataAccessException {
        return getReferencesByName(Constants.LINK, name);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllLinks() throws DataAccessException {
        return getAllReferences(Constants.LINK);
    }

    /**
     * {@inheritDoc}
     */
    public List searchLinks(QueryObject query)
        throws DataAccessException {
        return searchReferences(Constants.LINK, query);
    }

    /**
     * {@inheritDoc}
     */
    public LinkDto saveLink(LinkDto link) 
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        return (LinkDto) saveReference(Constants.LINK, link);
    }

    /**
     * {@inheritDoc}
     */
    public void removeLink(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        removeReference(Constants.LINK, key);
    }

    /**
     * {@inheritDoc}
     */
    public FormalPublicationDto getFormalPublicationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return (FormalPublicationDto) getReferenceByKey(
            Constants.FORMAL_PUBLICATION, key);
    }

    /**
     * {@inheritDoc}
     */
    public List getFormalPublicationsByName(String name)
        throws DataAccessException {
        return getReferencesByName(Constants.FORMAL_PUBLICATION, name);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllFormalPublications() throws DataAccessException {
        return getAllReferences(Constants.FORMAL_PUBLICATION);
    }

    /**
     * {@inheritDoc}
     */
    public List searchFormalPublications(QueryObject query)
        throws DataAccessException {
        return searchReferences(Constants.FORMAL_PUBLICATION, query);
    }

    /**
     * {@inheritDoc}
     */
    public FormalPublicationDto saveFormalPublication(
        FormalPublicationDto formalPublication)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        return (FormalPublicationDto) saveReference(
            Constants.FORMAL_PUBLICATION, formalPublication);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFormalPublication(int key)
        throws DataAccessException,
            JdbcUpdateAffectedIncorrectNumberOfRowsException {
        removeReference(Constants.FORMAL_PUBLICATION, key);
    }

    /**
     * {@inheritDoc}
     */
    public BookDto getBookByKey(int key) throws DataAccessException, 
        DataRetrievalFailureException {
        return (BookDto) getReferenceByKey(Constants.BOOK, key);
    }

    /**
     * {@inheritDoc}
     */
    public List getBooksByName(String name) throws DataAccessException {
        return getReferencesByName(Constants.BOOK, name);
    }

    /**
     * {@inheritDoc}
     */
    public List getAllBooks() throws DataAccessException {
        return getAllReferences(Constants.BOOK);
    }

    /**
     * {@inheritDoc}
     */
    public List searchBooks(QueryObject query)
        throws DataAccessException {
        return searchReferences(Constants.BOOK, query);
    }

    /**
     * {@inheritDoc}
     */
    public BookDto saveBook(BookDto book) throws DataAccessException, 
        InsertionFailureException, OptimisticLockingFailureException {
        return (BookDto) saveReference(Constants.BOOK, book);
    }

    /**
     * {@inheritDoc}
     */
    public void removeBook(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        removeReference(Constants.BOOK, key);
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
     * @throws InsertionFailureException
     *             If reference could not be inserted.
     */
    private void addReferenceKeywordRelationship(
        int referenceKey, int keywordKey) throws DataAccessException, 
            InsertionFailureException {
        ReferenceKeywordRelationshipDto ref
            = new ReferenceKeywordRelationshipDto();
        ref.setKeyReference(referenceKey);
        ref.setKeyKeyword(keywordKey);

        int count = getConvenienceSqlMapClientTemplate().update(
            "addReferenceKeywordRelationship", ref);
        if (count != 1) {
            CoreNotificationHelper.notifyInsertionFailure(
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
            new Integer(referenceKey));
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
    private void removeAllReferenceKeywordRelationshipsByKeyword(
            int keywordKey) throws DataAccessException {
        getConvenienceSqlMapClientTemplate().delete(
            "deleteAllReferenceKeywordRelationshipsByKeyword",
            new Integer(keywordKey));
    }
}