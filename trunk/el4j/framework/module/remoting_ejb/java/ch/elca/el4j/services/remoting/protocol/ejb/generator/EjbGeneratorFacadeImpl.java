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

package ch.elca.el4j.services.remoting.protocol.ejb.generator;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.services.remoting.AbstractRemotingProtocol;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;
import ch.elca.el4j.services.remoting.protocol.Ejb;
import ch.elca.el4j.services.remoting.protocol.ejb.xdoclet.XDocletTagGenerator;

/**
 * This class generates all the needed informations to create EJB wrappers.
 * It is uses in the build system plugin responsible for generating them.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class EjbGeneratorFacadeImpl implements EjbGeneratorFacade {

    /** Separates different configuration locations of the same type. */
    public static final String LOCATIONS_DELIMITER = ",";
    
    /** The static logger. */
    private static Log s_logger
        = LogFactory.getLog(EjbGeneratorFacadeImpl.class);
    
    /**
     * {@inheritDoc}
     */
    public EjbBean[] getEjbBeans(String inclusiveLocs, String exclusiveLocs) { 
        
        ArrayList result = new ArrayList();
        
        String[] inclusiveLocations;
        String[] exclusiveLocations;
        
        inclusiveLocations = getLocations(inclusiveLocs);
        exclusiveLocations = getLocations(exclusiveLocs);
        
        if (s_logger.isDebugEnabled()) {
            debugArray("inclusive", inclusiveLocations);
            debugArray("exclusive", exclusiveLocations);
        }
        
        ModuleApplicationContext context = new ModuleApplicationContext(
                inclusiveLocations, exclusiveLocations, true, null);
        
        Map exporters = context.getBeansOfType(
                RemotingServiceExporter.class);
        
        for (Iterator iter = exporters.entrySet().iterator(); iter.hasNext();) {
            Map.Entry next = (Map.Entry) iter.next();
            String name = (String) next.getKey();
            RemotingServiceExporter exporter 
                = (RemotingServiceExporter) next.getValue();
            
            if (exporter.getRemoteProtocol() instanceof Ejb) {
                result.add(
                        createEjbBean(name, exporter, context,
                                inclusiveLocations, exclusiveLocations));
            }
        }
        return (EjbBean[]) result.toArray(new EjbBean[result.size()]);
    }

    /**
     * Transforms a comma-separated String of configuration locations into an
     * array of strings.
     * 
     * @param locations The comma-separated string.
     * @return Returns an array representation of the comma-separated string.
     */
    protected String[] getLocations(String locations) {
        StringTokenizer tokenizer = new StringTokenizer(locations,
                LOCATIONS_DELIMITER);
        
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; i < result.length; i++) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }
    
    /**
     * Gathers all informations needed to build a wrapper for one specific
     * spring bean that is exported through EJB.
     * 
     * @param exporterBeanName
     *      The name of the exporter bean as it is registered in the spring
     *      context.
     *      
     * @param exporter
     *      The exporter object that exposes the spring bean.
     *      
     * @param appContext
     *      The application context in which the beans are registered.
     *       
     * @param inclusiveLocations
     *      The locations of configurations to include.
     *      
     * @param exclusiveLocations
     *      The locations of configurations to exclude.
     *       
     * @return Returns all the meta informations needed to build an EJB wrapper
     *      for one particular Spring bean. 
     */
    protected EjbBean createEjbBean(String exporterBeanName,
            RemotingServiceExporter exporter, ApplicationContext appContext,
            String[] inclusiveLocations, String[] exclusiveLocations) {
        
        AbstractRemotingProtocol protocol = exporter.getRemoteProtocol();
        Class serviceInterface = exporter.getServiceInterface();
        Class serviceBean = appContext.getType(exporter.getService());
        
        ImplicitContextPassingRegistry ctxPassingRegistry
            = protocol.getImplicitContextPassingRegistry();
        
        boolean contextPassingAvailable = ctxPassingRegistry != null;
        
        if (contextPassingAvailable) {
            serviceInterface = new RemotingServiceExporter().
                    getServiceInterfaceWithContext(serviceInterface);
        }

        EjbBeanImpl ejbBean = new EjbBeanImpl();
        ejbBean.setExporter(exporter);
        ejbBean.setInclusiveLocations(inclusiveLocations);
        ejbBean.setExclusiveLocations(exclusiveLocations);
        ejbBean.setExporterBeanName(exporterBeanName.substring(1));
        ejbBean.setServiceBeanType(serviceBean);
        ejbBean.setServiceInterface(serviceInterface);
        ejbBean.setXDocletTagGenerator(new XDocletTagGenerator(exporter));
        
        return ejbBean;
    }
    
    /**
     * Pushes the contents of the given array to the log (debug mode).
     * 
     * @param msg
     *      An additional message that is prepended to the output.
     *      
     * @param array
     *      The array which contents are logged.
     */
    private void debugArray(String msg, Object[] array) {
        StringBuffer buffer = new StringBuffer(msg);
        buffer.append(": ");
        
        for (int i = 0; i < array.length; i++) {
            buffer.append(array[i].toString());
            if (i < array.length - 1) {
                buffer.append(", ");
            }
        }
        
        s_logger.debug(buffer.toString());
    }
}
