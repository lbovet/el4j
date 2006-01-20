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

package ch.elca.el4j.services.remoting.protocol.ejb.exception;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

/**
 * This (static) helper class serializes objects into byte arrays and
 * deserializes objects from byte arrays and byte streams. <P>
 *
 * The class also fixes a bug (bug #4171142 from sun) of the normal java
 * serialization. (Remark: in case one looks for a serialization without
 * serialization header (for serialization to be more stateless), refer to the
 * trace console.)
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 */
public final class SerializationTool {

    /**
     * Hidden constructor (there is no need to create instances of this class).
     */
    private SerializationTool() {
    }

    /**
     * Serializes an object into a byte array.
     *
     * @param obj the object to serialize.
     * @return byte array representation of this object, null in case of
     *      failure.
     */
    public static byte[] encodeObject(Object obj) {
        byte[] result = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            baos.flush();

            result = baos.toByteArray();

            oos.close();
            baos.close();

        } catch (IOException ioe) {
//            Trace.catching( SerializationTool.class, "encodeObject",ioe,
//                           Trace.UNEXPECTED);
        }
        return result;
    }


    private static class ResolverInputStream extends ObjectInputStream {
        /**
         * Workaround against bug #4171142 (sun).
         */
        public static final Map m_name2Primitive;

        private ClassLoader m_resolver;

        static {
            m_name2Primitive = new HashMap();
            m_name2Primitive.put("int", int.class);
            m_name2Primitive.put("byte", byte.class);
            m_name2Primitive.put("short", short.class);
            m_name2Primitive.put("long", long.class);
            m_name2Primitive.put("char", char.class);
            m_name2Primitive.put("boolean", boolean.class);
            m_name2Primitive.put("float", float.class);
            m_name2Primitive.put("double", double.class);
        }

        public ResolverInputStream(InputStream stream) throws IOException {
            super(stream);
        }

        public ResolverInputStream(InputStream stream, ClassLoader resolver)
            throws IOException {
            
            super(stream);
            m_resolver = resolver;
        }

        public Class resolveClass (ObjectStreamClass osc)
            throws ClassNotFoundException, IOException {
            
            try {
                if (m_resolver != null) {
                    // Trace.debug ("Will use resolver " + m_resolver);

                    return m_resolver.loadClass(osc.getName());
                } else {
                    return super.resolveClass(osc);
                }
            } catch (ClassNotFoundException cnfe) {
                try {
                    return SerializationTool.class.
                        getClassLoader().loadClass(osc.getName());
                } catch (ClassNotFoundException e) {
                    /*
                     * Cf bug #4171142.
                     * The super method is not able to deal with class
                     * objects that represent primitive types
                     */
                    String name = osc.getName();
                    Class result = (Class) m_name2Primitive.get(name);
                    if (result != null) {
                        return result;
                    } else {
                        throw e;
                    }
                } // end 2nd catch
            } // end 1st catch


        } // end resolveClass
    }


    /**
     * Deserializes a byte stream into an object form.
     *
     * @param binaryStream stream holding the serialized object
     * @return deserialized object, null in case of any error.
     */
    public static Object decodeObject(InputStream binaryStream) {
        Object result = null;

        try {
            ObjectInputStream ois = new ResolverInputStream(binaryStream);
            result = ois.readObject();
        } catch (ClassNotFoundException cnfe) {
//            Trace.catching( SerializationTool.class, "decodeObject",cnfe,
//                           Trace.EXPECTED);
        } catch (IOException ioe) {
//            Trace.catching( SerializationTool.class, "decodeObject",ioe,
//                           Trace.EXPECTED);
        }
        return result;
    }

    /**
     * Deserializes a byte stream into an object form within a given
     * object's classloader.
     *
     * @param binaryStream stream holding the serialized object
     * @param classLoaderReference the object whose class's
     * classloader will be used to resolve the stream.
     *
     * @return deserialized object, null in case of any error.
     */
    public static Object decodeObject(InputStream binaryStream,
            Object classLoaderReference) {
        Object result = null;

        try {
            ObjectInputStream ois = new ResolverInputStream(binaryStream,
                      classLoaderReference.getClass().getClassLoader());
            result = ois.readObject();
        } catch (ClassNotFoundException cnfe) {
//            Trace.catching( SerializationTool.class, "decodeObject",cnfe,
//                           Trace.EXPECTED);
        } catch (IOException ioe) {
//            Trace.catching( SerializationTool.class, "decodeObject",ioe,
//                           Trace.EXPECTED);
        }
        return result;
    }

    /**
     * Deserializes a byte array into an object.
     *
     * @param byteArray byte array representation of an object
     * @return deserialized object, null in case of any error
     */
    public static Object decodeObject(byte[] byteArray) {
        Object result = null;

        try {
            ByteArrayInputStream ba = new ByteArrayInputStream(byteArray);
            result = decodeObject(ba);

            ba.close();

        } catch (IOException ioe) {
//            Trace.catching( SerializationTool.class, "decodeObject",ioe,
//                           Trace.EXPECTED);
        }
        return result;
    }

    /**
     * DeepClone of an object.
     *
     * @param o the object to clone, it must be serializable
     * @return the cloned object
     */
    public static Object deepClone(Object o) {
        return decodeObject(encodeObject(o));
    }

}
