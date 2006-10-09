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
package ch.elca.el4j.tests.util.metadata.annotations.helper;

import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.util.metadata.GenericMetaDataCollector;
import ch.elca.el4j.util.metadata.MetaDataCollectorAware;

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
 * @author Adrian Häfeli (ADH)
 */
public class ExampleMethodInterceptor implements MethodInterceptor,
    MetaDataCollectorAware {
    
    /**
     * Private logger of this class.
     */
    protected static Log s_logger = LogFactory
            .getLog(ExampleMethodInterceptor.class);
    
    /**
     * The meta data collector.
     */
    private GenericMetaDataCollector m_metaDataCollector;

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        // Get meta data (in this case annotations) from the interceted method
        Collection annotations = m_metaDataCollector
                .getMethodOperatingMetaData(methodInvocation);

        // Set the new arguments of the intercepted methods
        Object[] param = methodInvocation.getArguments();

        if (annotations != null && annotations.size() > 0) {
            param[0] = annotations.toArray();
        } else {
            param[0] = null;
            s_logger.info("There was no annotations found on "
                    + methodInvocation.getMethod());
        }

        // Proceed intercepted method and return its result
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
    public void setMetaDataSource(GenericMetaDataCollector metaDataSource) {
        m_metaDataCollector = metaDataSource;
        
    }
}