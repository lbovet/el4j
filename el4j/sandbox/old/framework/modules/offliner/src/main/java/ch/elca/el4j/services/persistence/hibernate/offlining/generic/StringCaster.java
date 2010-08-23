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

import java.util.LinkedList;
import java.util.List;

/**
 * This class uses the string strategies in a chain-of-command pattern to cast objects from/to
 * strings.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class StringCaster {

	/** The list of stretegies. */
	private List<StringSerializationStrategy> m_strategies;
	
	/**
	 * Constructor. Add the default strategies.
	 */
	public StringCaster() {
		m_strategies = new LinkedList<StringSerializationStrategy>();
		m_strategies.add(new StringStrategy());
		m_strategies.add(new IntegerStrategy());
		m_strategies.add(new LongStrategy());
	}
	
	/**
	 * Add a custom strategy.
	 * @param strategy The strategy.
	 */
	public void addStrategy(StringSerializationStrategy strategy) {
		m_strategies.add(strategy);
	}
	
	/**
	 * Create a string for an object.
	 * @param instance The object.
	 * @return The string.
	 * @throws IllegalArgumentException If no strategy is registered for the type.
	 */
	public String toString(Object instance) throws IllegalArgumentException {
		String string = null;
		for (StringSerializationStrategy strategy : m_strategies) {
			if (strategy.handledClass() == instance.getClass()) {
				string = strategy.identifier() + ":" 
					+ strategy.toString(instance);
			}
		}
		if (string == null) {
			throw new IllegalArgumentException("Class " + instance.getClass()
				+ " is not handled by any strategy.");
		}
		return string;
	}
	
	/**
	 * Recover an object from string.
	 * @param string The string.
	 * @return The object.
	 * @throws IllegalArgumentException If the class is not handled.
	 */
	public Object fromString(String string) throws IllegalArgumentException {
		Object obj = null;
		char identifier = string.substring(0, 1).toCharArray()[0];
		if (!":".equals(string.substring(1, 2))) {
			throw new IllegalArgumentException("Illegal string " + string
				+ ". String representations are "
				+ "id:value where id is one character.");
		}
		
		for (StringSerializationStrategy strategy : m_strategies) {
			if (identifier == strategy.identifier()) {
				obj = strategy.fromString(string.substring(2));
			}
		}
		
		if (obj == null) {
			throw new IllegalArgumentException("Identitier " + identifier
				+ " is not handled by any strategy.");
		}

		return obj;
	}
}
