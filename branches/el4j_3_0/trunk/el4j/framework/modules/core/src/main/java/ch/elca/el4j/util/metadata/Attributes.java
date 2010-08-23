/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * This Interface has been copied to the EL4J Framework from Spring 2.5.
 * The reason is that it is needed by more than one EL4J core class and
 * no longer integrated inside the Spring 3.0 libraries. 
 * 
 * Interface for accessing attributes at runtime. This is a facade,
 * which can accommodate any attributes API such as Jakarta Commons Attributes,
 * or (possibly in future) a Spring attributes implementation.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @author Mark Pollack
 * @author Rod Johnson
 * @author Jonas Hauenstein (JHN)
 * @since 2.0
 */
public interface Attributes {
	/**
	 * Return the class attributes of the target class.
	 * @param targetClass the class that contains attribute information
	 * @return a collection of attributes, possibly an empty collection, never <code>null</code>
	 */
	Collection getAttributes(Class targetClass);

	/**
	 * Return the class attributes of the target class of a given type.
	 * <p>The class attributes are filtered by providing a <code>Class</code>
	 * reference to indicate the type to filter on. This is useful if you know
	 * the type of the attribute you are looking for and don't want to sort
	 * through the unfiltered Collection yourself.
	 * @param targetClass the class that contains attribute information
	 * @param filter specify that only this type of class should be returned
	 * @return return only the Collection of attributes that are of the filter type
	 */
	Collection getAttributes(Class targetClass, Class filter);

	/**
	 * Return the method attributes of the target method.
	 * @param targetMethod the method that contains attribute information
	 * @return a Collection of attributes, possibly an empty Collection, never <code>null</code>
	 */
	Collection getAttributes(Method targetMethod);

	/**
	 * Return the method attributes of the target method of a given type.
	 * <p>The method attributes are filtered by providing a <code>Class</code>
	 * reference to indicate the type to filter on. This is useful if you know
	 * the type of the attribute you are looking for and don't want to sort
	 * through the unfiltered Collection yourself.
	 * @param targetMethod the method that contains attribute information
	 * @param filter specify that only this type of class should be returned
	 * @return a Collection of attributes, possibly an empty Collection, never <code>null</code>
	 */
	Collection getAttributes(Method targetMethod, Class filter);

	/**
	 * Return the field attributes of the target field.
	 * @param targetField the field that contains attribute information
	 * @return a Collection of attribute, possibly an empty Collection, never <code>null</code>
	 */
	Collection getAttributes(Field targetField);

	/**
	 * Return the field attributes of the target method of a given type.
	 * <p>The field attributes are filtered by providing a <code>Class</code>
	 * reference to indicate the type to filter on. This is useful if you know
	 * the type of the attribute you are looking for and don't want to sort
	 * through the unfiltered Collection yourself.
	 * @param targetField the field that contains attribute information
	 * @param filter specify that only this type of class should be returned
	 * @return a Collection of attributes, possibly an empty Collection, never <code>null</code>
	 */
	Collection getAttributes(Field targetField, Class filter);
}
