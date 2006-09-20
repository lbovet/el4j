/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.apps.refdb.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.generic.dao.ConvenientGenericDao;

/**
 * 
 * This interface represents a generic DAO for domain objects representing
 * sources of information.
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
public interface GenericReferenceDao<T extends Reference,
    ID extends Serializable> extends ConvenientGenericDao<T, ID> {
    
    /**
     * Get all the references with the same name.
     * 
     * @param name
     *            Is the name of a reference.
     * @return Returns the desired reference.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If reference could not be retrieved.
     */
    public List<T> getByName(String name)
        throws DataAccessException, DataRetrievalFailureException;
    
    /**
     * Checks whether a reference with the given id exists.
     * @param id The id of a reference
     * @return True if a reference with the given id exists
     */
    public boolean referenceExists(ID id);
}
