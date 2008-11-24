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

/**
 * An ExtentPart is the abstract super Class of Extent parts like ExtentEntity or ExtentCollection.
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
public abstract class AbstractExtentPart implements Serializable {
	/** The name of the extent-part. */
	protected String m_name;
	
	/** The parent of the entity, null if root */
	protected ExtentEntity m_parent;
	/** The method to get the extent-part. */
	//protected Method m_method;
	
	
	/**
	 * Name of the extent-part.
	 * @return the name of the extent-part.
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Getter-Name of the extent-part.
	 * Convention: "get" + name (with first letter in uppercase).
	 * @return the getter-name of the extent-part.
	 */
	public String getGetterName() {
		return "get" + firstCharUpper(m_name);
	}
	
	/**
	 * Return the parent entity of the current extent part,
	 * null if root or contained in a collection.
	 * @return the parent.
	 */
	public ExtentEntity getParent() {
		return m_parent;
	}
	
	/**
	 * Sets the parent of the extent-part.
	 * @param parent	the parent to set.
	 */
	protected void setParent(ExtentEntity parent) {
		m_parent = parent;
	}
	/**
	 * Method to get the extent-part, null if root entity,
	 * otherwise set latest when added as child.
	 * @return the method to get the extent-part.
	 * @throws NoSuchMethodException 
	 */
	public Method getMethod() throws SecurityException, NoSuchMethodException {
		return m_parent.getEntityClass().getMethod(getGetterName());
	}
	
	/* Helper Functions */
	
	/**
	 * Helper function to convert a string into its Getter-Method-name.
	 * @param str	string to be converted
	 * @return method name
	 */
	protected String toGetterName(String str) {
		return "get" + firstCharUpper(str);
	}
	
	/**
	 * Helper function to convert a string from Getter-Method-name
	 * to field name.
	 * @param str	string to be converted
	 * @return field name
	 */
	protected String toFieldName(String str) {
		return firstCharLower(str.substring(3));
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
