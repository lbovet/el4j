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

import java.lang.reflect.Method;

import org.springframework.util.StringUtils;

/**
 * This class helps transforming a class or class related structures into a
 * string representation.
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
public final class ClassUtils {
    
    /** Char used to mark a boolean array. */
    private static final char BOOLEAN_ELEMENT = 'Z';
    
    /** Char used to mark a byte array. */
    private static final char BYTE_ELEMENT = 'B';
    
    /** Char used to mark a char array. */
    private static final char CHAR_ELEMENT = 'C';
    
    /** Char used to mark a double array. */
    private static final char DOUBLE_ELEMENT = 'D';
    
    /** Char used to mark a float array. */
    private static final char FLOAT_ELEMENT = 'F';
    
    /** Char used to mark a int array. */
    private static final char INT_ELEMENT = 'I';
    
    /** Char used to mark a long array. */
    private static final char LONG_ELEMENT = 'J';
    
    /** Char used to mark a short array. */
    private static final char SHORT_ELEMENT = 'S';
    
    /** Char used to mark a class array. */
    private static final char CLASS_INTERFACE_ELEMENT = 'L';
    
    /** Char used to specify the array's dimension. */
    private static final char ARRAY_DIM_SYMBOL = '[';
    
    /**
     * Hides default constructor.
     */
    private ClassUtils() { }
    
    /**
     * Composes the canonical name of the given class. Arrays are transformed
     * into the same string representation as it is needed to define them
     * in Java (e.g. <code>java.lang.String[][]</code>).
     * 
     * @param clazz
     *      The class which canonical name has to be computed.
     *      
     * @return Returns the canonical name of the given class.
     */
    public static String getCanonicalClassName(Class clazz) {
        String name = clazz.getName();
        
        int arrayDimension = name.lastIndexOf(ARRAY_DIM_SYMBOL) + 1;
        if (arrayDimension > 0) {
            char element = name.charAt(arrayDimension);
            switch (element) {
                case BOOLEAN_ELEMENT: 
                    name = boolean.class.getName(); break;
                case BYTE_ELEMENT:
                    name = byte.class.getName(); break;
                case CHAR_ELEMENT:
                    name = char.class.getName(); break;
                case DOUBLE_ELEMENT:
                    name = double.class.getName(); break;
                case FLOAT_ELEMENT:
                    name = float.class.getName(); break;
                case INT_ELEMENT:
                    name = int.class.getName(); break;
                case LONG_ELEMENT:
                    name = long.class.getName(); break;
                case SHORT_ELEMENT:
                    name = short.class.getName(); break;
                case CLASS_INTERFACE_ELEMENT: 
                    name = name.substring(arrayDimension + 1,
                            name.length() - 1);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }
            
            for (int i = 0; i < arrayDimension; i++) {
                name += "[]";
            }
        }
        
        return name;
    }
    
    /**
     * Determines the class' package name.
     * 
     * @param clazz
     *      The class which package name has to be determined.
     *      
     * @return Returns the given class' package name.
     */
    public static String getPackageName(Class clazz) {
        String pkgName;
        if (clazz.getPackage() != null 
                && StringUtils.hasText(clazz.getPackage().getName())) {
            pkgName = clazz.getPackage().getName();
        } else {
            String className = clazz.getName();
            pkgName = className.substring(0, className.lastIndexOf('.'));
        }
        return pkgName;
    }
    
    /**
     * Transforms the given method into a string representation.
     * 
     * @param method
     *      The method to transform.
     *      
     * @return Returns a string representation of the given method's signature.
     */
    public static String getMethodSignature(Method method) {
        Class[] types = method.getParameterTypes();
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(method.getName());
        buffer.append("(");
        
        // add parameters types
        for (int i = 0; i < types.length; i++) {
            buffer.append(getCanonicalClassName(types[i]));
            
            if (i < types.length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append(")");
        
        return buffer.toString();
    }
}
