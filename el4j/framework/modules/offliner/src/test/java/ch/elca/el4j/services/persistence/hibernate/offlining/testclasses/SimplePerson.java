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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A simple person class w/o associations.
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
@Entity @Table(name = "SIMPLEPERSON")
public class SimplePerson extends AbstractEntity {

	/**
	 * The name.
	 */
	private String m_name;
	
	/** 
	 * The email address.
	 */
	private String m_email;

	/**
	 * @return Returns the name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return m_name;
	}

	/**
	 * @return Returns the email.
	 */
	@Column(name = "EMAIL")
	public String getEmail() {
		return m_email;
	}

	/**
	 * @param name Is the name to set.
	 */
	public void setName(String name) {
		this.m_name = name;
	}

	/**
	 * @param email Is the email to set.
	 */
	public void setEmail(String email) {
		this.m_email = email;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return m_name + " [" + m_email + "]";
	}
}
