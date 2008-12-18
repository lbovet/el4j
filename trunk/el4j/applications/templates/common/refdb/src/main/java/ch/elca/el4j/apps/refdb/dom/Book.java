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
package ch.elca.el4j.apps.refdb.dom;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.Pattern;

/**
 * Book domain object. This class is a formal publication and describes a book.
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
@Indexed
@Table(name = "BOOKS")
@PrimaryKeyJoinColumn(name = "KEYTOREFERENCE")
public class Book extends AbstractFormalPublication {
	
	/** See corresponding getter for informations. */
	private final String m_type = "Book";
	
	/**
	 * ISBN number of a book.
	 */
	private String m_isbnNumber;

	/**
	 * @return Returns the isbnNumber.
	 */
	@Pattern(regex = "[0-9-]{10,17}")
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
	
	/** {@inheritDoc} */
	@Transient
	@Override
	public String getType() {
		return m_type;
	}
}