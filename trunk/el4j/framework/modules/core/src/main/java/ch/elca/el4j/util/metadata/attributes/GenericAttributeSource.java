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
package ch.elca.el4j.util.metadata.attributes;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.metadata.Attributes;

/**
 * This class is ... TODO ADH | Class description
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Haefeli (ADH)
 * @deprecated use {@link AbstractGenericMetaDataCollector}.
 */
@Deprecated
public interface GenericAttributeSource {

    /**
     * Getter method to get the attributes implementation.
     *
     * @return Attributes
     */
    public Attributes getAttributes();

    /**
     * Setter method for the interceptedAttributes.
     *
     * @param interceptingAttributes
     *            The attributes to set
     */
    public void setInterceptingAttributes(List interceptingAttributes);

    /**
     * Getter method for the interceptedAttributes.
     *
     * @return Attributes
     */
    public List getInterceptingAttributes();

    /**
     * Return the attribute for this method invocation. Defaults to the class's
     * attribute if no method attribute is found.
     *
     * @param method
     *            method for the current invocation. Can't be null
     * @param clazz
     *            target class for this invocation. May be null.
     * @return Attribute for this method, or null if there is no such attribute
     *         defined at this method.
     *
     */
    public Object getAttribute(Method method, Class clazz);

}