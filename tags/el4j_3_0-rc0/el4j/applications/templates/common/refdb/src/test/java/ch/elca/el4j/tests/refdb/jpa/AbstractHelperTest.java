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
package ch.elca.el4j.tests.refdb.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dao.BookDao;
import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.apps.refdb.dao.LinkDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.jpa.helper.JpaHelper;
import ch.elca.el4j.services.persistence.jpa.util.QueryException;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;
import ch.elca.el4j.tests.core.AbstractTest;

/**
 * Test class testing the usage of jpa.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Sandra Weber (SWR)
 */
public abstract class AbstractHelperTest extends AbstractTest {
	/**
	 * Private logger.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(AbstractHelperTest.class);
	
	/**
	 * Data source. Created by application context.
	 */
	private DataSource m_dataSource;

	/**
	 * EntityManager. Got from the EntityManagerService.
	 */
	private JpaHelper m_helper;
		
	/**
	 * Hide default constructor.
	 */
	protected AbstractHelperTest() { }
	
	/**
	 * @return Returns the dataSource.
	 */
	protected DataSource getDataSource() {
		if (m_dataSource == null) {
			m_dataSource
				= (DataSource) getApplicationContext().getBean("dataSource");
		}
		return m_dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() throws Exception {
		Connection con = null;
		Statement s = null;
		try {
			con = getDataSource().getConnection();
			s = con.createStatement();
			s.executeUpdate("DELETE FROM REFERENCEKEYWORDRELATIONSHIPS");
			s.executeUpdate("DELETE FROM FILES");
			s.executeUpdate("DELETE FROM ANNOTATIONS");
			s.executeUpdate("DELETE FROM LINKS");
			s.executeUpdate("DELETE FROM BOOKS");
			s.executeUpdate("DELETE FROM FORMALPUBLICATIONS");
			s.executeUpdate("DELETE FROM REFERENCESTABLE");
			s.executeUpdate("DELETE FROM KEYWORDS");
			con.commit();
		} finally {
			if (s != null) {
				s.close();
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					s_logger.info("Connection could not be closed.");
				}
			}
		}
	}
	
	/**
	 * Method to get the {@link EntityManager}. The bean {@link JpaHelper} has to exist.
	 * @return Returns the entity manager.
	 */
	protected JpaHelper getHelper() {
		if (m_helper == null) {
			m_helper = (JpaHelper) getApplicationContext().getBean("jpaHelper");
		}
		return m_helper;
	}

	/**
	 * Create a annotation of the persistent state <code>NEW</code>.
	 * @param annotator The annotator of the annotation
	 * @param ref The reference the annotation belongs to.
	 * @return Returns a <code>NEW</code> book;
	 */
	protected Annotation newAnnotation(String annotator, Reference ref) {
		Annotation a = new Annotation();
		a.setAnnotator(annotator);
		a.setContent("blabla");
		a.setGrade(4);
		a.setReference(ref);
		return a;
	}
	
	/**
	 * Create a book of the persistent state <code>NEW</code>.
	 * @param name The name of the book.
	 * @return Returns a <code>NEW</code> book.
	 */
	protected Book newBook(String name) {
		Book b = new Book();
		b.setName(name);
		b.setAuthorName(name);
		b.setFiles(new HashSet<File>());
		b.setAnnotations(new HashSet<Annotation>());
		return b;
	}
	
	/**
	 * Create a book of the persistent state <code>MANAGED</code>.
	 * @param name The name of the book.
	 * @return Returns a <code>MANAGED</code> book.
	 */
	protected Book managedBook(String name) {
		Book b = newBook(name);
		getHelper().persist(b);
		return b;
	}
	
	/**
	 * Create a book of the persistent state <code>REMOVED</code>.
	 * @param name The name of the book.
	 * @return Returns a <code>REMOVED</code> book.
	 */
	protected Book removedBook(String name) {
		Book b = managedBook(name);
		getHelper().remove(b);
		return b;
	}
	
	/**
	 * Create a book of the persistent state <code>DETACHED</code>.
	 * @param name The name of the book.
	 * @return Returns a <code>DETACHED</code> book.
	 */
	protected Book detachedBook(String name) {
		Book b = managedBook(name);
		getHelper().detach(b);
		return b;
	}
	
	/**
	 * Checks if the book is in the state <code>MANAGED</code>.<br>
	 * Changes the description of the book. After a flush the book in the database is changed as well.
	 * @param b The book to check if managed.
	 * @param description The new description of the book
	 * @param expected True if a change in the database is expected
	 */
	protected void changeAndCheck(Book b, String description, Boolean expected) {
		b.setDescription(description);
//		getHelper().flush();
		Book b2 = getHelper().findByKey(Book.class, b.getKey());
		if (expected) {
			assertTrue("The book with name '" + b.getName() + "' was not updated. It is not MANAGED.",
				b2.getDescription().equals(description));
		} else {
			assertFalse("The book with name '" + b.getName() + "' was updated. It is MANAGED.",
				b2.getDescription().equals(description));
		}
	}
	
