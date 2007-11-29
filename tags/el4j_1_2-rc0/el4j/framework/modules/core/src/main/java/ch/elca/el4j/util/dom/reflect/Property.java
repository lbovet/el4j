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
package ch.elca.el4j.util.dom.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**  
 * represents a property. 
 * 
 * In code, a property is declared using a public field or a setter/getter pair.
 * Properties declared using fields are writable, properties declared using
 * accessors are writable if and only if setters are declared.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */

public class Property extends Member {
    /** this property's type. */
    public final Class<?> type;
    
    /** can this property not be written? */
    public final boolean readonly;
    
    /** creates the property information object declared by field {@code f} 
     * in {@code declaring.clazz}. */
    Property(EntityType declaring, Field f) {
        super(
            declaring,
            f.getName()
        );        
        type = f.getType();
        readonly = false;
    }

    /** creates the property information object describing a property
     * declared in a getter/setter pair.
     * @param declaring the declaring entity type
     * @param getter the getter
     * @param name the property's name as defined by the Java Beans standard
     * @param setter the setter
     */
    Property(EntityType declaring, Method getter, String name, Method setter) {
        super(
            declaring,
            name
        );
        type = getter.getReturnType();
        readonly = setter == null;
    }
}