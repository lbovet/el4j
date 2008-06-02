package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * DAO for annotations which is using Hibernate.
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
@AutocollectedGenericDao("annotationDao")
public class HibernateAnnotationDao
    extends GenericHibernateReferencedObjectDao<Annotation, Integer>
    implements AnnotationDao {
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Annotation> getAnnotationsByAnnotator(String annotator)
        throws DataAccessException {
        Reject.ifEmpty(annotator);
        String queryString = "from Annotation annotation where annotator "
            + "= :annotator";
        return getConvenienceHibernateTemplate().findByNamedParam(queryString,
            "annotator", annotator);
    }
    
}
