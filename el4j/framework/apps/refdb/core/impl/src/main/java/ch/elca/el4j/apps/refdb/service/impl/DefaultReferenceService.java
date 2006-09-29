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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.service.impl.DefaultKeywordService;
import ch.elca.el4j.apps.refdb.Constants;
import ch.elca.el4j.apps.refdb.dao.BookDao;
import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.apps.refdb.dao.LinkDao;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
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
 * @author Alex Mathey (AMA)
 */
public class DefaultReferenceService extends DefaultKeywordService
    implements ReferenceService {
    
    /**
     * Constructor.
     */
    public DefaultReferenceService() { }
    
    /**
     * @return Returns the DAO for files.
     */
    public FileDao getFileDao() {
        return (FileDao) getDaoRegistry()
            .getFor(File.class);
    }

    /**
     * @return Returns the DAO for links.
     */
    public LinkDao getLinkDao() {
        return (LinkDao) getDaoRegistry()
            .getFor(Link.class);
    }
    
    /**
     * @return Returns the DAO for formal publications.
     */
    public FormalPublicationDao getFormalPublicationDao() {
        return (FormalPublicationDao) getDaoRegistry()
            .getFor(FormalPublication.class);
    }
    
    /**
     * @return Returns the DAO for books.
     * @return
     */
    public BookDao getBookDao() {
        return (BookDao) getDaoRegistry()
            .getFor(Book.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getFileDao(), "fileDao", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getLinkDao(), "linkDao", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getFormalPublicationDao(), "formalPublicationDao", this);
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getBookDao(), "bookDao", this);
    }

    /**
     * {@inheritDoc}      
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public FileDescriptorView saveFileAndReturnFileDescriptorView(File file)
        throws DataAccessException, DataIntegrityViolationException, 
            OptimisticLockingFailureException {
        Reject.ifNull(file);
        File newFile = getFileDao().saveOrUpdate(file);
        
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
    public Reference getReferenceByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        Reference reference = null;
        
        if (getLinkDao().referenceExists(key)) {
            reference = getLinkDao().findById(key);
        } else if (getBookDao().referenceExists(key)) {
            reference = getBookDao().findById(key);
        } else if (getFormalPublicationDao().referenceExists(key)) {
            reference = getFormalPublicationDao().findById(key);
        } else {
            CoreNotificationHelper.notifyDataRetrievalFailure(
                Constants.REFERENCE);
        }
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Reference> getReferencesByName(String name) 
        throws DataAccessException {
        Reject.ifEmpty(name);
        
        List<Link> listLinks 
            = getLinkDao().getByName(name);
        List<FormalPublication> listFormalPublications 
            = getFormalPublicationDao().getByName(name);
        List<Book> listBooks 
            = getBookDao().getByName(name);
        
        List<Reference> list = new LinkedList<Reference>();
        list.addAll(listLinks);
        list.addAll(listFormalPublications);
        list.addAll(listBooks);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Reference> getAllReferences() throws DataAccessException {
        List<Link> listLinks 
            = getLinkDao().findAll();
        List<FormalPublication> listFormalPublications 
            = getFormalPublicationDao().findAll();
        List<Book> listBooks 
            = getBookDao().findAll();
        
        List<Reference> list = new LinkedList<Reference>();
        list.addAll(listLinks);
        list.addAll(listFormalPublications);
        list.addAll(listBooks);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)    
    public List<Reference> searchReferences(QueryObject query)
        throws DataAccessException {
        Reject.ifNull(query);
        
        List<Link> listLinks = getLinkDao().findByQuery(query);
        List<FormalPublication> listFormalPublications 
            = getFormalPublicationDao().findByQuery(query);
        List<Book> listBooks = getBookDao().findByQuery(query);
        
        List<Reference> list = new LinkedList<Reference>();
        list.addAll(listLinks);
        list.addAll(listFormalPublications);
        list.addAll(listBooks);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Reference saveReference(Reference reference)
        throws DataAccessException, DataIntegrityViolationException , 
            OptimisticLockingFailureException {
        Reject.ifNull(reference);
        
        Reference newReference = null;
        if (reference instanceof Link) {
            Link link = (Link) reference;
            newReference = getLinkDao().saveOrUpdate(link);
        } else if (reference instanceof Book) {
            Book book = (Book) reference;
            newReference = getBookDao().saveOrUpdate(book);
        } else if (reference instanceof FormalPublication) {
            FormalPublication formalPublication 
                = (FormalPublication) reference;
            newReference 
                = getFormalPublicationDao().saveOrUpdate(formalPublication);
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "Unknown kind of reference.");
        }
        return newReference;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeReference(int key)
        throws DataAccessException, OptimisticLockingFailureException  {
        
        if (getLinkDao().referenceExists(key)) {
            getLinkDao().delete(key);
        } else if (getBookDao().referenceExists(key)) {
            getBookDao().delete(key);
        } else if (getFormalPublicationDao().referenceExists(key)) {
            getFormalPublicationDao().delete(key);
        } else {
            CoreNotificationHelper.notifyOptimisticLockingFailure(
                Constants.REFERENCE);
        }
    }
}