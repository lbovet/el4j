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
package ch.elca.el4j.demos.rcp.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import ch.elca.el4j.util.env.EnvPropertiesUtils;

/**
 * 
 * This class reads the properties from the env.properties file and provides
 * lists of the properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class PropertyReader {

    /**
     * Columns to display (property from properties file).
     */
    private static final String ENV_COLUMNS = "masterDetail.columns";
    
    /**
     * Columns to exclude from being displayed (property from properties file).
     */
    private static final String ENV_EXCLUDE_COLUMNS 
        = "masterDetail.excludeColumns";
    
    /**
     * Fields (i.e. columns) to use for filtering.
     */
    private static final String FILTER_PROPERTIES 
        = "masterDetail.filterProperties";
    
    /**
     * The Separator.
     */
    private static final String SEPARATOR 
        = ",";
    
    /**
     * The Env properties file.
     */
    private static Properties s_properties;
    
    /**
     * Constructor.
     */
    protected PropertyReader() { }
    
    /**
     * @return A list of all columns to exclude
     */
    public static List<String> getExcludeColumns() {
        return readProperty(ENV_EXCLUDE_COLUMNS);  
    }
    
    /**
     * @return A list of all desired columns
     */
    public static List<String> getColumns() {
        return readProperty(ENV_COLUMNS);  
    }
    
    /**
     * @return A list of filterProperties
     */
    public static List<String> getFilterProperties() {
        return readProperty(FILTER_PROPERTIES);
        
    }
    
    /**
     * Read a specific property out of the env.properties file and convert into
     * a list of strings.
     * 
     * @param property
     *            The property to get
     * @return List of values
     */
    private static List<String> readProperty(String property) {
        List<String> result = new ArrayList<String>();

        String values = getProperties().getProperty(property);
        StringTokenizer tokenizer = new StringTokenizer(values, SEPARATOR);
        
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }
    
    /**
     * @return The env properties file.
     */
    private static Properties getProperties() {
        if (s_properties == null) {
            s_properties = EnvPropertiesUtils.getEnvProperties();
        }
        return s_properties;
    }
}
