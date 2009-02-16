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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

@Entity
@SequenceGenerator(name = "keyid_generator", sequenceName = "tooth_sequence")
public class Tooth extends AbstractIntKeyIntOptimisticLockingDto {
	private int m_age;
	private Person m_owner;
	
	public Tooth() { }
	
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
	public Person getOwner() {
		return m_owner;
	}

	/**
	 * @param owner Is the owner to set.
	 */
	public void setOwner(Person owner) {
		m_owner = owner;
	}
	
	@Override
	public String toString() {
		return m_owner + "'s tooth (age = " + m_age + ")";
	}
}