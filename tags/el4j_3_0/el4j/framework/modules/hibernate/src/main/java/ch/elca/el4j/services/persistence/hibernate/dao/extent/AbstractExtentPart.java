/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate.dao.extent;

import java.io.Serializable;
import java.lang.reflect.Method;

import ch.elca.el4j.util.codingsupport.BeanPropertyUtils;

/**
 * An ExtentPart is the abstract super Class of Extent parts like ExtentEntity or ExtentCollection.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Rueedlinger (ARR)
 */
public abstract class AbstractExtentPart implements Serializable, Comparable<AbstractExtentPart> {
	/** The name of the extent-part. */
	protected String m_name;
	
	/** The parent of the entity, null if root. */
	protected AbstractExtentPart m_parent;
	
	/**
	 * Name of the extent-part.
	 * @return the name of the extent-part.
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Return the parent entity of the current extent part,
	 * null if root or contained in a collection.
	 * @return the parent.
	 */
	public AbstractExtentPart getParent() {
		return m_parent;
	}
	
	/**
	 * @return the id of the extent part.
	 */
	public abstract String getId();
	
	/**
	 * Updates the id of the extent part.
	 * Should be used be children to inform its parent.
	 */
	protected abstract void updateId();
	
	/** {@inheritDoc} */
	public int compareTo(AbstractExtentPart other) {
		if (m_name != null) {
			return m_name.compareTo(other.m_name);
		} else if (other.m_name == null) {
			return 0;
		} else {
			return -1;
		}
	}
	
	/**
	 * Sets the parent of the extent-part.
	 * @param parent	the parent to set.
	 */
	protected void setParent(AbstractExtentPart parent) {
		m_parent = parent;
	}
	/**
	 * Method to get the extent-part, null if root entity,
	 * otherwise set latest when added as child.
	 * @return the method to get the extent-part.
	 * @throws NoSuchMethodException 
	 */
	public Method getMethod() throws SecurityException, NoSuchMethodException {
		if (m_parent instanceof ExtentCollection) {
			return null;
		} else {
			return BeanPropertyUtils.getReadMethod(((ExtentEntity) m_parent).getEntityClass(), getName());
		}
	}
	

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			return true;
		} else if (object instanceof AbstractExtentPart) {
			return getId().equals(((AbstractExtentPart) object).getId());
		} else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getId();
	}
	
	/**
	 * @return the old native implementation of the toString method.
	 */
	protected String nativeToString() {
		return super.toString();
	}
	
	/* Helper Functions */
	
	/**
	 * Helper function to convert a string from Getter-Method-name
	 * to field name.
	 * @param m		the method to get the field name of.
	 * @return field name
	 */
	protected String toFieldName(Method m) {
		String str = m.getName();
		if (m.getReturnType().equals(boolean.class)) {
			return firstCharLower(str.substring(2));
		} else {
			return firstCharLower(str.substring(3));
		}
		
	}
	
	/**
	 * Helper function to set first Character lower case.
	 * @param str	string to be changed
	 * @return changed string
	 */
	protected String firstCharLower(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	/**
	 * Helper function to set first Character upper case.
	 * @param str	string to be changed
	 * @return changed string
	 */
	protected String firstCharUpper(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}
