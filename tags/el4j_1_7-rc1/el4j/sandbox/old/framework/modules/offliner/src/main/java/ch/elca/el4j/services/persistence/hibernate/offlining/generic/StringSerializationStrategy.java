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
package ch.elca.el4j.services.persistence.hibernate.offlining.generic;

/**
 * Strategy to represent an object as string and restore the object from the string representation.
 * Each instance represents a strategy for one class that can be queried via handledClass().
 * <p>
 * This class is a simpler form of serialization for value holder classes.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public interface StringSerializationStrategy {

	/**
	 * @return The class this strategy handles.
	 */
	Class<?> handledClass();
	
	/**
	 * @return The identifying character for this class.
	 */
	char identifier();
	
	/**
	 * Create a string representation for an instance.
	 * PRE : The object is of the class we handle.
	 * @param instance The instance.
	 * @return A string representing this instance.
	 */
	String toString(Object instance);
	
	/** 
	 * Recreate an instance from a string.
	 * @param string The string.
	 * @return An object of the handled class that fits the string.
	 */
	Object fromString(String string);
}
