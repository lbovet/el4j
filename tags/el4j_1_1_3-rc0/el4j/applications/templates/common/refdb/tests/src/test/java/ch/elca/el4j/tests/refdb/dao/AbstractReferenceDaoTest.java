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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

// Checkstyle: MagicNumber off

/**
 * Abstract test case for the reference dao.
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
public abstract class AbstractReferenceDaoTest extends AbstractTestCaseBase {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(AbstractReferenceDaoTest.class);

    /**
     * Hide default constructor.
     */
    protected AbstractReferenceDaoTest() { }

    
    
    /**
     * This test inserts an annotation.
     */
    public void testInsertAnnotation() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
        annotation.setAnnotator("Martin Zeltner");
        annotation.setGrade(8);
        annotation.setContent("This is only a short comment.");
        dao.saveOrUpdate(annotation);
    }

    /**
     * This test inserts an annotation and looks up for it by annotation's
     * primary key.
     */
    public void testInsertGetAnnotationByKey() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
        annotation.setAnnotator("Hans Bauer");
        annotation.setGrade(1);
        annotation.setContent("This is an comment.");
        Annotation annotation2 = dao.saveOrUpdate(annotation);
        Annotation annotation3 
            = dao.findById(annotation2.getKey());
        
        // the next two lines are here due to a strange bug of mysql
        annotation2.getWhenInserted().setNanos(0);
        annotation3.getWhenInserted().setNanos(0);         
        
        assertEquals("The inserted and read domain objects are not equal", 
            annotation3, annotation2);
    }

    /**
     * This test inserts an annotation and looks up for it by annotator's name.
     */
    public void testInsertGetAnnotationByAnnotator() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
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
        annotation2.getWhenInserted().setNanos(0);
        annotation3.getWhenInserted().setNanos(0);       
        
        assertEquals("The inserted and read domain objects are not equal", 
            annotation3, annotation2);
    }

    /**
     * This test inserts two annotations and looks up for all. Tested will be
     * the number of annotations, should be two, and if they really are these
     * which it has added.
     */
    public void testInsertGetAllAnnotations() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
        annotation.setAnnotator("Martin Zeltner");
        annotation.setGrade(8);
        annotation.setContent("This is only a short comment.");

        Annotation annotation2 = new Annotation();
        annotation2.setKeyToReference(fakeReferenceKey);
        annotation2.setAnnotator("Hans Bauer");
        annotation2.setGrade(1);
        annotation2.setContent("This is an comment.");
        annotation = dao.saveOrUpdate(annotation);
        annotation2 = dao.saveOrUpdate(annotation2);
        List<Annotation> list = dao.getAll();
        assertEquals("Wrong number of annotations in DB", 2, list.size());
        
        // the next lines are here due to a strange bug of mysql
        annotation2.getWhenInserted().setNanos(0);
        annotation.getWhenInserted().setNanos(0);              
        for (Annotation l : list) {
            ((Annotation)l).getWhenInserted().setNanos(0);
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
    public void testInsertRemoveAnnotation() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
        annotation.setAnnotator("Martin Zeltner");
        annotation.setGrade(8);
        annotation.setContent("This is only a short comment.");
        Annotation annotation2 = dao.saveOrUpdate(annotation);
        dao.delete(annotation2.getKey());
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
    public void testInsertModificateRemoveAnnotationByTwoPersons() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
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
        dao.delete(annotation3.getKey());
    }

    /**
     * This test inserts, gets and removes an annotation with a content size of
     * maximal 1MB.
     */
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
        InputStream in = null;
        StringBuffer sb = new StringBuffer();
        try {
            in = resource.getInputStream();
            BufferedReader reader 
                = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null
                && sb.length() < maxClobSize) {
                sb.append(line);
                sb.append("\n");
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

        String content;
        if (sb.length() > maxClobSize) {
            content = sb.substring(0, maxClobSize);
        } else {
            content = sb.toString();
        }
        
        int fakeReferenceKey = addDefaultFakeReference();
        
        AnnotationDao dao = getAnnotationDao();
        Annotation annotation = new Annotation();
        annotation.setKeyToReference(fakeReferenceKey);
        annotation.setAnnotator(annotator);
        annotation.setGrade(10);
        annotation.setContent(content);
        annotation = dao.saveOrUpdate(annotation);
        Annotation annotation2 
            = (Annotation) dao.getAnnotationsByAnnotator(annotator).get(0);

        // the next two lines are here due to a strange bug of mysql
        annotation2.getWhenInserted().setNanos(0);
        annotation.getWhenInserted().setNanos(0);         
        
        assertEquals("The inserted and read domain objects are not equal", 
            annotation, annotation2);

        dao.delete(annotation.getKey());

        List<Annotation> list = dao.getAll();
        assertEquals("The removed annotation is still in the DB.", 
            0, list.size());
    }

    /**
     * This test inserts an file.
     */
    public void testInsertFile() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
        file.setName("iBatis Developer Guide");
        file.setMimeType("text/plain");
        byte[] content = "This is only a test content.".getBytes();
        file.setContent(content);
        dao.saveOrUpdate(file);
    }

    /**
     * This test inserts a file and looks up for it by file's primary key.
     */
    public void testInsertGetFileByKey() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
        file.setName("iBatis Developer Guide");
        file.setMimeType("text/plain");
        byte[] content = "This is only a test content.".getBytes();
        file.setContent(content);
        File file2 = dao.saveOrUpdate(file);
        File file3 = dao.findById(file2.getKey());
        assertEquals("The inserted and read domain objects are not equal",
            file3, file2);
    }

    /**
     * This test inserts a file and looks up for it by file's name.
     */
    public void testInsertGetFileByName() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
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
    public void testInsertGetAllFiles() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
        file.setName("iBatis Developer Guide");
        file.setMimeType("text/plain");
        byte[] content = "This is only a test content.".getBytes();
        file.setContent(content);
        File file2 = new File();
        file2.setKeyToReference(fakeReferenceKey);
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
    public void testInsertRemoveFile() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
        file.setName("iBatis Developer Guide");
        file.setMimeType("text/plain");
        byte[] content = "This is only a test content.".getBytes();
        file.setContent(content);
        File file2 = dao.saveOrUpdate(file);
        dao.delete(file2.getKey());
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
    public void testInsertModificateRemoveFileByTwoPersons() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
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
        dao.delete(file3.getKey());
    }

    /**
     * This test inserts, gets and removes a file with max content size 1MB.
     */
    public void testInsertGetRemoveFileWithMaxContentSize1MB() {
        final int MAX_BLOB_SIZE = 1024 * 1024;
        String filepath = "etc/testfiles/iBATIS-SqlMaps-2.pdf";
        String name = "iBATIS SQL Maps Developer Guide Version 2.0";
        String mimeType = "application/x-pdf";
        internalTestInsertGetRemoveFile(
            MAX_BLOB_SIZE, filepath, name, mimeType);
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
            while (out.size() <= maxBlobSize - ARRAY_SIZE
                && (readBytes = in.read(bytes)) > 0) {
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

        int fakeReferenceKey = addDefaultFakeReference();
        
        FileDao dao = getFileDao();
        File file = new File();
        file.setKeyToReference(fakeReferenceKey);
        file.setName(name);
        file.setMimeType(mimeType);
        file.setContent(content);
        file = dao.saveOrUpdate(file);
        File file2 = (File) dao.getByName(name).get(0);

        assertEquals("The inserted and read domain objects are not equal", 
            file, file2);

        dao.delete(file2.getKey());

        List<File> list = dao.getAll();
        assertEquals("The removed file is still in the DB.", 
            0, list.size());
    }
}
//Checkstyle: MagicNumber on
