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
package ch.elca.el4j.apps.refdb.dao.impl.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;

import ch.elca.el4j.apps.keyword.dao.impl.hibernate.HibernateKeywordDao;
import ch.elca.el4j.apps.refdb.Constants;
import ch.elca.el4j.apps.refdb.dao.ReferenceDao;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.FormalPublicationDto;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException;
import ch.elca.el4j.services.persistence.hibernate.criteria.CriteriaTransformer;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * 
 * Implementation of the reference DAO using Hibernate.
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
public class HibernateReferenceDao extends HibernateKeywordDao implements
    ReferenceDao {

    /**
     * {@inheritDoc}
     */
    public AnnotationDto getAnnotationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        return (AnnotationDto) getConvenienceHibernateTemplate()
            .getByIdStrong(AnnotationDto.class, key, Constants.ANNOTATION);
    }

    /**
     * {@inheritDoc}
     */
    public List getAnnotationsByAnnotator(String annotator)
        throws DataAccessException {
        Reject.ifEmpty(annotator);
        String queryString
            = "from AnnotationDto annotation where annotator = :annotator";
        return getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "annotator", annotator);
    }

    /**
     * {@inheritDoc}
     */
    public List getAnnotationsByReference(int key) throws DataAccessException {
        String queryString
            = "from AnnotationDto annotation where keyToReference = :key";
        return getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "key", new Integer(key));
    }

    /**
     * {@inheritDoc}
     */
    public List getAllAnnotations() throws DataAccessException {
        return getConvenienceHibernateTemplate().find("from AnnotationDto");
    }

    /**
     * {@inheritDoc}
     */
    public AnnotationDto saveAnnotation(AnnotationDto annotation)
        throws DataAccessException, InsertionFailureException,
        OptimisticLockingFailureException {
        Reject.ifNull(annotation);
        getConvenienceHibernateTemplate().saveOrUpdateStrong(annotation,
            Constants.ANNOTATION);
        return annotation;
    }

    /**
     * {@inheritDoc}
     */
    public void removeAnnotation(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceHibernateTemplate().deleteStrong(AnnotationDto.class, key,
            Constants.ANNOTATION);
    }

    /**
     * {@inheritDoc}
     */
    public FileDto getFileByKey(int key) throws DataAccessException,
        DataRetrievalFailureException {
        return (FileDto) getConvenienceHibernateTemplate()
            .getByIdStrong(FileDto.class, key, Constants.FILE);
    }
    
    /**
     * {@inheritDoc}
     */
    public List getFilesByName(String name) throws DataAccessException {
        Reject.ifEmpty(name);
        String queryString
            = "from FileDto file where name = :name";
        return getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
    }

    /**
     * {@inheritDoc}
     */
    public List getFilesByReference(int key) throws DataAccessException {
        String queryString
            = "from FileDto file where keyToReference = :key";
        return getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "key", new Integer(key));
    }

    /**
     * {@inheritDoc}
     */
    public List getAllFiles() throws DataAccessException {
        return getConvenienceHibernateTemplate().find("from FileDto");
    }

    /**
     * {@inheritDoc}
     */
    public FileDto saveFile(FileDto file) throws DataAccessException,
        InsertionFailureException, OptimisticLockingFailureException {
        Reject.ifNull(file);
        getConvenienceHibernateTemplate().saveOrUpdateStrong(file,
            Constants.FILE);
        return file;
    }

    /**
     * {@inheritDoc}
     */
    public void removeFile(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceHibernateTemplate().deleteStrong(FileDto.class, key,
            Constants.FILE);
    }

    /**
     * {@inheritDoc}
     */
    public List getFileDescriptorViewsByReference(int key)
        throws DataAccessException {
        String queryString
            = "from FileDescriptorView fileDescriptorView "
                + "where keyToReference = :key";
        return getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "key", new Integer(key));
    }

    /**
     * {@inheritDoc}
     */
    public FileDescriptorView modifyFileDescriptorView(
        FileDescriptorView fileView) throws DataAccessException,
        OptimisticLockingFailureException, DataRetrievalFailureException {
        Reject.ifNull(fileView);
        if (fileView.isKeyNew()) {
            // File must not be new!
            CoreNotificationHelper.notifyDataRetrievalFailure(
                Constants.FILE_DESCRIPTOR_VIEW);
        }
        getConvenienceHibernateTemplate().saveOrUpdateStrong(
            fileView, Constants.FILE_DESCRIPTOR_VIEW);
        return fileView;
    }

    /**
     * {@inheritDoc}
     */
    public LinkDto getLinkByKey(int key) throws DataAccessException,
        DataRetrievalFailureException {
        LinkDto link = (LinkDto) getConvenienceHibernateTemplate()
            .getByIdStrong(LinkDto.class, key, Constants.LINK);
        getConvenienceHibernateTemplate().initialize(link.getKeywords());    
        return link;
    }
    
    /**
     * {@inheritDoc}
     */
    public List getLinksByName(String name) throws DataAccessException {
        Reject.ifEmpty(name);
        String queryString = "from LinkDto link where name = :name";
        List result = getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
        Iterator it = result.iterator();
        while (it.hasNext()) {
            getConvenienceHibernateTemplate().initialize(((LinkDto) it.next())
                .getKeywords());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List getAllLinks() throws DataAccessException {
        return getConvenienceHibernateTemplate().find("from LinkDto link "
            + "left join fetch link.keywords");
    }

    /**
     * {@inheritDoc}
     */
    public List searchLinks(QueryObject query) throws DataAccessException {
        DetachedCriteria hibernateCriteria = CriteriaTransformer
            .transform(query,
            LinkDto.class);

        // HACK! IncludeCriteria are handled in the search method. In a future
        // version, this will be replaced by an easier solution, where the
        // IncludeCriteria will be treated by the CriteriaTransformer class.
        
        List criteriaList = query.getCriteriaList();

        if (containsIncludeCriteria(criteriaList)) {

            Iterator it = criteriaList.iterator();
            AbstractCriteria currentCriterion;
            List resultList = new ArrayList();
            List currentList;

            while (it.hasNext()) {
                currentCriterion = (AbstractCriteria) it.next();
                if (currentCriterion instanceof IncludeCriteria) {
                    hibernateCriteria.createCriteria(
                        ((IncludeCriteria) currentCriterion).getField()).add(
                            Expression.eq("key",
                                ((IncludeCriteria) currentCriterion)
                                    .getIntegerValue()));
                    currentList = getConvenienceHibernateTemplate()
                        .findByCriteria(hibernateCriteria);
                    resultList.add(currentList);
                    hibernateCriteria = CriteriaTransformer.transform(query,
                        LinkDto.class);
                }
            }
            Iterator it2 = resultList.iterator();
            currentList = (List) it2.next();
            List nextList = new ArrayList();
            while (it2.hasNext()) {
                nextList = (List) it2.next();
                currentList.retainAll(nextList);
            }
            return currentList;

        // Executed if the query does not include any IncludeCriteria    
        } else {
            return getConvenienceHibernateTemplate().findByCriteria(
                hibernateCriteria);
        }
    }

    /**
     * {@inheritDoc}
     */
    public LinkDto saveLink(LinkDto link) throws DataAccessException,
        InsertionFailureException, OptimisticLockingFailureException {
        Reject.ifNull(link);
        getConvenienceHibernateTemplate().saveOrUpdateStrong(link,
            Constants.LINK);
        return link;
    }

    /**
     * {@inheritDoc}
     */
    public void removeLink(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceHibernateTemplate().deleteStrong(LinkDto.class, key,
            Constants.LINK);

    }

    /**
     * {@inheritDoc}
     */
    public FormalPublicationDto getFormalPublicationByKey(int key)
        throws DataAccessException, DataRetrievalFailureException {
        FormalPublicationDto formalPublication
            = (FormalPublicationDto) getConvenienceHibernateTemplate()
                .getByIdStrong(FormalPublicationDto.class, key,
                    Constants.FORMAL_PUBLICATION);
        getConvenienceHibernateTemplate().initialize(formalPublication
            .getKeywords());    
        return formalPublication;
    }
    
    /**
     * {@inheritDoc}
     */
    public List getFormalPublicationsByName(String name)
        throws DataAccessException {
        Reject.ifEmpty(name);
        String queryString = "from FormalPublicationDto formalPublication "
            + "where name = :name";
        List result = getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
        Iterator it = result.iterator();
        FormalPublicationDto currentPublication;
        List finalResult = new ArrayList();
        while (it.hasNext()) {
            currentPublication = (FormalPublicationDto) it.next();
            if (!(currentPublication instanceof BookDto)) {
                getConvenienceHibernateTemplate().initialize(currentPublication
                    .getKeywords());
                finalResult.add(currentPublication);
            } 
        }
        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public List getAllFormalPublications() throws DataAccessException {
        List result = getConvenienceHibernateTemplate()
            .find("from FormalPublicationDto formalPublication left join "
                + "fetch formalPublication.keywords");
        
        Iterator it = result.iterator();
        FormalPublicationDto currentPublication;
        List finalResult = new ArrayList();
        while (it.hasNext()) {
            currentPublication = (FormalPublicationDto) it.next();
            if (!(currentPublication instanceof BookDto)) {
                finalResult.add(currentPublication);
            } 
        }
        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public List searchFormalPublications(QueryObject query)
        throws DataAccessException {
        
        DetachedCriteria hibernateCriteria = CriteriaTransformer
            .transform(query, FormalPublicationDto.class);

        // HACK! IncludeCriteria are handled in the search method. In a future
        // version, this will be replaced by an easier solution, where the
        // IncludeCriteria will be treated by the CriteriaTransformer class.
        
        List criteriaList = query.getCriteriaList();
        List currentList = new ArrayList();

        if (containsIncludeCriteria(criteriaList)) {

            Iterator it = criteriaList.iterator();
            AbstractCriteria currentCriterion;
            List resultList = new ArrayList();

            while (it.hasNext()) {
                currentCriterion = (AbstractCriteria) it.next();
                if (currentCriterion instanceof IncludeCriteria) {
                    hibernateCriteria.createCriteria(
                        ((IncludeCriteria) currentCriterion).getField()).add(
                            Expression.eq("key",
                                ((IncludeCriteria) currentCriterion)
                                    .getIntegerValue()));
                    currentList = getConvenienceHibernateTemplate()
                        .findByCriteria(hibernateCriteria);
                    resultList.add(currentList);
                    hibernateCriteria = CriteriaTransformer.transform(query,
                        FormalPublicationDto.class);
                }
            }
            Iterator it2 = resultList.iterator();
            currentList = (List) it2.next();
            List nextList = new ArrayList();
            while (it2.hasNext()) {
                nextList = (List) it2.next();
                currentList.retainAll(nextList);
            }
            
        // Executed if the query does not include any IncludeCriteria
        } else {
            currentList = getConvenienceHibernateTemplate().findByCriteria(
                hibernateCriteria);
        }

        Iterator it3 = currentList.iterator();
        FormalPublicationDto currentPublication;
        List finalResult = new ArrayList();
        while (it3.hasNext()) {
            currentPublication = (FormalPublicationDto) it3.next();
            if (!(currentPublication instanceof BookDto)) {
                finalResult.add(currentPublication);
            }
        }
        return finalResult;
    }

    /**
     * {@inheritDoc}
     */
    public FormalPublicationDto saveFormalPublication(
        FormalPublicationDto formalPublication) throws DataAccessException,
        InsertionFailureException, OptimisticLockingFailureException {
        Reject.ifNull(formalPublication);
        getConvenienceHibernateTemplate().saveOrUpdateStrong(formalPublication,
            Constants.FORMAL_PUBLICATION);
        return formalPublication;
    }

    /**
     * {@inheritDoc}
     */
    public void removeFormalPublication(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceHibernateTemplate().deleteStrong(
            FormalPublicationDto.class, key, Constants.FORMAL_PUBLICATION);
    }

    /**
     * {@inheritDoc}
     */
    public BookDto getBookByKey(int key) throws DataAccessException,
        DataRetrievalFailureException {
        BookDto book
            = (BookDto) getConvenienceHibernateTemplate()
                .getByIdStrong(BookDto.class, key,
                    Constants.BOOK);
        getConvenienceHibernateTemplate().initialize(book.getKeywords());    
        return book;    
    }
    
    /**
     * {@inheritDoc}
     */
    public List getBooksByName(String name) throws DataAccessException {
        Reject.ifEmpty(name);
        String queryString = "from BookDto book where name = :name";
        List result = getConvenienceHibernateTemplate()
            .findByNamedParam(queryString, "name", name);
        Iterator it = result.iterator();
        while (it.hasNext()) {
            getConvenienceHibernateTemplate().initialize(((BookDto) it.next()).
                getKeywords());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List getAllBooks() throws DataAccessException {
        return getConvenienceHibernateTemplate().find("from BookDto book "
            + "left join fetch book.keywords");
    }

    /**
     * {@inheritDoc}
     */
    public List searchBooks(QueryObject query) throws DataAccessException {
        
        DetachedCriteria hibernateCriteria = CriteriaTransformer
            .transform(query, BookDto.class);

        // HACK! IncludeCriteria are handled in the search method. In a future
        // version, this will be replaced by an easier solution, where the
        // IncludeCriteria will be treated by the CriteriaTransformer class.
        
        List criteriaList = query.getCriteriaList();

        // Handle IncludeCriteria
        if (containsIncludeCriteria(criteriaList)) {

            Iterator it = criteriaList.iterator();
            AbstractCriteria currentCriterion;
            List resultList = new ArrayList();
            List currentList;

            while (it.hasNext()) {
                currentCriterion = (AbstractCriteria) it.next();
                if (currentCriterion instanceof IncludeCriteria) {
                    hibernateCriteria.createCriteria(
                        ((IncludeCriteria) currentCriterion).getField()).add(
                            Expression.eq("key",
                                ((IncludeCriteria) currentCriterion)
                                    .getIntegerValue()));
                    currentList = getConvenienceHibernateTemplate()
                        .findByCriteria(hibernateCriteria);
                    resultList.add(currentList);
                    hibernateCriteria = CriteriaTransformer.transform(query,
                        BookDto.class);
                }
            }
            Iterator it2 = resultList.iterator();
            currentList = (List) it2.next();
            List nextList = new ArrayList();
            while (it2.hasNext()) {
                nextList = (List) it2.next();
                currentList.retainAll(nextList);
            }
            return currentList;

        // Executed if the query does not include any IncludeCriteria     
        } else {
            return getConvenienceHibernateTemplate().findByCriteria(
                hibernateCriteria);
        }
    }

    /**
     * {@inheritDoc}
     */
    public BookDto saveBook(BookDto book) throws DataAccessException,
        InsertionFailureException, OptimisticLockingFailureException {
        Reject.ifNull(book);
        getConvenienceHibernateTemplate().saveOrUpdateStrong(book,
            Constants.BOOK);
        return book;
    }

    /**
     * {@inheritDoc}
     */
    public void removeBook(int key) throws DataAccessException,
        JdbcUpdateAffectedIncorrectNumberOfRowsException {
        getConvenienceHibernateTemplate().deleteStrong(BookDto.class, key,
            Constants.BOOK);
    }

    /**
     * Checkas whether the given list contains at least one IncludeCriteria. 
     * @param criteriaList list of criteria
     * @return true if the list contains at least one IncludeCriteria
     */
    private boolean containsIncludeCriteria(List criteriaList) {
        Iterator it = criteriaList.iterator();
        while (it.hasNext()) {
            if (it.next() instanceof IncludeCriteria) {
                return true;
            }
        }
        return false;
    }
    
}
