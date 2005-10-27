/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.security.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The server part for <code>AuthorizationTestDistributed</code>.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class AuthorizationServer {

    /** The application context. */
    private static ApplicationContext s_appContext;
    
    /**
     * The main method.
     * 
     * @param args
     *      Command line parameters.
     */
    public static void main(String[] args) {
        s_appContext = new ClassPathXmlApplicationContext(args);
        String[] str = s_appContext.getBeanDefinitionNames();
        for (int i = 0; i < str.length; i++) {
            System.out.println(str[i]);

        }
    }
    
}