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
package ch.elca.el4j.tests.refdb.dao;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

// Checkstyle: MagicNumber off

/**
 * Abstract test case for the reference dao.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractReferenceDaoTest extends AbstractTestCaseBase {
	/**
	 * Private logger.
	 */
	private static Logger s_logger
		= LoggerFactory.getLogger(AbstractReferenceDaoTest.class);

	
	/**
	 * The transaction manager.
	 */
	private HibernateTransactionManager m_transactionManager;
	
	/**
	 * Hide default constructor.
	 */
	protected AbstractReferenceDaoTest() { }

	
	
	/**
	 * This test inserts an annotation.
	 */
	@Test
	public void testInsertAnnotation() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Martin Zeltner");
		annotation.setGrade(8);
		annotation.setContent("This is only a short comment.");
		dao.saveOrUpdate(annotation);
	}

	/**
	 * This test inserts an annotation and looks up for it by annotation's
	 * primary key.
	 */
	@Test
	public void testInsertGetAnnotationByKey() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Hans Bauer");
		annotation.setGrade(1);
		annotation.setContent("This is an comment.");
		Annotation annotation2 = dao.saveOrUpdate(annotation);
		Annotation annotation3
			= dao.findById(annotation2.getKey());
		
		// the next two lines are here due to a strange bug of mysql
//		annotation2.getWhenInserted().setNanos(0);
//		annotation3.getWhenInserted().setNanos(0);
		
		assertEquals("The inserted and read domain objects are not equal",
			annotation3, annotation2);
	}

	/**
	 * This test inserts an annotation and looks up for it by annotator's name.
	 */
	@Test
	public void testInsertGetAnnotationByAnnotator() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Martin Zeltner");
		annotation.setGrade(8);
		annotation.setContent("This is only a short comment.");
		Annotation annotation2 = dao.saveOrUpdate(annotation);
		List<Annotation> list = dao
			.getAnnotationsByAnnotator(annotation2.getAnnotator());
		assertEquals("List contains more than one annotation of annotator '"
			+ annotation2.getAnnotator() + "'", 1, list.size());
		Annotation annotation3 = list.get(0);
		
		// the next two lines are here due to a strange bug of mysql
//		annotation2.getWhenInserted().setNanos(0);
//		annotation3.getWhenInserted().setNanos(0);
//		
		assertEquals("The inserted and read domain objects are not equal",
			annotation3, annotation2);
	}

	/**
	 * This test inserts two annotations and looks up for all. Tested will be
	 * the number of annotations, should be two, and if they really are these
	 * which it has added.
	 */
	@Test
	public void testInsertGetAllAnnotations() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Martin Zeltner");
		annotation.setGrade(8);
		annotation.setContent("This is only a short comment.");

		Annotation annotation2 = new Annotation();
		annotation2.setReference(fakeReference);
		annotation2.setAnnotator("Hans Bauer");
		annotation2.setGrade(1);
		annotation2.setContent("This is an comment.");
		annotation = dao.saveOrUpdate(annotation);
		annotation2 = dao.saveOrUpdate(annotation2);
		List<Annotation> list = dao.getAll();
		assertEquals("Wrong number of annotations in DB", 2, list.size());
		
		// the next lines are here due to a strange bug of mysql
