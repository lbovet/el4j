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
package ch.elca.el4j.tests.refdb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

// Checkstyle: MagicNumber off

/**
 * Abstract test case for <code>DefaultReferenceService</code>.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractReferenceServiceTest
	extends AbstractTestCaseBase {
	/**
	 * Private logger.
	 */
	private static Logger s_logger = LoggerFactory.getLogger(AbstractReferenceServiceTest.class);

	/**
	 * Reference service. Created by application context.
	 */
	private ReferenceService m_referenceService;

	/**
	 * Keyword DAO. Created by application context.
	 */
	private KeywordDao m_keywordDao;
	
	/**
	 * Hide default constructor.
	 */
	protected AbstractReferenceServiceTest() { }
	
	/**
	 * @return Returns the reference service.
	 */
	protected ReferenceService getReferenceService() {
		if (m_referenceService == null) {
			m_referenceService
				= (ReferenceService) getApplicationContext().getBean(
					"referenceService");
		}
		return m_referenceService;
	}

	/**
	 * This test inserts a link and three keywords.
	 */
	@Test
	public void testInsertLink() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		Link link = new Link();
		link.setName("iBatis Data Mapper Developer Guide");
		link.setHashValue("xyz");
		link.setDescription("This page shows you how to develop an "
			+ "application with iBatis.");
		link.setVersion("2.0");
		link.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		link.setDate(new Date(c.getTimeInMillis()));
		link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");

		Keyword keyword = new Keyword();
		keyword.setName("iBatis");
		Keyword keyword2 = new Keyword();
		keyword2.setName("SqlMap 2.0");
		Keyword keyword3 = new Keyword();
		keyword3.setName("Data mapper");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		link.setKeywords(listKeywords);

		service.saveReference(link);
	}

	/**
	 * This test adds a link and keywords. Some of the keywords are related with
	 * the link and some are not. After that the link will be get by primary key
	 * and compared with the added one. At the end the link will be removed.
	 */
	@Test
	public void testInsertGetRemoveLink() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		Link link = new Link();
		link.setName("iBatis Data Mapper Developer Guide");
		link.setHashValue("xyz");
		link.setDescription("This page shows you how to develop an "
			+ "application with iBatis.");
		link.setVersion("2.0");
		link.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		link.setDate(new Date(c.getTimeInMillis()));
		link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");

		Keyword keyword = new Keyword();
		keyword.setName("iBatis");
		Keyword keyword2 = new Keyword();
		keyword2.setName("SqlMap 2.0");
		Keyword keyword3 = new Keyword();
		keyword3.setName("Data mapper");
		Keyword keyword4 = new Keyword();
		keyword4.setName("Dummy");
		keyword4.setDescription("This keyword is not "
			+ "assigned to any reference.");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);
		keywordDao.saveOrUpdate(keyword4);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		link.setKeywords(listKeywords);

		link = (Link) service.saveReference(link);
		
		Link link2 = (Link) service.getReferenceByKey(link.getKey());
		
		// the next two lines are here due to a strange bug of mysql
//		link.getWhenInserted().setNanos(0);
//		link2.getWhenInserted().setNanos(0);
		
		assertTrue("Links are not equal.", link.equals(link2));

		Set<Keyword> listKeywords2 = link2.getKeywords();
		assertEquals("There are not three keywords related with the link.",
			3, listKeywords2.size());
		
		for (Keyword k : listKeywords2) {
			if (!(k.equals(keyword)
				|| k.equals(keyword2)
				|| k.equals(keyword3))) {
				fail("There was an unexpected keyword related with link.");
			}
		}

		service.deleteReference(link2.getKey());
		List list = service.getReferencesByName(
			"iBatis Data Mapper Developer Guide");
		assertEquals("There is still a link with "
			+ "name 'iBatis Data Mapper Developer Guide'.", list.size(), 0);
		
		try {
			service.deleteReference(link2.getKey());
			fail("Reference already removed. Must fail!");
		} catch (OptimisticLockingFailureException e) {
			s_logger.debug("Expected exception catched.", e);
		}
	}

	/**
	 * This test adds a link with three keywords. After saving the link the same
	 * link will be get from database. This link get a new name and one keyword
	 * will be replaced by another. Afterwards changes will be saved to
	 * database.
	 */
	@Test
	public void testInsertChangeLink() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		Link link = new Link();
		link.setName("iBatis Data Mapper Developer Guide");
		link.setHashValue("xyz");
		link.setDescription("This page shows you how to develop an "
			+ "application with iBatis.");
		link.setVersion("2.0");
		link.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		link.setDate(new Date(c.getTimeInMillis()));
		link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");

		Keyword keyword = new Keyword();
		keyword.setName("iBatis");
		Keyword keyword2 = new Keyword();
		keyword2.setName("SqlMap 2.0");
		Keyword keyword3 = new Keyword();
		keyword3.setName("Data mapper");
		Keyword keyword4 = new Keyword();
		keyword4.setName("Dummy");
		keyword4.setDescription("This keyword is not "
			+ "assigned to any reference.");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);
		keywordDao.saveOrUpdate(keyword4);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		link.setKeywords(listKeywords);

		link = (Link) service.saveReference(link);

		List list = service.getReferencesByName("iBatis Data Mapper "
			+ "Developer Guide");
		assertEquals("Not one link with name 'iBatis Data Mapper "
			+ "Developer Guide'.", 1, list.size());
		Link link2 = (Link) list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		link.getWhenInserted().setNanos(0);
