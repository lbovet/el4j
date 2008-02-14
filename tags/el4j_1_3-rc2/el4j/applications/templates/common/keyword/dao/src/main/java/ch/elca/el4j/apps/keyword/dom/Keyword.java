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
package ch.elca.el4j.apps.keyword.dom;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * Keyword domain object.
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
@Table(name = "KEYWORDS")
@SequenceGenerator(name = "keyid_generator", sequenceName = "keyword_sequence")
public class Keyword extends AbstractIntKeyIntOptimisticLockingDto {
    
    /**
     * This is the name of the keyword.
     */
    private String m_name;

    /**
     * This is the description of a keyword.
     */
    private String m_description;

    // Checkstyle: MagicNumber off
    /**
     * @return Returns the name.
     */
    @NotNull
    //@Length(min = 3)
    @Pattern(regex = "[-'.a-zA-Z0-9 ]*")
    @Column(unique = true)
    public String getName() {
        return m_name;
    }

    // Checkstyle: MagicNumber on
    
    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        m_description = description;
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
            && object instanceof Keyword) {
            Keyword other = (Keyword) object;
            return ObjectUtils.nullSaveEquals(m_name, other.m_name)
                && ObjectUtils.nullSaveEquals(
                    m_description, other.m_description);
        } else {
            return false;
        }
    }
}