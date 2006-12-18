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

// Checkstyle: EmptyBlock off
// Checkstyle: MagicNumber off

package ch.elca.el4j.tests.remoting;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.tests.remoting.service.Calculator;

/**
 * This class is a test for the calculator.
 * It uses the XFire Protocol with a Jaxb binding. The service is then generated
 * using the {@link JAXBServiceFactory} to export the annotated web service.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
public class CalculatorXFireJaxbAnnotatedTest extends CalculatorXFireTest {

    /**
     * {@inheritDoc}
     */
    public void setUp() {
        ApplicationContext appContext = new ModuleApplicationContext(
            new String[] {"classpath*:mandatory/*.xml",
                "scenarios/client/remotingtests-xfire-client-config.xml"}, 
                false);
        setCalc((Calculator) appContext.getBean("calculatorJaxbAnnotated"));
        
        // TODO: Control me regularly
        // Remove the proxy manually, until 
        // http://jira.codehaus.org/browse/XFIRE-401 really is resolved
        System.clearProperty("http.proxyHost");
    }
}