//		link2.getWhenInserted().setNanos(0);
		
		assertTrue("Links are not equal.", link.equals(link2));

		Set<Keyword> listKeywords2 = link2.getKeywords();
		assertEquals("There are not three keywords related with the link.",
			3, listKeywords2.size());
		
		for (Keyword k : listKeywords2) {
			if (!(k.equals(keyword)
				|| k.equals(keyword2)
				|| k.equals(keyword3))) {
				fail("There was an unexpected keyword related with link.");
			}
		}
		
		listKeywords2.remove(keyword2);
		Keyword keyword5 = new Keyword();
		keyword5.setName("New");
		keyword5.setDescription("A brand new keyword.");
		keyword5 = keywordDao.saveOrUpdate(keyword5);
		listKeywords2.add(keyword5);

		link2.setName("iBatis SqlMap 2.0 Developer Guide");
		link2.setKeywords(listKeywords2);
		link2 = (Link) service.saveReference(link2);

		List<Reference> emptyList = service.getReferencesByName(
			"iBatis Data Mapper Developer Guide");
		assertEquals("Link with name 'iBatis Data Mapper Developer Guide' "
			+ "still exists.", emptyList.size(), 0);

		List<Reference> list2 = service.getReferencesByName("iBatis SqlMap 2.0 "
			+ "Developer Guide");
		assertEquals("Not one link with name 'iBatis SqlMap 2.0 "
			+ "Developer Guide'.", 1, list2.size());
		Link link3 = (Link) list.get(0);
		assertTrue("Links are not equal.", link2.equals(link3));

		Set<Keyword> listKeywords3 = link3.getKeywords();
		assertEquals("There are not three keywords related with the link.",
			3, listKeywords3.size());
		for (Keyword k : listKeywords3) {
			if (!(k.equals(keyword)
				|| k.equals(keyword3)
				|| k.equals(keyword5))) {
				fail("There was an unexpected keyword related with link.");
			}
		}
	}

	/**
	 * This test adds two links and gets them.
	 */
	@Test
	public void testGetAllLinks() {
		ReferenceService service = getReferenceService();
		Link link = new Link();
		link.setName("iBatis Data Mapper Developer Guide");
		link.setHashValue("xyz");
		link.setDescription("This page shows you how to develop an "
			+ "application with iBatis.");
		link.setVersion("2.0");
		link.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		link.setDate(new Date(c.getTimeInMillis()));
		link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");
		link = (Link) service.saveReference(link);

		Link link2 = new Link();
		link2.setName("LEO Dictionary English-German");
		link2.setHashValue("leo-en-de");
		link2.setDescription("This is the best online dictionary "
			+ "for English and German.");
		link2.setVersion("2004");
		link2.setIncomplete(false);
		Calendar c2 = Calendar.getInstance();
		c2.set(2004, Calendar.NOVEMBER, 8);
		link2.setDate(new Date(c.getTimeInMillis()));
		link2.setUrl("http://dict.leo.org/");
		link2 = (Link) service.saveReference(link2);

		List<Reference> list = service.getAllReferences();
		assertEquals("There are not two links.", 2, list.size());
		
		// the next two lines are here due to a strange bug of mysql
//		link.getWhenInserted().setNanos(0);
//		link2.getWhenInserted().setNanos(0);
		
		for (Reference l : list) {
//			l.getWhenInserted().setNanos(0);
			if (!(((Link) l).equals(link) || ((Link) l).equals(link2))) {
				fail("There is an unexpected link (1).");
			}
		}

		service.deleteReference(link.getKey());

		list = service.getAllReferences();
		assertEquals("There is not one link.", 1, list.size());
		Link link4 = (Link) list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		link4.getWhenInserted().setNanos(0);
//		link2.getWhenInserted().setNanos(0);
		
		assertTrue("There is an unexpected link (2).", link4.equals(link2));
	}

	/**
	 * This test inserts a formal publication and three keywords.
	 */
	@Test
	public void testInsertFormalPublication() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		FormalPublication formalPublication = new FormalPublication();
		formalPublication.setName("iBatis Data Mapper Developer Guide");
		formalPublication.setHashValue("xyz");
		formalPublication.setDescription(
			"This manual shows you how to develop an application with iBatis.");
		formalPublication.setVersion("2.0");
		formalPublication.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		formalPublication.setDate(new Date(c.getTimeInMillis()));
		formalPublication.setAuthorName("Clinton Begin");
		formalPublication.setPublisher("iBatis");
		formalPublication.setPageNum(53);

		Keyword keyword = new Keyword();
		keyword.setName("iBatis");
		Keyword keyword2 = new Keyword();
		keyword2.setName("SqlMap 2.0");
		Keyword keyword3 = new Keyword();
		keyword3.setName("Data mapper");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		formalPublication.setKeywords(listKeywords);

		service.saveReference(formalPublication);
	}

	/**
	 * This test adds a formal publication and keywords. Some of the keywords
	 * are related with the formal publication some are not. After that the
	 * formal publication will be get by primary key and compared with the added
	 * one. At the end the formal publication will be removed.
	 */
	@Test
	public void testInsertGetRemoveFormalPublication() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		FormalPublication formalPublication = new FormalPublication();
		formalPublication.setName("iBatis Data Mapper Developer Guide");
		formalPublication.setHashValue("xyz");
		formalPublication.setDescription(
			"This manual shows you how to develop an application with iBatis.");
		formalPublication.setVersion("2.0");
		formalPublication.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		formalPublication.setDate(new Date(c.getTimeInMillis()));
		formalPublication.setAuthorName("Clinton Begin");
		formalPublication.setPublisher("iBatis");
		formalPublication.setPageNum(53);

		Keyword keyword = new Keyword();
		keyword.setName("iBatis");
		Keyword keyword2 = new Keyword();
		keyword2.setName("SqlMap 2.0");
		Keyword keyword3 = new Keyword();
		keyword3.setName("Data mapper");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		formalPublication.setKeywords(listKeywords);

		formalPublication
			= (FormalPublication) service.saveReference(formalPublication);

		FormalPublication formalPublication2
			= (FormalPublication) service.getReferenceByKey(
				formalPublication.getKey());
		
		// the next two lines are here due to a strange bug of mysql
