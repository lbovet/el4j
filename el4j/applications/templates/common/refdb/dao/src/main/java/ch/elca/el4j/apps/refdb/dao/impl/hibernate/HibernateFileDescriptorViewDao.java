package ch.elca.el4j.apps.refdb.dao.impl.hibernate;


import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.FileDescriptorViewDao;
import ch.elca.el4j.apps.refdb.dom.FileDescriptorView;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * DAO for file descriptors which is using Hibernate.
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
@AutocollectedGenericDao("fileDescriptorViewDao")
public class HibernateFileDescriptorViewDao
    extends GenericHibernateFileDao<FileDescriptorView, Integer>
        implements FileDescriptorViewDao {
    
    /**
     * Creates a new HibernateFileDescriptorViewDao instance.
     */
    public HibernateFileDescriptorViewDao() {
        setPersistentClass(FileDescriptorView.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public FileDescriptorView modifyFileDescriptorView(
        FileDescriptorView fileView) throws DataAccessException,
        OptimisticLockingFailureException, DataRetrievalFailureException {
        Reject.ifNull(fileView);
        if (fileView.isKeyNew()) {
            // File must not be new!
            CoreNotificationHelper.notifyDataRetrievalFailure(
                getPersistentClassName());
        }
        getConvenienceHibernateTemplate().saveOrUpdateStrong(
            fileView, getPersistentClassName());
        return fileView;
    }
    
}
