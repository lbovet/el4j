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

package ch.elca.el4j.tests.util.metadata.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.metadata.GenericMetaDataSource;
import ch.elca.el4j.util.metadata.MetaDataSourceAware;

/**
 * The interceptor to be invoked if an ExampleAnnotation is set.
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
public class ExampleInterceptor
    implements MethodInterceptor, MetaDataSourceAware {

    /**
     * The annotation source.
     */
    private GenericMetaDataSource m_metaDataSource;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method invokedMethod = methodInvocation.getMethod();
        Class targetClass = null;
        if (!AopUtils.isAopProxy(methodInvocation.getThis())) {
            targetClass = methodInvocation.getThis().getClass();
        }
        
        Collection<Annotation> c = m_metaDataSource.getMetaData(
            invokedMethod, targetClass);

        Assert.isTrue(c != null && !c.isEmpty());
        Annotation annotation = c.iterator().next();
        int factor = -1;
        if (annotation instanceof ExampleAnnotationOne) {
            factor = ((ExampleAnnotationOne) annotation).factor();
        } else if (annotation instanceof ExampleAnnotationTwo) {
            ExampleAnnotationTwo eat = (ExampleAnnotationTwo) annotation;
            factor = eat.factor() * ExampleAnnotationTwo.CONSTANT_FACTOR;
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "There is no annotated annotation of type "
                + "ExampleAnnotation declared at method "
                + methodInvocation.getMethod());
        }

        Object[] param = methodInvocation.getArguments();
        param[0] = new Integer(factor);

        // This is an around advice.
        // Invoke the next interceptor in the chain.
        // This will normally result in a target object being invoked.
        return methodInvocation.proceed();
    }

    /**
     * {@inheritDoc}
     */
    public void setMetaDataSource(GenericMetaDataSource metaDataSource) {
        m_metaDataSource = metaDataSource;
    }
}