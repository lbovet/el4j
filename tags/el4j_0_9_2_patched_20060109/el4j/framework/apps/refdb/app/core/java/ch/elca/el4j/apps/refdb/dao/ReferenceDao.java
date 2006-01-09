/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.refdb.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.FormalPublicationDto;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;

/**
 * A reference DAO is responsible for accessing and storing reference
 * information as well as closely related information such as keywords for
 * example.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
public interface ReferenceDao extends KeywordDao {
    /**
     * Get annotation by primary key.
     * 
     * @param key
     *            Is the primary key
     * @return Returns desired annotation.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If annotation could not be retrieved.
     */
    public AnnotationDto getAnnotationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all annotations from one annotator.
     * 
     * @param annotator
     *            Is the name of the annotator.
     * @return Returns a list with annotations. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAnnotationsByAnnotator(String annotator)
        throws DataAccessException;

    /**
     * Get all annotations of a reference.
     * 
     * @param key
     *            Is the primary key of the referenced reference.
     * @return Returns a list with annotations. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAnnotationsByReference(int key)
        throws DataAccessException;

    /**
     * Get all annotations.
     * 
     * @return Returns all annotations. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAllAnnotations()
        throws DataAccessException;

    /**
     * Save annotation. If annotation is new, viz is has no primary key, it will
     * be inserted. Otherwise, the annotation will be updated.
     * 
     * @param annotation
     *            Is the annotation to save.
     * @return Returns the saved annotation.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If annotation could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If annotation has been modificated in the meantime.
     */
    public AnnotationDto saveAnnotation(AnnotationDto annotation)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove annotation. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the annotation, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If annotation could not be deleted.
     */
    public void removeAnnotation(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;

    /**
     * Get a file by primary key.
     * 
     * @param key
     *            Is the primary key.
     * @return Returns desired file.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If file could not be retrieved.
     */
    public FileDto getFileByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all files with the same name.
     * 
     * @param name
     *            Is the name of the file.
     * @return Returns a list with files. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getFilesByName(String name) throws DataAccessException;

    /**
     * Get all files of a reference.
     * 
     * @param key
     *            Is the primary key of the referenced reference.
     * @return Returns a list with files. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getFilesByReference(int key) throws DataAccessException;

    /**
     * Get all files.
     * 
     * @return Returns all files. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAllFiles() throws DataAccessException;

    /**
     * Save file. If file is new, viz is has no primary key, it will be
     * inserted. Otherwise, the file will be updated.
     * 
     * @param file
     *            Is the file to save.
     * @return Returns the saved file.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If file could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If file has been modificated in the meantime.
     */
    public FileDto saveFile(FileDto file)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove file. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the file, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If file could not be deleted.
     */
    public void removeFile(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;

    /**
     * Get all file descriptor views of a reference.
     * 
     * @param key
     *            Is the primary key of the referenced reference.
     * @return Returns a list with file descriptor views. Returns never 
     *         <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getFileDescriptorViewsByReference(int key)
        throws DataAccessException;

    /**
     * Modifies every field of a file, except the content.
     * 
     * @param fileView
     *            Is the file to modify.
     * @return Returns the modified file descriptor view.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws OptimisticLockingFailureException
     *             If file has been modificated in the meantime.
     * @throws DataRetrievalFailureException
     *             If file could not be retrieved.
     */
    public FileDescriptorView modifyFileDescriptorView(
        FileDescriptorView fileView)
        throws DataAccessException, OptimisticLockingFailureException,
            DataRetrievalFailureException;

    /**
     * Get a link by primary key.
     * 
     * @param key
     *            Is the primary key.
     * @return Returns desired link.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If link could not be retrieved.
     */
    public LinkDto getLinkByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all links with the same name.
     * 
     * @param name
     *            Is the name of the link.
     * @return Returns a list with links. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getLinksByName(String name) throws DataAccessException;

    /**
     * Get all links.
     * 
     * @return Returns a list with all links. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAllLinks() throws DataAccessException;

    /**
     * Search for links where name and description are like the given.
     * 
     * @param query
     *            Is the search query object.
     * @return Returns a list with links. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List searchLinks(QueryObject query)
        throws DataAccessException;

    /**
     * Save link. If link is new, viz is has no primary key, it will be
     * inserted. Otherwise, the link will be updated.
     * 
     * @param link
     *            Is the link to save.
     * @return Returns the saved link.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If link could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If link has been modificated in the meantime.
     */
    public LinkDto saveLink(LinkDto link)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove link. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the link, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If link could not be deleted.
     */
    public void removeLink(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;

    /**
     * Get a formal publication by primary key.
     * 
     * @param key
     *            Is the primary key.
     * @return Returns desired formal publication.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If formal publication could not be retrieved.
     */
    public FormalPublicationDto getFormalPublicationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all formal publications with the same name.
     * 
     * @param name
     *            Is the name of the formal publication.
     * @return Returns a list with formal publications. Returns never
     *         <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getFormalPublicationsByName(String name)
        throws DataAccessException;

    /**
     * Get all formal publications.
     * 
     * @return Returns a list with all formal publications. Returns never
     *         <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAllFormalPublications() throws DataAccessException;

    /**
     * Search for formal publications where name and description are like the
     * given.
     * 
     * @param query
     *            Is the search query object.
     * @return Returns a list with formal publications. Returns never
     *         <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List searchFormalPublications(QueryObject query)
        throws DataAccessException;

    /**
     * Save formal publication. If formal publication is new, viz is has no
     * primary key, it will be inserted. Otherwise, the formal publication will
     * be updated.
     * 
     * @param formalPublication
     *            Is the formal publication to save.
     * @return Returns the saved formal publication.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If formal publication could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If formal publication has been modificated in the meantime.
     */
    public FormalPublicationDto saveFormalPublication(
        FormalPublicationDto formalPublication)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove formal publication. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the formal publication, which should be
     *            deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If formal publication could not be deleted.
     */
    public void removeFormalPublication(int key)
        throws DataAccessException,
            JdbcUpdateAffectedIncorrectNumberOfRowsException;

    /**
     * Get a book by primary key.
     * 
     * @param key
     *            Is the primary key.
     * @return Returns desired book.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If book could not be retrieved.
     */
    public BookDto getBookByKey(int key) 
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all books with the same name.
     * 
     * @param name
     *            Is the name of the book.
     * @return Returns a list with books. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getBooksByName(String name) throws DataAccessException;

    /**
     * Get all books.
     * 
     * @return Returns a list with all books. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List getAllBooks() throws DataAccessException;

    /**
     * Search for books where name and description are like the given.
     * 
     * @param query
     *            Is the search query object.
     * @return Returns a list with books. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List searchBooks(QueryObject query)
        throws DataAccessException;

    /**
     * Save book. If book is new, viz is has no primary key, it will be
     * inserted. Otherwise, the book will be updated.
     * 
     * @param book
     *            Is the book to save.
     * @return Returns the saved book.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If book could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If book has been modificated in the meantime.
     */
    public BookDto saveBook(BookDto book)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove book. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the book, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If book could not be deleted.
     */
    public void removeBook(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;
}
