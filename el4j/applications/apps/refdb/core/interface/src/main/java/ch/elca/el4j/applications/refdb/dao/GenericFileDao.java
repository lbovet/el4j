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

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

/**
 * 
 * This interface represents a generic DAO for domain objects representing
 * files or file descriptors.
 * It defines the methods which are specific to these domain objects.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The generic type of the domain class the DAO is responsible for
 * @param <ID>
 *            The generic type of the domain class' identifier
 *
 * @author Alex Mathey (AMA)
 */
public interface GenericFileDao<T, ID extends Serializable>
    extends GenericReferencedObjectDao<T, ID> {
    
    /**
     * Get all the files or file descriptors with the same name.
     * 
     * @param name
     *            Is the name of a file or file descriptor.
     * @return Returns the desired file or file descriptor.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If file or file descriptor could not be retrieved.
     */
    public List<T> getByName(String name)
        throws DataAccessException, DataRetrievalFailureException;
    
}
