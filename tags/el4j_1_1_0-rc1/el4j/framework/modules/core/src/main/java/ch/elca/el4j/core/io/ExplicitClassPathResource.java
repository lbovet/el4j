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
package ch.elca.el4j.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * This is an explicit class path resource. A normal class path resource has
 * only a path that can point to multiple resources. By using the given url too
 * this resource explicitly points to one resource.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class ExplicitClassPathResource extends ClassPathResource {
    /**
     * See {@link #getURL()}.
     */
    private URL m_url;

    /**
     * Constructor with an explicit url for the given class path resource.
     * 
     * @param url
     *            The explicit url for this class path resource.
     * @param path
     *            The path to get this resource from class loader. This path can
     *            return a different resource than the given url points to.
     * @param classLoader
     *            The class loader used to load the given url. 
     */
    public ExplicitClassPathResource(URL url, String path, 
        ClassLoader classLoader) {
        super(path, classLoader);
        Assert.notNull(url);
        m_url = url;
    }
    
    /**
     * @return Returns the explicit url for this class path resource.
     */
    @Override
    public URL getURL() throws IOException {
        return m_url;
    }
    
    /**
     * @return Returns the input stream of the given url.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return m_url.openStream();
    }
}
