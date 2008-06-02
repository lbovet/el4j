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
package ch.elca.el4j.services.xmlmerge.springframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.ConfigurationException;
import ch.elca.el4j.services.xmlmerge.XmlMerge;
import ch.elca.el4j.services.xmlmerge.config.ConfigurableXmlMerge;
import ch.elca.el4j.services.xmlmerge.config.PropertyXPathConfigurer;
import ch.elca.el4j.services.xmlmerge.merge.DefaultXmlMerge;

/**
 * A spring resource merging XML files read from other resources.
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
public class XmlMergeResource extends AbstractResource {

    /**
     * An InputStream containing the data of this merged resource. 
     */
    InputStream m_result;
    
    /**
     * The list of resources to merge.
     */
    List m_resources;
    
    /**
     * An XmlMerge instance used to merge the resources.
     */
    XmlMerge m_xmlMerge;
    
    /**
     * Configuration properties.
     */
    Map m_properties;
        
    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "Resource merging other XML resources using XmlMerge";
    }
    
    /**
     * Returns an InputStream containing the data of this merged resource.
     * 
     * @return The InputStream containing the data of this merged resource.
     * @see org.springframework.core.io.AbstractResource
     */
    public InputStream getInputStream() throws IOException {

        if (m_properties == null) {
            // Default configuration
            m_xmlMerge = new DefaultXmlMerge();
        } else {
            try {
                m_xmlMerge = new ConfigurableXmlMerge(
                    new PropertyXPathConfigurer(m_properties));
            } catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        if (m_resources == null) {
            throw new RuntimeException("Resources not set");
        }

        InputStream[] sources = new InputStream[m_resources.size()];

        for (int i = 0; i < sources.length; i++) {
            sources[i] = ((Resource) m_resources.get(i)).getInputStream();
        }

        try {
            return m_xmlMerge.merge(sources);
        } catch (AbstractXmlMergeException e) {
            throw new RuntimeException(e);
        }
    }
     
    /**
      * Sets the list of resources to merge.
      * 
      * @param resources
      *            The list of resources to merge
      */
    public void setResources(List resources) {
        this.m_resources = resources;

    }
     
    /**
     * Sets the configuration properties.
     * 
     * @param map
     *            A map containing the configuration properties
     */
    public void setProperties(Map map) {
        m_properties = map;
    }

}
