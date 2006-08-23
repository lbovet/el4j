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

import javax.persistence.Entity;

import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * This class is a formal publication and describs a book.
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
public class BookDto extends FormalPublicationDto {
    /**
     * ISBN number of a book.
     */
    private String m_isbnNumber;

    /**
     * @return Returns the isbnNumber.
     */
    public String getIsbnNumber() {
        return m_isbnNumber;
    }

    /**
     * @param isbnNumber
     *            The isbnNumber to set.
     */
    public void setIsbnNumber(String isbnNumber) {
        this.m_isbnNumber = isbnNumber;
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
            && object instanceof BookDto) {
            BookDto other = (BookDto) object;

            return ObjectUtils.nullSaveEquals(
                m_isbnNumber, other.m_isbnNumber);
        } else {
            return false;
        }
    }
}