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
package ch.elca.el4j.util.metadata.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.metadata.Attributes;

/**
 * Helper class to get annotations of a field, method, and class. Annotations
 * can also fetched filtered.
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
public class Annotations implements Attributes {
    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Class targetClass) {
        return Arrays.asList(targetClass.getAnnotations());
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Method targetMethod) {
        return Arrays.asList(AnnotationUtils.getAnnotations(targetMethod));
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Field targetField) {
        return Arrays.asList(targetField.getAnnotations());
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Class targetClass, Class filter) {
        return filter(getAttributes(targetClass), filter);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Method targetMethod, Class filter) {
        return filter(getAttributes(targetMethod), filter);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getAttributes(Field targetField, Class filter) {
        return filter(getAttributes(targetField), filter);
    }

    /**
     * Filters the given collection. Only items assignable to the given filter
     * class will stay in collection.
     * 
     * @param c Is the collection to filter.
     * @param filter Is the class used for filtering.
     * @return Returns the filtered collection.
     */
    @SuppressWarnings("unchecked")
    protected Collection filter(Collection c, Class filter) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object a = it.next();
            if (!filter.isAssignableFrom(a.getClass())) {
                it.remove();
            }
        }
        return c;
    }
}
