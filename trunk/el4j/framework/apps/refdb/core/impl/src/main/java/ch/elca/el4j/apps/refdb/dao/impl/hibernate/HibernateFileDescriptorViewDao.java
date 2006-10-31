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

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.elca.el4j.apps.refdb.dao.FileDescriptorViewDao;
import ch.elca.el4j.apps.refdb.dom.FileDescriptorView;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
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
