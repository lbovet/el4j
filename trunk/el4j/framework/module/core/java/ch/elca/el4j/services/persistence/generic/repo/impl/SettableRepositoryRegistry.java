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
package ch.elca.el4j.services.persistence.generic.repo.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.persistence.generic.repo.RepositoryRegistry;
import ch.elca.el4j.services.persistence.generic.repo.SimpleGenericRepository;

/**
 * A RepositoryRegistry where repositories can be registered. This class
 * also provides an infrastructure to inject depedencies and initialize the
 * registered repositories.
 * 
 * @param <R> The type of repositories managed by this class.
 * 
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
public class SettableRepositoryRegistry<R extends SimpleGenericRepository<?>> 
    implements RepositoryRegistry, InitializingBean {
    
    /** The already created repositories. */
    private Map<Class<?>, R> m_repositories
        = new HashMap<Class<?>, R>();
    
    /** Injects depedencies into rep. */
    protected void injectInto(R rep) { }
    
    /**
     * Registers the passed repository.
     */
    public void register(R rep) {
        m_repositories.put(rep.getPersistentClass(), rep);        
    }
    
    /**
     * Registers the passed repositories.
     */
    public void register(R... reps) {
        for (R rep : reps) {
            register(rep);
        }
    }
    
    public void setRepos(R... reps) {
        register(reps);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> SimpleGenericRepository<T> getFor(Class<T> entityType) {
        return (SimpleGenericRepository<T>) m_repositories.get(entityType);
    }

    public void afterPropertiesSet() throws Exception {
        for (R rep : m_repositories.values()) {
            injectInto(rep);
            
            // TODO provide better BeanFactory illusion
            if (rep instanceof InitializingBean) {
                ((InitializingBean) rep).afterPropertiesSet();
            }
        }
    }
}
