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
package ch.elca.el4j.util.env;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.PropertiesHelper;

/**
 * This class provides access to the currently used environment properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 * @author Martin Zeltner (MZE)
 */
public class EnvPropertiesUtils {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(CollectionUtils.class);
    
    /**
     * Hide default constructor.
     */
    protected EnvPropertiesUtils() { }
    
    /**
     * Retrieves the currently used environment properties.
     * @return The currently used environment properties. 
     * 
     * @deprecated Use method {@link #getEnvPlaceholderProperties()} instead.
     */
    @Deprecated
    public static Properties getEnvProperties() {
        s_logger.debug(
            "DEPRECATED: Use method 'getEnvPlaceholderProperties' instead.");
        return getEnvPlaceholderProperties();
    }
    
    /**
     * Retrieves the currently used placeholder environment properties.
     * @return The currently used placeholder environment properties. 
     */
    public static Properties getEnvPlaceholderProperties() {
        return new PropertiesHelper()
            .loadProperties("classpath:env-placeholder.properties");
    }
    
    /**
     * Retrieves the currently used bean property environment properties.
     * @return The currently used bean property environment properties. 
     */
    public static Properties getEnvBeanPropertyProperties() {
        return new PropertiesHelper()
            .loadProperties("classpath:env-bean-property.properties");
    }
}
