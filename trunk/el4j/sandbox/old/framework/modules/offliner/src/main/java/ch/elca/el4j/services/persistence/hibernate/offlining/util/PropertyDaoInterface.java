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


/**
 * DAO for hibernate access to properties. We need an interface/impl pair as 
 * our dao might be proxied.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public interface PropertyDaoInterface {
	
	/**
	 * Check if a property exists.
	 * @param name The property name.
	 * @return <code>true</code> if the property exists.
	 */
	boolean isPropertyPresent(String name);
	
	/**
	 * Get an integer-valued property.
	 * @param name The property name.
	 * @return The property value.
	 */
	int getIntProperty(String name);
	
	/**
	 * Get a long-valued property.
	 * @param name The property name.
	 * @return The property value.
	 */
	long getLongProperty(String name);
	
	/**
	 * Get a string-valued property.
	 * @param name The property name.
	 * @return The property value.
	 */
	String getStringProperty(String name);
	
	/**
	 * Save a property.
	 * @param name The property name.
	 * @param value The property value.
	 */
	void saveProperty(String name, String value);
	
	/**
	 * Save a property.
	 * @param name The property name.
	 * @param value The property value.
	 */
	void saveProperty(String name, int value);
	
	/**
	 * Save a property.
	 * @param name The property name.
	 * @param value The property value.
	 */
	void saveProperty(String name, long value);
}
