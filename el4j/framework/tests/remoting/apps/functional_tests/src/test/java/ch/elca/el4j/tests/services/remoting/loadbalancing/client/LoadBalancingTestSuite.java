/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.services.remoting.loadbalancing.client;

import ch.elca.el4j.tests.services.remoting.loadbalancing.client.random.LbRandomNoContextPassingTest;
import ch.elca.el4j.tests.services.remoting.loadbalancing.client.roundrobin.LbRoundRobinTest;
import ch.elca.el4j.tests.services.remoting.loadbalancing.client.redirectuponfailure.LbClientRedirectUponFailureTest;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase ;

/**
 * 
 * Groups the test cases in this artifact into a test suite in order to ensure
 * that they are executed in a certain order.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Pleisch (SPL)
 */
public class LoadBalancingTestSuite extends TestCase {

    /** {@inheritDoc} */
    public static Test suite() {
        TestSuite suite = new TestSuite(
            "Test for ch.elca.el4j.tests.services.remoting.loadbalancing.client.random");
        //$JUnit-BEGIN$
        suite.addTestSuite(LbRandomNoContextPassingTest.class);
        suite.addTestSuite(LbRoundRobinTest.class);
        suite.addTestSuite(LbClientRedirectUponFailureTest.class);
        //$JUnit-END$

        return suite;
    } // suite()

} // CLASS LoadBalancingTestSuite
