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
package ch.elca.el4j.services.gui.richclient.executors.convenience;

import java.util.List;

import ch.elca.el4j.services.persistence.generic.RepositoryAgent;

/**
 * A generic executor to delete beans managed by a RepositoryAgent.
 * 
 * @param <T> The type of entities this executor can delete.
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
public class GenericBeanDeleteExecutor<T> 
        extends AbstractBeanDeleteExecutor<T> {

    /** The agent that should be used to carry out the operation. */
    private RepositoryAgent<T> m_agent;
    
    /**
     * Constructor.
     * @param agent see {@link #m_agent}
     */
    public GenericBeanDeleteExecutor(RepositoryAgent<T> agent) {
        m_agent = agent;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void deleteBeans(List<T> beans) {
        m_agent.delete(beans);
    }

    /** {@inheritDoc} */
    @Override
    protected T reloadBean(T entity) throws Exception {
        return m_agent.refresh(entity);
    }
}
