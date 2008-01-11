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
package ch.elca.el4j.tests.util.codingsupport;

import ch.elca.el4j.util.codingsupport.Version;

import junit.framework.TestCase;

/** 
 * 
 * For full tests, run this class in JDK 5 and 6
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philipp Oser (POS)
 */
public class VersionTest extends TestCase {

    public void testForJdk15(){
        if (System.getProperty("java.specification.version").equals("1.5")){
            assertEquals(true,Version.isJdk15OrNewer());
            assertEquals(false,Version.isJdk16OrNewer());
        }
    }

    public void testForJdk16(){
        if (System.getProperty("java.specification.version").equals("1.6")){
            assertEquals(true,Version.isJdk16OrNewer());            
            assertEquals(true,Version.isJdk15OrNewer());
        }
    }
       
}
