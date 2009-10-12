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
package ch.elca.el4j.demos.rcp.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

/**
 * 
 * This class is a helper that provides lists of getters for a specific class.
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
public class EntityHelper<T> {
        
    /**
     * The domain class this helper is for.
     */
    private Class m_type;
    
    /**
     * All getter names.
     */
    private List<String> m_getterNames;
    
    /**
     * Map of setters with names.
     */
    private Map<String, Method> m_setters;
    
    /**
     * Constructor.
     * 
     * @param type Class type
     */
    public EntityHelper(Class type) {
        m_type = type;
        m_getterNames = new ArrayList<String>();
        m_setters = new HashMap<String, Method>();
        findMethods(m_type, "get", m_getterNames);   
        
        List<String> columns = PropertyReader.getColumns();
        List<String> excludeColumns = PropertyReader.getExcludeColumns();
        
         // Check if columns are set. If not so, take all and
        if (!columns.isEmpty()) {
            List<String> newGetters = new ArrayList<String>();
            for (String s : columns) {
                if (m_getterNames.contains(s)) {
                    newGetters.add(s);
                }
            }
            m_getterNames = newGetters;
        } else if (!excludeColumns.isEmpty()) {
            for (String s : excludeColumns) {
                m_getterNames.remove(s);
            }
        }
    }
    
    /**
     * @return Return all properties, i.e. the transformed names of all getters
     */
    public List<String> getProperties() {
        return m_getterNames;
    }
    
    /**
     * Find all methods with the giving prefix in Class clazz.
     * 
     * @param clazz
     *            The class to look for methods
     * @param prefix
     *            The prefix the method must have, e.g. 'get' or 'set'
     * @param list
     *            The list to add the found methods to .
     */
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
     * @param prefix
     *            Prefix to remove
     * @param name
     *            Name of getter to transform
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
}
