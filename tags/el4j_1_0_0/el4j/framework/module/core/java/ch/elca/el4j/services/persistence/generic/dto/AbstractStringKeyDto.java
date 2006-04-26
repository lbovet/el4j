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

package ch.elca.el4j.services.persistence.generic.dto;

import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This class adds to the <code>AbstractDto</code> a primary key as a string.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @deprecated Please use <code>AbstractStringKeyIntOptimisticLockingDto</code>
 *             instead.
 * @see AbstractStringKeyIntOptimisticLockingDto
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractStringKeyDto extends AbstractDto {
    /**
     * Primary key.
     */
    private String m_key;

    /**
     * @return Returns the key.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * @param key
     *            The key to set.
     */
    public void setKey(String key) {
        m_key = key;
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
        if (obj instanceof AbstractStringKeyDto) {
            AbstractStringKeyDto other = (AbstractStringKeyDto) obj;
            return ObjectUtils.nullSaveEquals(m_key, other.m_key);
        } else {
            return false;
        }
    }
}