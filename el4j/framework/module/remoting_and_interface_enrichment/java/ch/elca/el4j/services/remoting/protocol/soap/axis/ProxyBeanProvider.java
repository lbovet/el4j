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

package ch.elca.el4j.services.remoting.protocol.soap.axis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.MessageContext;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.exceptions.BaseRTException;

/**
 * This provider is used in axis to be able to wrap spring beans.
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
public class ProxyBeanProvider extends RPCProvider {
    /**
     * Name of the proxy provider.
     */
    public static final String PROVIDER_PROXY_BEAN = "ProxyBean";

    /**
     * Constant xml attribute value for attribute "provider" in element 
     * "service".
     */
    public static final String WSDD_PROXY_BEAN_PROVIDER_DEFINITION 
        = "java:" + PROVIDER_PROXY_BEAN;

    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(ProxyBeanProvider.class);

    /**
     * Soap service proxies.
     */
    private static Map s_proxyBeans 
        = Collections.synchronizedMap(new HashMap());

    /**
     * Method to get the registered proxy bean.
     * 
     * {@inheritDoc}
     */
    protected Object makeNewServiceObject(MessageContext msgContext,
        String clsName) throws Exception {
        if (!s_proxyBeans.containsKey(clsName)) {
            throw new BaseRTException("No proxy bean with interface {0} "
                + "registered. Please check your configuration.", 
                new Object[] {clsName});
        }
        s_logger.info("Proxy bean '" + clsName + "' requested.");
        return s_proxyBeans.get(clsName);
    }
    
    /**
     * Method to register proxy beans, so axis can get them at later time.
     * 
     * @param proxyInterfaceName 
     *              Is the interface, which the given proxy bean implements.
     * @param proxyBean
     *              Is the proxy bean, which does all the work for the soap 
     *              service.
     */
    static synchronized void registerProxyBean(
        String proxyInterfaceName, Object proxyBean) {
        if (proxyInterfaceName == null || proxyBean == null) {
            throw new BaseRTException("Proxy bean and its iterface must not be"
                + " null.", (Object[]) null);
        }
        
        if (s_proxyBeans.containsKey(proxyInterfaceName)) {
            throw new BaseRTException("Proxy bean with interface {0} already "
                + "registered. Please check your configuration.", 
                new Object[] {proxyInterfaceName});
        }
        s_proxyBeans.put(proxyInterfaceName, proxyBean);
    }
}
