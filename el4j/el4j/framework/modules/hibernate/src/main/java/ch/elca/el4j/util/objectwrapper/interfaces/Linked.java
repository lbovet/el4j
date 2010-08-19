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
package ch.elca.el4j.util.objectwrapper.interfaces;

import java.util.Collection;

import ch.elca.el4j.util.objectwrapper.Wrappable;

/**
 * Apsect of having links to other objects. These links can be simple (fields containing the targets)
 * or collections containing targets.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public interface Linked extends Wrappable {

	/**
	 * @return The names of all (simple) links this object has.
	 */
	String[] getLinkNames();
	
	/**
	 * @return The names of all collection links this object has.
	 */
	String[] getCollectionLinkNames();
	
	/**
	 * Get a linked object by name.
	 * @param linkName The link name (from getLinkNames).
	 * @return The linked object.
	 */
	Object getlinkByName(String linkName);
	
	/**
	 * Get a collection of links by name.
	 * @param name The collection link name.
	 * @return The collection.
	 */
	Collection<?> getCollectionLinkByName(String name);
	
	/**
	 * Convenience method that returns all linked objects regardless of their location. 
	 * @return All linked objects.
	 */
	Object[] getAllLinked();
}
