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
package ch.elca.el4j.tests.services.remoting.loadbalancing.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elca.el4j.tests.services.remoting.loadbalancing.client.random.LbRandomNoContextPassingTest;
import ch.elca.el4j.tests.services.remoting.loadbalancing.client.redirectuponfailure.LbClientRedirectUponFailureTest;
import ch.elca.el4j.tests.services.remoting.loadbalancing.client.roundrobin.LbRoundRobinTest;

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
@RunWith(Suite.class)
@Suite.SuiteClasses({
    LbRandomNoContextPassingTest.class,
    LbRoundRobinTest.class,
    LbClientRedirectUponFailureTest.class
    })
public class LoadBalancingTestSuite {
    
}
