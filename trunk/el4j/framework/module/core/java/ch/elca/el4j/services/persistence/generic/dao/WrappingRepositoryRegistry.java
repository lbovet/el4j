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
package ch.elca.el4j.services.persistence.generic.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * A proxy to another repository registry that wraps every repository on first
 * use.
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
public abstract class WrappingRepositoryRegistry
implements RepositoryRegistry {
    
    /**
     * The backing registry.
     */
    RepositoryRegistry m_backing;
    
    /** 
     * The already wrapped repositories.
     */
    Map<Class<?>, SimpleGenericRepository<?>> m_wrappedRepositories 
        = new HashMap<Class<?>, SimpleGenericRepository<?>>();
    
    /**
     * Constructor. 
     * @param backing the backing registry
     */
    public WrappingRepositoryRegistry(RepositoryRegistry backing) {
        m_backing = backing;
    }
    
    /**
     * Wraps the given repository. The wrapped repository must store entities
     * of the same type as the given one. 
     */
    protected abstract <T>
    SimpleGenericRepository<T> wrap(SimpleGenericRepository<T> repo);

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> SimpleGenericRepository<T> getFor(Class<T> entityType) {
        SimpleGenericRepository<T> rep
            = (SimpleGenericRepository<T>)
                m_wrappedRepositories.get(entityType);
        if (rep == null) {
            rep = wrap(m_backing.getFor(entityType));
        }
        return rep;
    }
}
