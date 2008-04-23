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
package org.codehaus.plexus.logging.console.test;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.FormattedLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ColourLogger;

import junit.framework.TestCase;

public class ColourTest extends TestCase {

    public void testColourLogger() {
        ColourLogger logger = new ColourLogger(AbstractLogger.LEVEL_DEBUG,
            "Test Logger");
        doLogging(logger);
    }
    
    private void doLogging(Logger logger) {
        logger.debug("Debug message.");
        logger.info("Information.");
        logger.warn("Warning!");
        logger.error("Error message.");
        logger.fatalError("Fatal error");
    }
    
    public void testManager() {
        FormattedLoggerManager manager = new FormattedLoggerManager("debug");
        Logger logger = manager.getLoggerForComponent("Testing");
        doLogging(logger);
    }
}
