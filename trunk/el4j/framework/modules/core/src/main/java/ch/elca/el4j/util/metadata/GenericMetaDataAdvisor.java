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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.metadata.annotations.DefaultGenericAnnotationCollector;

/**
 * <p>
 * This class extends the Spring AOP so that annotations can be read from 
 * methods, classes, interfaces and packages. The Spring 2.0 AOP supports only 
 * advices on methods (cf. The Spring Framework - Reference Documentation, 
 * Chapter 6. Aspect Oriented Programming with Spring,
 * <code><a href="http://static.springframework.org/spring/docs/2.0.x/reference/aop.html">
 * http://static.springframework.org/spring/docs/2.0.x/reference/aop.html</a></code>).</p>
 * 
 * <p>
 * The advisor uses the {@link ch.elca.el4j.util.metadata} package to search
 * the specified meta data, for example Java Annotations 
 * ({@link #setMetaDataCollector(String)}). The meta data to which the Advice will 
 * react can be specified in the configuration file. If nothing is specified, 
 * all meta data will be collected.</p>
 * 
 * <p>
 * An object of {@link DefaultGenericAttributeSource} coordinates the collection of
 * the metaData. The collection takes respect on inheritence (cf. javadoc 
 * {@link DefaultGenericAttributeSource}</p>
 * 
 * <p>
 * In chapter '3 Documentation for module core' of the 
 * <code><a href="http://el4j.sourceforge.net/docs/pdf/ReferenceDoc.pdf">
 * el4j reference documentation</a></code> are further informations about the
 * concept and the use of this Sringa AOP extension.</p>
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
public class GenericMetaDataAdvisor extends DefaultPointcutAdvisor
    implements InitializingBean {

    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
            .getLog(GenericMetaDataAdvisor.class);
    
    /**
     * The meta data collecor.
     */
    private GenericMetaDataCollector m_metaDataCollector;
    
    /**
     * <p>
     * The attributes to which the interceptor reacts.</p>
     * 
     * <p>
     * Standard value is null. If it is not defined which meta Data Types has
     * to be collected, the interceptor will get all meta Data. 
     */
    private List <Class> m_interceptingMetaData = null;
    
    /**
     * Inheritance configuration. If it is null, the default configuration 
     * is used {@link DefaultGenericAttributeSource}.
     */
    private DefaultInheritanceConfiguration m_inheritanceConfiguration = null;

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
     * @return The meta data to intercept.
     */
    public List <Class> getInterceptingMetaData() {
        return m_interceptingMetaData;
    }

    /**
     * Sets the meta data to intercept.
     * 
     * @param interceptedAttributes
     *            A list of specific meta data to intercept.
     * @throws IllegalArgumentException
     *            If one or more element(s) defined in the list does not exists.
     */
    public void setInterceptingMetaData(List interceptedAttributes) {
        List <Class> interceptingMetaData = new LinkedList <Class>();
        Class metaDataType;
        
        //TODO ADH | Frage: Effizienz? Gibt es eine bessere Lösung
        /* Make shure, that it is a List of Classe set */
        for (Iterator iter = interceptedAttributes.iterator(); iter.hasNext();) {
            Object e = (Object) iter.next();
            if (e instanceof Class) {
                interceptingMetaData.add((Class) e);
            } else {
                try {
                    metaDataType = Class.forName((String) e);
                    interceptingMetaData.add(metaDataType);
                } catch (ClassNotFoundException e1) {
                    throw new IllegalArgumentException("The specified " 
                            + "meta data type " + e.toString() 
                            + " does not exists.");
                } 
            }      
        }
        m_interceptingMetaData = interceptingMetaData;
    }

    /**
     * @see AbstractGenericMetaDataCollector#setInheritenceConfiguration(InheritanceConfiguration) 
     * @param inheritanceConfiguration
     *                  The inheritance configuration object.
     */
    public void setInheritanceConfiguration(DefaultInheritanceConfiguration 
            inheritanceConfiguration) {
        m_inheritanceConfiguration = inheritanceConfiguration;
    }

    /**
     * Setter of the meta data collector. If nothing will be set, the annotation
     * collector {@link DefaultGenericAnnotationCollector} will be used.
     * 
     * @param metaDataCollector
     *          The Id of the metaDataType to set. The Id's of the
     *          metaDataTypes are defined in {@link DefaultMetaDataCollectorFactory}.
     */
    public void setMetaDataCollector(GenericMetaDataCollector metaDataCollector) {
        m_metaDataCollector = metaDataCollector;
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
        // nothing is defined, the standard collector for annotations
        // will be set.
        if (m_metaDataCollector == null) {
            m_metaDataCollector = new DefaultGenericAnnotationCollector();
        }

        /*
         * Section Two: Configure the needed objects
         */
        
        // Set the inheritance configuration
        m_metaDataCollector.setInheritenceConfiguration(m_inheritanceConfiguration);
    
        // Set the meta data which 'invoke' the Advice
        m_metaDataCollector.setInterceptingMetaData(m_interceptingMetaData);
  
        // In case the Advice implements MetaDataCollectorAware, we set
        // its meta data collector
        if (getAdvice() instanceof MetaDataCollectorAware) {
            ((MetaDataCollectorAware) getAdvice())
                .setMetaDataSource(m_metaDataCollector);
        }
        
    }

}