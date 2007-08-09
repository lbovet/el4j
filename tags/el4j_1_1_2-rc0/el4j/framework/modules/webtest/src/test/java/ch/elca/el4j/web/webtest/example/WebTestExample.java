/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.web.webtest.example;

import net.sourceforge.jwebunit.TestingEngineRegistry;
import net.sourceforge.jwebunit.WebTestCase;

/**
 * This class is an example for implementing web tests.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Florian Suess (FLS)
 */
public class WebTestExample extends WebTestCase {
    /**
     * Sets up the test fixture.
     * (Called before every test case method.)
     */
    public void setUp() {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        getTestContext().setBaseUrl("http://www.elca.ch");
    }

    /**
     * Tests ELCA homepage.
     *
     * @throws Exception If general execption occured.
     */
    public void testElcaURL() throws Exception {
        beginAt("/");
        assertTitleEquals("ELCA: Technology-Consulting-Innovation: "
            + "job opportunities: careers: Informatiker: software engineer: "
            + "Programmierer: jobs: software design: business strategy: crm: "
            + "web design: data warehouse: edm");
        assertTextPresent("ELCA is a leading Swiss IT solutions provider");
        assertLinkPresentWithText("Newsletter");
    }
}