//		annotation2.getWhenInserted().setNanos(0);
//		annotation.getWhenInserted().setNanos(0);
		for (Annotation l : list) {
//			((Annotation) l).getWhenInserted().setNanos(0);
		}
		
		assertTrue("First annotation has not been found",
			list.contains(annotation));
		assertTrue("Second annotation has not been found",
			list.contains(annotation2));
	}

	/**
	 * This test inserts an annotation and removes it. Afterwards that, the
	 * annotation should not be reachable.
	 */
	@Test
	public void testInsertRemoveAnnotation() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Martin Zeltner");
		annotation.setGrade(8);
		annotation.setContent("This is only a short comment.");
		Annotation annotation2 = dao.saveOrUpdate(annotation);
		dao.deleteById(annotation2.getKey());
		try {
			dao.findById(annotation2.getKey());
			fail("The removed annotation is still in the DB.");
		} catch (DataRetrievalFailureException e) {
			s_logger.debug("Expected exception catched.", e);
		}
	}

	/**
	 * This test inserts one annotation. Afterwards it will be looked up by two
	 * persons. Now, these persons edit the same annotation and would like to
	 * save changes. The person, which save as second must get a
	 * <code>AnnotationModificationException</code>. But this person should
	 * be able to remove this annotation, because removing is not under
	 * optimistic locking control.
	 */
	@Test
	public void testInsertModificateRemoveAnnotationByTwoPersons() {
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator("Martin Zeltner");
		annotation.setGrade(8);
		annotation.setContent("This is only a short comment.");
		dao.saveOrUpdate(annotation);
		Annotation annotation2
			= (Annotation) dao.getAnnotationsByAnnotator(
				"Martin Zeltner").get(0);
		Annotation annotation3
			= (Annotation) dao.getAnnotationsByAnnotator(
				"Martin Zeltner").get(0);
		annotation2.setContent("I think it is time to change this comment.");
		dao.saveOrUpdate(annotation2);
		annotation3.setContent(
			"I think someone has always the same ideas as me.");
		try {
			s_logger.error("Provoking hibernate StaleObjectStateException");
			dao.saveOrUpdate(annotation3);
			fail("The current annotation could be modificated "
				+ "by two persons on the same time.");
		} catch (OptimisticLockingFailureException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		dao.deleteById(annotation3.getKey());
	}

	/**
	 * This test inserts, gets and removes an annotation with a content size of
	 * maximal 1MB.
	 */
	@Test
	public void testInsertGetRemoveAnnotationWithMaxContentSize1MB() {
		final int MAX_CLOB_SIZE = 1024 * 1024;
		String filepath = "etc/testfiles/gulliverstravels.txt";
		String annotator = "Gulliver's Travels";
		internalTestInsertGetRemoveAnnotation(
			MAX_CLOB_SIZE, filepath, annotator);
	}

	/**
	 * This internal test inserts, gets and removes an annotation.
	 *
	 * @param maxClobSize
	 *            Is the maximal number of characters the content field should
	 *            be filled with.
	 * @param filepath
	 *            Is the path where this test can find his data for the content
	 *            field.
	 * @param annotator
	 *            Is the name of the annotator.
	 */
	private void internalTestInsertGetRemoveAnnotation(int maxClobSize,
		String filepath, String annotator) {
		Resource resource = new ClassPathResource(filepath);
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			InputStream in = resource.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null
				&& sb.length() < maxClobSize) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			fail("Unable to read file '" + filepath + "'");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					fail("Unable to close file '" + filepath + "'");
				}
			}
		}

		String content;
		if (sb.length() > maxClobSize) {
			content = sb.substring(0, maxClobSize);
		} else {
			content = sb.toString();
		}
		
		Reference fakeReference = addDefaultFakeReference();
		
		AnnotationDao dao = getAnnotationDao();
		Annotation annotation = new Annotation();
		annotation.setReference(fakeReference);
		annotation.setAnnotator(annotator);
		annotation.setGrade(10);
		annotation.setContent(content);
		annotation = dao.saveOrUpdate(annotation);
		Annotation annotation2
			= (Annotation) dao.getAnnotationsByAnnotator(annotator).get(0);

		// the next two lines are here due to a strange bug of mysql
