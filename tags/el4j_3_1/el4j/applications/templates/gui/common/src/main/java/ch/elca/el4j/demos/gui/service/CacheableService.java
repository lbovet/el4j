/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.gui.service;

/**
 * A very simple service to demonstrate EhCache setup and usage.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philipp Brüschweiler (PBW)
 */
public interface CacheableService {
	
	/**
	 * Compute some function that takes a long time.
	 * @param input The input.
	 * @return The result.
	 */
	int computeResult(int input);

	/**
	 * Same as above, this method is supposed to be cached by the instantiator.
	 * @param input The input.
	 * @return The result.
	 */
	int computeResultCached(int input);
	
	/**
	 * Delete the caches.
	 */
	void deleteCaches();
}
