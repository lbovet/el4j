/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.persistence.generic.dto;

import java.io.Serializable;

/**
 * This is an abstract class for optimistic locking. The used version type is an
 * integer. Here an example, how this dto must be used in combination with 
 * ibatis.<br>
 * <br>
 * <b>sql-map-file.xml:</b>
 * <pre><code>
 * ...
 *     <statement id="updateMyTable">
 *         update MYTABLE set COL1=#col1#, ..., 
 *             OPTIMISTIC_LOCKING_VERSION=OPTIMISTIC_LOCKING_VERSION+1
 *             where ID=#id# 
 *                 and OPTIMISTIC_LOCKING_VERSION=#optimisticLockingVersion#
 *     </statement>
 * ...
 * </code></pre>
 * 
 * In java code the update count must be checked to know if the version number
 * on database has been increased. If yes, the version number of dto must be
 * increased too.<br>
 * <br>
 * <b>Java code:</b>
 * <pre><code>
 * ...
 *     MyTableDto myDto = ...
 *     SqlMapClientTemplate smc = ...
 *     int count = smc.update("updateMyTable", myDto);
 *     if (count == 1) {
 *         myDto.increaseOptimisticLockingVersion();
 *     }
 * ...
 * </code></pre>
 * 
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
public abstract class AbstractIntOptimisticLockingDto 
    implements Serializable, OptimisticLockingObject {
    /**
     * Is the optimistic locking version number. Initial optimistic locking 
     * version number is zero.
     */
    private int m_optimisticLockingVersion = 0;

    /**
     * @return Returns the optimistic locking version.
     */
    public final int getOptimisticLockingVersion() {
        return m_optimisticLockingVersion;
    }

    /**
     * @param optimisticLockingVersion
     *            The optimistic locking version to set.
     */
    public final void setOptimisticLockingVersion(
        int optimisticLockingVersion) {
        m_optimisticLockingVersion = optimisticLockingVersion;
    }
    
    /**
     * Method to increase the int optimistic locking version number.
     */
    public final void increaseOptimisticLockingVersion() {
        m_optimisticLockingVersion++;
    }
}
