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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

@Entity
@SequenceGenerator(name = "keyid_generator", sequenceName = "brain_sequence")
public class Brain extends AbstractIntKeyIntOptimisticLockingDto {
	private int m_iq;
	private Person m_owner;

	/**
	 * Create a brain.
	 */
	public Brain() {
	}
	
	/**
	 * @return Returns the iq.
	 */
	public int getIq() {
		return m_iq;
	}

	/**
	 * @param iq Is the iq to set.
	 */
	public void setIq(int iq) {
		m_iq = iq;
	}

	/**
	 * @return Returns the owner.
	 */
	@OneToOne(mappedBy = "brain")
	public Person getOwner() {
		return m_owner;
	}

	/**
	 * @param owner Is the owner to set.
	 */
	public void setOwner(Person owner) {
		this.m_owner = owner;
	}
	
	@Override
	public String toString() {
		return m_owner + "'s brain (iq = " + m_iq + ")";
	}
}
