/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.util.codingsupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class supports methods to handle with collections. It covers only caps
 * of class <code>org.springframework.util.CollectionUtils</code>.
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
public final class CollectionUtils {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(CollectionUtils.class);
    
    /**
     * Hidden constructor.
     */
    private CollectionUtils() { }

    /**
     * Null save check if a collection is empty.
     * 
     * @param c Is the given collection
     * @return Returns true if the given collection is null or empty.
     */
    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }
    
    /**
     * Method to always return a list object.
     * 
     * @param list
     *            Is the list which should not be null.
     * @return Returns the given list if it is not null. Otherwise it returns an
     *         empty list.
     */
    public static List asList(List list) {
        return list == null ? new LinkedList() : list;
    }

    /**
     * Method to always return a set object.
     * 
     * @param set
     *            Is the set which should not be null.
     * @return Returns the given set if it is not null. Otherwise it returns an
     *         empty set.
     */
    public static Set asSet(Set set) {
        return set == null ? new HashSet() : set;
    }

    /**
     * Method to always return a map object.
     * 
     * @param map
     *            Is the map which should not be null.
     * @return Returns the given map if it is not null. Otherwise it returns an
     *         empty map.
     */
    public static Map asMap(Map map) {
        return map == null ? new HashMap() : map;
    }

    /**
     * Method to check if a collection contains only objects which are equals, a
     * subclass or implements one of the given classes. If the given collection
     * is empty, <code>true</code> will be returned.
     * 
     * @param c
     *            Is the collection to check.
     * @param containingClassTypes
     *            Are the class types which are expected.
     * @return Returns <code>true</code> if the given collection contains only
     *         objects which are equals, a subclass or implements one of the 
     *         given classes
     */
    public static boolean containsOnlyObjectsOfType(
        Collection c, Class[] containingClassTypes) {
        Reject.ifNull(c);
        Reject.ifNull(containingClassTypes);
        Reject.ifFalse(containingClassTypes.length > 0);
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Class elementClass = it.next().getClass();
            boolean noClassMatches = true;
            for (int i = 0; noClassMatches && i < containingClassTypes.length; 
                i++) {
                Class containingClassType = containingClassTypes[i];
                noClassMatches 
                    = !containingClassType.isAssignableFrom(elementClass);
            }
            if (noClassMatches) {
                if (s_logger.isDebugEnabled()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Found object of type '");
                    sb.append(elementClass.getName());
                    sb.append("' which is not in assignable form for one of ");
                    sb.append("the following types: ");
                    for (int i = 0; i < containingClassTypes.length; i++) {
                        Class containingClassType = containingClassTypes[i];
                        if (i > 0) { sb.append(", "); }
                        sb.append(containingClassType.getName());
                    }
                    s_logger.debug(sb.toString());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Method to check if a collection contains only objects which are equals, a
     * subclass or implements the given class. If the given collection is empty,
     * <code>true</code> will be returned.
     * 
     * @param c
     *            Is the collection to check.
     * @param containingClassType
     *            Is the class type which is expected.
     * @return Returns <code>true</code> if the given collection contains only
     *         objects which are equals, a subclass or implements the given
     *         class.
     */
    public static boolean containsOnlyObjectsOfType(
        Collection c, Class containingClassType) {
        Reject.ifNull(c);
        Reject.ifNull(containingClassType);
        return containsOnlyObjectsOfType(c, new Class[] {containingClassType});
    }
}