//		formalPublication.getWhenInserted().setNanos(0);
//		formalPublication2.getWhenInserted().setNanos(0);
		
		assertTrue("Formal publications are not equal.",
			formalPublication.equals(formalPublication2));

		Set<Keyword> listKeywords2 = formalPublication2.getKeywords();
		assertEquals("There are not three keywords related with the formal "
			+ "publication.", 3, listKeywords2.size());
		for (Keyword k : listKeywords2) {
			if (!(k.equals(keyword)
				|| k.equals(keyword2)
				|| k.equals(keyword3))) {
				fail("There was an unexpected keyword related with "
					+ "formal publication.");
			}
		}

		service.deleteReference(formalPublication2.getKey());
		List<Reference> list = service.getReferencesByName(
			"iBatis Data Mapper Developer Guide");
		assertEquals("There is still a formal publication with "
			+ "name 'iBatis Data Mapper Developer Guide'.", list.size(), 0);
	}

	/**
	 * This test adds a formal publication with three keywords. After saving the
	 * formal publication the same formal publication will be get from database.
	 * This formal publication get a new name and one keyword will be replaced
	 * by another. Afterwards changes will be saved to database.
	 */
	@Test
	public void testInsertChangeFormalPublication() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		FormalPublication formalPublication = new FormalPublication();
		formalPublication.setName("iBatis Data Mapper Developer Guide");
		formalPublication.setHashValue("xyz");
		formalPublication.setDescription(
			"This manual shows you how to develop an application with iBatis.");
		formalPublication.setVersion("2.0");
		formalPublication.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		formalPublication.setDate(new Date(c.getTimeInMillis()));
		formalPublication.setAuthorName("Clinton Begin");
		formalPublication.setPublisher("iBatis");
		formalPublication.setPageNum(53);

		Keyword keyword = new Keyword();
		keyword.setName("iBatis");
		Keyword keyword2 = new Keyword();
		keyword2.setName("SqlMap 2.0");
		Keyword keyword3 = new Keyword();
		keyword3.setName("Data mapper");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		formalPublication.setKeywords(listKeywords);

		formalPublication = (FormalPublication) service.saveReference(
			formalPublication);

		List list = service.getReferencesByName(
			"iBatis Data Mapper Developer Guide");
		assertEquals("Not one formal publication with name 'iBatis Data Mapper "
			+ "Developer Guide'.", 1, list.size());
		FormalPublication formalPublication2
			= (FormalPublication) list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		formalPublication.getWhenInserted().setNanos(0);
//		formalPublication2.getWhenInserted().setNanos(0);
		
		assertTrue("Formal publications are not equal.",
			formalPublication.equals(formalPublication2));

		Set<Keyword> listKeywords2 = formalPublication2.getKeywords();
		assertEquals("There are not three keywords related with the formal "
			+ "publication.", 3, listKeywords2.size());
		for (Keyword k : listKeywords2) {
			if (!(k.equals(keyword)
				|| k.equals(keyword2)
				|| k.equals(keyword3))) {
				fail("There was an unexpected keyword related with "
					+ "formal publication.");
			}
		}
		listKeywords2.remove(keyword2);
		Keyword keyword5 = new Keyword();
		keyword5.setName("New");
		keyword5.setDescription("A brand new keyword.");
		keywordDao.saveOrUpdate(keyword5);
		listKeywords2.add(keyword5);

		formalPublication2.setName("iBatis SqlMap 2.0 Developer Guide");
		formalPublication2.setKeywords(listKeywords2);
		formalPublication2 = (FormalPublication) service.saveReference(
			formalPublication2);

		List<Reference> emptyList = service.getReferencesByName(
			"iBatis Data Mapper Developer Guide");
		assertEquals("Formal publication with name 'iBatis Data Mapper "
			+ "Developer Guide' still exists.", emptyList.size(), 0);

		List<Reference> list2 = service.getReferencesByName(
			"iBatis SqlMap 2.0 Developer Guide");
		assertEquals("Not one formal publication with name 'iBatis SqlMap 2.0 "
			+ "Developer Guide'.", 1, list2.size());
		FormalPublication formalPublication3
			= (FormalPublication) list.get(0);
		assertTrue("Formal publications are not equal.",
				formalPublication2.equals(formalPublication3));

		Set<Keyword> listKeywords3 = formalPublication3.getKeywords();
		assertEquals("There are not three keywords related with the formal "
			+ "publication.", 3, listKeywords3.size());
		for (Keyword k : listKeywords3) {
			if (!(k.equals(keyword)
				|| k.equals(keyword3)
				|| k.equals(keyword5))) {
				fail("There was an unexpected keyword related with "
					+ "formal publication.");
			}
		}
	}

	/**
	 * This test adds two formal publications and gets them.
	 */
	@Test
	public void testGetAllFormalPublications() {
		ReferenceService service = getReferenceService();
		FormalPublication formalPublication = new FormalPublication();
		formalPublication.setName("iBatis Data Mapper Developer Guide");
		formalPublication.setHashValue("xyz");
		formalPublication.setDescription(
			"This manual shows you how to develop an application with iBatis.");
		formalPublication.setVersion("2.0");
		formalPublication.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.OCTOBER, 25);
		formalPublication.setDate(new Date(c.getTimeInMillis()));
		formalPublication.setAuthorName("Clinton Begin");
		formalPublication.setPublisher("iBatis");
		formalPublication.setPageNum(53);
		formalPublication = (FormalPublication) service.saveReference(
			formalPublication);

		FormalPublication formalPublication2 = new FormalPublication();
		formalPublication2.setName("Spring Reference Documentation");
		formalPublication2.setHashValue("asdf");
		formalPublication2.setDescription("This document shows you how to "
			+ "use the spring framework.");
		formalPublication2.setVersion("1.1.1");
		formalPublication2.setIncomplete(false);
		Calendar c2 = Calendar.getInstance();
		c2.set(2004, Calendar.SEPTEMBER, 12);
		formalPublication2.setDate(new Date(c2.getTimeInMillis()));
		formalPublication2.setAuthorName("Rod Johnson");
		formalPublication2.setPublisher("Spring");
		formalPublication2.setPageNum(187);
		formalPublication2 = (FormalPublication) service.saveReference(
			formalPublication2);

		List<Reference> list = service.getAllReferences();
		assertEquals("There are not two formal publications.", 2, list.size());
		
		// the next two lines are here due to a strange bug of mysql
