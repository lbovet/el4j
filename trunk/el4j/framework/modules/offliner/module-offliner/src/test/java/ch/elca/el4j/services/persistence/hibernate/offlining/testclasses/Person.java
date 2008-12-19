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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * A person.
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
@Entity @Table(name = "PERSON") @SequenceGenerator(name = "keyid_generator", sequenceName = "person_sequence")
public class Person extends AbstractEntity {
	
	/**
	 * The name.
	 */
	private String m_name;

	/**
	 * The parent. Each person only has one in this example.
	 */
	private Person m_parent;
	
	/** 
	 * The children.
	 */
	private Set<Person> m_children;

	/**
	 * Private constructor for create and delegation.
	 */
	private Person() { 
		m_name = "";
		m_children = new HashSet<Person>();
	}
	
	/**
	 * Create a person specifying the parent. To create a person without a 
	 * parent, use Person.create()
	 * @param parent The parent.
	 */
	public Person(Person parent) {
		this();
		parent.m_children.add(this);
		m_parent = parent;
	}
	
	/**
	 * @return Returns the name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return m_name;
	}

	/**
	 * @return Returns the parent.
	 */
	@ManyToOne @JoinColumn(name = "PARENT")
	public Person getParent() {
		return m_parent;
	}

	/**
	 * @return Returns the children.
	 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER) 
	public Set<Person> getChildren() {
		return m_children;
	}

	/**
	 * @param name Is the name to set.
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * @param parent Is the parent to set.
	 */
	public void setParent(Person parent) {
		m_parent = parent;
	}

	/**
	 * @param children Is the children to set.
	 */
	public void setChildren(Set<Person> children) {
		m_children = children;
	}
	
	/**
	 * @return A new Person instance with no parent.
	 */
	public static Person create() {
		return new Person();
	}

	/*
	 * PRE : No two people of the same name ever exist. 
	 */
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		if (m_name == null) {
			return 0;
		}
		return m_name.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return m_name;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Person) {
			Person other = (Person) obj;
			return other.m_name.equals(m_name);
		}
		return false;
	}	
	
	/**
	 * Adopt a child. 
	 * @param newChild The child to adopt.
	 */
	public void adopt(Person newChild) {
		if (newChild.m_parent == this) {
			return;
		}
		newChild.m_parent.m_children.remove(newChild);
		m_children.add(newChild);
		newChild.m_parent = this;
	}
}
