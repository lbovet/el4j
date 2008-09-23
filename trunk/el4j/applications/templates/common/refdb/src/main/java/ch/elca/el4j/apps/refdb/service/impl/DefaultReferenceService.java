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
package ch.elca.el4j.apps.refdb.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
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
	implements ReferenceService, ApplicationListener {
	
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
	
	/** {@inheritDoc} */
	public void onApplicationEvent(ApplicationEvent event) {
		super.onApplicationEvent(event);
		if (event instanceof ContextRefreshedEvent) {
			// Spring context is completely initialized (in contrast to afterPropertiesSet)
			CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
				getFileDao(), "fileDao", this);
			CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
				getLinkDao(), "linkDao", this);
			CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
				getFormalPublicationDao(), "formalPublicationDao", this);
			CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
				getBookDao(), "bookDao", this);
		}
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
			= getLinkDao().getAll();
		List<FormalPublication> listFormalPublications
			= getFormalPublicationDao().getAll();
		List<Book> listBooks
			= getBookDao().getAll();
		
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
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Reference> searchReferences(String field, String critera) {

		// Call hibernate Search
		List<Link> listLinks = getLinkDao().search(field, critera);
		List<FormalPublication> listFormalPublications = getFormalPublicationDao().search(field, critera);
		List<Book> listBooks = getBookDao().search(field, critera);

		List<Reference> listTmp = new LinkedList<Reference>();
		listTmp.addAll(listLinks);
		listTmp.addAll(listFormalPublications);
		listTmp.addAll(listBooks);

		// Delete the duplicate entity

		List<Reference> list = new LinkedList<Reference>();
		for (Reference entity : listTmp) {
			if (!list.contains(entity)) {
				list.add(entity);
			}
		}
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
	public void deleteReference(int key)
		throws DataAccessException, OptimisticLockingFailureException  {
		
		if (getLinkDao().referenceExists(key)) {
			getLinkDao().deleteById(key);
		} else if (getBookDao().referenceExists(key)) {
			getBookDao().deleteById(key);
		} else if (getFormalPublicationDao().referenceExists(key)) {
			getFormalPublicationDao().deleteById(key);
		} else {
			CoreNotificationHelper.notifyOptimisticLockingFailure(
				Constants.REFERENCE);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteKeyword(int key)
		throws OptimisticLockingFailureException {
		
		KeywordDao dao = getKeywordDao();
		dao.deleteById(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteReferenceAndKeywords(int refKey)
		throws DataIntegrityViolationException {
		
		Set<Keyword> keywordSet = new HashSet<Keyword>();
		
		if (getLinkDao().referenceExists(refKey)) {
			keywordSet = getLinkDao().findById(refKey).getKeywords();
			getLinkDao().deleteById(refKey);
		} else if (getBookDao().referenceExists(refKey)) {
			keywordSet = getBookDao().findById(refKey).getKeywords();
			getBookDao().deleteById(refKey);
		} else if (getFormalPublicationDao().referenceExists(refKey)) {
			keywordSet = getFormalPublicationDao().findById(refKey).
				getKeywords();
			getFormalPublicationDao().deleteById(refKey);
		} else {
			CoreNotificationHelper.notifyOptimisticLockingFailure(
				Constants.REFERENCE);
		}
		
		KeywordDao keywordDao = (KeywordDao) getDaoRegistry()
			.getFor(Keyword.class);
		
		Iterator<Keyword> it = keywordSet.iterator();
		while (it.hasNext()) {
			int delKey = it.next().getKey();
			keywordDao.deleteById(delKey);
		}
		
	}
	
	// TODO integrate this (ELJ-39)
	/*public void manualHibernateIndexing() throws DataAccessException, DataRetrievalFailureException {
		getLinkDao().manualHibernateIndexing();
		getFormalPublicationDao().manualHibernateIndexing();
		getBookDao().manualHibernateIndexing();
	}*/
}