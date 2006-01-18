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

package ch.elca.el4j.services.remoting;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This class passes the context additionally with every method call.
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
public class ServerContextInvocationHandler
    implements InvocationHandler, Serializable {
    
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(ServerContextInvocationHandler.class);

    /**
     * Is the implementation of the service which has to be called.
     */
    private final Object m_service;

    /**
     * Is the service interface which is implemented by the service.
     */
    private final Class m_serviceInterface;

    /**
     * Is the registry for the implicit context passing.
     */
    private final ImplicitContextPassingRegistry 
        m_implicitContextPassingRegistry;

    /**
     * Constructor.
     * 
     * @param service 
     *              Is the real service which does the work.
     * @param serviceInterface
     *              Is the interface which the service has implemented.
     * @param implicitContextPassingRegistry
     *              Is the implicit context passing registry.
     */
    public ServerContextInvocationHandler(Object service,
            Class serviceInterface,
            ImplicitContextPassingRegistry implicitContextPassingRegistry) {
        m_service = service;
        m_serviceInterface = serviceInterface;
        m_implicitContextPassingRegistry = implicitContextPassingRegistry;
        if (m_implicitContextPassingRegistry == null) {
            s_logger.warn("No ImplicitContextPassingRegistry defined! "
                    + "Context will not be passed through.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        Method m;
        Object[] newArgs;
        String methodName = method.getName();
        Class[] methodParametersTypes = method.getParameterTypes();
        Class declaringClass = method.getDeclaringClass();
        
        /* HACK Here we should ensure that the declaring class is the interface
         *      which the proxy was created for (and not just testing whether
         *      the method is declared in an (arbitrary) interface). But this
         *      resulted in a very strange behaviour in EJB containers, which
         *      weren't able anymore to reactivate passivated beans (i.e. they
         *      threw a ClassNotFoundException).
         */
        if (declaringClass.isInterface()) {
            Class[] methodParametersTypesWithoutContext 
                = new Class[methodParametersTypes.length - 1];
            for (int i = 0; 
                i < methodParametersTypesWithoutContext.length; i++) {
                methodParametersTypesWithoutContext[i] 
                    = methodParametersTypes[i];
            }
    
            m = m_serviceInterface.getMethod(methodName,
                    methodParametersTypesWithoutContext);
    
            newArgs = new Object[args.length - 1];
            for (int i = 0; i < newArgs.length; i++) {
                newArgs[i] = args[i];
            }
            Map map = (Map) args[args.length - 1];
            if (m_implicitContextPassingRegistry != null) {
                m_implicitContextPassingRegistry.pushAssembledImplicitContext(
                    map);
            }
        } else {
            m = declaringClass.getMethod(methodName, methodParametersTypes);
            newArgs = args;
        }

        try {
            return m.invoke(m_service, newArgs);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}