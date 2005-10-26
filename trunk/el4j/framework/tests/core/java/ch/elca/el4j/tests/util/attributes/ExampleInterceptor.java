/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.tests.util.attributes;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.util.attributes.AttributeSourceAware;
import ch.elca.el4j.util.attributes.GenericAttributeSource;

/**
 * The interceptor to be invoked if an ExampleAttribute is set.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class ExampleInterceptor implements MethodInterceptor,
    AttributeSourceAware {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory.getLog(ExampleInterceptor.class);

    /**
     * The attribute source.
     */
    private GenericAttributeSource m_attributeSource;

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        ExampleAttributeInterface att = null;
        Object obj = m_attributeSource.getAttribute(methodInvocation
            .getMethod(), methodInvocation.getClass());

        if (obj instanceof ExampleAttributeInterface) {
            att = (ExampleAttributeInterface) obj;
        } else {
            String message = "There is no attribute of type "
                + "ExampleAttributeInterface declared at method "
                + methodInvocation.getMethod();
            s_logger.error(message);
            throw new BaseException(message, (Object[]) null);
        }

        Object[] param = methodInvocation.getArguments();
        param[0] = new Integer(att.getFactor());

        Object retVal = null;
        try {
            // This is an around advice.
            // Invoke the next interceptor in the chain.
            // This will normally result in a target object being invoked.
            retVal = methodInvocation.proceed();
        } catch (Throwable ex) {
            throw ex;
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributeSource(GenericAttributeSource attributeSource) {
        m_attributeSource = attributeSource;
    }
}