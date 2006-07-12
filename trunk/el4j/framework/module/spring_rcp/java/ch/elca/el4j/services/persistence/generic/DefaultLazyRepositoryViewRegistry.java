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
package ch.elca.el4j.services.persistence.generic;

import ch.elca.el4j.services.persistence.generic.dao.RepositoryRegistry;
import ch.elca.el4j.services.persistence.generic.dao.SimpleGenericRepository;
import ch.elca.el4j.services.persistence.generic.dao.WrappingRepositoryRegistry;

/**
 * Wraps a repository registry's repositories with {@link LazyRepositoryView}.
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
public class DefaultLazyRepositoryViewRegistry 
        extends WrappingRepositoryRegistry
     implements LazyRepositoryViewRegistry {

    /**
     * Constructor.
     * @param backing the backing registry.
     */
    public DefaultLazyRepositoryViewRegistry(RepositoryRegistry backing) {
        super(backing);
    }

    /** {@inheritDoc} */
    @Override
    protected <T> 
    SimpleGenericRepository<T> wrap(SimpleGenericRepository<T> repo) {
        return new DefaultLazyRepositoryView<T>(repo);
    }
}
