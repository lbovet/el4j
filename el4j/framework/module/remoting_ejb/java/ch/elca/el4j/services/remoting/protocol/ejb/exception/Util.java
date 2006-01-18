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

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Various utility methods.
 * <p>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 **/
public class Util {
    ///////////////////// time and date stamp method //////////////

    /**
     *  Default format for simple date and timestamp.
     */
    public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm:ss z");

    /**
     * Internal method, generates a date and timestamp-string of the current
     * time according to the default format.
     * @see #TIMESTAMP
     */
    public static String dateAndTimestamp() {
        return TIMESTAMP.format(new Date());
    }


    ///////////////////// string routines ///////////////////////


    /**
     * Concat an Object[] whose element are interpreted as
     * String to a String. (I.e. concatenate the results of a toString() method
     * call on all elements of the Object[].)
     *
     * This method does not tolerate exceptions during the conversion of Objects
     * to String.
     * @see #safeObjectArray2String
     *
     * @param messageAsObjectArray the Object[] to convert to a string
     * @return the string representation of the Object[]
     */
    public static String objectArray2String(Object[] messageAsObjectArray) {
        StringBuffer stringMessage = new StringBuffer(
                30 * messageAsObjectArray.length);
        for (int i = 0; i < messageAsObjectArray.length; i++) {
            stringMessage.append(String.valueOf(messageAsObjectArray[i]));
        }

        return stringMessage.toString();
    }


    /**
     * Concat an Object[] whose element are interpreted as
     * String to a String. (I.e. concatenate the results of a toString() method
     * call on all elements of the Object[].) This method does tolerates
     * exceptions during the conversion of Objects to String.
     *
     * @param messageAsObjectArray the Object[] to convert to a string
     * @return the string representation of the Object[]
     */
    public static String safeObjectArray2String(Object[] messageAsObjectArray) {
        StringBuffer stringMessage = new StringBuffer(
                30 * messageAsObjectArray.length);
        String s;
        for (int i = 0; i < messageAsObjectArray.length; i++) {
            try {
                s = String.valueOf(messageAsObjectArray[i]);
            } catch (Throwable t) {
                s = "[toStringFailed]";
            }
            stringMessage.append(s);
        }

        return stringMessage.toString();
    }


    /**
     * Efficiently (?) gets the stack trace of an exception.
     * Wrapped exceptions are added if possible, see method 
     * checkWrappedException().
     */
    public static String exceptionStackTrace(Throwable t) {
//        Assert.ensure(t != null, "Null throwable was given as parameter");

        String result = "";
        while (t != null) {
            // print t
            CharArrayWriter caw = new CharArrayWriter(1000);
            PrintWriter pw = new PrintWriter(caw);
            t.printStackTrace(pw);
            pw.close();
            result += caw.toString();
            // there may be an inner exception
            t = checkWrappedException(t);
        }

        return result;
    }


    /**
     * Return the wrapped exception, if there is any. Method names checked to
     * find it:<br>
     * - getCause<br>
     * - getTargetError<br>
     * - getTargetException<br>
     *
     * @param t
     *      The throwable to check.
     * @return
     *      The wrapped throwable, or null if there is no wrapped throwable or
     *      if it cannot be retrieved.
     */
    public static Throwable checkWrappedException(Throwable t) {
        Class clazz = t.getClass();
        try { // jdk 1.4
            Method method = clazz.getMethod("getCause", null);
            Object wrappedEx = method.invoke(t, null);
            if ((wrappedEx != null) && (wrappedEx instanceof Throwable)) {
                return (Throwable) wrappedEx;
            }
        } catch (Throwable throwable) {
        }

        try { // JMX 1.1
            Method method = clazz.getMethod("getTargetError", null);
            Object wrappedEx = method.invoke(t, null);
            if ((wrappedEx != null) && (wrappedEx instanceof Throwable)) {
                return (Throwable) wrappedEx;
            }
        } catch (Throwable throwable) {
        }
        
        try { // JMX 1.1
            Method method = clazz.getMethod("getTargetException", null);
            Object wrappedEx = method.invoke(t, null);
            if ((wrappedEx != null) && (wrappedEx instanceof Throwable)) {
                return (Throwable) wrappedEx;
            }
        } catch (Throwable throwable) {
        }        
        
        return null;
    }


    public static String replace(String where, String what, String with) {
        int found = where.indexOf(what);
        if (found < 0) {
            return where;
        }

        StringBuffer result = new StringBuffer();
        int lastIndex = 0;
        while (found > -1) {
            result.append(where.substring(lastIndex, found));
            result.append(with);
            lastIndex = found + what.length();
            found = where.indexOf(what, found + what.length());
        }

        if (lastIndex < where.length()) {
            result.append(where.substring(lastIndex));
        }

        return result.toString();
    }


