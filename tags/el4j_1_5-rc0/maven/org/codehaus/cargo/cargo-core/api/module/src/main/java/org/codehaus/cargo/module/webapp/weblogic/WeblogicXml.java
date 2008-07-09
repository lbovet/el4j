/*
 * ========================================================================
 *
 * Copyright 2005 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package org.codehaus.cargo.module.webapp.weblogic;

import java.util.Iterator;

import org.codehaus.cargo.module.AbstractDescriptor;
import org.codehaus.cargo.module.DescriptorType;
import org.codehaus.cargo.module.webapp.EjbRef;
import org.codehaus.cargo.module.webapp.VendorWebAppDescriptor;
import org.jdom.Element;

/**
 * Encapsulates the DOM representation of a weblogic web deployment descriptor
 * <code>weblogic.xml</code> to provide convenience methods for easy access and manipulation.
 *
 * @version $Id: WeblogicXml.java 1476 2007-05-29 09:35:24Z magnayn $
 */
public class WeblogicXml extends AbstractDescriptor implements VendorWebAppDescriptor
{
    /**
     * File name of this descriptor.
     */
    private static final String FILE_NAME = "weblogic.xml";

    /**
     * Constructor.
     *
     * @param rootElement The document root element
     * @param type The descriptor type
     */
    public WeblogicXml(Element rootElement, DescriptorType type)
    {
        super(rootElement, type);
    }

    /**
     * @return weblogic.xml
     */
    public final String getFileName()
    {
        return FILE_NAME;
    }

    /**
     * Adds a ejb reference description to the weblogic.xml.
     * @param ref the reference to add
     */
    public final void addEjbReference(EjbRef ref)
    {
        Element refDescr;
        Iterator i = getElements(WeblogicXmlTag.REFERENCE_DESCRIPTOR);
        if (i.hasNext())
        {
            refDescr = (Element) i.next();
        }
        else
        {          
            refDescr =
                new Element(WeblogicXmlTag.REFERENCE_DESCRIPTOR);
            refDescr = addElement(getDescriptorType().getTagByName(
                WeblogicXmlTag.REFERENCE_DESCRIPTOR), refDescr, getRootElement());
        }

        Element ejbRefElement = new Element(WeblogicXmlTag.EJB_REFERENCE_DESCRIPTION);
        ejbRefElement.addContent(createNestedText(
            getDescriptorType().getTagByName(WeblogicXmlTag.EJB_REF_NAME), ref.getName()));
        ejbRefElement.addContent(createNestedText(
            getDescriptorType().getTagByName(WeblogicXmlTag.JNDI_NAME), ref.getJndiName()));
        addElement(getDescriptorType().getTagByName(
            WeblogicXmlTag.EJB_REFERENCE_DESCRIPTION), ejbRefElement, refDescr);
    }
}
