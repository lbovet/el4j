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

package ch.elca.el4j.util.metadata;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.util.metadata.annotations.Annotations;

/**
 * This class simplifies the metadata programming.
 * 
 * <p>
 * One way to set up AOP in Spring is to define a DefaultAdvisorAutoProxyCreator
 * bean which will create AOP proxies for all Advisors that are defined in the
 * same BeanFactory. These advisors are the starting points for Attributes. For
 * each attribute, at least 4 beans have to be defined:
 * <dl>
 * <dt>Advisor:</dt>
 *   <dd>Base interface holding AOP advice (action to take at a joinpoint).</dd>
 * <dt>Advice:</dt>
 *   <dd>A possible advice is for example a MethodInterceptor which intercepts 
 *       runtime events that occur within a base program. There is a link from 
 *       the Advisor to the Advice.</dd>
 * <dt>Source:</dt>
 *   <dd>Sources the attributes. There is a link from the Advice to the Source 
 *       telling the Advice which attribute sources it has to consider.</dd>
 * <dt>Attributes:</dt>
 *   <dd>The attributes implementation to use. There is a link from Source to 
 *       Attributes to define where it has to take the attributes from.</dd>
 * </dl>
 * </p>
 * 
 * <p>
 * This class simplifies the creation of attributes. By using it, only two beans
 * have to be defined:
 * <dl>
 * <dt>GenericMetaDataAdvisor:</dt>
 *   <dd>This bean is an advisor implementing the matches(Method, Class) method.
 *       This advisor also creates a DefaultGenericAttributeSource in case no 
 *       one is defined in the configuration file. The attributes to which the 
 *       Advice will react also have to be defined in the configuration 
 *       file.</dd>
 * <dt>Advice:</dt>
 *   <dd>An advice which will be invoked by this Advisor has to be injected via 
 *       the configuration file.</dd>
 * </dl>
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
public class GenericMetaDataAdvisor extends StaticMethodMatcherPointcutAdvisor
    implements InitializingBean {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(GenericMetaDataAdvisor.class);

    /**
     * The metadata source to lookup metadata.
     */
    private GenericMetaDataSource m_metaDataSource;

    /**
     * Are the metadata types where the interceptor will be applied.
     */
    private List<Class> m_interceptingMetaData;

    /**
     * Default constructor.
     */
    public GenericMetaDataAdvisor() { }

    /**
     * Constructor which sets the advice being received as parameter.
     * 
     * @param advice The advice to set.
     */
    public GenericMetaDataAdvisor(Advice advice) {
        setAdvice(advice);
    }

    /**
     * @return Returns the interceptingMetaData.
     */
    public List<Class> getInterceptingMetaData() {
        return m_interceptingMetaData;
    }

    /**
     * @param interceptingMetaData Is the interceptingMetaData to set.
     */
    public void setInterceptingMetaData(List<Class> interceptingMetaData) {
        m_interceptingMetaData = interceptingMetaData;
    }

    /**
     * @return Returns the metaDataSource.
     */
    public GenericMetaDataSource getMetaDataSource() {
        return m_metaDataSource;
    }

    /**
     * @param metaDataSource Is the metaDataSource to set.
     */
    public void setMetaDataSource(GenericMetaDataSource metaDataSource) {
        m_metaDataSource = metaDataSource;
    }

    /**
     * A convenience getter method for the method interceptor.
     * 
     * @return The methodInterceptor
     */
    public MethodInterceptor getMethodInterceptor() {
        Advice advice = getAdvice();
        Assert.isInstanceOf(MethodInterceptor.class, advice,
                "Only method interception supported.");
        return (MethodInterceptor) advice;
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
        return getMetaDataSource().getMetaData(method, targetClass) != null;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {

        // Check whether an Advice has been defined.
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getAdvice(), "advice", this);

        // In case no MetaDataSource was defined, we create it on our own.
        if (getMetaDataSource() == null) {
            s_logger.debug("No specific metadata source set. Using default.");
            setMetaDataSource(new DefaultGenericMetaDataSource());
        }

        // Set the metadata which 'invoke' the Advice
        if (getMetaDataSource().getInterceptingMetaData() == null) {
            s_logger.debug("No intercepting metadata set on metadata source. "
                + "Using intercepting metadata from advisor.");
            getMetaDataSource().setInterceptingMetaData(
                getInterceptingMetaData());
        }

        // In case no metadata implementation is defined, we take the
        // Annotation implementation. In order to provide splitting metadata
        // on multiple source file we interposed the MetaDataCollector.
        if (getMetaDataSource().getMetaDataDelegator() == null) {
            s_logger.debug("No metadata delegator set on metadata source. "
                + "Using metadata collector in combination with the metadata "
                + "delegator for Java 5 annotations.");
            MetaDataCollector collector = new MetaDataCollector();
            collector.setMetaDataDelegator(new Annotations());
            getMetaDataSource().setMetaDataDelegator(collector);
        }

        // In case the Advice implements MetaDataSourceAware, we set
        // its MetaDataeSource
        if (getAdvice() instanceof MetaDataSourceAware) {
            ((MetaDataSourceAware) getAdvice())
                .setMetaDataSource(getMetaDataSource());
        }
    }

}