    public static String replace(String where, String[] what, String[] with) {
        StringBuffer result = new StringBuffer();
        int lastIndex = 0;
        int foundAt = 0;
        int foundWhat = -1;
        int temp;

        while (foundAt > -1) {
            foundAt = -1;
            for (int i = 0; i < what.length; i++) {
                temp = where.indexOf(what[i], lastIndex);
                if (temp > -1) {
                    if (foundAt > -1) {
                        if (temp < foundAt) {
                            foundAt = temp;
                            foundWhat = i;
                        }
                    } else {
                        foundAt = temp;
                        foundWhat = i;
                    }
                }
            }
            if (foundAt > -1) {
                result.append(where.substring(lastIndex, foundAt));
                result.append(with[foundWhat]);

                lastIndex = foundAt + what[foundWhat].length();
            }
        }

        if (lastIndex <= 0) {
            return where;
        }
        if (lastIndex < where.length()) {
            result.append(where.substring(lastIndex));
        }

        return result.toString();
    }


    /**
     * Tool method. : catches any exception that would occur in
     * Object.toString() overloading.
     */
    public static String safeToString(Object o) {
        String result = "";
        try {
            result = String.valueOf(o);
        } catch (Throwable t) {
//            Trace.catching(Util.class, "safeToString", t, Trace.NORMAL);
            result = "[toStringFailed]";
        }

        return result;
    }


    /**
     * Tool method that nicely prints the arrays and that eventually
     * would be able to nicely print other types. TBD
     */
    public static String niceToString(Object o) {
        String result = "";
        if (o instanceof Object[]) {
            // then, o is not null
            Object[] arr = (Object[]) o;
            int maxIdx = arr.length;
            StringBuffer tmp = new StringBuffer();
            tmp.append("[ ");

            for (int i = 0; (i < 5) && (i < maxIdx); i++) {
                tmp.append(niceToString(arr[i]));
                if (i != (maxIdx - 1)) {
                    tmp.append(", ");
                }
            }
            if (maxIdx > 5) {
                tmp.append("...");
            }
            tmp.append(" ]");
            result = tmp.toString();
        } else {
            result = safeToString(o);
        }

        return result;
    }


    ///////////////////// Vector routines ///////////////////////


    /**
     * Merges 2 Vectors.
     *
     * @param v1 the first Vector, may be null.
     * @param v2 the second Vector, may be null.
     * @return a copy of v1+v2 with the right order, may be null.
     */
    public static Vector concat(Vector v1, Vector v2) {
        if (v1 == null) {
            if (v2 == null) {
                return null;
            } else {
                return ((Vector) v2.clone());
            }
        }
        Vector res = ((Vector) v1.clone());
        if (v2 != null) {
            int size = v2.size();
            for (int i = 0; i < size; i++) {
                res.add(v2.get(i));
            }
        }

        return res;
    }


    /**
     * This method can be used to tunnel a byte[] over strings.
     * The byte[] is encoded in the form: "byte space byte space ..."
     *
     * The way <code>new String(byte[])</code> and
     * <code>String.getBytes()</code>is not reliable.
     *
     * @see #stringToByteArray(String)
     * @param b the byte array to convert
     * @return the converted string in the form "byte space byte space ..."
     */
    public static String byteArrayToString(byte[] b) {
        StringBuffer buf = new StringBuffer(b.length * 3);
        for (int i = 0; i < b.length; i++) {
            buf.append(b[i]);
            buf.append(" ");
        }

        return buf.toString();
    }


    /**
     * This method can be used to tunnel a byte[] over strings.
     * A string in the form "byte space byte space ..." is required.
     *
     * The way <code>new String(byte[])</code> and
     * <code>String.getBytes()</code>is not reliable.
     *
     * @see #byteArrayToString(byte[])
     * @param s the string to convert
     * @return the converted byte array
     */
    public static byte[] stringToByteArray(String s) {
        StringTokenizer t = new StringTokenizer(s, " ");
        int length = t.countTokens();
        byte[] b = new byte[length];

        for (int i = 0; t.hasMoreTokens(); i++) {
            b[i] = Byte.parseByte(t.nextToken());
        }

        return b;
    }

    public static class ArrayPrinter implements Serializable {
        private Object[] m_array;

        public ArrayPrinter(Object[] array) {
            m_array = array;
        }

        public String toString() {
            return Util.objectArray2String(m_array);
        }
    }
}
