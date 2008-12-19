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
package ch.elca.el4j.services.persistence.hibernate.offlining.impl;

import java.io.Serializable;

import ch.elca.el4j.services.persistence.hibernate.offlining.generic.StringCaster;
import ch.elca.el4j.services.persistence.hibernate.offlining.util.GenericSerializableUtil;



/**
 * Unique key for an object. The unique key abstraction makes a 1:1 mapping from the set of all keyed objects
 * (i.e. domain objects) to unique keys. Object identity can be defined as having equal unique keys. 
 *
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
public class UniqueKey implements Serializable, Comparable<UniqueKey> {
	
	/** Serialization constant. */
	private static final long serialVersionUID = -3936817151818074544L;
	
	/** The object's primary key. */
	protected Serializable m_key;
	
	/** The object's class. */
	protected Class<?> m_class;
	
	/**
	 * @param key The primary key.
	 * @param cls The object class.
	 */
	public UniqueKey(Serializable key, Class<?> cls) {
		m_key = key;
		m_class = cls;
	}

	/**
	 * Try and create a unique key from a string representation.
	 * @param string The string representing the unique key.
	 * @return The instance or <code>null</code> for a null key.
	 */
	public static UniqueKey fromString(String string) {
		if (string == null || string.equals("null")) {
			// A null (remote) key. This is a legitimate case.
			return null;
		}
		
		int splitIndex = string.indexOf(':');
		String className = string.substring(0, splitIndex);
		String keyName = string.substring(splitIndex + 1);
		if (splitIndex == -1) {
			throw new IllegalArgumentException("Invalid string: no separator ':' .");
		}
		
		Class<?> cls;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Class " + className + " does not exist.", e);
		}
		Serializable key = (Serializable) new StringCaster().fromString(keyName);
		return new UniqueKey(key, cls);
	}
	
	/**
	 * Get the key.
	 * @return The key.
	 */
	public Serializable getKey() {
		return m_key;
	}

	/**
	 * Get the class.
	 * @return The class.
	 */
	public Class<?> getObjectClass() {
		return m_class;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof UniqueKey) {
			UniqueKey other = (UniqueKey) obj;
			return other.m_class == m_class
				&& other.m_key.equals(m_key); 
		} else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + m_class.hashCode();
		hash = 31 * hash + m_key.hashCode();
		return hash;
	}

	/** 
	 * Create a string representation for this unique key.
	 * This is only meaningful if the toString() method of the key class is 1:1,
	 * that is different keys have different string representations. 
	 * @return A string representation of this key.
	 */
	@Override
	public String toString() {
		return m_class.getCanonicalName() + ":" + new StringCaster().toString(m_key);
	}

	/** 
	 * {@inheritDoc} 
	 * Compare unique keys by class (as string), then primary key.
	 * Objects that have a non-comparable primary key generate an exception.
	 */
	public int compareTo(UniqueKey other) {
		if (other.m_class != m_class) {
			return other.m_class.toString().compareTo(
				m_class.toString());
		}
		
		return GenericSerializableUtil.compare(m_key, other.m_key);
	}
}
