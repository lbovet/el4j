/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.services.remoting.protocol.ejb;

import java.util.HashMap;

/**
 * This class maps class names to interfaces. It is used to store enriched and
 * dynamically created interfaces.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class InterfacesStorageSingleton {

    /** The singleton instance of this class. */
    private static InterfacesStorageSingleton s_instance;
    
    /** Map that stores the interfaces. */
    private HashMap m_interfaceMap;
  
    /**
     * Creates a new instance.
     */
    protected InterfacesStorageSingleton() {
        m_interfaceMap = new HashMap();
    }

    /**
     * @return Returns the singleton instance of this class.
     */
    public static InterfacesStorageSingleton getInstance() {
        if (s_instance == null) {
            s_instance = new InterfacesStorageSingleton();
        }
        return s_instance;
    }
    
    /**
     * Adds a new interface to the storage.
     * 
     * @param clazz
     *      The interface to store.
     */
    public void putInterface(Class clazz) {
        m_interfaceMap.put(clazz.getName(), clazz);
    }
    
    /**
     * Retrieves the interface for the given name.
     * 
     * @param className
     *      The interfaces class name.
     *      
     * @return Returns the requested interface.
     */
    public Class getInterface(String className) {
        return (Class) m_interfaceMap.get(className);
    }
}
