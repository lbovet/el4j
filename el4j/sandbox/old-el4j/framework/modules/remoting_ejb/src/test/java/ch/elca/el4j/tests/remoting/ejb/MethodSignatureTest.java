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

package ch.elca.el4j.tests.remoting.ejb;

import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;

import org.springframework.jdbc.object.SqlFunction;

import ch.elca.el4j.services.remoting.protocol.ejb.generator.MethodSignature;

import junit.framework.TestCase;

/**
 * Tests the method signature implementation used in the EJB session bean
 * generator.
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
public class MethodSignatureTest extends TestCase {

    /**
     * Sample interface used to drive this tests.
     */
    interface Foo {
        /**
         * Foo method.
         * 
         * @param a Is first parameter.
         * @param b Is second parameter.
         * @param c Is third parameter.
         * @param d Is fourth parameter.
         * @return Returns a foo value.
         * @throws Exception If something occurs.
         */
        public int foo(int a, float b, String c, Double d) throws Exception;
        
        /**
         * Bar method.
         * 
         * @param f Is the sql function.
         * @return Returns something.
         * @throws IllegalStateException If bla.
         * @throws ConcurrentModificationException If bli.
         */
        public String[] bar(SqlFunction f) throws IllegalStateException,
            ConcurrentModificationException;
        
        /**
         * Another bar method.
         * 
         * @param a Is a four dimensional array.
         * @param b Is a number.
         * @param c Is a three dimensional array.
         * @return Returns a two dimensional array.
         */
        public Object[][] bar(Object[][][][] a, int b, char[][][] c);
    }
    
    /**
     * Tests the signature of a simple method.
     * 
     * @throws Exception
     *      Whenever something goes wrong.
     */
    public void testSimpleMethodSignature() throws Exception {
        Method m = Foo.class.getMethod("foo",
                new Class[] {
                    int.class,
                    float.class,
                    String.class,
                    Double.class});
        
        MethodSignature s = new MethodSignature(m, false, false, null);
        
        assertEquals("Illegal method signature!", "public int foo(int arg0,"
                + " float arg1, java.lang.String arg2, java.lang.Double arg3)"
                + " throws java.lang.Exception", s.toString());
        
        assertEquals("Illegal parameter type list", "int.class, float.class,"
                + " java.lang.String.class, java.lang.Double.class",
                s.getArgTypesAsList());
        
        assertEquals("Illegal parameter list", "new java.lang.Integer(arg0),"
                + " new java.lang.Float(arg1), arg2, arg3", s.getArgsAsList());
    }
    
    /**
     * Tests a method that has arrays as arguments and that returns an array.
     * 
     * @throws Exception
     *      Whenever something goes wrong.
     */
    public void testComplexMethodSignature() throws Exception {
        Method m = Foo.class.getMethod("bar",
                new Class[] {
                    Object[][][][].class,
                    int.class,
                    char[][][].class});
        
        MethodSignature s = new MethodSignature(m, false, false, null);
        
        assertEquals("Illegal method signature!", "public "
                + "java.lang.Object[][] bar(java.lang.Object[][][][] arg0, "
                + "int arg1, char[][][] arg2)", s.toString());
        
        assertEquals("Illegal parameter type list",
                "java.lang.Object[][][][].class, int.class, char[][][].class",
                s.getArgTypesAsList());
        
        assertEquals("Illegal parameter list",
                "arg0, new java.lang.Integer(arg1), arg2", s.getArgsAsList());
    }
    
    /**
     * Tests whether runtime exceptions are wrapped by WrapperExceptions.
     * 
     * @throws Exception
     *      Whenever something goes wrong.
     */
    public void testRTMethodSiganture() throws Exception {
        Method m = Foo.class.getMethod("bar",
                new Class[] {SqlFunction.class});
        
        MethodSignature s = new MethodSignature(m, false, true, null);
        
        assertEquals("Illegal method signature!", "public "
                + "java.lang.String[] bar(org.springframework.jdbc.object."
                + "SqlFunction arg0) throws java.lang.IllegalStateException, "
                + "java.util.ConcurrentModificationException, "
                + "ch.elca.el4j.services.remoting.protocol.ejb.exception."
                + "WrapperException", s.toString());
        
        assertEquals("Illegal parameter type list",
                "org.springframework.jdbc.object.SqlFunction.class",
                s.getArgTypesAsList());
        
        assertEquals("Illegal parameter list", "arg0", s.getArgsAsList());
    }
}
