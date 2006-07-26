/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.persistence.generic.impl;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.HierarchyRepositoryChangeNotifier;
import ch.elca.el4j.services.persistence.generic.LazyRepositoryWatcher;
import ch.elca.el4j.services.persistence.generic.LazyRepositoryWatcherRegistry;
import ch.elca.el4j.services.persistence.generic.repo.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.repo.IdentityFixedRepository;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.Change;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.EntityDeleted;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryRegistry;

import static ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.FUZZY_CHANGE;

/**
 * Wraps a repository registry's repositories with 
 * {@link LazyRepositoryWatcher}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class DefaultLazyRepositoryWatcherRegistry 
    implements LazyRepositoryWatcherRegistry {

    /**
     * The backing registry.
     */
    protected RepositoryRegistry m_backing;
    
    /**
     * The identity fixer to be injected.
     */
    protected AbstractIdentityFixer.GenericInterceptor m_identityFixerAdvice;
    
    /** 
     * The already created repository watchers.
     */
    protected Map<Class<?>, LazyRepositoryWatcher<?>> m_repWatchers 
        = new HashMap<Class<?>, LazyRepositoryWatcher<?>>();
    
    
    /**
     * Constructor.
     * @param backing the backing registry.
     */
    public DefaultLazyRepositoryWatcherRegistry(AbstractIdentityFixer ifixer,
                                                RepositoryRegistry backing) {
        m_backing = backing;
        ifixer.getChangeNotifier().subscribe(this);
        m_identityFixerAdvice 
            = ifixer.new GenericInterceptor(IdentityFixedRepository.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> LazyRepositoryWatcher<T> getFor(Class<T> entityType) {
        LazyRepositoryWatcher<T> rep
            = (LazyRepositoryWatcher<T>)
                m_repWatchers.get(entityType);
        if (rep == null) {
            ProxyFactory pf = new ProxyFactory();
            pf.setProxyTargetClass(true);
            pf.addAdvice(
                new WatcherIntroducer(
                    LazyRepositoryWatcher.class,
                    new DefaultHierarchyRepositoryChangeNotifier<T>(entityType)
                )
            );
            pf.addAdvice(m_identityFixerAdvice);
            pf.setTarget(m_backing.getFor(entityType));
            
            rep = (LazyRepositoryWatcher<T>) pf.getProxy();
            m_repWatchers.put(entityType, rep);
        }
        return rep;
    }

    /** 
     * Asks all repositories to announce this change if they are responsible.
     */
    public void process(Change change) {
        for (LazyRepositoryWatcher<?> w : m_repWatchers.values()) {
            w.announceIfResponsible(change);
        }
    }
    
    /**
     * Introduces a watcher that automatically reloads beans if a concurrent
     * modification is detected.
     */
    protected class WatcherIntroducer
            extends DelegatingIntroductionInterceptor {
        
        /**
         * Constructor.
         * @param intf The interface to be introduced.
         */
        @SuppressWarnings("unchecked")
        public WatcherIntroducer(
                Class<? extends LazyRepositoryWatcher> intf,
                HierarchyRepositoryChangeNotifier cn) {
            super(cn);
            publishedInterfaces.add(intf);      
        }
        
        /** {@inheritDoc} */
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (isMethodOnIntroducedInterface(invocation)) {
                return super.invoke(invocation);
            } else {
                try {
                    Object retVal = invocation.proceed();
                    // TODO: fire notifications for cascadingly deleted entities
                    //       and generify detection of delete methods.
                    //       (have them return the set of deleted objects?)
                    if ("delete".equals(invocation.getMethod().getName())) {
                        EntityDeleted ed = new EntityDeleted();
                        ed.changee = invocation.getArguments()[0];
                        process(ed);
                    }
                    return retVal;
                } catch (OptimisticLockingFailureException e) {
                    process(FUZZY_CHANGE);
                    throw e;
                }
            }
        }
    }
}