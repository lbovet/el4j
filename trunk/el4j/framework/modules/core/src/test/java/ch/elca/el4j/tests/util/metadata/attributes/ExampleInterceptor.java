/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.tests.util.metadata.attributes;

import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.metadata.GenericMetaDataSource;
import ch.elca.el4j.util.metadata.MetaDataSourceAware;

/**
 * The interceptor to be invoked if an ExampleAttribute is set.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 * @author Martin Zeltner (MZE)
 */
public class ExampleInterceptor
    implements MethodInterceptor, MetaDataSourceAware {

    /**
     * The attribute source.
     */
    private GenericMetaDataSource m_metaDataSource;

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        ExampleAttributeInterface att = null;
        Collection c = m_metaDataSource.getMetaData(
            methodInvocation.getMethod(), methodInvocation.getClass());

        Assert.isTrue(c != null && !c.isEmpty());
        Object obj = c.iterator().next();
        if (obj instanceof ExampleAttributeInterface) {
            att = (ExampleAttributeInterface) obj;
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "There is no attribute of type "
                + "ExampleAttributeInterface declared at method "
                + methodInvocation.getMethod());
        }

        Object[] param = methodInvocation.getArguments();
        param[0] = new Integer(att.getFactor());

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