package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;

/**
 *
 * DAO for files which is using Hibernate.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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
		= new DataExtent(File.class).all().without("data", "content").freeze();
	
	/**
	 * Heavy fetching variant: fetch the whole file, including content.
	 * @return the heavy fetch extent.
	 */
	public static final DataExtent ALL
		= new DataExtent(File.class).all().freeze();
	

}
