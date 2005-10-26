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

package ch.elca.el4j.services.remoting;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;

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
public class ClientContextInvocationHandler implements InvocationHandler {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(ClientContextInvocationHandler.class);

    /**
     * Is the remote object where the invocations has to be delegated.
     */
    private final Object m_innerRemoteObject;

    /**
     * Is the modificated service interface to be able to send context
     * information with.
     */
    private final Class m_serviceInterfaceWithContext;

    /**
     * Describes how the service interface has to be modificated.
     */
    private final ContextEnrichmentDecorator m_contextInterfaceDecorator;

    /**
     * Is the registry for the implicit context passing.
     */
    private final ImplicitContextPassingRegistry 
        m_implicitContextPassingRegistry;
    
    /**
     * Constructor.
     * 
     * @param innerRemoteObject 
     *                  Is the object to which invokations must be delegated.
     * @param serviceInterfaceWithContext
     *                  Is the enriched service interface.
     * @param implicitContextPassingRegistry
     *                  Is the implicit context passing registry.
     */
    public ClientContextInvocationHandler(Object innerRemoteObject,
            Class serviceInterfaceWithContext,
            ImplicitContextPassingRegistry implicitContextPassingRegistry) {
        m_innerRemoteObject = innerRemoteObject;
        m_serviceInterfaceWithContext = serviceInterfaceWithContext;
        m_contextInterfaceDecorator = new ContextEnrichmentDecorator();
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
            MethodDescriptor md = new MethodDescriptor();
            md.setParameterTypes(methodParametersTypes);
            m_contextInterfaceDecorator.changedMethodSignature(md);
            Class[] methodParametersTypesWithContext = md.getParameterTypes();
            
            m = m_serviceInterfaceWithContext.getMethod(methodName,
                    methodParametersTypesWithContext);
    
            int argsLength = (args == null) ? 0 : args.length;
            newArgs = new Object[argsLength + 1];
            for (int i = 0; i < argsLength; i++) {
                newArgs[i] = args[i];
            }
            Map map = null;
            if (m_implicitContextPassingRegistry != null) {
                map = m_implicitContextPassingRegistry
                        .getAssembledImplicitContext();
            }
            newArgs[newArgs.length - 1] = map;
        } else {
            m = declaringClass.getMethod(methodName, methodParametersTypes);
            newArgs = args;
        }

        try {
            return m.invoke(m_innerRemoteObject, newArgs);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * @return Returns the inner remote object where method calls are delegated
     * to.
     */
    protected Object getInnerRemoteObject() {
        return m_innerRemoteObject;
    }
}