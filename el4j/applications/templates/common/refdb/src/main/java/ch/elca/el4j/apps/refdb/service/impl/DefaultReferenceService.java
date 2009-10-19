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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.core.context.ModuleApplicationListener;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.monitoring.notification.PersistenceNotificationHelper;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This is the default implementation of the reference service.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 */
public class DefaultReferenceService extends DefaultKeywordService
	implements ReferenceService, ModuleApplicationListener {
	
	/**
	 * Constructor.
	 */
	public DefaultReferenceService() { }
	
	/**
	 * @return Returns the DAO for files.
	 */
	public FileDao getFileDao() {
		return (FileDao) getDaoRegistry().getFor(File.class);
	}

	/**
	 * @return Returns the DAO for links.
	 */
	public LinkDao getLinkDao() {
		return (LinkDao) getDaoRegistry().getFor(Link.class);
	}
	
	/**
	 * @return Returns the DAO for formal publications.
	 */
	public FormalPublicationDao getFormalPublicationDao() {
		return (FormalPublicationDao) getDaoRegistry().getFor(FormalPublication.class);
	}
	
	/**
	 * @return Returns the DAO for books.
	 * @return
	 */
	public BookDao getBookDao() {
		return (BookDao) getDaoRegistry().getFor(Book.class);
	}
	
	/** {@inheritDoc} */
	public synchronized void onContextRefreshed() {
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
			PersistenceNotificationHelper.notifyDataRetrievalFailure(Constants.REFERENCE);
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
		
		List<Link> listLinks = getLinkDao().getByName(name);
		List<FormalPublication> listFormalPublications = getFormalPublicationDao().getByName(name);
		List<Book> listBooks = getBookDao().getByName(name);
		
		List<Reference> list = new LinkedList<Reference>();
		list.addAll(listLinks);
		list.addAll(listFormalPublications);
		list.addAll(listBooks);
		return list;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Reference> getReferencesByKeywords(List<Keyword> keywords) throws DataAccessException {
		if (!CollectionUtils.isEmpty(keywords)) {
			final String query = "select distinct r from Reference r left join r.keywords as keyword "
				+ "where keyword in (:list)";
			return getKeywordDao().getConvenienceHibernateTemplate().findByNamedParam(query, "list", keywords);
		} else {
			return new ArrayList<Reference>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Reference> getAllReferences() throws DataAccessException {
		List<Link> listLinks = getLinkDao().getAll();
		List<FormalPublication> listFormalPublications = getFormalPublicationDao().getAll();
		List<Book> listBooks = getBookDao().getAll();
		
		List<Reference> list = new LinkedList<Reference>();
		list.addAll(listLinks);
		list.addAll(listFormalPublications);
		list.addAll(listBooks);
		return list;
	}

	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Reference> searchReferences(QueryObject query)
		throws DataAccessException {
		Reject.ifNull(query);
		
		List<Link> listLinks = getLinkDao().findByQuery(query);
		List<FormalPublication> listFormalPublications = getFormalPublicationDao().findByQuery(query);
		List<Book> listBooks = getBookDao().findByQuery(query);
		
		List<Reference> list = new LinkedList<Reference>();
		list.addAll(listLinks);
		list.addAll(listFormalPublications);
		list.addAll(listBooks);
		return list;
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Reference> searchReferences(String[] fields, String critera) {

		// Call hibernate Search
		List<Link> listLinks = getLinkDao().search(fields, critera);
		List<FormalPublication> listFormalPublications = getFormalPublicationDao().search(fields, critera);
		List<Book> listBooks = getBookDao().search(fields, critera);

		// use set to avoid duplicate entries
		Set<Reference> resultSet = new HashSet<Reference>();
		resultSet.addAll(listLinks);
		resultSet.addAll(listFormalPublications);
		resultSet.addAll(listBooks);
		
		return new LinkedList<Reference>(resultSet);
	}

	/** {@inheritDoc} */
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
			FormalPublication formalPublication = (FormalPublication) reference;
			newReference = getFormalPublicationDao().saveOrUpdate(formalPublication);
		} else {
			CoreNotificationHelper.notifyMisconfiguration("Unknown kind of reference.");
		}
		return newReference;
	}

	/** {@inheritDoc} */
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
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(Constants.REFERENCE);
		}
	}
	
	/** {@inheritDoc} */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteKeyword(int key)
		throws OptimisticLockingFailureException {
		
		KeywordDao dao = getKeywordDao();
		dao.deleteById(key);
	}
	
	/** {@inheritDoc} */
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
			PersistenceNotificationHelper.notifyOptimisticLockingFailure(
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
	
	/** {@inheritDoc} */
	public void createHibernateSearchIndex() throws DataAccessException, DataRetrievalFailureException {
		getLinkDao().createHibernateSearchIndex();
		getBookDao().createHibernateSearchIndex();
		getFormalPublicationDao().createHibernateSearchIndex();
	}
}