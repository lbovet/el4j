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
package ch.elca.el4j.apps.refdb.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.search.QueryObject;

/**
 * This interface provides all available business methods, which can be used in
 * presentation layer.
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
public interface ReferenceService extends KeywordService {
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
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
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
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
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
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
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
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
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
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public FileDescriptorView modifyFileDescriptorView(
        FileDescriptorView fileView)
        throws DataAccessException, OptimisticLockingFailureException,
            DataRetrievalFailureException;

    /**
     * Save file. If file is new, viz is has no primary key, it will be
     * inserted. Otherwise, the file will be updated.
     * 
     * @param file
     *            Is the file to save.
     * @return Returns the saved file without its content (FileDescriptorView).
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If file could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If file has been modificated in the meantime.
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public FileDescriptorView saveFileAndReturnFileDescriptorView(FileDto file)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Get a reference by primary key.
     * 
     * @param key
     *            Is the primary key.
     * @return Returns desired reference.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If link could not be retrieved.
     * 
     * @@attrib.transaction.SupportsReadOnly()
     */
    public ReferenceDto getReferenceByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all references with the same name.
     * 
     * @param name
     *            Is the name of the reference.
     * @return Returns a list with references. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     *             
     * @@attrib.transaction.SupportsReadOnly()            
     */
    public List getReferencesByName(String name) throws DataAccessException;

    /**
     * Get all references.
     * 
     * @return Returns a list with all references. Returns never 
     *         <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     *             
     * @@attrib.transaction.SupportsReadOnly()            
     */
    public List getAllReferences() throws DataAccessException;

    /**
     * Search references.
     * 
     * @param query
     *            Is the search query object.
     * @return Returns a list with reference. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List searchReferences(QueryObject query)
        throws DataAccessException;

    /**
     * Save reference. If reference is new, viz is has no primary key, it will
     * be inserted. Otherwise, the reference will be updated.
     * 
     * @param reference
     *            Is the reference to save.
     * @return Returns the saved reference.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws InsertionFailureException
     *             If reference could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If reference has been modificated in the meantime.
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public ReferenceDto saveReference(ReferenceDto reference)
        throws DataAccessException, InsertionFailureException, 
            OptimisticLockingFailureException;

    /**
     * Remove reference. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the reference, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws JdbcUpdateAffectedIncorrectNumberOfRowsException
     *             If reference could not be deleted.
     */
    @Transactional(rollbackFor = {DataAccessException.class,
            RuntimeException.class, Error.class })
    public void removeReference(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException;
}
