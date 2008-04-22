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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.persistence.Entity;

import ch.elca.el4j.util.env.EnvPropertiesUtils;

/**
 * 
 * This class is a helper for work related to reflection.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T> Class type this class is helper for
 * @author David Stefan (DST)
 */
public class ReflectionHelper<T> {
    
    /**
     * Columns to display.
     */
    private static final String ENV_COLUMNS = "masterDetail.columns";
    
    /**
     * Columns to include.
     */
    private static final String ENV_INCLUDE_COLUMNS 
        = "masterDetail.includeColumns";
    
    /**
     * Columns to exclude from being displayed.
     */
    private static final String ENV_EXCLUDE_COLUMNS 
        = "masterDetail.excludeColumns";
    
    /**
     * The domain class this helper is for.
     */
    private Class m_type;
    
    /**
     * All getter names.
     */
    private List<String> m_getterNames;
    
    /**
     * All setter names.
     */
    private List<String> m_setterNames;
    
    /**
     * Map of setters with names.
     */
    private Map<String, Method> m_setters;
    
    /**
     * Constructor.
     * 
     * @param type Class type
     */
    public ReflectionHelper(Class type) {
        m_type = type;
        m_getterNames = new ArrayList<String>();
        m_setterNames = new ArrayList<String>();
        m_setters = new HashMap<String, Method>();
        findMethods(m_type, "get", m_getterNames);
        findMethods(m_type, "set", m_setterNames);
        
        Properties props = EnvPropertiesUtils.getEnvProperties();
        
        if (props.containsKey(ENV_COLUMNS)) {
            List<String> newGetters = new ArrayList<String>();
            String values = props.getProperty(ENV_COLUMNS);
            StringTokenizer tokenizer = new StringTokenizer(values, ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (m_getterNames.contains(token)) {
                    newGetters.add(token);
                }
            }
            m_getterNames = newGetters;
        } else {
            if (props.containsKey(ENV_INCLUDE_COLUMNS)) {
                String values = props.getProperty(ENV_INCLUDE_COLUMNS);
                StringTokenizer tokenizer = new StringTokenizer(values, ",");
                while (tokenizer.hasMoreTokens()) {
                    m_getterNames.add(tokenizer.nextToken());
                }
            }
            
            if (props.containsKey(ENV_EXCLUDE_COLUMNS)) {
                String values = props.getProperty(ENV_EXCLUDE_COLUMNS);
                StringTokenizer tokenizer = new StringTokenizer(values, ",");
                while (tokenizer.hasMoreTokens()) {
                    m_getterNames.remove(tokenizer.nextToken());
                }
            }
        }
    }
    
    /**
     * Find all getters of this type of class and store them in a List.
     *
     * @param clazz .
     * @param prefix .
     * @param list .
.     */
    private void findMethods(Class<?> clazz, String prefix, List<String> list) {
        if (clazz.isAnnotationPresent(Entity.class)) {
            // Get all methods of the DOM class and add them to
            // the list of getters
            for (Method f : clazz.getDeclaredMethods()) {
                if (f.getName().startsWith(prefix)) {
                    // HACK to get rid of complex type getters/setters
                    // check if return type or parameter is complex
                    if (f.getParameterTypes().length > 0) {
                        if (!Iterable.class.isAssignableFrom(f
                            .getParameterTypes()[0])) {
                            list.add(removePrefix(prefix, f.getName()));
                            m_setters.put(removePrefix(prefix, f.getName()), f);
                        }
                    } else {
                        if (!Iterable.class.isAssignableFrom(f.
                            getReturnType())) {
                            list.add(removePrefix(prefix, f.getName()));
                        }
                    }
                }
            }
            // Check superclass too
            if (clazz.getSuperclass() != Object.class) {
                findMethods(clazz.getSuperclass(), prefix, list);
            }
        }
    }
    
    /**
     * Transform name of getter to format we need.
     * 
     * @param prefix Prefix to remove
     * @param name Name of getter to transform
     * @return Name of getter without 'get' and leading character lower case
     */
    private String removePrefix(String prefix, String name) {
        String result = name;
        // Get rid of 'get'
        result = result.replace(prefix, "");
        // lower first character
        result 
            = result.substring(0, 1).toLowerCase().concat(result.substring(1));
        return result;
    }

    /**
     * @return Return all properties, i.e. the transformed names of all getters
     */
    public List<String> getProperties() {
        return m_getterNames;
    }
    
    /**
     * @return Return all properties that are settable
     */
    public List<String> getSettableProperties() {
        return m_setterNames;
    }
    
}