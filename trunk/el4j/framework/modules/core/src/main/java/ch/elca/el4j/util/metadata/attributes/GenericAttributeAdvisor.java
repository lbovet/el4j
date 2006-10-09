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

import java.util.List;

import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.util.metadata.GenericMetaDataAdvisor;

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
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 * @deprecated use class {@link GenericMetaDataAdvisor}.
 */
@Deprecated
public class GenericAttributeAdvisor extends GenericMetaDataAdvisor {
    
    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
            .getLog(GenericAttributeAdvisor.class);

    /**
     * The attribute source.
     */
    private DefaultGenericAttributeSource m_attributeSource;
    
    /**
     * Default constructor. The advice must be set with
     * {@link #setMethodInterceptor(MethodInterceptor)}
     */
    public GenericAttributeAdvisor() {
        /* The contructor is needed, because there is an
         * constructor which takes an argument. If no
         * default constructor is set, the other constructor
         * has to be called. But in Spring it is common
         * to use setters, to set the dependencies. That this
         * is possible, it needs this default constructor.
         */
    }
    
    /**
     * Constructor which sets the advice being received as
     * parameter.
     * 
     * @param advice
     *            The advice to set.
     * @deprecated 
     *            In Spring it is common to use the constructor
     *            to set dependencies. Therefore set the advice with
     *            {@link #setMethodInterceptor(MethodInterceptor)}
     */
    @Deprecated
    public GenericAttributeAdvisor(Advice advice) {
        setAdvice(advice);
    }

    /**
     * Sets the attributes to intercept.
     * 
     * @see #setInterceptingMetaData(List)
     * @deprecated use method {@link #setInterceptingMetaData(List)}           
     */
    public void setInterceptingAttributes(List interceptedAttributes) {
        setInterceptingMetaData(interceptedAttributes);
    }
    
    /**
     * Sets the attribute source.
     *
     * @param attributeSource
     *            The AttributeSource to set
     */
    public void setAttributeSource(DefaultGenericAttributeSource attributeSource) {
        m_attributeSource = attributeSource;
    }
    
    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        
        
        /* 
         * Section One: Check if the necessary Properties has been set 
         */
    
        // Check whether an Advice has been defined.
        if (getAdvice() == null) {
            String message = "An 'org.aopalliance.aop.Advice' "
                + "has to be defined.";
            s_logger.error(message);
            throw new BaseException(message, (Throwable) null);
        }
        
        // Check whether an meta data collector has been defined. If 
        // nothing is defined, the old Default implementation of
        // DefaultGenericAttributeSource is used.
        if (m_attributeSource == null) {
            m_attributeSource = new DefaultGenericAttributeSource();
        }

        /*
         * Section Two: Configure the needed objects
         */
    
        // Set the meta data which 'invoke' the Advice
        m_attributeSource.setInterceptingMetaData(
            getInterceptingMetaData());
  
        // In case the Advice implements MetaDataCollectorAware, we set
        // its meta data collector
        if (getAdvice() instanceof AttributeSourceAware) {
            ((AttributeSourceAware) getAdvice())
                .setAttributeSource(m_attributeSource);
        }
        
    }

}