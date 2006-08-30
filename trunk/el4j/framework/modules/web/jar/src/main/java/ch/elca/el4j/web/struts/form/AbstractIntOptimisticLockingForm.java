/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.web.struts.form;

import org.apache.struts.action.ActionForm;

/**
 * Abstract struts form for optimistic locking version number.
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
public abstract class AbstractIntOptimisticLockingForm extends ActionForm {
    /**
     * Version of optimistic locking.
     */
    private Integer m_optimisticLockingVersion;

    /**
     * @return Returns the optimisticLockingVersion.
     */
    public final Integer getOptimisticLockingVersion() {
        return m_optimisticLockingVersion;
    }

    /**
     * @param optimisticLockingVersion
     *            The optimisticLockingVersion to set.
     */
    public final void setOptimisticLockingVersion(
        Integer optimisticLockingVersion) {
        m_optimisticLockingVersion = optimisticLockingVersion;
    }
}