//		formalPublication.getWhenInserted().setNanos(0);
//		formalPublication2.getWhenInserted().setNanos(0);
		
		for (Reference f : list) {
//			f.getWhenInserted().setNanos(0);
			if (!(((FormalPublication) f).equals(formalPublication)
				|| ((FormalPublication) f).equals(formalPublication2))) {
				fail("There is an unexpected formal publication.");
			}
		}

		service.deleteReference(formalPublication.getKey());

		list = service.getAllReferences();
		assertEquals("There is not one formal publication.", 1, list.size());
		FormalPublication formalPublication4
			= (FormalPublication) list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		formalPublication4.getWhenInserted().setNanos(0);
//		formalPublication2.getWhenInserted().setNanos(0);
		
		assertTrue("There is an unexpected formal publication.",
			formalPublication4.equals(formalPublication2));
	}

	/**
	 * This test inserts a book and three keywords.
	 */
	@Test
	public void testInsertBook() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		Book book = new Book();
		book.setName("Expert One-on-One J2EE Development without EJB");
		book.setHashValue("xyz");
		book.setDescription("This book shows you how to develop with "
			+ "the spring framework.");
		book.setVersion("1.0");
		book.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.JUNE, 21);
		book.setDate(new Date(c.getTimeInMillis()));
		book.setAuthorName("Rod Johnson, Juergen Hoeller");
		book.setPublisher("Wrox");
		book.setPageNum(576);
		book.setIsbnNumber("0764558315");

		Keyword keyword = new Keyword();
		keyword.setName("spring");
		Keyword keyword2 = new Keyword();
		keyword2.setName("j2ee");
		Keyword keyword3 = new Keyword();
		keyword3.setName("framework");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		book.setKeywords(listKeywords);

		service.saveReference(book);
	}

	/**
	 * This test adds a book and keywords. Some of the keywords are related with
	 * the book some are not. After that the book will be get by primary key and
	 * compared with the added one. At the end the book will be removed.
	 */
	@Test
	public void testInsertGetRemoveBook() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		Book book = new Book();
		book.setName("Expert One-on-One J2EE Development without EJB");
		book.setHashValue("xyz");
		book.setDescription(
			"This book shows you how to develop with the spring framework.");
		book.setVersion("1.0");
		book.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.JUNE, 21);
		book.setDate(new Date(c.getTimeInMillis()));
		book.setAuthorName("Rod Johnson, Juergen Hoeller");
		book.setPublisher("Wrox");
		book.setPageNum(576);
		book.setIsbnNumber("0764558315");

		Keyword keyword = new Keyword();
		keyword.setName("spring");
		Keyword keyword2 = new Keyword();
		keyword2.setName("j2ee");
		Keyword keyword3 = new Keyword();
		keyword3.setName("framework");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		book.setKeywords(listKeywords);

		book = (Book) service.saveReference(book);

		Book book2 = (Book) service.getReferenceByKey(book.getKey());
		
		// the next two lines are here due to a strange bug of mysql
//		book.getWhenInserted().setNanos(0);
//		book2.getWhenInserted().setNanos(0);
		
		assertTrue("Books are not equal.", book.equals(book2));

		Set<Keyword> listKeywords2 = book2.getKeywords();
		assertEquals("There are not three keywords related with the book.",
			3, listKeywords2.size());
		
		for (Keyword k : listKeywords2) {
			if (!(k.equals(keyword)
				|| k.equals(keyword2)
				|| k.equals(keyword3))) {
				fail("There was an unexpected keyword related with book.");
			}
		}

		service.deleteReference(book2.getKey());
		List<Reference> list = service.getReferencesByName(
			"Expert One-on-One J2EE Development without EJB");
		assertEquals("There is still a formal publication with name 'Expert "
			+ "One-on-One J2EE Development without EJB'.", list.size(), 0);
	}

	/**
	 * This test adds a book with three keywords. After saving the book the same
	 * book will be get from database. This book get a new name and one keyword
	 * will be replaced by another. Afterwards changes will be saved to
	 * database.
	 */
	@Test
	public void testInsertChangeBook() {
		ReferenceService service = getReferenceService();
		KeywordDao keywordDao = getKeywordDao();
		Book book = new Book();
		book.setName("Expert One-on-One J2EE Development without EJB");
		book.setHashValue("xyz");
		book.setDescription(
			"This book shows you how to develop with the spring framework.");
		book.setVersion("1.0");
		book.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.JUNE, 21);
		book.setDate(new Date(c.getTimeInMillis()));
		book.setAuthorName("Rod Johnson, Juergen Hoeller");
		book.setPublisher("Wrox");
		book.setPageNum(576);
		book.setIsbnNumber("0764558315");

		Keyword keyword = new Keyword();
		keyword.setName("spring");
		Keyword keyword2 = new Keyword();
		keyword2.setName("j2ee");
		Keyword keyword3 = new Keyword();
		keyword3.setName("framework");
		keywordDao.saveOrUpdate(keyword);
		keywordDao.saveOrUpdate(keyword2);
		keywordDao.saveOrUpdate(keyword3);

		Set<Keyword> listKeywords = new HashSet<Keyword>();
		listKeywords.add(keyword);
		listKeywords.add(keyword2);
		listKeywords.add(keyword3);
		book.setKeywords(listKeywords);

		book = (Book) service.saveReference(book);

		List<Reference> list = service.getReferencesByName(
			"Expert One-on-One J2EE Development without EJB");
		assertEquals("Not one book with name 'Expert One-on-One "
			+ "J2EE Development without EJB'.", 1, list.size());
		Book book2 = (Book) list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		book.getWhenInserted().setNanos(0);
