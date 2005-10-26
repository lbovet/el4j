/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.tests.remoting.service.soap;

import java.rmi.RemoteException;
import java.util.Calendar;

import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;

/**
 * Jax-Rpc 1.1 wsdl fault specification conform exception.
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
public class RemoteExceptionWithData extends RemoteException {
    /**
     * Is an index of this exception.
     */
    private int m_index;

    /**
     * Is the message of this exception.
     */
    private String m_message;

    /**
     * Is the data of this exception.
     */
    private byte[] m_data;

    /**
     * Is the calendar of this exception.
     */
    private Calendar m_calendar;
    
    /**
     * Are the calculator value objects.
     */
    private CalculatorValueObject[] m_calculatorValueObjects;

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return m_index;
    }

    /**
     * @param index The index to set.
     */
    public void setIndex(int index) {
        m_index = index;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return m_message;
    }

    /**
     * @param message The message to set.
     */
    public void setMessage(String message) {
        m_message = message;
    }

    /**
     * @return Returns the calendar.
     */
    public Calendar getCalendar() {
        return m_calendar;
    }

    /**
     * @param calendar
     *            The calendar to set.
     */
    public void setCalendar(Calendar calendar) {
        m_calendar = calendar;
    }

    /**
     * @return Returns the data.
     */
    public byte[] getData() {
        return m_data;
    }

    /**
     * @param data
     *            The data to set.
     */
    public void setData(byte[] data) {
        m_data = data;
    }

    /**
     * @return Returns the calculatorValueObjects.
     */
    public CalculatorValueObject[] getCalculatorValueObjects() {
        return m_calculatorValueObjects;
    }

    /**
     * @param calculatorValueObjects The calculatorValueObjects to set.
     */
    public void setCalculatorValueObjects(
        CalculatorValueObject[] calculatorValueObjects) {
        m_calculatorValueObjects = calculatorValueObjects;
    }
}
