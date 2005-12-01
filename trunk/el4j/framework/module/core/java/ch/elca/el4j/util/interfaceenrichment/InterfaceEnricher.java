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

package ch.elca.el4j.util.interfaceenrichment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

import net.sf.cglib.core.ReflectUtils;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class generates an <em>enriched</em> shadow interface for an existing
 * interface. The interface EnrichmentDecorator describes how the shadow
 * interface is changed from the existing interface. The interface's bytecode 
 * is generated during runtime. Its bytecode can either be directly loaded or
 * it can be saved to a file. The loading of the bytecode is done as in
 * the cglib library.
 * 
 * Possible usages for this:
 *  *One can create RMI or EJB-conformant interface for any Java interface 
 *   (E.g. by implementing java.ejb.Remote and by adding RemoteExceptions)
 *  *One can add additional parameters to each method of an interface.
 *    This can be used for implicit context passing via remoting protocols
 *    that do not support it. Please refer to the remoting of EL4J.
 *  *Writing other decorators or adapters that need to have an interface
 *   that is slightly modified.
 *
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 * @see EnrichmentDecorator
 */
public class InterfaceEnricher {
    /**
     * This string is used to circumvent package problems of the generated 
     * class. Packages can be signed and so no further class could be added.
     */
    private static final String PACKAGE_PREFIX = "ch.elca.el4j.generated.";

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(InterfaceEnricher.class);

    /**
     * Creates a shadow interface and returns it as a byte array.
     * 
     * @param serviceInterfaceOld
     *            Is the interface which has to be converted.
     * @param interfaceDecorator
     *            Is the decorator which tolds how does the given interface has
     *            to be changed.
     * @return Returns the generated shadow interface as a byte array.
     */
    public byte[] createShadowInterface(Class serviceInterfaceOld,
            EnrichmentDecorator interfaceDecorator) {
        JavaClass jc = internalCreateShadowInterface(serviceInterfaceOld,
                interfaceDecorator, null);
        return jc.getBytes();
    }

    /**
     * Creates a shadow interface, loads it in the given class loader and
     * returns the loaded class as a class.
     * 
     * @param serviceInterfaceOld
     *            Is the interface which has to be converted.
     * @param interfaceDecorator
     *            Is the decorator which tolds how does the given interface has
     *            to be changed.
     * @param cl
     *            Is the ClassLoader where to load the generated class.
     * @return Returns the generated shadow interface as a class.
     */
    public Class createShadowInterfaceAndLoadItDirectly(
            Class serviceInterfaceOld, EnrichmentDecorator interfaceDecorator,
            ClassLoader cl) {
        JavaClass jc = internalCreateShadowInterface(serviceInterfaceOld,
                interfaceDecorator, null);

        try {
            return cl.loadClass(jc.getClassName());
        } catch (ClassNotFoundException cnfe) {
            s_logger.info("Seams that class does not already exist in class "
                    + "loader. Trying to define a new one.");
            
            Class classToReturn = null;
            try {
                classToReturn = ReflectUtils.defineClass(jc.getClassName(), jc
                        .getBytes(), cl);
                
            } catch (Exception e) {
                s_logger.error("Generated class could not be "
                        + "loaded by class loader.", e);
            }
            return classToReturn;
        }
    }

