/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.remoting;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a complex number.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 */
@XmlRootElement(name = "MyOwnCmplexNumber")
@XmlType(name = "")
public class ComplexNumber implements java.io.Serializable {
    /**
     * The real part of the complex number.
     */
    private int m_real;
    /**
     * The complex part of the complex number.
     */
    private int m_imag;
 
    /**
     * Default constructor.
     */
    public ComplexNumber() {
        m_real = 0;
        m_imag = 0;
    }
    
    /**
     * Construct a ComplexNumber by initializing its real and complex 
     * components.
     * @param real This is the real component.
     * @param imag This is the complex component.
     */
    public ComplexNumber(int real, int imag) {
        super();
        this.m_real = real;
        this.m_imag = imag;
    }
    
    /**
     * Get the imag component.
     * @return Returns the imag component.
     */
    public int getImag() {
        return m_imag;
    }
    
    /**
     * Set the imag component.
     * @param imag Set the imag component.
     */
    public void setImag(int imag) {
        this.m_imag = imag;
    }
    
    /**
     * Get the real component.
     * @return Returns the real component.
     */
    public int getReal() {
        return m_real;
    }
    
    /**
     * Set the real component.
     * @param real Set the real component.
     */
    public void setReal(int real) {
        this.m_real = real;
    }
}