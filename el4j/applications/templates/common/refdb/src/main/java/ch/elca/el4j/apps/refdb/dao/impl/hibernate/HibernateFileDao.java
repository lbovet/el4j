package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 *
 * DAO for files which is using Hibernate.
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
@AutocollectedGenericDao("fileDao")
public class HibernateFileDao extends GenericHibernateFileDao<File, Integer>
	implements GenericHibernateFileDaoInterface {
	

	/***  Predefined Fetching extents ***/
	
	/**
	 * Light fetching variant: only fetch header data, no content.
	 * @return the light fetch extent.
	 */
	public static final DataExtent HEADER
		= new DataExtent(File.class).all().without("data", "content");
	
	/**
	 * Heavy fetching variant: fetch the whole file, including content.
	 * @return the heavy fetch extent.
	 */
	public static final DataExtent ALL
		= new DataExtent(File.class).all();
	

}
