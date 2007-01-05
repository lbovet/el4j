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
package ch.elca.el4j.addressbook.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import ch.elca.el4j.addressbook.dom.Contact;
import ch.elca.el4j.services.persistence.generic.dao.ConvenienceGenericDao;

/**
 * 
 * This interface represents a DAO for the keyword domain object.
 * It defines the methods which are specific to the keyword domain object. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public interface ContactDao
    extends ConvenienceGenericDao<Contact, Integer> {
    
    /**
     * Get contact by lastName.
     * 
     * @param lastname
     *            Is the name of a keyword.
     * @return Returns the desired keyword.
     * @throws DataAccessException
     *             If general data access problem occurred.
     * @throws DataRetrievalFailureException
     *             If keyword could not be retrieved.
     */
    public Contact getContactByLastName(String lastname)
        throws DataAccessException, DataRetrievalFailureException;
}
