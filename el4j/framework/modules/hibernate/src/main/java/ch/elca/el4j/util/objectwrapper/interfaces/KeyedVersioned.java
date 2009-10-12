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

import java.io.Serializable;

import ch.elca.el4j.util.objectwrapper.Wrappable;

/**
 * Aspect of having a key and version.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/KeyedVersionedAspect.java $",
 *    "$Revision: 1571 $",
 *    "$Date: 2008-08-06 10:49:49 +0200 (Wed, 06 Aug 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public interface KeyedVersioned extends Wrappable {

	/**
	 * @return The key this object holds.
	 */
	Serializable getKey();
	
	/**
	 * Set the object's key.
	 * @param key The key to set.
	 */
	void setKey(Serializable key);
	
	/**
	 * @return The version.
	 */
	Serializable getVersion();
	
	/**
	 * Set the version.
	 * @param version The version to set.
	 */
	void setVersion(Serializable version);
	
	/**
	 * @return The effective class of the key property.
	 */
	Class<?> getKeyClass();
	
	/**
	 * @return The effective class of the version property.
	 */
	Class<?> getVersionClass();
}
