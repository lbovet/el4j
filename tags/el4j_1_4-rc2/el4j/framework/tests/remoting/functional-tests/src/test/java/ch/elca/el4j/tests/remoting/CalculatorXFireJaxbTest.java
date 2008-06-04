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

// Checkstyle: EmptyBlock off
// Checkstyle: MagicNumber off

package ch.elca.el4j.tests.remoting;

import ch.elca.el4j.tests.remoting.service.Calculator;

/**
 * This class is a test for the calculator.
 * It uses the XFire Protocol with a Jaxb binding.
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
public class CalculatorXFireJaxbTest extends AbstractXFireTest {
   
    /**
     * A calculator.
     */
    private Calculator m_calc;
    
    /**
     * 
     * {@inheritDoc}
     */
    public Calculator getCalc() {
        if (m_calc == null) {
            m_calc = (Calculator) getApplicationContext().
                getBean("calculatorJaxb");
        }
        return m_calc;
    }
}
