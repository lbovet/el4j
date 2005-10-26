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

/**
 * Abstract struts form with an integer key an integer optimistic locking
 * version number.
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
public abstract class AbstractIntKeyIntOptimisticLockingForm
    extends AbstractIntOptimisticLockingForm {
    
    /**
     * Is the primary key.
     */
    private Integer m_key;

    /**
     * @return Returns the key.
     */
    public final Integer getKey() {
        return m_key;
    }

    /**
     * @param key
     *            The key to set.
     */
    public final void setKey(Integer key) {
        m_key = key;
    }
}
