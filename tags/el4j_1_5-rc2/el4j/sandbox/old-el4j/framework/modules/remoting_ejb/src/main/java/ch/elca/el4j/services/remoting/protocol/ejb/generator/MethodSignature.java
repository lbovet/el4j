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

package ch.elca.el4j.services.remoting.protocol.ejb.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.remoting.protocol.ejb.exception.WrapperException;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletException;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletTagGenerator;
import ch.elca.el4j.util.codingsupport.ClassUtils;

/**
 * This class provides meta informations for methods and contains methods to
 * simplify java source code generation.
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
public class MethodSignature {

    /** The method used to get the primitive value of an Integer. */
    public static final String INT_TRANSFORMER = ".intValue()";
    
    /** The method used to get the primitive value of a Float. */
    public static final String FLOAT_TRANSFORMER = ".floatValue()";
    
    /** The method used to get the primitive value of a Double. */
    public static final String DOUBLE_TRANSFORMER = ".doubleValue()";
    
    /** The method used to get the primitive value of a Character. */
    public static final String CHAR_TRANSFORMER = ".charValue()";
    
    /** The method used to get the primitive value of a Byte. */
    public static final String BYTE_TRANSFORMER = ".byteValue()";
    
    /** The method used to get the primitive value of a Short. */
    public static final String SHORT_TRANSFORMER = ".shortValue()";
    
    /** The method used to get the primitive value of a Long. */
    public static final String LONG_TRANSFORMER = ".longValue()";
    
    /** The method used to get the primitive value of a Boolean. */
    public static final String BOOLEAN_TRANSFORMER = ".booleanValue()";
    
    /** Constant string used to create the exception handler. */
    public static final String TARGET_INSTANCEOF = "target instanceof ";
    
    /** String constant representing the logical OR operator. */
    public static final String OR = " || ";
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(MethodSignature.class);
    
    /** The method to provide meta info. */
    private Method m_method;
    
    /** True if context passing is enabled. */
    private boolean m_contextPassing;
    
    /** Whether to wrap runtime exceptions to send them to the client. */
    private boolean m_wrapRTExceptions;
    
    /** The XDoclet tag generator instance that builds the method's tags. */
    private XDocletTagGenerator m_xDocletTagGenerator;
    
    /**
     * Crates a new instance.
     * 
     * @param method
     *      The method to build meta informations for.
     *      
     * @param contextPassing
     *      Whether context passing is enabled.
     *      
     * @param wrapRTExceptions
     *      Whether runtime exceptions have to be wrapped into a
     *      WrapperException.
     * 
     * @param xDocletTagGenerator
     *      The XDoclet tag generator responsible for creating tags for this
     *      method.
     */
    public MethodSignature(Method method, boolean contextPassing,
            boolean wrapRTExceptions, XDocletTagGenerator xDocletTagGenerator) {
        this.m_method = method;
        this.m_contextPassing = contextPassing;
        this.m_wrapRTExceptions = wrapRTExceptions;
        this.m_xDocletTagGenerator = xDocletTagGenerator;
    }
    
    /**
     * Looks up the wrapper as string for the given primitive type.
     * 
     * @param primitiveType
     *      The primitive type's type.
     *      
     * @return Returns the wrapper type of the primitive type.
     * int -> Integer
     * float -> Float
     *   ...
     */
    private String getWrapperType(Class primitiveType) {
        String result;
        if (primitiveType == int.class) {
            result = Integer.class.getName();
        } else if (primitiveType == float.class) {
            result = Float.class.getName();
        } else if (primitiveType == double.class) {
            result = Double.class.getName();
        } else if (primitiveType == char.class) {
            result = Character.class.getName();
        } else if (primitiveType == byte.class) {
            result = Byte.class.getName();
        } else if (primitiveType == short.class) {
            result = Short.class.getName();
        } else if (primitiveType == long.class) {
            result = Long.class.getName();
        } else if (primitiveType == boolean.class) {
            result = Boolean.class.getName();
        } else {
            result = Void.class.getName();
        }
        return result;
    }
    
    /**
     * @param primitiveType The primitive class.
     * @return Returns the wrapper method to retrieve the primitive
     *         value of the given primitive type. For example:
     *         getWrapperTypeValueMethod(int) -> ".intValue()".
     */
    private String getWrapperTypeValueMethod(Class primitiveType) {
        String result;
        if (primitiveType == int.class) {
            result = INT_TRANSFORMER;
        } else if (primitiveType == float.class) {
            result = FLOAT_TRANSFORMER;
        } else if (primitiveType == double.class) {
            result = DOUBLE_TRANSFORMER;
        } else if (primitiveType == char.class) {
            result = CHAR_TRANSFORMER;
        } else if (primitiveType == byte.class) {
            result = BYTE_TRANSFORMER;
        } else if (primitiveType == short.class) {
            result = SHORT_TRANSFORMER;
        } else if (primitiveType == long.class) {
            result = LONG_TRANSFORMER;
        } else if (primitiveType == boolean.class) {
            result = BOOLEAN_TRANSFORMER;
        } else {
            result = "";
        }
        return result;
    }
    
    /**
     * @return Returns the types of the wrapped method's argument.
     *      <code>type_1, type_2, ..., type_n</code>
     */
    public String getArgTypesAsList() {
        return classArray2ClassNameString(
                m_method.getParameterTypes(), ".class");
    }
    
    /**
     * Transforms an array of classes into a string representation consisting
     * of the classes' fully qualified names, comma separated.
     * 
     * @param classes
     *      The classes to get their names from.
     *      
     * @param suffix
     *      The suffix to add to each class' string representation.
     *      
     * @return Returns a string consisting of the classes fully qualified names,
     *      extended by the given suffix.
     */
    private String classArray2ClassNameString(Class[] classes, String suffix) {
        StringBuffer buffer = new StringBuffer();
        // add exceptions (if any)
        if (classes.length > 0) {
            for (int i = 0; i < classes.length; i++) {
                buffer.append(ClassUtils.getCanonicalClassName(classes[i]));
                buffer.append(suffix);
                if (i < classes.length - 1) {
                    buffer.append(", ");
                }
            }
        }
        return buffer.toString();
    }
    /**
     * @return Returns the arguments of the wrapped method. Object types are
     *      simply appended where simple types are wrapped:
     *      <code>obj_arg_0, ..., obj_arg_i, ...,
     *      new wrapperType(arg_j), ...</code>
     */
    public String getArgsAsList() {
        
        StringBuffer buffer = new StringBuffer();
        
        Class[] parameterTypes = m_method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            
            if (parameterTypes[i].isPrimitive()) {
                String wrapperType = getWrapperType(parameterTypes[i]);
                buffer.append("new " + wrapperType + "(arg" + i + ")");
                
            } else {
                if (m_contextPassing && (i == (parameterTypes.length - 1))) {
                    buffer.append("contexts");
                } else {
                    buffer.append("arg" + i);
                }
            }
            
            if (i < parameterTypes.length - 1) {
                buffer.append(", ");
            }
        }
        
        return buffer.toString();
    }

    /**
     * @return Returns the name of the method.
     */
    public String getName() {
        return m_method.getName();
    }
    
    
    /**
     * @return If this method returns a primitive type, getCastType returns the
     *         wrapper type. For example, if this method returns an int,
     *         getCastType() will return "(Integer)". If this method does not
     *         return a primitive type, it simply returns the return type.
     */
    public String getCastType() {
        Class returnType = m_method.getReturnType();
        if (returnType.isPrimitive()) {
            return getWrapperType(returnType);
        } else {
            return ClassUtils.getCanonicalClassName(returnType);
        }
    }
    
    /**
     * @return If this method returns a primitive type, getPrimitiveValue
     *         returns the method (as a string) to retrieve the primitive value
     *         of the wrapper type. For example, if this method return type is
     *         int, getPrimitiveValue returns ".intValue()". If this method
     *         return type is not primitive, getPrimitiveValue returns the
     *         empty string "".
     */
    public String getPrimitiveValue() {
        Class returnType = m_method.getReturnType();
        if (returnType.isPrimitive()) {
            return getWrapperTypeValueMethod(returnType);
        } else {
            return "";
        }
    }
    
    /**
     * @return Returns true if the return type of this method is void.
     */
    public boolean returnTypeIsVoid() {
        return m_method.getReturnType() == Void.TYPE;
    }
    
    /**
     * @return Returns the fully qualified class name for all exceptions
     *      thrown by the hosted method as an array.
     */
    public String[] getExceptions() {
        Class[] exceptions = m_method.getExceptionTypes();
        String[] exceptionNames = new String[exceptions.length];
        for (int i = 0; i < exceptionNames.length; i++) {
            exceptionNames[i] = ClassUtils.getCanonicalClassName(exceptions[i]);
        }
        return exceptionNames;
    }
    
    /**
     * @return Returns the java method signature representation.
     */
    public String toString() {
        Class[] types = m_method.getParameterTypes();
        Class returnType = m_method.getReturnType();
        Class[] exceptions;
        
        if (m_wrapRTExceptions) {
            Class[] origExceptions = m_method.getExceptionTypes();
            exceptions = new Class[origExceptions.length + 1];
            for (int i = 0; i < exceptions.length - 1; i++) {
                exceptions[i] = origExceptions[i];
            }
            // add additional wrapper exception
            exceptions[exceptions.length - 1] = WrapperException.class;
            
        } else {
            exceptions = m_method.getExceptionTypes();
        }
        
        int mod = m_method.getModifiers();
        if (Modifier.isAbstract(mod)) {
            mod = mod ^ Modifier.ABSTRACT;
        }
        
        StringBuffer buffer = new StringBuffer(Modifier.toString(mod));
        buffer.append(" ");
        buffer.append(ClassUtils.getCanonicalClassName(returnType));
        buffer.append(" ");
        buffer.append(m_method.getName());
        buffer.append("(");
        
        // add parameters
        for (int i = 0; i < types.length; i++) {
            buffer.append(ClassUtils.getCanonicalClassName(types[i]));
            if (m_contextPassing && i == types.length - 1) {
                buffer.append(" contexts");
            } else {
                buffer.append(" arg");
                buffer.append(i);
            }
            
            if (i < types.length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append(")");
        
        String exceptonSignature = classArray2ClassNameString(exceptions, "");
        if (StringUtils.hasText(exceptonSignature)) {
            buffer.append(" throws ");
            buffer.append(exceptonSignature);
        }
        
        return buffer.toString();
    }

    /**
     * @return Returns XDoclet tags for the method represented by this
     *      instance.
     */
    public String getXDocletTags() {
        try {
            return m_xDocletTagGenerator.getTagsForMethod(m_method).toString();
        } catch (XDocletException e) {
            s_logger.fatal("Could not create XDoclet tags for method '"
                    + m_method.getName() + "'.", e);
        }
        return null;
    }

    /**
     * @return Returns whether this method wraps runtime exceptions.
     */
    public boolean isWrapRTExceptions() {
        return m_wrapRTExceptions;
    }
}