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

import ch.elca.el4j.apps.refdb.dao.GenericReferenceDao;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * Generic DAO for references which is using Hibernate.
 * 
 * This DAO is not intended to be used directly. Only the concrete DAOs that are
 * subclasses of this generic DAO should be used directly. To access or modify 
 * reference objects which can be of different types, the service layer should
 * be used.
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
public class GenericHibernateReferenceDao<T extends Reference,
    ID extends Serializable> extends GenericHibernateDao<T, ID> 
    implements GenericReferenceDao<T, ID> {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() throws DataAccessException {
        String domainClassName = getPersistentClassName();
        String domainClassNameLowerCase = domainClassName.toLowerCase();
        String queryString = "from " + domainClassName + " "
            + domainClassNameLowerCase + " left join fetch "
            + domainClassNameLowerCase + ".keywords";
        return getConvenienceHibernateTemplate().find(queryString);
    }
    
    
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
        for (T reference : result) {
            getConvenienceHibernateTemplate().initialize(
                reference.getKeywords());
        }
        return result;
    }
    
    /**
     * Checks whether the given list contains at least one IncludeCriteria. 
     * @param criteriaList list of criteria
     * @return true if the list contains at least one IncludeCriteria
     */
    protected boolean containsIncludeCriteria(List<AbstractCriteria>
        criteriaList) {
        for (AbstractCriteria c : criteriaList) {
            if (c instanceof IncludeCriteria) {
                return true;
            }
        }
        return false;
    }
    
   /**
    * {@inheritDoc}
    */
    @SuppressWarnings("unchecked")
    public boolean referenceExists(ID id) {
        String domainClassName = getPersistentClassName();
        String domainClassNameLowerCase = domainClassName.toLowerCase();
        String queryString = "select " + domainClassNameLowerCase + ".id from "
            + domainClassName + " " + domainClassNameLowerCase;  
        List<ID> idList = getConvenienceHibernateTemplate().find(queryString);
        if (idList.contains(id)) {
            return true;
        }
        return false;
    }
    
}
