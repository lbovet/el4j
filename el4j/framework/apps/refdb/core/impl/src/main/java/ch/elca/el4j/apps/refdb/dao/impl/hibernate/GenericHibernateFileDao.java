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
package ch.elca.el4j.apps.refdb.dao.impl.hibernate;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.GenericFileDao;
import ch.elca.el4j.util.codingsupport.Reject;


/**
 * Generic DAO for files or file descriptors which is using
 * Hibernate.
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
 * @author Alex Mathey (AMA)
 */
public class GenericHibernateFileDao<T, ID extends Serializable> 
    extends GenericHibernateReferencedObjectDao<T, ID>
    implements GenericFileDao<T, ID> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<T> getByName(String name) throws DataAccessException,
        DataRetrievalFailureException {
        Reject.ifEmpty(name);
        String domainClassName = getPersistentClassName();
        String queryString = "from " + domainClassName + " " 
            + domainClassName.toLowerCase() + " where name = :name"; 
        List<T> result = getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
        return result;
    }
    
}

        
    

