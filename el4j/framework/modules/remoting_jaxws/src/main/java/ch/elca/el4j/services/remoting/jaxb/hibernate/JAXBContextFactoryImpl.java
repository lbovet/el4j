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
package ch.elca.el4j.services.remoting.jaxb.hibernate;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.bind.v2.ContextFactory;
import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.developer.JAXBContextFactory;

/**
 * A customized {@link JAXBContext} that enables the {@link JAXBRIContext#XMLACCESSORFACTORY_SUPPORT}
 * in order to getting rid of the LazyInitializationException when using JAXB together with Hibernate.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Do Phuong Hoang (PHD)
 */
public class JAXBContextFactoryImpl implements JAXBContextFactory {

	@Override
	public JAXBRIContext createJAXBContext(SEIModel seiModel, List<Class> classes, List<TypeReference> typeRefs)
		throws JAXBException {
		return ContextFactory.createContext(classes.toArray(new Class[classes.size()]), typeRefs, null, null, false,
			new RuntimeInlineAnnotationReader(), true, false);
	}

}
