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
 * Strategy for integers.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class IntegerStrategy implements StringSerializationStrategy {

	/** {@inheritDoc} */
	public Object fromString(String string) {
		return Integer.parseInt(string);
	}

	/** {@inheritDoc} */
	public Class<?> handledClass() {
		return Integer.class;
	}

	/** {@inheritDoc} */
	public String toString(Object instance) {
		return Integer.toString((Integer) instance);
	}

	/** {@inheritDoc} */
	public char identifier() {
		return 'I';
	}
}
