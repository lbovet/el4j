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

package ch.elca.el4j.tests.remoting.service;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.Constants;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elca.el4j.core.contextpassing.AbstractImplicitContextPasser;
import ch.elca.el4j.core.exceptions.BaseRTException;

/**
 * Implicit context passer to test client side soap header modification.
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
public class SoapHeaderImplicitContextPasser
    extends AbstractImplicitContextPasser {

    /** 
     * The test namespace.
     */
    public static final String MY_NAMESPACE
        = "http://soap.header.test.el4j.elca.ch";
    
    /**
     * General document to build xml nodes.
     */
    private final Document m_doc;

    /**
     * Default constructor. 
     */
    public SoapHeaderImplicitContextPasser() {
        try {
            m_doc = XMLUtils.newDocument();
        } catch (ParserConfigurationException e) {
            throw new BaseRTException(
                "No new xml document could be created.", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getImplicitlyPassedContext() {
        Element username = m_doc.createElementNS(MY_NAMESPACE, "username");
        username.setAttributeNS(MY_NAMESPACE, "contextLevel", "0");
        username.setAttributeNS(Constants.URI_DEFAULT_SCHEMA_XSI, "type", 
            "xsd:string");
        username.appendChild(m_doc.createTextNode("abc"));
        
        Element password = m_doc.createElementNS(MY_NAMESPACE, "password");
        password.setAttributeNS(MY_NAMESPACE, "contextLevel", "1");
        password.setAttributeNS(Constants.URI_DEFAULT_SCHEMA_XSI, "type", 
            "xsd:string");
        password.appendChild(m_doc.createTextNode("WELLnEss"));
        
        
        Element street = m_doc.createElementNS(MY_NAMESPACE, "street");
        street.setAttributeNS(MY_NAMESPACE, "contextLevel", "3");
        street.setAttributeNS(Constants.URI_DEFAULT_SCHEMA_XSI, "type", 
            "xsd:string");
        street.appendChild(m_doc.createTextNode("Steinstrasse 21"));
        
        Element city = m_doc.createElementNS(MY_NAMESPACE, "city");
        city.setAttributeNS(MY_NAMESPACE, "contextLevel", "4");
        city.setAttributeNS(Constants.URI_DEFAULT_SCHEMA_XSI, "type", 
            "xsd:string");
        city.appendChild(m_doc.createTextNode("Zürich"));

        Element address = m_doc.createElementNS(MY_NAMESPACE, "address");
        address.setAttributeNS(MY_NAMESPACE, "contextLevel", "2");
        address.appendChild(street);
        address.appendChild(city);
        
        Element myContext = m_doc.createElementNS(MY_NAMESPACE, "myContext");
        myContext.appendChild(username);
        myContext.appendChild(password);
        myContext.appendChild(address);
        
        return myContext;
    }

    /**
     * {@inheritDoc}
     */
    public void pushImplicitlyPassedContext(Object context) {
        // TODO Add test for server side soap header.
    }

}
