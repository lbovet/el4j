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
package ch.elca.el4j.apps.refdb.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.service.impl.DefaultKeywordService;
import ch.elca.el4j.apps.refdb.Constants;
import ch.elca.el4j.apps.refdb.dao.ReferenceDao;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.FormalPublicationDto;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This is the default implementation of the reference service.
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
public class DefaultReferenceService extends DefaultKeywordService
    implements ReferenceService {
    /**
     * Inner reference to the working dao.
     */
    private ReferenceDao m_referenceDao;

    /**
     * @return Returns the referenceDao.
     */
    public ReferenceDao getReferenceDao() {
        return m_referenceDao;
    }

    /**
     * @param referenceDao
     *            The referenceDao to set.
     */
    public void setReferenceDao(ReferenceDao referenceDao) {
        m_referenceDao = referenceDao;
        setKeywordDao(referenceDao);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getReferenceDao(), "referenceDao", this);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public AnnotationDto getAnnotationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return getReferenceDao().getAnnotationByKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getAnnotationsByAnnotator(String annotator)
        throws DataAccessException {
        Reject.ifEmpty(annotator);
        return getReferenceDao().getAnnotationsByAnnotator(annotator);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getAnnotationsByReference(int key) throws DataAccessException {
        return getReferenceDao().getAnnotationsByReference(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getAllAnnotations() throws DataAccessException {
        return getReferenceDao().getAllAnnotations();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnnotationDto saveAnnotation(AnnotationDto annotation)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(annotation);
        return getReferenceDao().saveAnnotation(annotation);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAnnotation(int key) throws DataAccessException, 
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getReferenceDao().removeAnnotation(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public FileDto getFileByKey(int key) throws DataAccessException, 
        DataRetrievalFailureException {
        return getReferenceDao().getFileByKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getFilesByName(String name) throws DataAccessException {
        Reject.ifEmpty(name);
        return getReferenceDao().getFilesByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getFilesByReference(int key) throws DataAccessException {
        return getReferenceDao().getFilesByReference(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getAllFiles() throws DataAccessException {
        return getReferenceDao().getAllFiles();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public FileDto saveFile(FileDto file) throws DataAccessException, 
        InsertionFailureException, OptimisticLockingFailureException {
        Reject.ifNull(file);
        return getReferenceDao().saveFile(file);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeFile(int key) throws DataAccessException, 
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getReferenceDao().removeFile(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getFileDescriptorViewsByReference(int key)
        throws DataAccessException {
        return getReferenceDao().getFileDescriptorViewsByReference(key);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public FileDescriptorView modifyFileDescriptorView(
        FileDescriptorView fileView) throws DataAccessException, 
            DataRetrievalFailureException, OptimisticLockingFailureException {
        Reject.ifNull(fileView);
        return getReferenceDao().modifyFileDescriptorView(fileView);
    }

    /**
     * {@inheritDoc}      
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public FileDescriptorView saveFileAndReturnFileDescriptorView(FileDto file)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(file);
        FileDto newFile = getReferenceDao().saveFile(file);
        
        FileDescriptorView fileView = new FileDescriptorView();
        fileView.setKey(
            newFile.getKey());
        fileView.setKeyToReference(
            newFile.getKeyToReference());
        fileView.setName(
            newFile.getName());
        fileView.setMimeType(
            newFile.getMimeType());
        fileView.setSize(
            newFile.getSize());
        fileView.setOptimisticLockingVersion(
            newFile.getOptimisticLockingVersion());
        return fileView;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ReferenceDto getReferenceByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        ReferenceDto reference = null;
        boolean referenceExists = false;
        // Checkstyle: EmptyBlock off
        try {
            reference = getReferenceDao().getLinkByKey(key);
            referenceExists = true;
        } catch (DataRetrievalFailureException e) { }
        if (!referenceExists) {
            try {
                reference = getReferenceDao().getBookByKey(key);
                referenceExists = true;
            } catch (DataRetrievalFailureException e) { }
        }
        if (!referenceExists) {
            try {
                reference = getReferenceDao().getFormalPublicationByKey(key);
                referenceExists = true;
            } catch (DataRetrievalFailureException e) { }
        }
        // Checkstyle: EmptyBlock on
        if (!referenceExists) {
            CoreNotificationHelper.notifyDataRetrievalFailure(
                Constants.REFERENCE);
        }
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getReferencesByName(String name) throws DataAccessException {
        Reject.ifEmpty(name);
        
        List listLinks 
            = getReferenceDao().getLinksByName(name);
        List listFormalPublications 
            = getReferenceDao().getFormalPublicationsByName(name);
        List listBooks 
            = getReferenceDao().getBooksByName(name);
        
        List list = new LinkedList();
        list.addAll(listLinks);
        list.addAll(listFormalPublications);
        list.addAll(listBooks);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List getAllReferences() throws DataAccessException {
        List listLinks 
            = getReferenceDao().getAllLinks();
        List listFormalPublications 
            = getReferenceDao().getAllFormalPublications();
        List listBooks 
            = getReferenceDao().getAllBooks();
        
        List list = new LinkedList();
        list.addAll(listLinks);
        list.addAll(listFormalPublications);
        list.addAll(listBooks);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)    
    public List searchReferences(QueryObject query)
        throws DataAccessException {
        Reject.ifNull(query);
        
        List listLinks = getReferenceDao().searchLinks(query);
        List listFormalPublications 
            = getReferenceDao().searchFormalPublications(query);
        List listBooks = getReferenceDao().searchBooks(query);
        
        List list = new LinkedList();
        list.addAll(listLinks);
        list.addAll(listFormalPublications);
        list.addAll(listBooks);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceDto saveReference(ReferenceDto reference)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException {
        Reject.ifNull(reference);
        
        ReferenceDto newReference = null;
        if (reference instanceof LinkDto) {
            LinkDto link = (LinkDto) reference;
            newReference = getReferenceDao().saveLink(link);
        } else if (reference instanceof BookDto) {
            BookDto book = (BookDto) reference;
            newReference = getReferenceDao().saveBook(book);
        } else if (reference instanceof FormalPublicationDto) {
            FormalPublicationDto formalPublication 
                = (FormalPublicationDto) reference;
            newReference 
                = getReferenceDao().saveFormalPublication(formalPublication);
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "Unknown kind of reference dto.");
        }
        return newReference;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeReference(int key) throws DataAccessException, 
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        boolean referenceExists = false;
        // Checkstyle: EmptyBlock off
        try {
            getReferenceDao().removeLink(key);
            referenceExists = true;
        } catch (DataAccessException e) { }
        if (!referenceExists) {
            try {
                getReferenceDao().removeBook(key);
                referenceExists = true;
            } catch (DataAccessException e) { }
        }
        if (!referenceExists) {
            try {
                getReferenceDao().removeFormalPublication(key);
                referenceExists = true;
            } catch (DataAccessException e) { }
        }
        // Checkstyle: EmptyBlock on
        if (!referenceExists) {
            CoreNotificationHelper
                .notifyJdbcUpdateAffectedIncorrectNumberOfRows(
                    Constants.REFERENCE, "deleteReferenceType", 1, 0);
        }
    }
}