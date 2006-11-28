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

package ch.elca.el4j.demos.statistics.light;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//Checkstyle: UncommentedMain off

/**
 * This class is a demo that loads an application context, registers the light
 * statistics performance monitor and invokes some methods. The thread sleeps
 * after performing this steps for a long time. This allows to query JMX
 * (default URL: <code>http://localhost:9092/</code>).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class LightStatisticsDemo {

    /** Position of the mandatory configurations. */
    private static String s_mandatoryFiles = "classpath*:mandatory/*.xml";
    
    /** The test bean's name. */
    private static String s_bean = "bean";

    /**
     * Hide default constructor.
     */
    protected LightStatisticsDemo() {
    }
    
    /**
     * The demo main method. It computes three different fibonacci numbers and
     * sleeps for a second.
     * @param args Arguments provided by the command line.
     */
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext(
                s_mandatoryFiles);

        Foo bean = (Foo) ac.getBean(s_bean);
        
        // execute some operations
        // Checkstyle: MagicNumber off
        bean.fibonacci(20);
        bean.fibonacci(10);
        bean.sleepOneSecond();
        bean.fibonacci(15);
        // Checkstyle: MagicNumber on
        
        System.out.println("Waiting forever...");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//Checkstyle: UncommentedMain on
