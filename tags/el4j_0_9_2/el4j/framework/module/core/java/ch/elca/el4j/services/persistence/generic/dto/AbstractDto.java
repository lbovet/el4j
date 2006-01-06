/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

import java.io.Serializable;

import ch.elca.el4j.services.persistence.generic.primarykey.PrimaryKeyGenerator;

/**
 * This abstract dto brings some basic elements for managing optimistic locking.
 * Optimistic locking is implemented by using a 
 * <code>PrimaryKeyGenerator</code>. Primary key are strings in this case.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @deprecated Please use <code>AbstractIntOptimisticLockingDto</code> instead.
 * @see AbstractIntOptimisticLockingDto
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractDto implements Serializable {
    /**
     * Is used for optimistic locking. This last modification key must be equals
     * with the one in database to be able to edit an entry, otherwise the entry
     * has been edited.
     */
    private String m_lastModificationKey;

    /**
     * Is used for optimistic locking. This is the next modification key. If the
     * dto will be saved successfully on database, the last modification will be
     * set to the current modification key to indicate, that this entry in
     * database has been modificated.
     */
    private String m_currentModificationKey;

    /**
     * Primary key generator to generate modification keys for dtos.
     */
    private PrimaryKeyGenerator m_modificationKeyGenerator;

    /**
     * @param modificationKeyGenerator
     *            Is the modificationKeyGenerator to set.
     */
    public void setModificationKeyGenerator(
        final PrimaryKeyGenerator modificationKeyGenerator) {
        this.m_modificationKeyGenerator = modificationKeyGenerator;
    }

    /**
     * @return Returns the lastModificationKey.
     */
    public String getLastModificationKey() {
        return m_lastModificationKey;
    }

    /**
     * @param lastModificationKey
     *            The lastModificationKey to set.
     */
    public void setLastModificationKey(String lastModificationKey) {
        m_lastModificationKey = lastModificationKey;
    }

    /**
     * This method will be called to get the current modification key, which
     * will be used to save the current modification key as the last
     * modification key on database.
     * 
     * @return Generates and returns for each instance a new modification key.
     */
    public String getCurrentModificationKey() {
        if (m_currentModificationKey == null) {
            m_currentModificationKey = getNewModificationKey();
        }
        return m_currentModificationKey;
    }

    /**
     * This method will be called by user if this dto was written to database
     * and so the last modification key has been replaced by the current
     * modification key. This method sets the last modification key to the
     * current modification key and generates a new current modififation key.
     */
    public void useNextModificationKey() {
        m_lastModificationKey = m_currentModificationKey;
        m_currentModificationKey = getNewModificationKey();
    }

    /**
     * This method generates a new modification key.
     * 
     * @return Returns the new generated modification key.
     */
    private String getNewModificationKey() {
        return m_modificationKeyGenerator.getPrimaryKey();
    }
}