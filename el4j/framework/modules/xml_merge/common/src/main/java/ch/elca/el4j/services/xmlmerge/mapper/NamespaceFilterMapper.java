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
package ch.elca.el4j.services.xmlmerge.mapper;

import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import ch.elca.el4j.services.xmlmerge.Mapper;

/**
 * Filters out elements and attributes with a specified namespace.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class NamespaceFilterMapper implements Mapper {        
    
    /**
     * The namespace defining the elements and attributes to be filtered out.
     */
    Namespace m_namespace;
    
    /**
     * Creates a new NamespaceFilterMapper.
     * 
     * @param filteredNamespace
     *            String representing the namespace defining the elements and
     *            attributes to be filtered out
     */
    public NamespaceFilterMapper(String filteredNamespace) {
        this.m_namespace = Namespace.getNamespace(filteredNamespace);
    }

    /**
     * {@inheritDoc}
     */
    public Element map(Element patchElement) {
        if (patchElement == null) {
            return null;
        }
        if (patchElement.getNamespace().equals(m_namespace)) {
            return null;
        } else {
            return filterAttributes(patchElement);
        }
    }

    /**
     * Filters an element's attributes.
     * @param element An element whose attributes will be filtered
     * @return The input element whose attributes have been filtered 
     */
    private Element filterAttributes(Element element) {
        Element result = (Element) element.clone();

        List attributes = result.getAttributes();
        Iterator it = attributes.iterator();

        while (it.hasNext()) {
            Attribute attr = (Attribute) it.next();

            if (attr.getNamespace().equals(m_namespace)) {
                it.remove();
            }
        }

        return result;
    }

    /**
     * Sets the namespace defining the elements and attributes to be filtered
     * out.
     * 
     * @param namespace
     *            The namespace defining the elements and attributes to be
     *            filtered out.
     */
    public void setFilteredNamespace(Namespace namespace) {
        this.m_namespace = namespace;
    }
    
}
