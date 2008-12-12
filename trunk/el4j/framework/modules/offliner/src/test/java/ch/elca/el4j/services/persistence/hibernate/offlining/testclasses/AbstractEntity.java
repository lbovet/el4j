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
package ch.elca.el4j.services.persistence.hibernate.offlining.testclasses;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Base class for entity classes.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	/**
	 * The primary key.
	 */
	private long m_id;
	
	/** 
	 * The version number.
	 */
	private long m_version;

	/**
	 * @return Returns the id.
	 */
	@Id @Column(name = "ID") @GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return m_id;
	}

	/**
	 * @return Returns the version.
	 */
	@Version @Column(name = "VERSION")
	public long getVersion() {
		return m_version;
	}

	/**
	 * @param id Is the id to set.
	 */
	public void setId(long id) {
		m_id = id;
	}

	/**
	 * @param version Is the version to set.
	 */
	public void setVersion(long version) {
		m_version = version;
	}
	
	
}
