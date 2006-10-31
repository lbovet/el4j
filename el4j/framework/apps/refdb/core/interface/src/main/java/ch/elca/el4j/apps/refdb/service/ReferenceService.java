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
package ch.elca.el4j.apps.refdb.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.apps.keyword.service.KeywordService;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.core.transaction.annotations.RollbackConstraint;
import ch.elca.el4j.services.search.QueryObject;

/**
 * This interface provides the business methods which can be used in the
 * presentation layer and which are not already present in the underlying DAOs.
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
public interface ReferenceService extends KeywordService {
    
    /**
     * Save file. If file is new, viz is has no primary key, it will be
     * inserted. Otherwise, the file will be updated.
     * 
     * @param file
     *            Is the file to save.
     * @return Returns the saved file without its content (FileDescriptorView).
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataIntegrityViolationException
     *             If file could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If file has been modificated in the meantime.
     */
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            DataIntegrityViolationException.class,
            OptimisticLockingFailureException.class })
    public FileDescriptorView saveFileAndReturnFileDescriptorView(File file)
        throws DataAccessException, DataIntegrityViolationException, 
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
     */
    public Reference getReferenceByKey(int key)
        throws DataAccessException, DataRetrievalFailureException;

    /**
     * Get all references with the same name.
     * 
     * @param name
     *            Is the name of the reference.
     * @return Returns a list with references. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List<Reference> getReferencesByName(String name)
        throws DataAccessException;

    /**
     * Get all references.
     * 
     * @return Returns a list with all references. Returns never 
     *         <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List<Reference> getAllReferences() throws DataAccessException;

    /**
     * Search references.
     * 
     * @param query
     *            Is the search query object.
     * @return Returns a list with reference. Returns never <code>null</code>.
     * @throws DataAccessException
     *             If general data access problem occurred.
     */
    public List<Reference> searchReferences(QueryObject query)
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
     * @throws DataIntegrityViolationException
     *             If reference could not be inserted.
     * @throws OptimisticLockingFailureException
     *             If reference has been modificated in the meantime.
     */
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            DataIntegrityViolationException.class,
            OptimisticLockingFailureException.class })
    public Reference saveReference(Reference reference)
        throws DataAccessException, DataIntegrityViolationException, 
            OptimisticLockingFailureException;

    /**
     * Delete reference. Primary key will be used.
     * 
     * @param key
     *            Is the primary key of the reference, which should be deleted.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws OptimisticLockingFailureException
     *             If reference could not be deleted.
     */
    @RollbackConstraint(rollbackFor = { DataAccessException.class,
            OptimisticLockingFailureException.class })
    public void deleteReference(int key)
        throws DataAccessException, OptimisticLockingFailureException;
}