    /**
     * Creates a shadow interface and returns it as a byte array.
     * 
     * @param serviceInterfaceOld
     *            Is the interface which has to be converted.
     * @param interfaceDecorator
     *            Is the decorator which tolds how does the given interface has
     *            to be changed.
     * @param pathToSaveClassOnDisk
     *            Is the path, where the shadow interface should be saved. If
     *            the string is <code>null</code> the file will not be saved.
     * @return Returns the generated shadow interface as a BCEL JavaClass.
     */
    private JavaClass internalCreateShadowInterface(Class serviceInterfaceOld,
        EnrichmentDecorator interfaceDecorator, 
        String pathToSaveClassOnDisk) {
        if (serviceInterfaceOld == null || !serviceInterfaceOld.isInterface()) {
            throw new RuntimeException("The given class is not an interface.");
        }

        JavaClass jcServiceInterfaceOld = Repository
                .lookupClass(serviceInterfaceOld);
        ClassGen cgShadowInterfaceNew = new ClassGen(jcServiceInterfaceOld);

        /**
         * Set the new class name.
         */
        String nameOfNewServiceInterface = interfaceDecorator
                .changedInterfaceName(serviceInterfaceOld.getName());
        cgShadowInterfaceNew.setClassName(nameOfNewServiceInterface);

        /**
         * Update methods.
         */
        Method[] serviceInterfaceMethodsOld = jcServiceInterfaceOld
                .getMethods();
        ConstantPoolGen cp = cgShadowInterfaceNew.getConstantPool();
        for (int i = 0; i < serviceInterfaceMethodsOld.length; i++) {
            /**
             * Create method descriptor for method 'i'.
             */
            MethodDescriptor md = new MethodDescriptor();
            md.setMethodName(serviceInterfaceMethodsOld[i].getName());
            md.setParameterTypes(getParamTypes(serviceInterfaceMethodsOld[i]));
            md.setReturnType(getClass(serviceInterfaceMethodsOld[i]
                    .getReturnType()));
            md.setThrownExceptions(
                getExceptionTypes(serviceInterfaceMethodsOld[i]
                    .getExceptionTable()));

            /**
             * Get new method descriptor.
             */
            MethodDescriptor nmd = interfaceDecorator
                    .changedMethodSignature(md);

            /**
             * Set values of new method.
             */
            MethodGen shadowMethodNew = new MethodGen(
                    serviceInterfaceMethodsOld[i], nameOfNewServiceInterface,
                    cp);
            shadowMethodNew.setName(nmd.getMethodName());
            shadowMethodNew.setArgumentTypes(getArgumentTypes(nmd
                    .getParameterTypes()));
            Class c = nmd.getReturnType();
            if (c == null) {
                shadowMethodNew.setReturnType(Type.VOID);
            } else {
                shadowMethodNew.setReturnType(Type.getType(c));
            }
            shadowMethodNew.removeExceptions();
            Class[] thrownExceptionsNew = nmd.getThrownExceptions();
            for (int j = 0; j < thrownExceptionsNew.length; j++) {
                shadowMethodNew.addException(thrownExceptionsNew[j].getName());
            }

            /**
             * Finishing new method.
             */
            cgShadowInterfaceNew.replaceMethod(serviceInterfaceMethodsOld[i],
                    shadowMethodNew.getMethod());
        }

        /**
         * Set new parent interfaces.
         */
        String[] extendedInterfacesOld = cgShadowInterfaceNew
                .getInterfaceNames();
        Class[] extendedInterfaceClassesOld 
            = new Class[extendedInterfacesOld.length];
        ClassLoader cl = serviceInterfaceOld.getClassLoader();
        for (int i = 0; i < extendedInterfaceClassesOld.length; i++) {
            try {
                extendedInterfaceClassesOld[i] = cl
                        .loadClass(extendedInterfacesOld[i]);
                cgShadowInterfaceNew.removeInterface(extendedInterfacesOld[i]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Interface '"
                        + extendedInterfacesOld[i] + "' could not be found.");
            }
        }
        Class[] extendedInterfaceClassesNew = interfaceDecorator
                .changedExtendedInterface(extendedInterfaceClassesOld);
        for (int i = 0; i < extendedInterfaceClassesNew.length; i++) {
            cgShadowInterfaceNew.addInterface(extendedInterfaceClassesNew[i]
                    .getName());
        }
        
        /**
         * Change package name.
         */
        if (!cgShadowInterfaceNew.getClassName().startsWith(PACKAGE_PREFIX)) {
            cgShadowInterfaceNew.setClassName(PACKAGE_PREFIX 
                + cgShadowInterfaceNew.getClassName());
        }

        /**
         * Create the java class and load it into the bcel repository, so it
         * can be enriched again.
         */
        JavaClass jcShadowInterfacesNew = cgShadowInterfaceNew.getJavaClass();
        Repository.addClass(jcShadowInterfacesNew);

        /**
         * Save .class file to disk if desired.
         */
        if (pathToSaveClassOnDisk != null 
            && pathToSaveClassOnDisk.length() > 0) {
            /**
             * Save the generated class on path 'pathToSaveClassOnDisk'.
             */
            String newClassFileName = jcShadowInterfacesNew.getClassName()
                    .replace('.', '/')
                    + ".class";

            String destFilePath = pathToSaveClassOnDisk.replace('\\', '/');
            if (destFilePath.lastIndexOf('/') != destFilePath.length() - 1) {
                destFilePath = destFilePath + '/' + newClassFileName;
            } else {
                destFilePath = destFilePath + newClassFileName;
            }

            try {
                jcShadowInterfacesNew.dump(new File(destFilePath));
                s_logger.info("Generated shadow interface stored at '"
                        + destFilePath + "'.");
            } catch (IOException e) {
                throw new RuntimeException(
                        "Newly generated class file could not be written to '"
                                + destFilePath + "'.", e);
            }
        }

        Repository.addClass(jcShadowInterfacesNew);
        return jcShadowInterfacesNew;
    }

