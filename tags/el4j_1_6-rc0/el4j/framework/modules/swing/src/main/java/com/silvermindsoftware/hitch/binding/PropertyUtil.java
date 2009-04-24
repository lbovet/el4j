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
package com.silvermindsoftware.hitch.binding;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.ObjectProperty;
import org.jdesktop.beansbinding.Property;

/**
 * This utility class unifies creating Properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public final class PropertyUtil {
	/**
	 * Util classes should not be instantiated.
	 */
	private PropertyUtil() { }
	
	
	/**
	 * Create a {@link Property}. If expression is empty, the property refers to the object itself.
	 * If expression contains a $, then it is parsed as {@link ELProperty}.
	 * Otherwise a simple {@link BeanProperty} is created.
	 * 
	 * @param <S>           the source type
	 * @param <V>           the value type
	 * @param expression    the expression to specify the property
	 * @return              a {@link Property} according to the expression
	 */
	@SuppressWarnings("unchecked")
	public static <S, V> Property<S, V> create(String expression) {
		if (expression == null || expression.length() == 0) {
			return (Property<S, V>) ObjectProperty.create();
		} else if (expression.contains("$")) {
			return ELProperty.create(expression);
		} else {
			return BeanProperty.create(expression);
		}
	}

}
