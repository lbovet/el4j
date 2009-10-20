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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.graphwalker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.interfaces.Linked;

/**
 * Sample linked object for testing. Can be used in a doubly-linked graph.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class LinkedObject implements Linked, UniqueKeyed {

	/** The parent. */
	private Set<LinkedObject> m_ancestors;
	
	/** The children. */
	private Set<LinkedObject> m_successors;
	
	/** The name. */
	private String m_name;
	
	/**
	 * Create an object.
	 * @param name The name.
	 */
	public LinkedObject(String name) {
		m_ancestors = new HashSet<LinkedObject>();
		m_successors = new HashSet<LinkedObject>();
		m_name = name;
	}

	/**
	 * Get the ancestors. Result is safe for modification.
	 * @return The ancestors.
	 */
	public Set<LinkedObject> getAncestors() {
		Set<LinkedObject> value = new HashSet<LinkedObject>();
		value.addAll(m_ancestors);
		return value;
	}

	/**
	 * Get the successors. Result is safe for modification.
	 * @return The successors.
	 */
	public Set<LinkedObject> getSuccessors() {
		Set<LinkedObject> value = new HashSet<LinkedObject>();
		value.addAll(m_successors);
		return value;
	}

	/**
	 * Get the name.
	 * @return The name.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Setter for ancestors.
	 * @param ancestors The new ancestors to set.
	 */
	public void setAncestors(Set<LinkedObject> ancestors) {
		for (LinkedObject old : m_ancestors) {
			old.m_successors.remove(this);
		}
		for (LinkedObject current : ancestors) {
			current.m_successors.add(this);
		}
		m_ancestors = ancestors;
	}

	/**
	 * Setter for successors.
	 * @param successors The new successors to set.
	 */
	public void setSuccessors(Set<LinkedObject> successors) {
		for (LinkedObject old : m_successors) {
			old.m_ancestors.remove(this);
		}
		for (LinkedObject current : successors) {
			current.m_ancestors.add(this);
		}
		m_successors = successors;
	}

	/**
	 * Setter for name.
	 * @param name The new name to set.
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Add an ancestor.
	 * @param object The ancestor to add.
	 */
	public void addAncestor(LinkedObject object) {
		m_ancestors.add(object);
		object.m_successors.add(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		}
		if (obj instanceof LinkedObject) {
			LinkedObject other = (LinkedObject) obj;
			return other.m_name.equals(m_name);
		} else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return m_name.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "LinkedObject " + m_name;
	}

	/*
	 * Object wrapper.
	 */
	
	/** {@inheritDoc} */
	public Object[] getAllLinked() {
		return m_successors.toArray();
	}

	/** {@inheritDoc} */
	public Collection<?> getCollectionLinkByName(String name) {
		if (name.equals("successors")) {
			return m_successors;
		}
		throw new IllegalArgumentException();
	}

	/** {@inheritDoc} */
	public String[] getCollectionLinkNames() {
		return new String[] {"successors"};
	}

	/** {@inheritDoc} */
	public Object getlinkByName(String linkName) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	public String[] getLinkNames() {
		return new String[0];
	}

	/** {@inheritDoc} */
	public UniqueKey getUniqueKey() {
		return new UniqueKey(m_name, LinkedObject.class);
	}
	
	/** {@inheritDoc} */
	public UniqueKey getLocalUniqueKey() {
		return getUniqueKey();
	}

	/** {@inheritDoc} */
	public void setUniqueKey(UniqueKey key) throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}
}