	/**
	 * Checks if the book is in the state <code>MANAGED</code> or <code>DETACHED</code>.<br>
	 * Searches the book by key. If an EntityNotFound exception occurs the book 
	 * does not exist in the database.
	 * @param b The book to check if managed or detaches.
	 * @param expected True if result of the search is expected
	 */
	protected void findAndCheck(Book b, Boolean expected) {
		Book b2 = null; 
		if ((b != null) && (!b.isKeyNew())) {
			b2 = getHelper().findByKey(Book.class, b.getKey());
		}
		if (expected) {
			assertNotNull("The book with name '" + b.getName() + "' is not in the database. "
					+ "It is neither MANAGED nor DETACHED", b2);
		} else {
			assertNull("The book with name '" + b.getName() + "' is not in the database. "
				+ "It is neither MANAGED nor DETACHED", b2);
		}
	}
	
	/**
	 * Tests all the states and the transactions between them. 
	 */
	@Test
	public void testAllStatesAndTransitions() {
		getHelper().doInTransaction(new Runnable() {
			@Override
			public void run() {
				Book book = newBook("A book to test with");
				
				s_logger.info("State: NEW");
				findAndCheck(book, false);
				
				s_logger.info("Transition: NEW -> MANAGED");
				getHelper().persist(book);
				//getHelper().flush();
				
				s_logger.info("State: MANAGED");
				assertTrue("After persist. The book is not a managed entity.", getHelper().contains(book));
				assertFalse("After persist. The primary key of the book is not set.", book.isKeyNew());
				changeAndCheck(book, "Add a description.", true);
				
				s_logger.info("Transition: MANAGED -> DETACHED");
				getHelper().detach(book);
				
				s_logger.info("State: DETACHED");
				assertFalse("After detach. The book is still a managed entity.", getHelper().contains(book));
				findAndCheck(book, true);
				
				s_logger.info("Transition: DETACHED -> MANAGED");
				// WRONG: getHelper().merge(book);
				book = getHelper().merge(book);
				
				s_logger.info("State: MANAGED");
				assertTrue("After merge. The book is not a managed entity.", getHelper().contains(book));
				changeAndCheck(book, "Add an other description.", true);
				
				Book bookCopied = book;
				changeAndCheck(bookCopied, "A copied managed entity is still MANAGED.", true);
				
				changeAndCheck(book, "The original managed entity is also MANAGED", true);
				
				s_logger.info("Transition: MANAGED -> MANAGED");
				getHelper().refresh(book);
				
				s_logger.info("State: MANAGED");
				assertTrue("After merge. The book is not a managed entity.", getHelper().contains(book));
				changeAndCheck(book, "Add an other description.", true);
				
				s_logger.info("Transition: MANAGED -> REMOVED");
				getHelper().remove(book);
				getHelper().flush();
				
				s_logger.info("State: REMOVED");
				assertFalse("After remove. The book is still a managed entity.", getHelper().contains(book));
//				findAndCheck(book, false);
				
				//Transition: REMOVED -> MANAGED
				//Usually not needed and also not really possible.
		
			}
		});
	}
	
	/**
	 * Tests the usage of collections.
	 */
	@Test
	public void testCollectionUsage() {
		getHelper().doInTransaction(new Runnable() {
			@Override
			public void run() {
				Book b = managedBook("That's a managed book");
				Annotation a1 = newAnnotation("Myself Person", b);
				Annotation a2 = newAnnotation("Another Person", b);
				Annotation a3 = newAnnotation("Some Nobody", b);
				
				s_logger.warn("Add some Annotations");
				/* (don't forget first to persist, otherwise 
				the hashCode won't be set yet)  */
				getHelper().persist(a1);
				b.getAnnotations().add(a1);
				getHelper().persist(a2);
				b.getAnnotations().add(a2);
				getHelper().persist(a3);
				b.getAnnotations().add(a3);
				
				s_logger.warn("Remove an Annotation");
				getHelper().remove(a1);
				//The Book still contains the reference to the Annotation
				b.getAnnotations().remove(a1);
								
				s_logger.warn("Remove the book, to check if cascading works");
				getHelper().remove(b);
				assertTrue("The cascading does not work. The Annotations (child) of the Book (parent) are not deleted.",
					getHelper().selectFrom(Annotation.class).execute().isEmpty());
				
			}
		});
	}
}
