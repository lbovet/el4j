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

import static ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier
    .FUZZY_CHANGE;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.dao.OptimisticLockingFailureException;

import ch.elca.el4j.services.persistence.generic.DaoAgency;
import ch.elca.el4j.services.persistence.generic.DaoAgent;
import ch.elca.el4j.services.persistence.generic.HierarchyDaoChangeNotifier;
import ch.elca.el4j.services.persistence.generic.dao.AbstractIdentityFixer;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;
import ch.elca.el4j.services.persistence.generic.dao.IdentityFixedDao;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.Change;
import ch.elca.el4j.services.persistence.generic.dao.DaoChangeNotifier.EntityDeleted;

/**
 * Wraps a DAO registry's daos with {@link DaoAgent}.
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
public class DefaultDaoAgency 
    implements DaoAgency {

    /**
     * The backing registry.
     */
    protected DaoRegistry m_backing;
    
    /**
     * The identity fixer to be injected.
     */
    protected AbstractIdentityFixer.GenericInterceptor m_identityFixerAdvice;
    
    /** 
     * The already created agents.
     */
    protected Map<Class<?>, DaoAgent<?>> m_agents 
        = new HashMap<Class<?>, DaoAgent<?>>();
    
    
    /**
     * Constructor.
     * 
     * @param ifixer The identity fixer to be injected
     * @param backing The backing registry
     */
    public DefaultDaoAgency(AbstractIdentityFixer ifixer,
                                                DaoRegistry backing) {
        m_backing = backing;
        ifixer.getChangeNotifier().subscribe(this);
        m_identityFixerAdvice 
            = ifixer.new GenericInterceptor(IdentityFixedDao.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> DaoAgent<T> getFor(Class<T> entityType) {
        DaoAgent<T> agent
            = (DaoAgent<T>)
                m_agents.get(entityType);
        if (agent == null) {
            GenericDao target = m_backing.getFor(entityType);
            ProxyFactory pf = new ProxyFactory(target);
            pf.addInterface(DaoAgent.class);
            pf.setProxyTargetClass(false);
            pf.addAdvice(
                new AgentIntroducer(
                    new DefaultHierarchyDaoChangeNotifier<T>(entityType)
                )
            );
            pf.addAdvice(m_identityFixerAdvice);
            
            agent = (DaoAgent<T>) pf.getProxy();
            m_agents.put(entityType, agent);
        }
        return agent;
    }

    /** 
     * Asks all DAOs to announce this change if they are responsible.
     * 
     * @param change The change to be announced
     */
    public void changed(Change change) {
        for (DaoAgent<?> w : m_agents.values()) {
            w.announceIfResponsible(change);
        }
    }
    
    /**
     * Introduces an agent that automatically reloads beans if a concurrent
     * modification is detected.
     * 
     * @see DaoAgent
     */
    protected class AgentIntroducer
            extends DelegatingIntroductionInterceptor {
        
        /**
         * Constructor.
         * @param cn Change notifier to intercept the client side proxy.
         */
        @SuppressWarnings("unchecked")
        public AgentIntroducer(HierarchyDaoChangeNotifier cn) {
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
                        ed.setChangee(invocation.getArguments()[0]);
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