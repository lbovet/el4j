/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.util.attributes;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.metadata.Attributes;
import org.springframework.metadata.commons.CommonsAttributes;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class simplifies Attributes programming.
 * 
 * <p>
 * One way to set up AOP in Spring is to define a DefaultAdvisorAutoProxyCreator
 * bean which will create AOP proxies for all Advisors that are defined in the
 * same BeanFactory. These advisors are the starting points for Attributes. For
 * each attribute, at least 4 beans have to be defined:
 * <ul>
 * <li><b>Advisor </b>: Base interface holding AOP advice (action to take at a
 * joinpoint).
 * <li><b>Advice </b>: A possible advice is for example a MethodInterceptor
 * which intercepts runtime events that occur within a base program. There is a
 * link from the Advisor to the Advice.
 * <li><b>Source </b>: Sources the attributes. There is a link from the Advice
 * to the Source telling the Advice which attribute sources it has to consider.
 * <li><b>Attributes </b>: The attributes implementation to use. There is a
 * link from Source to Attributes to define where it has to take the attributes
 * from.
 * </ul>
 * 
 * <p>
 * This class simplifies the creation of attributes. By using it, only two beans
 * have to be defined:
 * <ul>
 * <li><b>GenericAttributeAdvisor </b>: This bean is an advisor implementing
 * the matches(Method, Class) method. This advisor also creates a
 * DefaultGenericAttributeSource in case no one is defined in the configuration
 * file. The attributes to which the Advice will react also have to be defined
 * in the configuration file.
 * <li><b>Advice </b>: An advice which will be invoked by this Advisor has to
 * be injected via the configuration file.
 * </ul>
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
public class GenericAttributeAdvisor extends StaticMethodMatcherPointcutAdvisor
    implements InitializingBean {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
        .getLog(GenericAttributeAdvisor.class);

    /**
     * The attribute source.
     */
    private GenericAttributeSource m_attributeSource;

    /**
     * The attributes to which the interceptor reacts.
     */
    private List m_interceptingAttributes;

    /**
     * Default constructor.
     */
    public GenericAttributeAdvisor() {
    }

    /**
     * Constructor which sets the advice being received as
     * parameter.
     * 
     * @param advice
     *            The advice to set.
     */
    public GenericAttributeAdvisor(Advice advice) {
        setAdvice(advice);
    }

    /**
     * A convenience getter method for the method interceptor.
     * 
     * @return The methodInterceptor
     */
    public MethodInterceptor getMethodInterceptor() {
        Reject.ifFalse(getAdvice() instanceof MethodInterceptor,
                "The advice is not of type "
                + "'org.aopalliance.intercept.MethodInterceptor'");
        
        return (MethodInterceptor) getAdvice();
    }

    /**
     * A convenience setter method for the method Interceptor.
     * 
     * @param methodInterceptor
     *            The methodInterceptor to set.
     */
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        setAdvice(methodInterceptor);
    }

    /**
     * Returns the attribute source.
     * 
     * @return The Attribute Source
     */
    public GenericAttributeSource getAttributeSource() {
        return m_attributeSource;
    }

    /**
     * Sets the attribute source.
     * 
     * @param attributeSource
     *            The AttributeSource to set
     */
    public void setAttributeSource(GenericAttributeSource attributeSource) {
        m_attributeSource = attributeSource;
    }

    /**
     * Getter method for the attributes to intercept.
     * 
     * @return The attributes to intercept
     */
    public List getInterceptingAttributes() {
        return m_interceptingAttributes;
    }

    /**
     * Sets the attributes to intercept.
     * 
     * @param interceptedAttributes
     *            A list of the attributes to intercept
     */
    public void setInterceptingAttributes(List interceptedAttributes) {
        m_interceptingAttributes = interceptedAttributes;
    }

    /**
     * Perform static checking. If this returns false, no runtime check will be
     * made.
     * 
     * @param method
     *            the candidate method
     * @param targetClass
     *            target class (may be null, in which case the candidate class
     *            must be taken to be the method's declaring class)
     * @return whether or not this method matches statically
     */
    public boolean matches(Method method, Class targetClass) {
        return (getAttributeSource().getAttribute(method, targetClass) != null);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {

        // Check whether an Advice has been defined.
        if (getAdvice() == null) {
            String message = "An 'org.aopalliance.aop.Advice' "
                + "has to be defined.";
            s_logger.error(message);
            throw new BaseException(message, (Throwable) null);
        }

        // In case no AttributeSource was defined, we create it on our own.
        if (getAttributeSource() == null) {
            GenericAttributeSource gas = new DefaultGenericAttributeSource();
            setAttributeSource(gas);
        }

        // Set the attributes which 'invoke' the Advice
        if (getAttributeSource().getInterceptingAttributes() == null) {
            getAttributeSource().setInterceptingAttributes(
                getInterceptingAttributes());
        }

        // In case no Attribute implementation is defined, we take the
        // CommonsAttributes implementation. In order to provide interface
        // annotation, too, the AttributesCollector is interposed.
        if (getAttributeSource().getAttributes() == null) {
            AttributesCollector attColl = new AttributesCollector();
            Attributes attType = new CommonsAttributes();
            attColl.setAttributes(attType);
            getAttributeSource().setAttributes(attColl);
        }

        // In case the Advice implements AttributeSourceAware, we set
        // its AttributeSource
        if (getAdvice() instanceof AttributeSourceAware) {
            ((AttributeSourceAware) getAdvice())
                .setAttributeSource(getAttributeSource());
        }
    }

}