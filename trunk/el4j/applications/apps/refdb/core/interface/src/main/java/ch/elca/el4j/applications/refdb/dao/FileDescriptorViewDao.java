/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.applications.refdb.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.applications.refdb.dom.FileDescriptorView;

/**
 * 
 * This interface represents a DAO for the file descriptor view domain object.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public interface FileDescriptorViewDao 
    extends GenericFileDao<FileDescriptorView, Integer> {
    
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
}
