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

package ch.elca.el4j.tests.services.security.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sample service implementation.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Pfenninger (APR)
 */
public class SampleServiceImpl implements SampleService {
	
	/** The static logger. */
	private static Logger s_logger = LoggerFactory.getLogger(SampleServiceImpl.class);
	
	/**
	 * {@inheritDoc}
	 */
	public int addOne(int i) {
		int result = i + 1;
		s_logger.debug(i + " + 1 = " + result);
		return result;
	}

}
