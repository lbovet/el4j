/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
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
 *   ("$Source$",
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
