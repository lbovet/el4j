/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.demos.distribution;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class demonstrates how the El4Ant distribution plugin is used to create
 * an executable distribution.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class DistributionDemo {

    /** The registered person beans. */
    public static final String[] PEOPLE = {"anna", "bernd", "homer"};
    
    /** The config locatons to load at the applications context's init. */
    public static final String[] CONFIG_LOCATIONS = {
        "classpath*:mandatory/*.xml",
        "classpath*:distributionOverride.xml"
    };
    
    /** The Application context. */
    private ApplicationContext m_appCtx;
    
    /**
     * Creates a new instance and initializes the application context.
     */
    public DistributionDemo() {
        m_appCtx = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS);
    }
    
    /**
     * Main method.
     * 
     * @param args
     *      Any arguments provided through the command line.
     */
    public static void main(String[] args) {
        DistributionDemo demo = new DistributionDemo();
        demo.start();
    }
    
    /**
     * Starts the analysis.
     */
    public void start() {
        for (int i = 0; i < PEOPLE.length; i++) {
            Person p = (Person) m_appCtx.getBean(PEOPLE[i]);
            
            System.out.println("Person [" + PEOPLE[i] + "] is called '"
                    + p.getName() + "' and is " + p.getAge() + " years old.");
        }
        System.out.println();
    }
}
