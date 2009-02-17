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
 * Strategy for string class. This is required to fit the strategy/chain of command pattern;
 * it does not do anything interesting.
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
public class StringStrategy implements StringSerializationStrategy {

	/** {@inheritDoc} */
	public Object fromString(String string) {
		return string;
	}

	/** {@inheritDoc} */
	public Class<?> handledClass() {
		return String.class;
	}

	/** {@inheritDoc} */
	public String toString(Object instance) {
		return (String) instance;
	}

	/** {@inheritDoc} */
	public char identifier() {
		return 'S';
	}

}
