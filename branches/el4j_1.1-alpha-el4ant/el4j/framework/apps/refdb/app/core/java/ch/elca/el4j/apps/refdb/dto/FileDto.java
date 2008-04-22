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
package ch.elca.el4j.apps.refdb.dto;

import java.util.Arrays;

import javax.persistence.Entity;

import org.hibernate.validator.NotNull;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * Dto for a file.
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
@Entity
public class FileDto extends AbstractIntKeyIntOptimisticLockingDto {
    /**
     * Primary key of related reference.
     */
    private int m_keyToReference;

    /**
     * Name of the document.
     */
    private String m_name;

    /**
     * Mime type of the binary content of the file.
     */
    private String m_mimeType;

    /**
     * Data of the document (typically binary).
     */
    private byte[] m_content;

    /**
     * Size of the content in bytes.
     */
    private int m_size;

    /**
     * Reference this file is associated with (only used if
     * Hibernate is used to perform ORM).
     */
    //private ReferenceDto m_reference;
    
    /**
     * @return Returns the content.
     */
    @NotNull
    public byte[] getContent() {
        return m_content;
    }
 
    /**
     * @param content
     *            The content to set.
     */
    public void setContent(byte[] content) {
        m_content = content;
    }

    /**
     * @return Returns the mimeType
     */
    @NotNull
    public String getMimeType() {
        return m_mimeType;
    }

    /**
     * @param mimeType
     *            The mimeType to set.
     */
    public void setMimeType(String mimeType) {
        m_mimeType = mimeType;
    }

    /**
     * @return Returns the name
     */
    @NotNull
    public String getName() {
        return m_name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @return Returns the size of the content in bytes.
     */
    @NotNull
    public int getSize() {
        if (m_content != null && m_size <= 0) {
            m_size = m_content.length;
        }
        return m_size;
    }

    /**
     * @param size
     *            The size of the content in bytes.
     */
    public void setSize(int size) {
        m_size = size;
    }

    /**
     * @return Returns the key to reference.
     */
    @NotNull
    public int getKeyToReference() {
        return m_keyToReference;
    }

    /**
     * @param keyToReference
     *            The key to reference to set.
     */
    public void setKeyToReference(int keyToReference) {
        m_keyToReference = keyToReference;
    }

    /**
     * @return Returns the reference this file is associated with (only
     *         used if Hibernate is used to perform ORM).
     */
    /*public ReferenceDto getReference() {
        return m_reference;
    }*/
    
    /**
     * @param reference
     *            The reference this file will be associated with (only
     *            used if Hibernate is used to perform ORM).
     */
    /*public void setReference(ReferenceDto reference) {
        m_reference = reference;
    }*/
    
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
            && object instanceof FileDto) {
            FileDto other = (FileDto) object;

            return m_keyToReference == other.m_keyToReference
                && ObjectUtils.nullSaveEquals(m_name, other.m_name)
                && ObjectUtils.nullSaveEquals(m_mimeType, other.m_mimeType)
                && Arrays.equals(m_content, other.m_content);
        } else {
            return false;
        }
    }
}