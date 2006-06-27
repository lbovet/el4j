/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.generic.dao;

import java.util.List;

import ch.elca.el4j.services.search.QueryObject;

/**
 * 
 * This interface serves as generic access to storage repositories. It is the
 * interface for the DDD-Book's Repository pattern. The repository pattern is
 * similar to the DAO pattern, but a bit more generic. This interface can be
 * implemented in a generic way and can be extended in case a user needs more
 * specific methods. Based on an idea from the Hibernate website.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T>
 *            The domain class the repository is responsible for
 * @param <ID>
 *            The type of the domain class' identifier
 *
 * @author Philipp Oser (POS)
 * @author Alex Mathey (AMA)
 */
public interface GenericRepository<T, ID> {

    /**
     * Retrieves a domain object by identifier.
     * 
     * @param id
     *            The id of a domain object
     * @param lock
     *            Indicates whether a database lock should be obtained for this
     *            operation
     * @return The desired domain object
     */   
    T findById(ID id, boolean lock);

    /**
     * Retrieves all the domain objects of type T.
     * 
     * @return The list containing all the domain objects of type T
     */
    List<T> findAll();   

    /**
     * Executes a query based on a given example domain object.
     * 
     * @param exampleInstance
     *            An instance of the desired domain object, serving as example
     *            for "query-by-example"
     * @return A list containing 0 or more domain objects
     */
    List<T> findByExample(T exampleInstance);

    /**
     * Executes a query based on a given query object.
     * 
     * @param q
     *            The search query object
     * @return A list containing 0 or more domain objects
     */
    List<T> findByQuery(QueryObject q);

    /**
     * Saves or updates the given domain object.
     * 
     * @param entity
     *            The domain object to save or update
     * @return The saved or updated domain object
     */
    T saveOrUpdate(T entity);

    /**
     * Deletes the domain object with the given id.
     * 
     * @param id
     *            The id of the domain object to delete
     */
    void delete(ID id);

}