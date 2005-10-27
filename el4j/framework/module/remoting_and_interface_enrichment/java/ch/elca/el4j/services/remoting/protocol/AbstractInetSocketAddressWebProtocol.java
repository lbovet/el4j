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

package ch.elca.el4j.services.remoting.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * This is an abstract <code>InetSocketAddress</code> protocol for using in
 * web servers. It is used to map servlets to a path in the current context.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractInetSocketAddressWebProtocol 
    extends AbstractInetSocketAddressProtocol {

    /**
     * Is the context path of the webserver where the service is running.
     */
    private String m_contextPath;

    /**
     * This map contains all url mappings for this protocol.
     */
    private Map m_urlMappings = new HashMap();

    /**
     * This map contains all flags, which indicates if an url map is already
     * initialized.
     */
    private Map m_urlMappingsInitialized = new HashMap();

    /**
     * @return Returns the contextPath.
     */
    public String getContextPath() {
        return m_contextPath;
    }

    /**
     * @param contextPath
     *            The contextPath to set.
     */
    public void setContextPath(String contextPath) {
        m_contextPath = contextPath;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
                getContextPath(), "contextPath", this);
    }

    /**
     * {@inheritDoc}
     */
    public void prepareExporterDependentBeans(
            RemotingServiceExporter exporterBean) {
        /**
         * If the this bean runs in a web server, it has to be a servlet and so
         * it needs to be mapped on the current context. In this phase we only
         * prepare the mapping. It will be done in a second phase to prohibit a
         * circular dependency.
         */
        if (exporterBean.getApplicationContext() 
            instanceof AbstractRefreshableWebApplicationContext) {
            AbstractRefreshableWebApplicationContext parentAppContext 
                = (AbstractRefreshableWebApplicationContext) exporterBean
                    .getApplicationContext();
            Properties mappings = new Properties();
            mappings.put("/" + exporterBean.getServiceName(), exporterBean
                    .getBeanName());
            SimpleUrlHandlerMapping urlMapping = new SimpleUrlHandlerMapping();
            urlMapping.setMappings(mappings);

            m_urlMappings.put(exporterBean.getBeanName(), urlMapping);

            ConfigurableListableBeanFactory configurableBeanFactory 
                = parentAppContext.getBeanFactory();
            configurableBeanFactory.registerSingleton("handlerMappingForBean"
                    + exporterBean.getBeanName(), urlMapping);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void finalizeExporterDependentBeans(
            RemotingServiceExporter exporterBean) {
        /**
         * If the url mapping is not already allocated to the application, this
         * will be done here. This allocation will be done only once.
         */
        String beanName = exporterBean.getBeanName();
        if (!m_urlMappingsInitialized.containsKey(beanName)
                && m_urlMappings.containsKey(beanName)) {
            SimpleUrlHandlerMapping urlMapping 
                = (SimpleUrlHandlerMapping) m_urlMappings
                    .get(beanName);
            m_urlMappingsInitialized.put(beanName, urlMapping);
            urlMapping.setApplicationContext(exporterBean
                    .getApplicationContext());
        }
    }
}