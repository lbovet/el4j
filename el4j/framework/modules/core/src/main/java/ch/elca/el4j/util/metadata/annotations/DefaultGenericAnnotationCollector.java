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
package ch.elca.el4j.util.metadata.annotations;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import ch.elca.el4j.util.metadata.AbstractGenericMetaDataCollector;
import ch.elca.el4j.util.metadata.InheritanceConfiguration;

/**
 * Specific collector for java annotations.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Häfeli (ADH)
 */
public class DefaultGenericAnnotationCollector 
    extends AbstractGenericMetaDataCollector {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getClassName(Object object) {
        String name = ((Annotation)object).annotationType().getName();
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection clean(Collection metaData, ElementType elementType) {
        
        String className = null;
        Target annotationTarget;
        ElementType[] target;
        ArrayList cleanedMetaData = new ArrayList();
       
        
        for (Iterator iter = metaData.iterator(); iter.hasNext();) {
            Object m = iter.next();
            
            try {
                className = getClassName(m);
                Class a = Class.forName(className);
                annotationTarget = (Target) a.getAnnotation(Target.class);
                target = annotationTarget.value();
                
                for (int i = 0; i < target.length; i++) {
                    if (target[i].equals(elementType)) {
                        /* The defined targets on that annotation type
                         * corresponds to the requested target.
                         */
                        cleanedMetaData.add(m);
                        continue;
                    }
                }
                

                
            } catch (ClassNotFoundException e) {
                s_logger.error("Annotation not found", e); //TODO bessere Nachtricht
                //TODO Was machen
            } 
        }
        
        return cleanedMetaData;
    }
    
    public Collection getAttributes(Class targetClass) {              
        return getList(targetClass.getAnnotations()); 
}

    public Collection getAttributes(Method targetMethod) {
        return getList(targetMethod.getAnnotations());
    }

    public Collection getAttributes(Field targetField) {
        return getList(targetField.getAnnotations());
    }

    public Collection getAttributes(Class targetClass, Class filter) {
        if (filter == null) {
            return getAttributes(targetClass);
        }
        /** A class has only one annotation of the same type */
        return getList(targetClass.getAnnotation(filter));
    }

    public Collection getAttributes(Method targetMethod, Class filter) {
        if (filter == null) {
            return getAttributes(targetMethod);
        }
        /** A method has only one annotation of the same type */
        return getList(targetMethod.getAnnotation(filter));
    }

    public Collection getAttributes(Field targetField, Class filter) {
        if (filter == null) {
            return getAttributes(targetField);
        }
        /** A field has only one annotation of the same type */
        return getList(targetField.getAnnotation(filter));
    }

    /**
     * Returns an <code>ArrayList</code> with the values of the specified
     * Array. <b>Note</b><br />
     * The method does not the same as
     * {@link java.util.Arrays.asList(Arrays...a)}. This method returns a inner
     * class <code>ArrayList</code> from class <code>Arrays</code>. This
     * inner class does not implement the add method! Do not confuse this inner
     * class with {@link java.util.ArrayList}.
     * 
     * @param array
     *            Array with the values to add to the array list.
     * @return ArrayList containing the values of the specified array.
     */
    private Collection getList(Object[] array) {
        Collection alist = new ArrayList();
        Collections.addAll(alist, array);
        return alist;
    }


    /**
     * @see ch.elca.el4j.util.attributes.MetaDataCollectorAnnotationImpl.getList()
     * @param object
     *            value to add to the array list, can be null.
     * @return ArrayList containing the specified value; if the specified value
     *         is null, an empty ArrayList will returned.
     */
    private Collection getList(Object object) {
        Collection alist = new ArrayList();
        if (object != null) {
            alist.add(object);
        }
        return alist;
    }

}
