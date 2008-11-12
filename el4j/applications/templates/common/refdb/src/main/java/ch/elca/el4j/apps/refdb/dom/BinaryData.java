/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;


/**
 * BinaryData domain object.
 * The parameter <T> represents the parent classtype.
 * @param <T>		parent type.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Rueedlinger (ARR)
 */
@Entity
@Table(name = "BLOBS")
@SequenceGenerator(name = "keyid_generator", sequenceName = "blob_sequence")
public class BinaryData<T> extends AbstractIntKeyIntOptimisticLockingDto {
	
	/** The data. */
	private byte[] m_data;
	
	/** The parent object. */
	private T m_parent;
	
	/**
	 * @return the data
	 */
	@Column(name = "CONTENT")
	public byte[] getData() {
		return m_data;
	}
	
	/**
	 * @param data	data to set.
	 */
	public void setData(byte[] data) {
		m_data = data;
	}
	
	/**
	 * Returns the parent object.
	 * @return parent object.
	 */
	@OneToOne(mappedBy = "data")
	public T getParent() {
		return m_parent;
	}
	
	/**
	 * Sets the parent.
	 * @param parent the parent to set.
	 */
	public void setParent(T parent) {
		m_parent = parent;
	}
	
	
}
