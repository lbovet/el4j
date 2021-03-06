/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.remoting.protocol.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.EJBObject;

import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;
import ch.elca.el4j.services.remoting.ClientContextInvocationHandler;

/**
 * This class helps adding the context method invocations on EJB session beans.
 * It is used on client side and allows to pass <code>remove</code> invocations
 * and unwraps runtime exceptions.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public class EjbClientContextInvocationHandler extends
        ClientContextInvocationHandler {

    /** The EJB remove method's name. */
    public static final String EJB_REMOVE = "remove";
    
    /**
     * Creates a new instance.
     * 
     * @param innerRemoteObject
     *      The object where method calls without the context are delegated to.
     * @param serviceInterfaceWithContext
     *      The enriched service interface.
     * @param implicitContextPassingRegistry
     *      The implicit context passing registry that adds and removes the
     *      context map.
     */
    public EjbClientContextInvocationHandler(Object innerRemoteObject,
            Class serviceInterfaceWithContext,
            ImplicitContextPassingRegistry implicitContextPassingRegistry) {
        
        super(innerRemoteObject, serviceInterfaceWithContext,
                implicitContextPassingRegistry);
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        
        Object result;
        if (EJB_REMOVE.equals(method.getName())
            && method.getDeclaringClass().equals(EJBObject.class)
            && (args == null || args.length == 0)) {
            result = method.invoke(getInnerRemoteObject(), args);
        } else {
            try {
                result = super.invoke(proxy, method, args);
            } catch (InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
        return result;
    }
}
