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
package ch.elca.el4j.util.metadata;

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.metadata.Attributes;


/**
 * <p>
 * A meta data collector collects meta data (e.g. common attributes, annotations)
 * from the specified target.</p>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Haefeli (ADH)
 */
public interface GenericMetaDataCollector extends Attributes {

    /**
     * <p>
     * Returns the meta data (e.g common attributes or annotations) for the
     * specified elementType.<p>
     * 
     * <p><b>Note</b><br />
     * This method can be useful to get all class metaData, after a 
     * Interceptor has been constructed.</p>
     * 
     * <p><b>Pay Attention</b><br />
     * At the moment are just the types Method and Class implemented. At 
     * the moment is not clear, if it is useful to implement other types, 
     * like Constructors or packages.</p>
     * 
     * @param target 
     *                    Target which will be intercepted.             
     * @param targetClass 
     *                    The invocated class. 
     * @param elementType 
     *                    ElementType of the target (e. g. Type, Method)
     * @return Meta data for this method, or null if there is no meta data
     * defined at this method.
     * 
     * @throws ClassCastException 
     *                          If the target does not corresponding to 
     *                          the specified elementType.
     * @throws UnsupportedOperationException 
     *                          If a elementType is specified, which is not 
     *                          yet supported. Refer to the section 
     *                          'Pay Attention' for more details.
     */
    public Collection getMetaData(Object target, ElementType elementType, 
        Class targetClass);

    /**
     * <p>
     * Returns the meta data (e.g common attributes or annotations) for the
     * specified elementType.<p>
     * 
     * @param target 
     *              Target which will be intercepted.
     * @param methodInvocation 
     *              The invocation object of the interception containing
     *              the context of the target method call.
     * @param elementType 
     *              ElementType of the target (e. g. Type, Method).
     * @return Meta data for this method, or null if there is no meta data
     * defined at this method.
     * 
     * @throws ClassCastException 
     *              If the target does not corresponding to the specified
     *              elementType.
     * @throws UnsupportedOperationException 
     *              If a elementType is specified, which is not yet supported.
     *              Refer to the section 'Pay Attention' for more details.
     * 
     * @see GenericMetaDataCollector#getMetaData(Object, ElementType, Class)
     */
    public Collection getMetaData(Object target, 
        MethodInvocation methodInvocation, ElementType elementType);

    /**
     * <p>
     * Returns the meta data (e.g. common attributes or annotations) for the
     * specified method.<p>
     * 
     * @param targetMethod 
     *                  Method from wich the metaData has to be found.
     * @param targetClass 
     *                  The invocated class.
     * 
     * @return Meta data for this method. If not meta data found, the returned
     * Collection is empty.
     */
    public Collection getMethodOperatingMetaData(Method targetMethod, 
        Class targetClass);

    /**
     * Returns the meta data (e.g. Common Attributes or annotations) for the
     * method which is defined in the specified methodInvocation.
     * 
     * @param methodInvocation
     *              The invocation object of the interception containing
     *              the context of the target method call.
     * 
     * @return Meta data for this method. If not meta data found, the returned 
     * Collection is empty.
     * 
     * @see GenericMetaDataCollector#getMethodOperatingMetaData(Method, Class)
     */
    public Collection getMethodOperatingMetaData(MethodInvocation methodInvocation);

    /**
     * Setter of the interceptedAttributes. The collector considers just the
     * meta data specified in the <code>List interceptingMetaData</code>.
     * 
     * @param interceptingMetaData 
     *                            The intercepting attributes
     */
    public void setInterceptingMetaData(List <Class> interceptingMetaData);

    /**
     * Setter for inheritance configuration. The inheritance configuration 
     * defines how deep meta data will be inherited to its childs. If no 
     * configuration is set, the collection is based on the default
     * configuration.
     *  
     * @param inheritanceConfiguration
     *                          The inheritence confifiguration object.
     */
    public void setInheritenceConfiguration(InheritanceConfiguration 
            inheritanceConfiguration);

}