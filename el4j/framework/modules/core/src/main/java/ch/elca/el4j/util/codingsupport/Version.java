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
package ch.elca.el4j.util.codingsupport;

/**
 * JDK Version utilities
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
public class Version {

    /**
     * @return Are we running in JDK 1.5 or later?
     */    
    public static boolean isJdk15OrNewer(){
        return (System.getProperty("java.specification.version").equals("1.5") ||
            isJdk16OrNewer());
    }
    
    /**
     * @return Are we running in JDK 1.6 or later?
     */
    public static boolean isJdk16OrNewer(){
        String versionString = System.getProperty("java.specification.version");
        
        // are we using JDK Version 1.10 or newer?
        boolean version110OrNewer =
            versionString.startsWith("1.1")&&(!versionString.equals("1.1"));
        
        // ensure we get an error with JDK 1.10 and later JDKs
        assert !version110OrNewer : " JDK is 1.10 or newer, update Version class";
        
        return (versionString.compareTo("1.6")<=0);
    }
    
}
