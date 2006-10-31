package ch.elca.el4j.util.env;
import java.util.Properties;

import ch.elca.el4j.util.codingsupport.PropertiesHelper;

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
 */
public class EnvPropertiesUtils {
    
    /**
     * Hide default constructor.
     */
    protected EnvPropertiesUtils() { }
    
    /**
     * Retrieves the currently used environment properties.
     * @return The currently used environment properties. 
     */
    public static Properties getEnvProperties() {
        return new PropertiesHelper()
            .loadProperties("classpath:env/env.properties");
    }
}
