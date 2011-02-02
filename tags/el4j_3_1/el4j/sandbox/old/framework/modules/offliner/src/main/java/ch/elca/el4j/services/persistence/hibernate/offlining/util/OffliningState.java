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
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

/**
 * The different states (for synchronization) a offlined object can be in.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public enum OffliningState {

	/** Created in the local db. */
	NEW,
	
	/** Created by offline() and unchanged in the local db since then. */
	OFFLINED,
	
	/** Changed in the local db since the last synchronization. */
	CHANGED,
	
	/** Deleted locally. */
	DELETED,
	
	/** Processed during the current synchronization already. */
	PROCESSED,
	
	/** Conflict occurred during the current synchronization. */
	CONFLICTED
}
