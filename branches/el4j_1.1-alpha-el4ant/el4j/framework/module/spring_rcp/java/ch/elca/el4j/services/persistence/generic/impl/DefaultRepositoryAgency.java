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

import static ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.FUZZY_CHANGE;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.HierarchyRepositoryChangeNotifier;
import ch.elca.el4j.services.persistence.generic.RepositoryAgency;
import ch.elca.el4j.services.persistence.generic.RepositoryAgent;
import ch.elca.el4j.services.persistence.generic.repo.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.repo.IdentityFixedRepository;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryRegistry;
import ch.elca.el4j.services.persistence.generic.repo.SimpleGenericRepository;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.Change;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.EntityDeleted;

/**
 * Wraps a repository registry's repositories with 
 * {@link RepositoryAgent}.
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
public class DefaultRepositoryAgency 
    implements RepositoryAgency {

    /**
     * The backing registry.
     */
    protected RepositoryRegistry m_backing;
    
    /**
     * The identity fixer to be injected.
     */
    protected AbstractIdentityFixer.GenericInterceptor m_identityFixerAdvice;
    
    /** 
     * The already created agents.
     */
    protected Map<Class<?>, RepositoryAgent<?>> m_agents 
        = new HashMap<Class<?>, RepositoryAgent<?>>();
    
    
    /**
     * Constructor.
     * @param backing the backing registry.
     */
    public DefaultRepositoryAgency(AbstractIdentityFixer ifixer,
                                                RepositoryRegistry backing) {
        m_backing = backing;
        ifixer.getChangeNotifier().subscribe(this);
        m_identityFixerAdvice 
            = ifixer.new GenericInterceptor(IdentityFixedRepository.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> RepositoryAgent<T> getFor(Class<T> entityType) {
        RepositoryAgent<T> rep
            = (RepositoryAgent<T>)
                m_agents.get(entityType);
        if (rep == null) {
            SimpleGenericRepository target = m_backing.getFor(entityType);
            ProxyFactory pf = new ProxyFactory(target);
            pf.addInterface(RepositoryAgent.class);
            pf.setProxyTargetClass(false);
            pf.addAdvice(
                new AgentIntroducer(
                    new DefaultHierarchyRepositoryChangeNotifier<T>(entityType)
                )
            );
            pf.addAdvice(m_identityFixerAdvice);
            
            rep = (RepositoryAgent<T>) pf.getProxy();
            m_agents.put(entityType, rep);
        }
        return rep;
    }

    /** 
     * Asks all repositories to announce this change if they are responsible.
     */
    public void changed(Change change) {
        for (RepositoryAgent<?> w : m_agents.values()) {
            w.announceIfResponsible(change);
        }
    }
    
    /**
     * Introduces an agent that automatically reloads beans if a concurrent
     * modification is detected.
     * 
     * @see RepositoryAgent
     */
    protected class AgentIntroducer
            extends DelegatingIntroductionInterceptor {
        
        /**
         * Constructor.
         * @param cn Change notifier to intercept the client side proxy.
         */
        @SuppressWarnings("unchecked")
        public AgentIntroducer(HierarchyRepositoryChangeNotifier cn) {
            super(cn);
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
                        changed(ed);
                    }
                    return retVal;
                } catch (OptimisticLockingFailureException e) {
                    changed(FUZZY_CHANGE);
                    throw e;
                }
            }
        }
    }
}