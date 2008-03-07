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

package ch.elca.el4j.tests.remoting.service.soap.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;
import ch.elca.el4j.tests.remoting.service.soap.ExceptionThrower;
import ch.elca.el4j.tests.remoting.service.soap.RemoteExceptionWithData;

/**
 * Implementation of the Jax-Rpc 1.1 wsdl fault specification conform service.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ExceptionThrowerImpl implements ExceptionThrower {
    /**
     * Any index used in exception.
     */
    public static final int EXCEPTION_INDEX = 4892;
    
    /**
     * Any message used in exception.
     */
    public static final String EXCEPTION_MESSAGE 
        = "Something went wrong. Always problems with special characters "
            + "like ?, %, ç, ä, è and so on.";
    
    /**
     * Any byte data used in exception.
     */
    public static final byte[] EXCEPTION_DATA = EXCEPTION_MESSAGE.getBytes();
    
    /**
     * Any calendar object used in exception.
     */
    public static final Calendar EXCEPTION_CALENDAR 
        = new GregorianCalendar(1979, Calendar.DECEMBER, 27, 14, 10);
    
    /**
     * Double value used in first calculator value object. 
     */
    public static final double EXCEPTION_C1_MYDOUBLE = 2.3;

    /**
     * String used in second calculator value object.
     */
    public static final String EXCEPTION_C2_MYSTRING = "This is my string.";

    /**
     * {@inheritDoc}
     */
    public void throwExceptionWithData() throws RemoteExceptionWithData {
        RemoteExceptionWithData e = new RemoteExceptionWithData();
        e.setIndex(EXCEPTION_INDEX);
        e.setMessage(EXCEPTION_MESSAGE);
        e.setData(EXCEPTION_DATA);
        e.setCalendar(EXCEPTION_CALENDAR);
        
        CalculatorValueObject c1 = new CalculatorValueObject();
        c1.setMyDouble(EXCEPTION_C1_MYDOUBLE);
        CalculatorValueObject c2 = new CalculatorValueObject();
        c2.setMyString(EXCEPTION_C2_MYSTRING);
        e.setCalculatorValueObjects(new CalculatorValueObject[] {c1, c2});
        
        throw e;
    }
}