    /**
     * This method converts classes into BCEL type objects.
     * 
     * @param classes
     *            Are the classes which have to be converted.
     * @return Returns the converted BCEL type objects.
     */
    private Type[] getArgumentTypes(Class[] classes) {
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = Type.getType(classes[i]);
        }
        return types;
    }

    /**
     * This method extracts the kind of classes of which the given method has as
     * parameters.
     * 
     * @param method
     *            Is the BCEL method which has to be extracted.
     * @return Returns the extracted parameters as classes.
     */
    private Class[] getParamTypes(Method method) {
        Type[] types = method.getArgumentTypes();
        Class[] classes = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            classes[i] = getClass(types[i]);
        }
        return classes;
    }

    /**
     * This method extracts the kind of classes which the given exception table
     * contains.
     * 
     * @param table
     *            Is the BCEL exception table which has to be extracted.
     * @return Returns the extracted exceptions as classes.
     */
    private Class[] getExceptionTypes(ExceptionTable table) {
        if (table == null) {
            return new Class[0];
        }
        String[] exceptionNames = table.getExceptionNames();
        Class[] classes = new Class[exceptionNames.length];
        for (int i = 0; i < exceptionNames.length; i++) {
            try {
                classes[i] = Class.forName(exceptionNames[i]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;

    }

    /**
     * This method converts a BCEL type to a class object.
     * 
     * @param type
     *            Is the BCEL type which should be converted.
     * @return Returns the converted class object.
     */
    private Class getClass(Type type) {
        Class clazz = null;
        if (type instanceof ObjectType) {
            try {
                clazz = Class.forName(((ObjectType) type).getClassName());
            } catch (ClassNotFoundException e) {
                s_logger.error("BCEL Type could not be converted to class.", e);
            }
        } else if (type instanceof ArrayType) {
            ArrayType at = (ArrayType) type;
            Class baseType = getClass(at.getBasicType());
            int [] dim = new int[at.getDimensions()];
            clazz = Array.newInstance(baseType, dim).getClass();
        } else if (type == Type.BOOLEAN) {
            clazz = boolean.class;
        } else if (type == Type.BYTE) {
            clazz = byte.class;
        } else if (type == Type.CHAR) {
            clazz = char.class;
        } else if (type == Type.DOUBLE) {
            clazz = double.class;
        } else if (type == Type.FLOAT) {
            clazz = float.class;
        } else if (type == Type.INT) {
            clazz = int.class;
        } else if (type == Type.LONG) {
            clazz = long.class;
        } else if (type == Type.SHORT) {
            clazz = short.class;
        } else if (type == Type.VOID) {
            clazz = void.class;
        } else {
            s_logger.error("Unknown BCEL type '" + type.toString() + "'.");
        }
        return clazz;
    }
}