//		book2.getWhenInserted().setNanos(0);
//		
		assertTrue("Books are not equal.", book.equals(book2));

		Set<Keyword> listKeywords2 = book2.getKeywords();
		assertEquals("There are not three keywords related with the book.",
			3, listKeywords2.size());
		
		for (Keyword k : listKeywords2) {
			if (!(k.equals(keyword)
				|| k.equals(keyword2)
				|| k.equals(keyword3))) {
				fail("There was an unexpected keyword related with book.");
			}
		}
		listKeywords2.remove(keyword2);
		Keyword keyword5 = new Keyword();
		keyword5.setName("New");
		keyword5.setDescription("A brand new keyword.");
		keyword5 = getKeywordDao().saveOrUpdate(keyword5);
		listKeywords2.add(keyword5);

		book2.setName("Springframework - J2EE Development without EJB");
		book2.setKeywords(listKeywords2);
		book2 = (Book) service.saveReference(book2);

		List<Reference> emptyList = service
			.getReferencesByName("Expert One-on-One J2EE "
			+ "Development without EJB");
		assertEquals("Formal publication with name 'Expert One-on-One J2EE "
			+ "Development without EJB' still exists.", emptyList.size(), 0);

		List<Reference> list2 = service
			.getReferencesByName("Springframework - J2EE "
			+ "Development without EJB");
		assertEquals("Not one book with name 'Springframework - J2EE "
			+ "Development without EJB'.", 1, list2.size());
		Book book3 = (Book) list.get(0);
		assertTrue("Books are not equal.", book2.equals(book3));

		Set<Keyword> listKeywords3 = book3.getKeywords();
		assertEquals("There are not three keywords related with the book.",
			3, listKeywords3.size());
		
		for (Keyword k : listKeywords3) {
			if (!(k.equals(keyword)
				|| k.equals(keyword3)
				|| k.equals(keyword5))) {
				fail("There was an unexpected keyword related with book.");
			}
		}
	}

	/**
	 * This test adds two books and gets them.
	 */
	@Test
	public void testGetAllBooks() {
		ReferenceService service = getReferenceService();
		Book book = new Book();
		book.setName("Expert One-on-One J2EE Development without EJB");
		book.setHashValue("xyz");
		book.setDescription(
			"This book shows you how to develop with the spring framework.");
		book.setVersion("1.0");
		book.setIncomplete(false);
		Calendar c = Calendar.getInstance();
		c.set(2004, Calendar.JUNE, 21);
		book.setDate(new Date(c.getTimeInMillis()));
		book.setAuthorName("Rod Johnson, Juergen Hoeller");
		book.setPublisher("Wrox");
		book.setPageNum(576);
		book.setIsbnNumber("0764558315");
		book = (Book) service.saveReference(book);

		Book book2 = new Book();
		book2.setName("The Complete Log4j Manual");
		book2.setHashValue("lkjh");
		book2.setDescription("This book shows you how to use Log4J.");
		book2.setVersion("1.0");
		book2.setIncomplete(false);
		Calendar c2 = Calendar.getInstance();
		c2.set(2003, Calendar.MAY, 7);
		book2.setDate(new Date(c2.getTimeInMillis()));
		book2.setAuthorName("Ceki Gulcu");
		book2.setPublisher("QOS.ch");
		book2.setPageNum(206);
		book2.setIsbnNumber("2970036908");
		book2 = (Book) service.saveReference(book2);

		List<Reference> list = service.getAllReferences();
		assertEquals("There are not two books.", 2, list.size());
		
		// the next two lines are here due to a strange bug of mysql
//		book.getWhenInserted().setNanos(0);
//		book2.getWhenInserted().setNanos(0);
		
		for (Reference b : list) {
//			b.getWhenInserted().setNanos(0);
			if (!(((Book) b).equals(book) || ((Book) b).equals(book2))) {
				fail("There is an unexpected book.");
			}
		}

		service.deleteReference(book.getKey());

		list = service.getAllReferences();
		assertEquals("There is not one book.", 1, list.size());
		Book book4 = (Book) list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		book4.getWhenInserted().setNanos(0);
//		book2.getWhenInserted().setNanos(0);
		
		assertTrue("There is an unexpected book.", book4.equals(book2));
	}
	
	/**
	 * Test tries to add incomplete references.
	 */
	@Test
	public void testAddingIncompleteReferences() {
		ReferenceService service = getReferenceService();
		Link link = new Link();
		link.setDescription("Bla bla.");
		try {
			service.saveReference(link);
			fail("No exception although name of reference is missing.");
		} catch (DataIntegrityViolationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		
		FormalPublication formalPublication = new FormalPublication();
		formalPublication.setDescription("Bla bla.");
		try {
			service.saveReference(formalPublication);
			fail("No exception although name of reference is missing.");
		} catch (DataIntegrityViolationException e) {
			s_logger.debug("Expected exception catched.", e);
		}

		Book book = new Book();
		book.setDescription("Bla bla.");
		try {
			service.saveReference(book);
			fail("No exception although name of reference is missing.");
		} catch (DataIntegrityViolationException e) {
			s_logger.debug("Expected exception catched.", e);
		}
	}
	
	/**
	 * This test tries out many possible combinations of searching on
	 * references.
	 */
	@Test
	public void testSearchReferences() {
		ReferenceService service = getReferenceService();
		KeywordDao dao = getKeywordDao();
		
		Keyword kUml = createAndSaveKeyword(
			"uml", "Unified modeling language.", dao);
		Keyword kLanguage = createAndSaveKeyword(
			"language", "Program or design language.", dao);
		Keyword kJsp = createAndSaveKeyword(
			"jsp", "Java server pages.", dao);
		Keyword kJava = createAndSaveKeyword(
			"java", "Java program language.", dao);
		Keyword kStruts = createAndSaveKeyword(
			"struts", "Web MVC product.", dao);
		Keyword kDictionary = createAndSaveKeyword(
			"dictionary", "Dictionary.", dao);
		Keyword kJ2ee = createAndSaveKeyword(
			"j2ee", "Java enterprise library.", dao);
		Keyword kIbatis = createAndSaveKeyword(
			"ibatis", "Apache IBatis software.", dao);
		Keyword kDeveloperguide = createAndSaveKeyword(
			"developerguide", "Guide for developers.", dao);
		Keyword kBarcode = createAndSaveKeyword(
			"barcode", "Machine readable string.", dao);
		Keyword k2d = createAndSaveKeyword(
			"2d", "Two dimensions.", dao);
		Keyword kEnglish = createAndSaveKeyword(
			"english", "English language.", dao);
		Keyword kGerman = createAndSaveKeyword(
			"german", "German language.", dao);
		createAndSaveKeyword("zombie", "", dao);
		
		
		Book bUmlDistilled = new Book();
		bUmlDistilled.setName("UML Distilled");
		bUmlDistilled.setDescription(
			"A brief guide to the standard object modeling language.");
		bUmlDistilled.setIncomplete(false);
		bUmlDistilled.setAuthorName("Martin Fowler, Kendall Scott");
		Set<Keyword> keywordsUmlDistilled = new HashSet<Keyword>();
		keywordsUmlDistilled.add(kUml);
		keywordsUmlDistilled.add(kLanguage);
		keywordsUmlDistilled.add(kEnglish);
		bUmlDistilled.setKeywords(keywordsUmlDistilled);

		Book bBeginningJsp2 = new Book();
		bBeginningJsp2.setName("Beginning Jsp 2.0");
		bBeginningJsp2.setDescription(
			"Build Web Applications Using Jsp, Java, and Struts.");
		bBeginningJsp2.setIncomplete(true);
		bBeginningJsp2.setAuthorName("Ben Galbraith, Peter den Haan");
		Set<Keyword> keywordsBeginningJsp2 = new HashSet<Keyword>();
		keywordsBeginningJsp2.add(kJ2ee);
		keywordsBeginningJsp2.add(kJava);
		keywordsBeginningJsp2.add(kJsp);
		keywordsBeginningJsp2.add(kStruts);
		keywordsBeginningJsp2.add(kEnglish);
		bBeginningJsp2.setKeywords(keywordsBeginningJsp2);

		Book bZombie = new Book();
		bZombie.setName("Zombie");
		bZombie.setAuthorName("Zombie");

		Link lLeoEngGer = new Link();
		lLeoEngGer.setName("LEO Dictionary English-German");
		lLeoEngGer.setDescription(
			"An online dictionary to translate German to English and reverse.");
		lLeoEngGer.setIncomplete(false);
		Set<Keyword> keywordsLeoEngGer = new HashSet<Keyword>();
		keywordsLeoEngGer.add(kEnglish);
		keywordsLeoEngGer.add(kGerman);
		keywordsLeoEngGer.add(kDictionary);
		keywordsLeoEngGer.add(kLanguage);
		lLeoEngGer.setKeywords(keywordsLeoEngGer);

		Link lJ2eeJsp = new Link();
		lJ2eeJsp.setName("J2EE - JavaServer Pages Technology");
		lJ2eeJsp.setDescription("JavaServer Pages (JSP) technology provides "
			+ "a simplified, fast way to create dynamic web content. "
			+ "JSP technology enables rapid development of web-based "
			+ "applications that are server- and platform-independent.");
		lJ2eeJsp.setIncomplete(true);
		Set<Keyword> keywordsJ2eeJsp = new HashSet<Keyword>();
		keywordsJ2eeJsp.add(kJ2ee);
		keywordsJ2eeJsp.add(kJsp);
		keywordsJ2eeJsp.add(kJava);
		keywordsJ2eeJsp.add(kEnglish);
		lJ2eeJsp.setKeywords(keywordsJ2eeJsp);

		FormalPublication fSqlMaps2DevGuide = new FormalPublication();
		fSqlMaps2DevGuide.setName("iBatis SqlMap 2.0 Developer Guide");
		fSqlMaps2DevGuide.setDescription("This guide shows you how to "
			+ "develop an application using SqlMap 2.0.");
		fSqlMaps2DevGuide.setIncomplete(true);
		fSqlMaps2DevGuide.setAuthorName("iBatis");
		Set<Keyword> keywordsSqlMaps2DevGuide = new HashSet<Keyword>();
		keywordsSqlMaps2DevGuide.add(kJava);
		keywordsSqlMaps2DevGuide.add(kIbatis);
		keywordsSqlMaps2DevGuide.add(kDeveloperguide);
		keywordsSqlMaps2DevGuide.add(kEnglish);
		fSqlMaps2DevGuide.setKeywords(keywordsSqlMaps2DevGuide);

		FormalPublication f2dBarcodePdf417 = new FormalPublication();
		f2dBarcodePdf417.setName("2D barcode PDF417");
		f2dBarcodePdf417.setDescription("Describse who a "
			+ "PDF417 2D barcode is built-up.");
		f2dBarcodePdf417.setIncomplete(false);
		f2dBarcodePdf417.setAuthorName("Some author");
		Set<Keyword> keywords2dBarcodePdf417 = new HashSet<Keyword>();
		keywords2dBarcodePdf417.add(k2d);
		keywords2dBarcodePdf417.add(kBarcode);
		keywords2dBarcodePdf417.add(kEnglish);
		f2dBarcodePdf417.setKeywords(keywords2dBarcodePdf417);

		service.saveReference(bUmlDistilled);
		service.saveReference(bBeginningJsp2);
		service.saveReference(bZombie);
		service.saveReference(lLeoEngGer);
		service.saveReference(lJ2eeJsp);
		service.saveReference(fSqlMaps2DevGuide);
		service.saveReference(f2dBarcodePdf417);

		List<Reference> list;
		//Iterator it;

		QueryObject query = new QueryObject();
		list = service.searchReferences(query);
		assertEquals("There are not seven references for "
			+ "query name='' and description=''.", 7, list.size());

		query = new QueryObject();
		query.addCriteria(ComparisonCriteria.equals("incomplete", false));
		list = service.searchReferences(query);
		assertEquals("There are not three references for query name='' and "
			+ "description='' and incomplete=false.", 3, list.size());

		// the next two lines are here due to a strange bug of mysql
//		bUmlDistilled.getWhenInserted().setNanos(0);
//		lLeoEngGer.getWhenInserted().setNanos(0);
//		f2dBarcodePdf417.getWhenInserted().setNanos(0);
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!(r.equals(bUmlDistilled)
				|| r.equals(lLeoEngGer)
				|| r.equals(f2dBarcodePdf417))) {
				fail("There was an unexpected reference.");
			}
		}

		query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive("name", "%2%"));
		list = service.searchReferences(query);
		assertEquals("There are not four references for "
			+ "query name='2' and description=''.", 4, list.size());
		
		// the next two lines are here due to a strange bug of mysql
