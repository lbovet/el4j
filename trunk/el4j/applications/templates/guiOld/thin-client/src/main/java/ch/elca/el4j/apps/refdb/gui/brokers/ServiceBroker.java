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
package ch.elca.el4j.apps.refdb.gui.brokers;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.apps.refdb.dao.AnnotationDao;
import ch.elca.el4j.apps.refdb.dao.BookDao;
import ch.elca.el4j.apps.refdb.dao.FileDao;
import ch.elca.el4j.apps.refdb.dao.FileDescriptorViewDao;
import ch.elca.el4j.apps.refdb.dao.FormalPublicationDao;
import ch.elca.el4j.apps.refdb.dao.LinkDao;
import ch.elca.el4j.apps.refdb.dom.Annotation;
import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.File;
import ch.elca.el4j.apps.refdb.dom.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dom.FormalPublication;
import ch.elca.el4j.apps.refdb.dom.Link;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.utils.Services;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

/**
 * Broker for services and DAOs.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Alex Mathey (AMA)
 */
public class ServiceBroker {
    /**
     * Is the cached reference service.
     */
    private static ReferenceService s_cachedReferenceService = null;
    
    /**
     * Is the cached keyword DAO.
     */
    private static KeywordDao s_cachedKeywordDao = null;
    
    /**
     * Is the cached link DAO.
     */
    private static LinkDao s_cachedLinkDao = null;
    
    /**
     * Is the cached formal publication DAO.
     */
    private static FormalPublicationDao s_cachedFormalPublicationDao = null;
    
    /**
     * Is the cached book DAO.
     */
    private static BookDao s_cachedBookDao = null;
    
    /**
     * Is the cached annotation DAO.
     */
    private static AnnotationDao s_cachedAnnotationDao = null;
    
    /**
     * Is the cached file DAO.
     */
    private static FileDao s_cachedFileDao = null;
    
    /**
     * Is the cached file descriptor view DAO.
     */
    private static FileDescriptorViewDao s_cachedFileDescriptorViewDao = null;
    
    /**
     * Hide default constructor.
     */
    protected ServiceBroker() { }
    
    
    /**
     * @return Returns the cached reference service.
     * @see #getReferenceService(boolean)
     */
    public static synchronized ReferenceService getReferenceService() {
        return getReferenceService(true);
    }
    
    /**
     * Returns an instance of the reference service. If caching is on always the
     * same instance will be returned.
     * 
     * @param allowCaching
     *            Flags if it is allowed to cache the reference service.
     * @return Returns a new or the cached reference service.
     */
    public static synchronized ReferenceService getReferenceService(
        boolean allowCaching) {
        ReferenceService referenceService = null;
        if (s_cachedReferenceService == null || !allowCaching) {
            referenceService = Services.getBean(
                "referenceService", ReferenceService.class
            ); 
        } else {
            referenceService = s_cachedReferenceService;
        }
        
        if (allowCaching) {
            s_cachedReferenceService = referenceService;
        } else {
            s_cachedReferenceService = null;
        }
        return referenceService;
    }

    /**
     * Returns the generic DAO for entities of type {@code entityType}.
     * 
     * @param entityType
     *            The domain class for which a generic DAO will be returned
     * @param allowCaching
     *            Flags if it is allowed to cache the DAO
     * @param cachedDao
     *            Is the cached DAO for the given domain class
     * @return A generic DAO for the given type
     */
    public static synchronized GenericDao<?> getDaoFor(Class<?> entityType,
        boolean allowCaching, GenericDao<?> cachedDao) {
        
        GenericDao<?> dao = null;
        DaoRegistry registry = null;
        if (cachedDao == null || !allowCaching) {
            registry = Services.getBean("daoRegistry", DaoRegistry.class);
            dao = registry.getFor(entityType);
        } else {
            dao = cachedDao;
        }
        
        if (allowCaching) {
            cachedDao = dao;
        } else {
            cachedDao = null;
        }
        return dao;
    }
    
    /**
     * @return Returns the cached keyword DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized KeywordDao getKeywordDao() {
        return (KeywordDao) getDaoFor(Keyword.class, true, s_cachedKeywordDao);
    }
    
    /**
     * @return Returns the cached link DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized LinkDao getLinkDao() {
        return (LinkDao) getDaoFor(Link.class, true, s_cachedLinkDao);
    }
    
    /**
     * @return Returns the cached formal publication DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized FormalPublicationDao getFormalPublicationDao() {
        return (FormalPublicationDao) getDaoFor(FormalPublication.class, true,
            s_cachedFormalPublicationDao);   
    }
    
    /**
     * @return Returns the cached book DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized BookDao getBookDao() {
        return (BookDao) getDaoFor(Book.class, true, s_cachedBookDao);   
    }
    
    /**
     * @return Returns the cached annotation DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized AnnotationDao getAnnotationDao() {
        return (AnnotationDao) getDaoFor(Annotation.class, true, 
            s_cachedAnnotationDao);   
    }
    
    /**
     * @return Returns the cached file DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized FileDao getFileDao() {
        return (FileDao) getDaoFor(File.class, true, s_cachedFileDao);   
    }
    
    /**
     * @return Returns the cached file descriptor view DAO.
     * @see #getDaoFor(Class, boolean, GenericDao)
     */
    public static synchronized FileDescriptorViewDao getFileFileDescriptorView()
    {
        return (FileDescriptorViewDao) getDaoFor(FileDescriptorView.class,
            true, s_cachedFileDescriptorViewDao);
    }
    
}