//		annotation2.getWhenInserted().setNanos(0);
//		annotation.getWhenInserted().setNanos(0);
		
		assertEquals("The inserted and read domain objects are not equal",
			annotation, annotation2);

		dao.deleteById(annotation.getKey());

		List<Annotation> list = dao.getAll();
		assertEquals("The removed annotation is still in the DB.",
			0, list.size());
	}

	/**
	 * This test inserts an file.
	 */
	@Test
	public void testInsertFile() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		dao.saveOrUpdate(file);
	}

	/**
	 * This test inserts a file and looks up for it by file's primary key.
	 */
	@Test
	public void testInsertGetFileByKey() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		File file2 = dao.saveOrUpdate(file);
		
		// Explicitly load everything
		File file3 = dao.findById(file2.getKey());
		
		assertEquals("The inserted and read domain objects are not equal",
			file3, file2);
	}

	/**
	 * This test inserts a file and looks up for it by file's name.
	 */
	@Test
	public void testInsertGetFileByName() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		File file2 = dao.saveOrUpdate(file);
		
		List<File> list = dao.getByName(file2.getName());
		assertEquals("List contains more than one file of name '"
			+ file2.getName() + "'", 1, list.size());
		File file3 = (File) list.get(0);
		assertEquals("The inserted and read domain objects are not equal",
			file3, file2);
	}

	/**
	 * This test inserts two files and looks up for all. Tested will be the
	 * number of annotations, should be two, and if they really are these which
	 * it has added.
	 */
	@Test
	public void testInsertGetAllFiles() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		File file2 = new File();
		file2.setReference(fakeReference);
		file2.setName("Spring Reference Documentation");
		file2.setMimeType("text/html");
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<title>Test content</title>");
		sb.append("<body>This is a html <b>test</b> content.</body>");
		sb.append("</html>");
		byte[] content2 = sb.toString().getBytes();
		file2.setContent(content2);
		dao.saveOrUpdate(file);
		dao.saveOrUpdate(file2);
		List<File> list = dao.getAll();
		assertEquals("Wrong number of files in DB", 2, list.size());
		assertTrue("First file has not been found", list.contains(file));
		assertTrue("Second file has not been found", list.contains(file2));
	}

	/**
	 * This test inserts a file and removes it. Afterwards that, the file should
	 * not be reachable.
	 */
	@Test
	public void testInsertRemoveFile() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		File file2 = dao.saveOrUpdate(file);
		dao.deleteById(file2.getKey());
		try {
			dao.findById(file2.getKey());
			fail("The removed file is still in the DB.");
		} catch (DataRetrievalFailureException e) {
			s_logger.debug("Expected exception catched.", e);
		}
	}

	/**
	 * This test inserts one file. Afterwards it will be looked up by two
	 * persons. Now, these persons edit the same file and would like to save
	 * changes. The person, which save as second must get a
	 * <code>FileModificationException</code>. But this person should be able
	 * to remove this file, because removing is not under optimistic locking
	 * control.
	 */
	@Test
	public void testInsertModificateRemoveFileByTwoPersons() {
		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName("iBatis Developer Guide");
		file.setMimeType("text/plain");
		byte[] content = "This is only a test content.".getBytes();
		file.setContent(content);
		dao.saveOrUpdate(file);

		File file2 = (File) dao.getByName(
			"iBatis Developer Guide").get(0);
		File file3 = (File) dao.getByName(
			"iBatis Developer Guide").get(0);
		content = "This is another test content.".getBytes();
		file2.setContent(content);
		dao.saveOrUpdate(file2);
		content = "I think someone has always the same ideas as me.".getBytes();
		file3.setContent(content);
		try {
			s_logger.error("Provoking hibernate StaleObjectStateException");
			dao.saveOrUpdate(file3);
			fail("The current file could be modificated "
				+ "by two persons on the same time.");
		} catch (OptimisticLockingFailureException e) {
			s_logger.debug("Expected exception catched.", e);
		}
		dao.deleteById(file3.getKey());
	}

	/**
	 * This test inserts, gets and removes a file with max content size 1MB.
	 */
	@Test
	public void testInsertGetRemoveFileWithMaxContentSize1MB() {
		final int MAX_BLOB_SIZE = 1024 * 1024;
		String filepath = "etc/testfiles/iBATIS-SqlMaps-2.pdf";
		String name = "iBATIS SQL Maps Developer Guide Version 2.0";
		String mimeType = "application/x-pdf";
		internalTestInsertGetRemoveFile(
			MAX_BLOB_SIZE, filepath, name, mimeType);
		
	}
	/**
	 * Tests inheritance.
	 * Create a FormalPublication, a Book and a reference. Then, getAll() on
	 * FormalPublicationDAO must only return the FormalPublication
	 * object, not the Reference and not the Book
	 */
	@Test
	public void testInsertGetFormalPublication() {
		this.addFakeReference("Testref");
		
		//at first, reference
		FormalPublication fp = new FormalPublication();
		
		//next, FormalPublication
		fp.setName("TestFormalPublication");
		fp.setAuthorName("Mister X");
		fp.setPageNum(21);
		
		FormalPublicationDao dao = getFormalPublicationDao();
		
		dao.saveOrUpdate(fp);
		
		//and at least the Book
		Book book = new Book();
		
		book.setName("Testbook");
		book.setAuthorName("Mister Y");
		book.setPageNum(22);
		
		getBookDao().saveOrUpdate(book);
		
		//now only! load all FormalPublications
		List<FormalPublication> list = dao.getAll();
		
		
		assertTrue("Not only FormalPublication returned",
			list.size() == 1 && list.get(0).getClass() == FormalPublication.class);
	}
	
	/**
	 * Tests the insertion of multiple keywords in several updates.
	 */
	@Test
	public void testInsertMultipleKeywords() {
		try {
			// add a book
			Book book = new Book();
			book.setName("Testbook");
			book.setAuthorName("Mister Y");
			book.setPageNum(22);
			Annotation a1 = new Annotation();
			a1.setAnnotator("arr");
			a1.setContent("very good testbook!");
			Annotation a2 = new Annotation();
			a2.setAnnotator("arr");
			a2.setContent("it is snowing outside");
			book.setAnnotations(new HashSet<Annotation>());
			book.getAnnotations().add(a1);
			a1.setReference(book);
			book.getAnnotations().add(a2);
			a2.setReference(book);
			Keyword k1 = new Keyword();
			k1.setName("Testkeyword97");
			book.setKeywords(new HashSet<Keyword>());
			book.getKeywords().add(k1);
			TransactionStatus transaction = null;
			try {
				transaction = getTransactionManager().getTransaction(new DefaultTransactionDefinition());
				
				k1 = getKeywordDao().saveOrUpdate(k1);
				book = getBookDao().saveOrUpdate(book);
				a1 = getAnnotationDao().saveOrUpdate(a1);
				a2 = getAnnotationDao().saveOrUpdate(a2);
				
				getTransactionManager().getSessionFactory().getCurrentSession().flush();
				getTransactionManager().commit(transaction);
			} catch (Exception e) {
				m_transactionManager.rollback(transaction);
				fail("Could not save the data: " + e.getMessage());
			}
			assertEquals("Keyword not inserted", 1, getBookDao().reload(book).getKeywords().size());
			
			book.setKeywords(new HashSet<Keyword>(book.getKeywords()));
			
			Keyword k2 = new Keyword();
			k2.setName("FancyKeyword77");
			
			k2 = getKeywordDao().saveOrUpdate(k2);
//			getKeywordDao().getAll();
			
			book.getKeywords().add(k2);
			book = getBookDao().saveOrUpdate(book);
			/*try {
				TransactionStatus transaction = getTransactionManager().getTransaction(new DefaultTransactionDefinition());
				
				book = getBookDao().saveOrUpdate(book);
				a1 = getAnnotationDao().saveOrUpdate(a1);
				a2 = getAnnotationDao().saveOrUpdate(a2);
				
				getTransactionManager().getSessionFactory().getCurrentSession().flush();
				getTransactionManager().commit(transaction);
			} catch (Exception e) {
				fail("Could not save the data after setting the second keyword: " + e.getMessage());
			}*/
			assertEquals("Not all keywords inserted", 2, getBookDao().reload(book).getKeywords().size());
		
		} finally {
			for (Book b : getBookDao().getAll()) {
				getBookDao().delete(b);
			}
			for (Keyword k : getKeywordDao().getAll()) {
				getKeywordDao().delete(k);
			}
			
		}
		
	}
	
