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
package ch.elca.el4j.demos.gui.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elca.el4j.demos.gui.service.CacheableService;

/**
 * The implementation of the cacheable service.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philipp Brüschweiler (PBW)
 */
public class CacheableServiceImpl implements CacheableService {
	
	/** The logger. */
	private static final Logger s_log
		= LoggerFactory.getLogger(CacheableServiceImpl.class);

	@Override
	public int computeResult(int input) {
		s_log.info("I'm on it!");
		try {
			Thread.sleep(2000);
			s_log.info("Just a little more...");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			s_log.warn("Interrupted while sleeping. This shouldn't happen...");
		}
		s_log.info("Done!");
		return input + 5;
	}
	
	@Override
	public int computeResultCached(int input) {
		// The caching is handled by the annonations in caching.xml
		return computeResult(input);
	}

	@Override
	public void deleteCaches() {
		// Noop. Let the proxy handle it.
	}

}
