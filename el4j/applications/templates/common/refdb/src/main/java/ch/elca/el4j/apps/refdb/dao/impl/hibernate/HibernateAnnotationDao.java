package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * DAO for annotations which is using Hibernate.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Alex Mathey (AMA)
 */
@Repository("annotationDao")
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
