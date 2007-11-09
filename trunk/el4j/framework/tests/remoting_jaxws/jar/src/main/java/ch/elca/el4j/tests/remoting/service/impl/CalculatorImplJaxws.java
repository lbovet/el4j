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

import ch.elca.el4j.tests.remoting.service.CalculatorException;
import ch.elca.el4j.tests.remoting.service.CalculatorJaxws;
import ch.elca.el4j.tests.remoting.service.CalculatorValueObjectJaxws;
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
 */

/**
 * IMPORTANT:
 * 
 * WebService naming convention:
 * name = name of implemented core interface + "WS"
 * serviceName = name of implemented core interface + "WSService"
 * targetNamespace = "http://gen." + package name of implemented core interface
 */
@WebService(name = "CalculatorJaxwsWS", serviceName="CalculatorJaxwsWSService", targetNamespace="http://gen.service.remoting.tests.el4j.elca.ch/")
public class CalculatorImplJaxws implements CalculatorJaxws {
    private CalculatorImpl delegate;
    
    public CalculatorImplJaxws() {
        delegate = new CalculatorImpl();
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public int countNumberOfUppercaseLetters(String text) {
        return delegate.countNumberOfUppercaseLetters(text);
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public CalculatorValueObjectJaxws echoValueObjectJaxws(CalculatorValueObjectJaxws o) {
        return o;
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public double getArea(double a, double b) {
        return delegate.getArea(a, b);
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public void throwMeASpecialException(
        String action) throws SpecialCalculatorException {
        delegate.throwMeASpecialException(action);
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public void throwMeAnException() throws CalculatorException {
        delegate.throwMeAnException();
    }

}
