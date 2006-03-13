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
package ch.elca.el4j.services.gui.richclient.executors;

import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;

/**
 * Abstract executor with displayable component.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractDisplayableBeanExecutor 
    extends AbstractBeanExecutor {
    /**
     * Is the displayable for this executor.
     */
    private ExecutorDisplayable m_displayable;

    /**
     * @return Returns the displayable.
     */
    public final ExecutorDisplayable getDisplayable() {
        if (m_displayable == null) {
            m_displayable = getDefaultDisplayable();
        }
        return m_displayable;
    }

    /**
     * @param displayable Is the displayable to set.
     */
    public final void setDisplayable(ExecutorDisplayable displayable) {
        m_displayable = displayable;
    }
    
    /**
     * @return Returns the default displayable for this executor.
     */
    protected abstract ExecutorDisplayable getDefaultDisplayable();
}
