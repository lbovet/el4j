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
package ch.elca.el4j.services.remoting.protocol.xfire;

import javax.xml.bind.JAXBContext;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.jaxb2.JaxbTypeRegistry;
import org.codehaus.xfire.jaxb2.JaxbWSDLBuilderFactory;
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
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Rashid Waraich (RWA)
 * @author Philippe Jacot (PJA)
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
	
	/**
	 * This constructor can take a transportManager as parameter and a
	 * <code>JAXBContext</code>.
	 * @param transportManager The transportManger for the ServiceFactroy.
	 * @param context The JAXBContext to indicate JAXB annotated classes
	 */
	public JaxbServiceFactoryWithoutWebAnnotations
	(TransportManager transportManager, JAXBContext context) {
		super(transportManager,
			new AegisBindingProvider(new JaxbTypeRegistry(context)));
		
		setWsdlBuilderFactory(new JaxbWSDLBuilderFactory());
	}
}




