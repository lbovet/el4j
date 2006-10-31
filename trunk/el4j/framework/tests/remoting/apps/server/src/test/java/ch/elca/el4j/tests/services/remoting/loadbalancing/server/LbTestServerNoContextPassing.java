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
package ch.elca.el4j.tests.services.remoting.loadbalancing.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext ;

import ch.elca.el4j.tests.services.remoting.loadbalancing.common.BusinessObject;
import ch.elca.el4j.core.context.ModuleApplicationContext;


/**
 * Defines the server used to test the idempotent invocation module. Launches a
 * business object whose unique purpose is to illustrate the behavior of the
 * module. <script type="text/javascript">printFileStatus ("$URL$",
 * "$Revision$", "$Date$", "$Author$" );</script>
 * 
 * @author Stefan Pleisch (SPL)
 */
public class LbTestServerNoContextPassing {

    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(LbTestServerNoContextPassing.class);

    /** {@inheritDoc} */
    public static void main(String args[]) {

/**
        // This is a hack, because the ModuleApplicationContext does not work
        // with the antrun plugin.
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"classpath*:mandatory/*.xml",
            "classpath:loadbalancing/server/startup-nocontextpassing.xml",
        "classpath*:loadbalancing/remoting/rmi-nocontext-protocol-config.xml"});
*/       
        ApplicationContext applicationContext = new ModuleApplicationContext(
            new String[] {"classpath*:mandatory/*.xml",
                "classpath:loadbalancing/server/startup-nocontextpassing.xml",
                "classpath*:loadbalancing/remoting/rmi-nocontext-protocol-config.xml"},
            (String[]) null, 
            false, 
            (ApplicationContext) null);

        s_logger.debug("Starting up ....");

//        BusinessObject obj = (BusinessObject)applicationContext.getBean("rmiTestObjExporter");
        BusinessObject obj = (BusinessObject)applicationContext.getBean("rmiTestObjImpl");


        int iterations = 1;
        while (iterations < LbServerConstants.NBR_ITERATIONS) {

            try {
                // 100s
                Thread.sleep(LbServerConstants.SLEEPING_TIME);
            } catch (Exception e) {
                System.err.println("Problem:");
                e.printStackTrace();
                System.exit(-1);
            }

            iterations += 1;
            s_logger.debug("Looping around, iteration: " + iterations);

        } // while

        s_logger.debug("Done.");
        System.exit(0);
    } // main()
}
