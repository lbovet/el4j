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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/**
 * This class is part of an example DOM of EL4J,
 * describing the tooth of a person.
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
@SequenceGenerator(name = "keyid_generator", sequenceName = "tooth_sequence")
public class Tooth extends AbstractIntKeyIntOptimisticLockingDto {
	
	/** See corresponding getter for informations. */
	private int m_age;
	
	/** See corresponding getter for informations. */
	private Person m_owner;
	
	/**
	 * Create a tooth.
	 */
	public Tooth() { }
	
	/**
	 * Create a tooth with a given age.
	 * @param age the age of the tooth.
	 */
	public Tooth(int age) {
		m_age = age;
	}

	/**
	 * @return Returns the age.
	 */
	public int getAge() {
		return m_age;
	}

	/**
	 * @param age Is the age to set.
	 */
	public void setAge(int age) {
		m_age = age;
	}

	/**
	 * @return Returns the owner.
	 */
	@ManyToOne
	@JoinColumn(name = "owner_keyid", nullable = false,
		unique = false, updatable = false)
	public Person getOwner() {
		return m_owner;
	}

	/**
	 * @param owner Is the owner to set.
	 */
	public void setOwner(Person owner) {
		m_owner = owner;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return m_owner + "'s tooth (age = " + m_age + ")";
	}
}
