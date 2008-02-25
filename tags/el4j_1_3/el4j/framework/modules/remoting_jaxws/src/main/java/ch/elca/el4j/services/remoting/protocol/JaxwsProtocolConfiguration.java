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
package ch.elca.el4j.services.remoting.protocol;

import javax.xml.ws.Service;

import ch.elca.el4j.services.remoting.ProtocolSpecificConfiguration;

/**
 * The JAX-WS protocoll configuration class.
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
public class JaxwsProtocolConfiguration implements 
    ProtocolSpecificConfiguration {
    
    /**
     * The service implementation class.
     */
    Class<? extends Service> m_serviceImplementation;
    
    /** {@inheritDoc} */
    public void afterPropertiesSet() throws Exception { }

    /**
     * @return Returns the serviceImplementation.
     */
    public Class<? extends Service> getServiceImplementation() {
        return m_serviceImplementation;
    }

    /**
     * @param serviceImplementation Is the serviceImplementation to set.
     */
    public void setServiceImplementation(
        Class<? extends Service> serviceImplementation) {
        
        m_serviceImplementation = serviceImplementation;
    }

}
