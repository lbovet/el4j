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
import javax.jws.soap.SOAPBinding;

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
 * @author Philippe Jacot (PJA)
 */
@WebService(
    endpointInterface = "ch.elca.el4j.tests.remoting.service.Calculator",
    serviceName = "Calculator")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class CalculatorImplAnnotated extends CalculatorImpl {

    /**
     * 
     * {@inheritDoc}
     */
    @WebMethod
    public int countNumberOfUppercaseLetters(String text) {
        return super.countNumberOfUppercaseLetters(text);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @WebMethod
    public CalculatorValueObject echoValueObject(CalculatorValueObject o) {
        return super.echoValueObject(o);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @WebMethod
    public double getArea(double a, double b) {
        return super.getArea(a, b);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @WebMethod
    public void throwMeASpecialException(
        String action) throws SpecialCalculatorException {
        super.throwMeASpecialException(action);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @WebMethod
    public void throwMeAnException() throws CalculatorException {
        super.throwMeAnException();
    }

}
