/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.remoting.protocol.xfire;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.jaxb2.*;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;

/**
 * This class is a new implementation of the JaxbServiceFactory, which
 * can be found in the "org.codehaus.xfire.jaxb2" package. The reason
 * for reimplementing the JaxbServiceFactory class was, that it
 * inherits from the AnnotationServiceFactory. Because of this service classes 
 * (interfaces + implementation) are required to have WebAnnoations? on them.
 * 
 * The here implemented "JaxbServiceFactoryWithoutWebAnnotations" inherits
 * directly from the ObjectServiceFactory, by this it does not require serice
 * classes to have WebAnnoations on them.
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


public class JaxbServiceFactoryWithoutWebAnnotations
    extends ObjectServiceFactory {
    
    /**
     * Default constructor.
     */
    public JaxbServiceFactoryWithoutWebAnnotations() {
        this(XFireFactory.newInstance().getXFire().getTransportManager());
    }

    /**
     * This constructor can take a transportManager as parameter.
     * @param transportManager The transportManger for the ServiceFactroy.
     */
    public JaxbServiceFactoryWithoutWebAnnotations
    (TransportManager transportManager) {
        super(transportManager, 
              new AegisBindingProvider(new JaxbTypeRegistry()));
        
        setWsdlBuilderFactory(new JaxbWSDLBuilderFactory());
    }    
}




