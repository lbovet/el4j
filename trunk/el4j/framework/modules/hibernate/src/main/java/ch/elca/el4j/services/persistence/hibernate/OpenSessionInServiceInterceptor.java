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
package ch.elca.el4j.services.persistence.hibernate;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This interceptor is similar to {@link OpenSessionInViewInterceptor}, but can
 * also be used e.g. for batch job processing where no HTTP requests are used.
 * Another use case is to take this class as a replacement for
 * {@link HibernateInterceptor}, now having more control over the flush mode and
 * a single session feature.
 * 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Pham Quoc Ky (QKP)
 */
public class OpenSessionInServiceInterceptor implements MethodInterceptor,
    Serializable {
    
    /**
     * The logger.
     */
    private static Log s_logger
        = LogFactory.getLog(OpenSessionInServiceInterceptor.class);
    
    /**
     * Is single session mode used?
     */
    private boolean m_singleSession = true;
    
    /**
     * The Hibernate session factory.
     */
    private SessionFactory m_sessionFactory;
    
    /**
     * The flushing stragtegy to use.
     */
    private FlushMode m_flushMode = FlushMode.COMMIT;

    
    /**
     * @return the singleSession
     */
    public boolean isSingleSession() {
        return m_singleSession;
    }

    /**
     * @param singleSession
     *            the singleSession to set
     */
    public void setSingleSession(boolean singleSession) {
        this.m_singleSession = singleSession;
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    /**
     * @param sessionFactory
     *            the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.m_sessionFactory = sessionFactory;
    }

    /**
     * @return the flushMode
     */
    public FlushMode getFlushMode() {
        return m_flushMode;
    }

    /**
     * @param flushMode
     *            the flushMode to set
     */
    public void setFlushMode(FlushMode flushMode) {
        this.m_flushMode = flushMode;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean participate = false;

        if (isSingleSession()) {
            // single session mode
            if (TransactionSynchronizationManager
                .hasResource(m_sessionFactory)) {
                // Do not modify the Session: just set the participate flag.
                participate = true;
            } else {
                s_logger.debug("Opening single Hibernate Session in "
                        + "OpenSessionInServiceInterceptor");
                Session session = getSession(m_sessionFactory);
                TransactionSynchronizationManager.bindResource(
                    m_sessionFactory, new SessionHolder(session));
            }
        } else {
            // deferred close mode
            if (SessionFactoryUtils.isDeferredCloseActive(m_sessionFactory)) {
                // Do not modify deferred close: just set the participate flag.
                participate = true;
            } else {
                SessionFactoryUtils.initDeferredClose(m_sessionFactory);
            }
        }

        try {
            return invocation.proceed();
        } finally {
            if (!participate) {
                if (isSingleSession()) {
                    // single session mode
                    SessionHolder sessionHolder = (SessionHolder)
                        TransactionSynchronizationManager
                            .unbindResource(m_sessionFactory);
                    s_logger.debug("Closing single Hibernate Session in "
                            + "OpenSessionInServiceInterceptor");
                    closeSession(sessionHolder.getSession());
                } else {
                    // deferred close mode
                    SessionFactoryUtils.processDeferredClose(m_sessionFactory);
                }
            }
        }
    }

    /**
     * Get a Session for the SessionFactory that this filter uses.
     * Note that this just applies in single session mode!
     * <p>
     * The default implementation delegates to the
     * <code>SessionFactoryUtils.getSession</code> method and sets the
     * <code>Session</code>'s flush mode to "NEVER".
     * <p>
     * Can be overridden in subclasses for creating a Session with a custom
     * entity interceptor or JDBC exception translator.
     * 
     * @param sessionFactory
     *            the SessionFactory that this filter uses
     * @return the Session to use
     * @throws DataAccessResourceFailureException
     *             if the Session could not be created
     * @see org.springframework.orm.hibernate3.SessionFactoryUtils#getSession(SessionFactory,
     *      boolean)
     * @see org.hibernate.FlushMode#COMMIT
     */
    protected Session getSession(SessionFactory sessionFactory)
        throws DataAccessResourceFailureException {
        
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        FlushMode flushMode = getFlushMode();
        if (flushMode != null) {
            session.setFlushMode(flushMode);
        }

        return session;
    }

    /**
     * Close the given Session. Note that this just applies in single
     * session mode!
     * <p>
     * Can be overridden in subclasses, e.g. for flushing the Session before
     * closing it. See class-level javadoc for a discussion of flush handling.
     * Note that you should also override getSession accordingly, to set the
     * flush mode to something else than NEVER.
     * 
     * @param session
     *            the Session used for filtering
     */
    protected void closeSession(Session session) {
        SessionFactoryUtils.closeSession(session);
    }
}
