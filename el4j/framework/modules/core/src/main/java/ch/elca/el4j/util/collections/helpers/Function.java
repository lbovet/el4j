/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.collections.helpers;

/**
 * A function.
 *
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @param <D> the function's domain
 * @param <R> the function's range
 * @author Adrian Moos (AMS)
 */
public interface Function<D, R> {
	/** returns this function's value at {@code d}.
	 * @param d the function's argument
	 * @return the function's return type
	 **/
	R apply(D d);
}