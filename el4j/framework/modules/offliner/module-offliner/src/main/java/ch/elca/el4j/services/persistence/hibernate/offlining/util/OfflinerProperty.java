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
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Allows the offliner to save certain values between invocations.
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
@Entity @Table(name = "OFFLINERPROPERTIES")
@SequenceGenerator(name = "keyid_generator", sequenceName = "properties_sequence")
public class OfflinerProperty {

	// Used properties.
	
	/** The persistent property name for the last commit. */
	public static final String LAST_COMMIT_PROP = "lastCommit";
	
	/** The persistent property name for the last successful commit. */
	public static final String LAST_SUCCESSFUL_COMMIT_PROP = "lastSuccessfulCommit";
	
	/** The current state of the offliner. This allows us to start up in the same state as we last shut down. */
	public static final String CURRENT_STATE = "currentState";
	
	/** The key. */
	private int m_id;
	
	/** The property name. */
	private String m_name;
	
	/** 
	 * The value. It is saved via the stringizer and can be a string
	 * or an integer.
	 */
	private String m_value;

	/**
	 * Get the id.
	 * @return The id.
	 */
	@Id @Column(name = "ID") 
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "keyid_generator")
	public int getId() {
		return m_id;
	}

	/**
	 * Get the name.
	 * @return The name.
	 */
	@Column(name = "PROPNAME")
	public String getPropertyName() {
		return m_name;
	}

	/**
	 * Get the value.
	 * @return The value.
	 */
	@Column(name = "PROPVALUE")
	public String getPropertyValue() {
		return m_value;
	}

	/**
	 * Setter for id.
	 * @param id The new id to set.
	 */
	public void setId(int id) {
		m_id = id;
	}

	/**
	 * Setter for name.
	 * @param name The new name to set.
	 */
	public void setPropertyName(String name) {
		m_name = name;
	}

	/**
	 * Setter for value.
	 * @param value The new value to set.
	 */
	public void setPropertyValue(String value) {
		m_value = value;
	}
}
