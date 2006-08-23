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

package ch.elca.el4j.util.codingsupport;


/**
 * This class supports methods to handle with objects. It covers only caps of 
 * class <code>org.springframework.util.ObjectUtils</code>.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public final class ObjectUtils {
    
    /**
     * Default constructor.
     */
    private ObjectUtils() {
    }
    
    /**
     * This method is used to compare two strings.
     * 
     * @param s1 Is the first string.
     * @param s2 Is the second string.
     * @return Returns true if s1 and s2 are null or empty and naturally if they
     *         are equals.
     */
    public static boolean nullSaveEquals(String s1, String s2) {
        if ((s1 == null || s1.length() == 0)
                && (s2 == null || s2.length() == 0)) {
            return true;
        } else {
            return s1 != null && s1.equals(s2);
        }
    }
    
    /**
     * Method which always returns a string object.
     * 
     * @param s
     *            Is the string to check.
     * @return Returns an empty string if given string object is null, otherwise
     *         the given string.
     */
    public static String asString(String s) {
        return s == null ? "" : s;
    }
}