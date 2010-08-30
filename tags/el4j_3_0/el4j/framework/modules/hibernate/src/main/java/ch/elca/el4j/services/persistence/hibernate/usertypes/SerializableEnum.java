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
package ch.elca.el4j.services.persistence.hibernate.usertypes;

/**
 * @deprecated As of release 1.7.
 * 		Has been moved to package ch.elca.el4j.util.codingsupport in module-core.
 * 
 * This interface is required to persist an enum type using 
 * {@link GenericEnumUserType}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @param <T>    enum value type
 *
 * @author Stefan Wismer (SWI)
 */
public interface SerializableEnum<T> {
	/**
	 * @deprecated As of release 1.7.
	 * 		The interface has been moved to package ch.elca.el4j.util.codingsupport in module-core.
	 * @return    the value of the enum
	 */
	public T getValue();
}
