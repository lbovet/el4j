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

package ch.elca.el4j.tests.remoting.service;


/**
 * This is a value object for test reason.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * 
 * 
 * IMPORTANT
 * 
 * Don't use @Xml... annotations!
 */
public class CalculatorValueObjectJaxws {
    /**
     * Int test value.
     */
    private int m_myInt;

    /**
     * Long test value.
     */
    private long m_myLong;
    
    /**
     * Double test value.
     */
    private double m_myDouble;
    
    /**
     * String test value.
     */
    private String m_myString;
    
    /**
     * Byte array test value.
     * 
     * There is no @XmlElementWrapper possible.
     * byte[] is translated to xs:base64Binary
     */
    private byte[] m_myByteArray;
    
    /**
     * String array test value.
     */
    private String[] m_myStringArray;
    
    /**
     * String array test value.
     */
    private int[] m_myIntArray;
    
    /**
     * A nested object.
     */
    private SomeIntValueJaxws m_myNestedObject;
    

    /**
     * @return Returns the myByteArray.
     */
    public byte[] getMyByteArray() {
        return m_myByteArray;
    }

    /**
     * @param myByteArray
     *            The myByteArray to set.
     */
    public void setMyByteArray(byte[] myByteArray) {
        m_myByteArray = myByteArray;
    }

    /**
     * @return Returns the myDouble.
     */
    public double getMyDouble() {
        return m_myDouble;
    }

    /**
     * @param myDouble
     *            The myDouble to set.
     */
    public void setMyDouble(double myDouble) {
        m_myDouble = myDouble;
    }

    /**
     * @return Returns the myInt.
     */
    public int getMyInt() {
        return m_myInt;
    }

    /**
     * @param myInt
     *            The myInt to set.
     */
    public void setMyInt(int myInt) {
        m_myInt = myInt;
    }

    /**
     * @return Returns the myLong.
     */
    public long getMyLong() {
        return m_myLong;
    }

    /**
     * @param myLong
     *            The myLong to set.
     */
    public void setMyLong(long myLong) {
        m_myLong = myLong;
    }

    /**
     * @return Returns the myString.
     */
    public String getMyString() {
        return m_myString;
    }

    /**
     * @param myString
     *            The myString to set.
     */
    public void setMyString(String myString) {
        m_myString = myString;
    }
    
    /**
     * @return Returns myStringArray
     */
    public String[] getMyStringArray() {
        return m_myStringArray;
    }
    
    /**
     * @param myStringArray
     *            The String array to set
     */
    public void setMyStringArray(String[] myStringArray) {
        m_myStringArray = myStringArray;
    }
    
    /**
     * @return Returns myIntArray
     */
    public int[] getMyIntArray() {
        return m_myIntArray;
    }
    
    /**
     * @param myIntArray
     *            The int array to set
     */
    public void setMyIntArray(int[] myIntArray) {
        m_myIntArray = myIntArray;
    }
    
    /**
     * @return Returns myNestedObject
     */
    public SomeIntValueJaxws getSomeValue() {
        return m_myNestedObject;
    }
    
    /**
     * @param object
     *             The SomeIntValueJaxws to set
     */
    public void setSomeValue(SomeIntValueJaxws object) {
        m_myNestedObject = object;
    }
}
