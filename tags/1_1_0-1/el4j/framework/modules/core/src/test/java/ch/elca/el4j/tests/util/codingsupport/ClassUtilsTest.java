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

package ch.elca.el4j.tests.util.codingsupport;

import java.util.ArrayList;

import ch.elca.el4j.util.codingsupport.ClassUtils;

import junit.framework.TestCase;

/**
 * This class tests the class utilities.
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
public class ClassUtilsTest extends TestCase {
    
    /**
     * Tests that a boolean array is properly converted into a string.
     */
    public void testBooleanArray() {
        assertEquals("boolean[]",
                ClassUtils.getCanonicalClassName((new boolean[2]).getClass()));
    }
    
    /**
     * Tests that a byte array is properly converted into a string.
     */
    public void testByteArray() {
        assertEquals("byte[]",
                ClassUtils.getCanonicalClassName((new byte[2]).getClass()));
    }
    
    /**
     * Tests that a char array is properly converted into a string.
     */
    public void testCharArray() {
        assertEquals("char[]",
                ClassUtils.getCanonicalClassName((new char[2]).getClass()));
    }
    
    /**
     * Tests that a double array is properly converted into a string.
     */
    public void testDoubleArray() {
        assertEquals("double[]",
                ClassUtils.getCanonicalClassName((new double[2]).getClass()));
    }
    
    /**
     * Tests that a float array is properly converted into a string.
     */
    public void testFloatArray() {
        assertEquals("float[]",
                ClassUtils.getCanonicalClassName((new float[2]).getClass()));
    }
    
    /**
     * Tests that a int array is properly converted into a string.
     */
    public void testIntArray() {
        assertEquals("int[]",
                ClassUtils.getCanonicalClassName((new int[2]).getClass()));
    }
    
    /**
     * Tests that a long array is properly converted into a string.
     */
    public void testLongArray() {
        assertEquals("long[]",
                ClassUtils.getCanonicalClassName((new long[2]).getClass()));
    }
    
    /**
     * Tests that a short array is properly converted into a string.
     */
    public void testShortArray() {
        assertEquals("short[]",
                ClassUtils.getCanonicalClassName((new short[2]).getClass()));
    }
    
    /**
     * Tests that a multi dimensional int array is properly converted into a
     * string.
     */
    public void testIntMultiDimArray() {
        assertEquals("int[][][][]",
                ClassUtils.getCanonicalClassName(
                        (new int[2][2][2][2]).getClass()));
    }
    
    /**
     * Tests that an object array is properly converted into a string.
     */
    public void testObjArray() {
        assertEquals("java.lang.Object[]",
                ClassUtils.getCanonicalClassName((new Object[2]).getClass()));
    }
    
    /**
     * Tests that an array of ArrayLists is properly converted into a string.
     */
    public void testArrayListArray() {
        assertEquals("java.util.ArrayList[]",
                ClassUtils.getCanonicalClassName(
                        (new ArrayList[2]).getClass()));
    }
    
    /**
     * Tests that a multi dimensional object array is properly converted into a
     * string.
     */
    public void testMultyDimObjArray() {
        assertEquals("java.lang.Object[][][][][]",
                ClassUtils.getCanonicalClassName(
                        (new Object[2][2][2][2][2]).getClass()));
    }
    
    /**
     * Tests that an int is properly converted into a string.
     */
    public void testInt() {
        assertEquals("int", ClassUtils.getCanonicalClassName(int.class));
    }
    
    /**
     * Tests that an object is properly converted into a string.
     */
    public void testObject() {
        assertEquals("java.lang.Object",
                ClassUtils.getCanonicalClassName(Object.class));
    }
}
