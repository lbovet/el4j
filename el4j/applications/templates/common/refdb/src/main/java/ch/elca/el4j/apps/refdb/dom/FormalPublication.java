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


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.Length;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotNull;

/**
 * FormalPublication domain object. This class is a reference and describes a
 * formal publication.
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
@Table(name = "FORMALPUBLICATIONS")
@PrimaryKeyJoinColumn(name = "KEYTOREFERENCE")
public class FormalPublication extends Reference {
	/**
	 * Name of the author.
	 */
	private String m_authorName;

	/**
	 * Name of the publisher.
	 */
	private String m_publisher;

	/**
	 * Number pages of the publication.
	 */
	private int m_pageNum;

	// Checkstyle: MagicNumber off
	
	/**
	 * @return Returns the authorName.
	 */
	@NotNull
	@Length(min = 3)
	public String getAuthorName() {
		return m_authorName;
	}
	
	/**
	 * @param authorName
	 *            The authorName to set.
	 */
	public void setAuthorName(String authorName) {
		m_authorName = authorName;
	}

	/**
	 * @return Returns the pageNum.
	 */
	@Min(0)
	public int getPageNum() {
		return m_pageNum;
	}
	
	// Checkstyle: MagicNumber on
	
	/**
	 * @param pageNum
	 *            The pageNum to set.
	 */
	public void setPageNum(int pageNum) {
		m_pageNum = pageNum;
	}

	/**
	 * @return Returns the publisher.
	 */
	public String getPublisher() {
		return m_publisher;
	}

	/**
	 * @param publisher
	 *            The publisher to set.
	 */
	public void setPublisher(String publisher) {
		m_publisher = publisher;
	}

}