//		bBeginningJsp2.getWhenInserted().setNanos(0);
//		lJ2eeJsp.getWhenInserted().setNanos(0);
//		fSqlMaps2DevGuide.getWhenInserted().setNanos(0);
//		f2dBarcodePdf417.getWhenInserted().setNanos(0);
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!(r.equals(bBeginningJsp2) || r.equals(lJ2eeJsp)
				|| r.equals(fSqlMaps2DevGuide)
				|| r.equals(f2dBarcodePdf417))) {
				fail("There was an unexpected reference.");
			}
		}

		query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive("name", "%2%"));
		query.addCriteria(ComparisonCriteria.equals("incomplete", true));
		list = service.searchReferences(query);
		assertEquals("There are not three references for query name='2' and "
			+ "description='' and incomplete=true.", 3, list.size());
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!(r.equals(bBeginningJsp2)
				|| r.equals(lJ2eeJsp)
				|| r.equals(fSqlMaps2DevGuide))) {
				fail("There was an unexpected reference.");
			}
		}

		query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive(
			"description", "%deVeLop%"));
		list = service.searchReferences(query);
		assertEquals("There are not two references for query name='' and "
			+ "description='deVeLop'.", 2, list.size());
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!(r.equals(lJ2eeJsp) || r.equals(fSqlMaps2DevGuide))) {
				fail("There was an unexpected reference.");
			}
		}

		query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive("name", "%JAVA%"));
		query.addCriteria(LikeCriteria.caseInsensitive("description", "%WEB%"));
		list = service.searchReferences(query);
		assertEquals("There was not one reference for "
			+ "query name='JAVA' and description='WEB'.", 1, list.size());
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!r.equals(lJ2eeJsp)) {
				fail("There was an unexpected reference.");
			}
		}
		
		query = new QueryObject();
		
		query.addCriteria(new IncludeCriteria("keywords",
			kJava.getKeyAsObject()));
		
		list = service.searchReferences(query);
		assertEquals("Unexpected number of references with keyword java.",
			3, list.size());
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!(r.equals(bBeginningJsp2) || r.equals(lJ2eeJsp)
				|| r.equals(fSqlMaps2DevGuide))) {
				fail("There was an unexpected reference.");
			}
		}
		
		query = new QueryObject();
		
		query.addCriteria(new IncludeCriteria("keywords",
			kJava.getKeyAsObject()));
		
		query.addCriteria(LikeCriteria.caseInsensitive("description", "%WEB%"));
		list = service.searchReferences(query);
		assertEquals("Unexpected number of references with keyword java and "
			+ "description='WEB'.",
			2, list.size());
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!(r.equals(bBeginningJsp2) || r.equals(lJ2eeJsp))) {
				fail("There was an unexpected reference.");
			}
		}
		
		query = new QueryObject();
		
		query.addCriteria(new IncludeCriteria("keywords",
			kJava.getKeyAsObject()));
		query.addCriteria(new IncludeCriteria("keywords",
			kStruts.getKeyAsObject()));
		
		query.addCriteria(LikeCriteria.caseInsensitive("description", "%WEB%"));
		list = service.searchReferences(query);
		assertEquals("Unexpected number of references with keyword java, "
			+ "keyword struts and description='WEB'.",
			1, list.size());
		
		for (Reference r : list) {
//			r.getWhenInserted().setNanos(0);
			if (!r.equals(bBeginningJsp2)) {
				fail("There was an unexpected reference.");
			}
		}
	}

	/**
	 * Creates, saves and returns a keyword by using given parameters.
	 *
	 * @param name Is the name of the keyword to create.
	 * @param description Is the description of the keyword to create.
	 * @param dao Is the dao where to save the created keyword.
	 * @return Returns the created and saved keyword.
	 */
	protected Keyword createAndSaveKeyword(String name, String description,
		KeywordDao dao) {
		Keyword keyword = new Keyword();
		keyword.setName(name);
		keyword.setDescription(description);
		return dao.saveOrUpdate(keyword);
	}
	
	/**
	 * This test tries to insert an annotation without a content. Because the
	 * annotation and its content are written in two steps, the first step
	 * should be redone if the second step fails.
	 *
	 * This test will be executed on service layer level, because transaction
	 * attributes are defined on service layer.
	 */
	@Test
	public void testIncompleteAnnotationInsertion() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao annotationDao = getAnnotationDao();
		
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Mister Lazy");
		annotation.setGrade(1);
		// Extra do not add a content.
		annotation.setContent(null);
		try {
			annotation = annotationDao.saveOrUpdate(annotation);
			fail("An annotation with no content is allowed.");
		} catch (DataAccessException e) {
			s_logger.debug("Expected exception because content of an "
				+ "annotation can not be null.", e);
		}

		List list = annotationDao.getAnnotationsByAnnotator("Mister Lazy");
		assertEquals("There is still an annotation "
			+ "with annotator 'Mister Lazy' in database.", list.size(), 0);
	}
	
	@Test
	/**
	 * This tests generates references and related keywords. In a second
	 * step, a service method deletes one reference and all its connected
	 * keywords. Because another reference still is related to some of
	 * now-deleted keywords, the service method should be rolled back.
	 */
	public void testMissingKeyword() {
		
		ReferenceService service = getReferenceService();
		KeywordDao dao = getKeywordDao();
		
		Keyword kCls = createAndSaveKeyword(
			"cls", "Classics.", dao);
		Keyword kFut = createAndSaveKeyword(
			"fut", "Future.", dao);
		
		Book bGolding = new Book();
		bGolding.setName("Lord of the Flies");
		Set<Keyword> keywordsGolding = new HashSet<Keyword>();
		keywordsGolding.add(kFut);
		bGolding.setKeywords(keywordsGolding);
		bGolding.setAuthorName("Balthazar Getty");
		bGolding = (Book) service.saveReference(bGolding);
		
		Book bHuxley = new Book();
		bHuxley.setName("Brave New World");
		Set<Keyword> keywordsHuxley = new HashSet<Keyword>();
		keywordsHuxley.add(kCls);
		keywordsHuxley.add(kFut);
		bHuxley.setKeywords(keywordsHuxley);
		bHuxley.setAuthorName("Aldous Huxley");
		bHuxley = (Book) service.saveReference(bHuxley);
		
		int delKey = bHuxley.getKey();
		
		try {
			service.deleteReferenceAndKeywords(delKey);
			fail("Foreign key constraint violated.");
		} catch (DataIntegrityViolationException e) {
			s_logger.debug("Caught data integrity violation.");
		}
		
		List<Reference> referenceList = service.getAllReferences();
		assertEquals("Reference removal was not rolled back.",
			2, referenceList.size());
		
		List<Keyword> keywordList = dao.getAll();
		assertEquals("Keyword removal was not rolled back.",
			2, keywordList.size());
	
		service.deleteReference(bGolding.getKey());
		service.deleteReference(bHuxley.getKey());
		referenceList = service.getAllReferences();
		assertEquals("References not properly removed.",
			0, referenceList.size());
		
		service.deleteKeyword(kCls.getKey());
		service.deleteKeyword(kFut.getKey());
		keywordList = dao.getAll();
		assertEquals("Keywords not properly removed.",
			0, keywordList.size());
	}
	
	/**
	 * Test Hibernate Search.
	 */
	@Test
	public void testHibernateSearch() {
		ReferenceService service = getReferenceService();
		
		Book book1 = new Book();
		book1.setName("ABC DEF");
		Set<Keyword> keywordsGolding = new HashSet<Keyword>();
		book1.setKeywords(keywordsGolding);
		book1.setAuthorName("GHI");
		book1 = (Book) service.saveReference(book1);
		
		Book book2 = new Book();
		book2.setName("JKL DEF");
		Set<Keyword> keywordsHuxley = new HashSet<Keyword>();
		book2.setKeywords(keywordsHuxley);
		book2.setAuthorName("MNO");
		book2 = (Book) service.saveReference(book2);
		
		List<Reference> abcList = service.searchReferences(new String[] {"name"}, "ABC");
		assertEquals(1, abcList.size());
		assertEquals(book1, abcList.get(0));
		
		// lucene tokenizer should only work on whole words
		List<Reference> emptyList = service.searchReferences(new String[] {"name"}, "AB");
		assertEquals(0, emptyList.size());
		
		List<Reference> defList = service.searchReferences(new String[] {"name"}, "DEF");
		assertEquals(2, defList.size());
		assertTrue(defList.contains(book1));
		assertTrue(defList.contains(book2));
		
		List<Reference> mnoList = service.searchReferences(new String[] {"name", "authorName"}, "MNO");
		assertEquals(1, mnoList.size());
		assertEquals(book2, mnoList.get(0));
	}
}
//Checkstyle: MagicNumber on
