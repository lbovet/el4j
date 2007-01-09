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
package ch.elca.el4j.applications.refdb.dom;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * ReferenceKeywordRelationship domain object. This class describes a
 * relationship between a keyword and a reference.
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
public class ReferenceKeywordRelationship 
    extends AbstractIntKeyIntOptimisticLockingDto {
    /**
     * This is the primary key from the keyword.
     */
    private int m_keyKeyword;

    /**
     * This is the primary key from the reference.
     */
    private int m_keyReference;

    /**
     * @return Returns the keyKeyword.
     */
    public int getKeyKeyword() {
        return m_keyKeyword;
    }

    /**
     * @param keyKeyword
     *            The keyKeyword to set.
     */
    public void setKeyKeyword(int keyKeyword) {
        m_keyKeyword = keyKeyword;
    }

    /**
     * @return Returns the keyReference.
     */
    public int getKeyReference() {
        return m_keyReference;
    }

    /**
     * @param keyReference
     *            The keyReference to set.
     */
    public void setKeyReference(int keyReference) {
        m_keyReference = keyReference;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object object) {
        if (super.equals(object)
            && object instanceof ReferenceKeywordRelationship) {
            ReferenceKeywordRelationship other 
                = (ReferenceKeywordRelationship) object;

            return m_keyKeyword == other.m_keyKeyword
                && m_keyReference == other.m_keyReference;
        } else {
            return false;
        }
    }
}