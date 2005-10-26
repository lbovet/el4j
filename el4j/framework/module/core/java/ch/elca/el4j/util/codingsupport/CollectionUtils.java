/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
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
 *   ("$Source$",
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
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Class elementClass = it.next().getClass();
            if (!containingClassType.isAssignableFrom(elementClass)) {
                s_logger.debug("Found object of type '" + elementClass.getName()
                    + "' which is not in assignable form for type '" 
                    + containingClassType.getName() + "'.");
                return false;
            }
        }
        return true;
    }
}
