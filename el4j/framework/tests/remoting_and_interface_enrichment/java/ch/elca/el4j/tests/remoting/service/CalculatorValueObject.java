/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.tests.remoting.service;

/**
 * This is a value object for test reason.
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
public class CalculatorValueObject {
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
     */
    private byte[] m_myByteArray;

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
}
