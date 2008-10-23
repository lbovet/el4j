/*
 * Copyright 2006 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */
package ch.elca.el4j.util.objectwrapper.interfaces;

import java.util.Collection;

import ch.elca.el4j.util.objectwrapper.Wrappable;

/**
 * Apsect of having links to other objects. These links can be simple (fields containing the targets)
 * or collections containing targets.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://cvs.elca.ch/subversion/el4j-internal/trunk/sandbox/cacher/src/main/java/caching/aspects/LinkedAspect.java $",
 *    "$Revision: 1549 $",
 *    "$Date: 2008-07-30 14:25:13 +0200 (Wed, 30 Jul 2008) $",
 *    "$Author: dbd@ELCA.CH $"
 * );</script>
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
