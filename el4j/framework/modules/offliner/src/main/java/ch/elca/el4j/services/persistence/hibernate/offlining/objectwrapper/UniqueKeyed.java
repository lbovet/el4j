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
package ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.util.objectwrapper.Wrappable;



/**
 * Wrappable of having a unique key.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public interface UniqueKeyed extends Wrappable {

	/** 
	 * Get the UniqueKey. 
	 * @return The key.
	 */
	UniqueKey getUniqueKey();
	
	/**
	 * Set a key. 
	 * @param key The key.
	 * @throws IllegalArgumentException If the key does not apply to this object.
	 */
	void setUniqueKey(UniqueKey key) throws IllegalArgumentException;
	
	/**
	 * Get the local unique key. The object may have a local or remote key.
	 * @return The local unique key.
	 */
	UniqueKey getLocalUniqueKey();
}
