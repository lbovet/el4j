package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;

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
    implements FileDao {

    /**
     * Creates a new HibernateFileDao instance.
     */
    public HibernateFileDao() {
        setPersistentClass(File.class);
    }
    
}
