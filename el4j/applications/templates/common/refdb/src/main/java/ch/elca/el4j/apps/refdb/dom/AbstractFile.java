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

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.NotNull;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * Base class for File domain object.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */

@MappedSuperclass
public abstract class AbstractFile extends AbstractIntKeyIntOptimisticLockingDto {

	/**
	 * Related reference.
	 */
	private Reference m_reference;

	/**
	 * Name of the document.
	 */
	private String m_name;

	/**
	 * Mime type of the binary content of the file.
	 */
	private String m_mimeType;

	/**
	 * Size of the content in bytes.
	 */
	private int m_size;

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
	@Column(name = "CONTENTSIZE")
	public int getSize() {
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
	 * @return Returns the related reference.
	 */
	@NotNull
	@ManyToOne
	@JoinColumn(name = "keyToReference", nullable = false,
		unique = false, updatable = false)
	public Reference getReference() {
		return m_reference;
	}

	/**
	 * @param reference
	 *            The related reference to set.
	 */
	public void setReference(Reference reference) {
		m_reference = reference;
	}
	
	/** {@inheritDoc} */
	public String toString() {
		return m_name;
	}
}