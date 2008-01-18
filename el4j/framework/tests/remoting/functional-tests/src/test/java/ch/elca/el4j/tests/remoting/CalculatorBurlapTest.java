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
package ch.elca.el4j.tests.remoting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

/**
 * This class is a test for the calculator. <script
 * type="text/javascript">printFileStatus ("$URL:
 * https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/tests/remoting/functional-tests/src/test/java/ch/elca/el4j/tests/remoting/CalculatorBurlapTest.java
 * $", "$Revision$", "$Date: 2006-10-31 15:42:42 +0100 (Di, 31 Okt 2006)
 * $", "$Author$" );</script>
 * 
 * @author Martin Zeltner (MZE)
 * @author Waraich Rashid (RWA)
 */
public class CalculatorBurlapTest extends AbstractCalculatorTest {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(CalculatorBurlapTest.class);

    /**
     * {@inheritDoc}
     */
    ApplicationContext getContext() {
        return new ModuleApplicationContext(new String[] {
            "classpath*:mandatory/*.xml",
            "scenarios/client/remotingtests-burlap-client-config.xml"}, false);
    }

    /**
     * Test 'testAbilityToHandleEnumerations' disabled due burlap is not able to
     * handle enumerations. {@inheritDoc}
     */
    @Override
    public void testAbilityToHandleEnumerations() {
        s_logger.info("Test 'testAbilityToHandleEnumerations' disabled due"
            + "burlap is not able to handle enumerations.");
    }
}
