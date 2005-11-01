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

package ch.elca.el4j.services.persistence.generic.dto;

import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This class is an abstract dto which uses a string as key value and an integer
 * for optimistic locking version controlling.
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
public abstract class AbstractStringKeyIntOptimisticLockingDto 
    extends AbstractIntOptimisticLockingDto
    implements PrimaryKeyOptimisticLockingObject {
    
    /**
     * Primary key.
     */
    private String m_key;

    /**
     * {@inheritDoc}
     */
    public boolean isKeyNew() {
        return m_key == null;
    }

    /**
     * @return Returns the key.
     */
    public final String getKey() {
        return m_key;
    }

    /**
     * @param key The key to set.
     */
    public final void setKey(String key) {
        m_key = key;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setKey(Object keyObject) {
        String key = (keyObject == null) ? null : keyObject.toString();
        setKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (m_key != null) ? m_key.hashCode() : 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof AbstractStringKeyIntOptimisticLockingDto) {
            AbstractStringKeyIntOptimisticLockingDto other 
                = (AbstractStringKeyIntOptimisticLockingDto) obj;
            return ObjectUtils.nullSaveEquals(m_key, other.m_key);
        } else {
            return false;
        }
    }
}