//	/**
//	 * Tests the reparenting of an annotation to a new reference.
//	 */
//	@Test
//	public void testReparenting() {
//		try {
//			// add a book
//			Book book = new Book();
//			book.setName("Testbook");
//			book.setAuthorName("Mister Y");
//			book.setPageNum(22);
//			Annotation a1 = new Annotation();
//			a1.setAnnotator("arr");
//			a1.setContent("very good testbook!");
//			Annotation a2 = new Annotation();
//			a2.setAnnotator("arr");
//			a2.setContent("it is snowing outside");
//			book.setAnnotations(new HashSet<Annotation>());
//			book.getAnnotations().add(a1);
//			a1.setReference(book);
//			book.getAnnotations().add(a2);
//			a2.setReference(book);
//			
//			book = getBookDao().saveOrUpdate(book);
//			
//			// add a second book
//			Book book2 = new Book();
//			book2.setName("Testbook2");
//			book2.setAuthorName("Mister Z");
//			book2.setPageNum(23);
//			book2.setAnnotations(new HashSet<Annotation>());
//			book2 = getBookDao().saveOrUpdate(book2);
//			
//			
//			Annotation aReparent = book.getAnnotations().iterator().next();
////			aReparent.setReference(book2);
////			book2.getAnnotations().add(aReparent);
//			
//			TransactionStatus transaction = null;
//			try {
//				transaction = getTransactionManager().getTransaction(new DefaultTransactionDefinition());
//				
//				Session s = getTransactionManager().getSessionFactory().getCurrentSession();
//				Annotation a = (Annotation) s.get(Annotation.class, aReparent.getKey());
//				Book b1 = (Book) s.get(Book.class, book.getKey());
//				Book b2 = (Book) s.get(Book.class, book2.getKey());
//				a.setReference(b2);
//				a.setAnnotator("arr(updated");
//				s.flush();
//				
////				getTransactionManager().getSessionFactory().getCurrentSession().flush();
//				getTransactionManager().commit(transaction);
//				
//				book = getBookDao().reload(book);
//				book2 = getBookDao().reload(book2);
//				
//			} catch (Exception e) {
//				m_transactionManager.rollback(transaction);
//				fail("Could not save the data: " + e.getMessage());
//			}
//			assertEquals("Annotation not reparented", 1, book.getAnnotations().size());
//			
//		} finally {
//			for (Book b : getBookDao().getAll()) {
//				getBookDao().delete(b);
//			}
//			
//		}
//		
//	}

	protected HibernateTransactionManager getTransactionManager() {
		if (m_transactionManager == null) {
			m_transactionManager = (HibernateTransactionManager) getApplicationContext()
				.getBean("transactionManager");
		}
		return m_transactionManager;
	}
	/**
	 * This internal test inserts, gets and removes a file.
	 *
	 * @param maxBlobSize
	 *            Is the maximal number of bytes the content field should be
	 *            filled with.
	 * @param filepath
	 *            Is the path where this test can find his data for the content
	 *            field.
	 * @param name
	 *            Is the name of the file.
	 * @param mimeType
	 *            Is the mime type for this file.
	 */
	private void internalTestInsertGetRemoveFile(int maxBlobSize,
		String filepath, String name, String mimeType) {
		final int ARRAY_SIZE = 32 * 1024;
		Resource resource = new ClassPathResource(filepath);
		BufferedInputStream in = null;
		byte[] content = null;
		try {
			in = new BufferedInputStream(resource.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int readBytes;
			byte[] bytes = new byte[ARRAY_SIZE];
			while (out.size() <= maxBlobSize - ARRAY_SIZE && (readBytes = in.read(bytes)) > 0) {
				out.write(bytes, 0, readBytes);
				out.flush();
			}
			readBytes = in.read(bytes);
			if (maxBlobSize - out.size() > 0 && readBytes > 0) {
				int restBytes = Math.min(maxBlobSize - out.size(), readBytes);
				out.write(bytes, 0, restBytes);
				out.flush();
			}
			if (out.size() > 0) {
				content = out.toByteArray();
			}
		} catch (IOException e) {
			fail("Unable to read file '" + filepath + "'");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					fail("Unable to close file '" + filepath + "'");
				}
			}
		}

		Reference fakeReference = addDefaultFakeReference();
		
		FileDao dao = getFileDao();
		File file = new File();
		file.setReference(fakeReference);
		file.setName(name);
		file.setMimeType(mimeType);
		file.setContent(content);
		file = dao.saveOrUpdate(file);
		
		File file2 = (File) dao.getByName(name).get(0);

		assertEquals("The inserted and read domain objects are not equal", file, file2);

		dao.deleteById(file2.getKey());

		List<File> list = dao.getAll();
		assertEquals("The removed file is still in the DB.", 0, list.size());
	}
}
//Checkstyle: MagicNumber on
