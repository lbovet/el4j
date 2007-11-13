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
package ch.elca.el4j.tests.remoting.service.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;

import ch.elca.el4j.tests.remoting.service.Calculator;
import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorValueObject;
import ch.elca.el4j.tests.remoting.service.SpecialCalculatorException;

/**
 * 
 * This class is a annotated version of {@link CalculatorImpl}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 * 
 * 
 * IMPORTANT:
 * 
 * WebService naming convention:
 * name = name of implemented core interface + "WS"
 * serviceName = name of implemented core interface + "WSService"
 * targetNamespace = "http://gen." + package name of implemented core interface
 */
@WebService(name = "CalculatorWS",
    serviceName = "CalculatorWSService",
    targetNamespace = "http://gen.service.remoting.tests.el4j.elca.ch/")
public class CalculatorImplJaxws implements Calculator {

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public int countNumberOfUppercaseLetters(String text) {
        if (text == null) {
            return 0;
        }
 
        int numberOfUppercaseLetters = 0;
        char[] c = text.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 'A' && c[i] <= 'Z') {
                numberOfUppercaseLetters++;
            }
        }
        return numberOfUppercaseLetters;
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public CalculatorValueObject echoValueObjectJaxws(
        CalculatorValueObject valueObject) {
        
        return valueObject;
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public double getArea(double a, double b) {
        return a * b;
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public void throwMeASpecialException(
        String action) throws SpecialCalculatorException {
        throw new SpecialCalculatorException(action);
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public void throwMeAnException() throws CalculatorException {
        throw new CalculatorException();
    }

}
