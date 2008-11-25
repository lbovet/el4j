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
package ch.elca.el4j.tests.person.dom;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

/**
 * Person domain object.
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
@Table(name = "person")
@SequenceGenerator(name = "keyid_generator", sequenceName = "person_sequence")
public class Person extends AbstractDomainObject {
	
	/** See corresponding setter method for more details. */
	private String m_name;
	
	/** See corresponding setter method for more details. */
	private Brain m_brain;
	
	/** See corresponding setter method for more details. */
	private List<Tooth> m_teeth;
	
	/** See corresponding setter method for more details. */
	private List<Person> m_friends;
	
	/** See corresponding setter method for more details. */
	private LegalStatus m_legalStatus;
	
	/** Default constructor. */
	public Person() {
		m_legalStatus = LegalStatus.SINGLE;
	}
	
	/** 
	 * Default constructor. 
	 * @param name		the name of the person.
	 */
	public Person(String name) {
		this();
		m_name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @param name Is the name to set.
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * @return Returns the brain.
	 */
	@NotNull
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "brain_key", unique = true)
	public Brain getBrain() {
		return m_brain;
	}

	/**
	 * @param brain Is the brain to set.
	 */
	public void setBrain(Brain brain) {
		m_brain = brain;
	}

	/**
	 * @return Returns the teeth.
	 */
	@OneToMany(cascade = { CascadeType.ALL })
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public List<Tooth> getTeeth() {
		return m_teeth;
	}

	/**
	 * @param teeth Is the teeth to set.
	 */
	public void setTeeth(List<Tooth> teeth) {
		m_teeth = teeth;
	}

	/**
	 * @return Returns the friends.
	 */
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinTable(name = "friends")
	public List<Person> getFriends() {
		return m_friends;
	}

	/**
	 * @param friends Is the friends to set.
	 */
	public void setFriends(List<Person> friends) {
		m_friends = friends;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return m_name == null ? "unnamed" : m_name;
	}
	
	/**
	 * Get the legalStatus.
	 * @return The legalStatus.
	 */
	public LegalStatus getLegalStatus() {
		return m_legalStatus;
	}

	/**
	 * Setter for legalStatus.
	 * @param legalStatus The new legalStatus to set.
	 */
	public void setLegalStatus(LegalStatus legalStatus) {
		m_legalStatus = legalStatus;
	}
	
	/**
	 * The legal status of a person.
	 * <i>Used to test enum fields and their display with toString().</i>
	 */
	public static enum LegalStatus {

		/** Single. */
		SINGLE("single"),
		
		/** Married. */
		MARRIED("married"),
		
		/** Divorced. */
		DIVORCED("divorced"),
		
		/** Widowed. */
		WIDOWED("widowed"),
		
		/** Civil partnership. */
		CIVIL_PARTNERSHIP("in civil partnership");
		
		/** The description. */
		private String m_description;
		
		/**
		 * Constructor.
		 * @param description The description. Used in toString().
		 */
		LegalStatus(String description) {
			m_description = description;
		}
		
		/** {@inheritDoc} */
		@Override public String toString() {
			return m_description;
		}
	}
}
