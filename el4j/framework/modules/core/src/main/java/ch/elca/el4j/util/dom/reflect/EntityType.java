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
package ch.elca.el4j.util.dom.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.helpers.Function;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;
import ch.elca.el4j.util.dom.annotations.MemberOrder;

/**
 * Represents an entity type. Instances are obtained using {@link #get(Class)}.
 * Example:
 * 
 * <pre>EntityType.get(Person.class)</pre>
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

// Style: Keep public final fields? Keeps notation down, eases debugging,
// is not problematic to later extension.
// Style: Require "m" prefix even for public fields?

public class EntityType {
    /** the already reflected types. */
    static Map<Class< ? >, EntityType> s_types 
        = new HashMap<Class< ? >, EntityType>();
    
    /** the domain object class. */
    public final Class<?> clazz;
    
    /** this type's name. */
    @ImplementationAssumption("unqualified entity type name is unique within DOM")
    public final String name;
    
    /** the type's properties.*/
    public final List<Property> props;
    
    /** the type's operations.*/
    public final List<Operation> ops;

    /** 
     * reflects about the domain model class <code>c</code> and creates its
     * EntityType.
     */
    protected EntityType(Class<?> c) {
        this.clazz = c;
        name = c.getSimpleName();
        ExtendedWritableList<Property> mprops 
            = new ExtendedArrayList<Property>();
        List<Operation> mops = new ArrayList<Operation>();

        for (Field f : c.getFields()) {
            mprops.add(new Property(this, f));
        }

        for (Method m : c.getMethods()) {
            String n = m.getName();
            boolean getter;
            // Checkstyle: MagicNumber off
            if (n.startsWith("get")) {
                if (n.equals("getClass")) {
                    continue;
                }
                n = n.substring(3);
                getter = true;
            } else if (n.startsWith("is")) {
                n = n.substring(2);
                getter = true;
            } else {
                getter = false;
                continue;
            }
            // Checkstyle: MagicNumber on

            if (getter) {
                String s = "set" + n;
                Method setter;
                try {
                    setter = c.getMethod(s, new Class[] {m.getReturnType()});
                } catch (NoSuchMethodException e) {
                    setter = null;
                }
                n = n.substring(0, 1).toLowerCase() + n.substring(1);
                mprops.add(new Property(this, m, n, setter));
            } else {
                mops.add(new Operation(this, m));
            }
        }
        
        MemberOrder mo = c.getAnnotation(MemberOrder.class);
        if (mo != null) {
            mprops.mapped(
                new Function<Property, String>() {
                    public String apply(Property d) {
                        return d.name;
                    }                    
                }                
            ).orderLike(
                Arrays.asList(mo.value())
            );
        }
        
        props = Collections.unmodifiableList(
            // restrict to entries present in member order if member order is
            // present. TODO: find a semantically cleaner way to ignore members.
            mo != null ? mprops.subList(0, mo.value().length) : mprops
        );
        ops   = Collections.unmodifiableList(mops);
    }
    
    /** 
     * Returns the property named {@code name} or throws a 
     * {@code NoSuchElementException} if no such propery exists.  
     * @param name see above
     * @return see above
     */
    public Property find(String name) {
        return props.get(
            CollectionUtils.find(
                CollectionUtils.mapped(props, Member.toName),
                name,
                0
            )
        );
    }

    /** @return the entityType describing the domain class c. */
    public static EntityType get(Class c) {
        EntityType t = s_types.get(c);
        if (t == null) {
            t = new EntityType(c);
            s_types.put(c, t);
        }

        assert t.clazz.equals(c);
        return t;
    }    
}
