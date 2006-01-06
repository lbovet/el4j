/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.util.codingsupport;

import org.springframework.util.StringUtils;


/**
 * This class supports methods to handle with numbers. It covers only caps
 * of class <code>org.springframework.util.NumberUtils</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public final class NumberUtils {
    
    /**
     * Hidden constructor.
     */
    private NumberUtils() { }

    /**
     * Method to test if a given value is inside the given boundaries.
     * 
     * @param value
     *            Is the given value to test.
     * @param min
     *            Is the minimum that the value can be.
     * @param max
     *            Is the maximum that the value can be.
     * @return Returns true if <code>value</code> is greater or equals than
     *         minimum and is less or equals than maximum.
     */
    public static boolean isNumberInsideBoundaries(
        int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Method to test if a given value is inside the given boundaries.
     * 
     * @param value
     *            Is the given value to test.
     * @param min
     *            Is the minimum that the value can be.
     * @param max
     *            Is the maximum that the value can be.
     * @return Returns true if <code>value</code> is greater or equals than
     *         minimum and is less or equals than maximum.
     */
    public static boolean isNumberInsideBoundaries(
        long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * Method to test if a given value is inside the given boundaries.
     * 
     * @param value
     *            Is the given value to test.
     * @param min
     *            Is the minimum that the value can be.
     * @param max
     *            Is the maximum that the value can be.
     * @return Returns true if <code>value</code> is greater or equals than
     *         minimum and is less or equals than maximum.
     */
    public static boolean isNumberInsideBoundaries(
        double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Method to parse a string to an integer.
     * 
     * @param s
     *            Is the string to parse.
     * @return Return the <code>Integer</code> object if success else
     *         <code>null</code>.
     */
    public static Integer parseToInteger(String s) {
        Integer result = null;
        if (StringUtils.hasText(s)) {
            try {
                int number = Integer.parseInt(s.trim());
                result = new Integer(number);
            } catch (NumberFormatException e) {
                result = null;
            }
        }
        return result;
    }
    
    /**
     * Method to parse a string to a long.
     * 
     * @param s
     *            Is the string to parse.
     * @return Return the <code>Long</code> object if success else
     *         <code>null</code>.
     */
    public static Long parseToLong(String s) {
        Long result = null;
        if (StringUtils.hasText(s)) {
            try {
                long number = Long.parseLong(s.trim());
                result = new Long(number);
            } catch (NumberFormatException e) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Method to parse a string to a double.
     * 
     * @param s
     *            Is the string to parse.
     * @return Return the <code>Double</code> object if success else
     *         <code>null</code>.
     */
    public static Double parseToDouble(String s) {
        Double result = null;
        if (StringUtils.hasText(s)) {
            try {
                double number = Double.parseDouble(s.trim());
                result = new Double(number);
            } catch (NumberFormatException e) {
                result = null;
            }
        }
        return result;
    }
}
