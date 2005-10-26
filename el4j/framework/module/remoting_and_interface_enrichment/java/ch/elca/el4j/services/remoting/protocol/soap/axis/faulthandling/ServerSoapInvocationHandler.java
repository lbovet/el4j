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

package ch.elca.el4j.services.remoting.protocol.soap.axis.faulthandling;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invocation handler to connect an rmi interface with a non rmi conform 
 * service. Used for soap services. Further each business exception will
 * be translated into a <code>SOAPFaultException</code>.
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
public class ServerSoapInvocationHandler implements InvocationHandler {
    /**
     * Is the implementation of the service which has to be called.
     */
    private final Object m_service;

    /**
     * Is the interface which is implemented by the service.
     */
    private final Class m_serviceInterface;

    /**
     * The soap exception manager which handles exceptions.
     */
    private final SoapExceptionManager m_exceptionManager;

    /**
     * Flag to indicate if exceptions should be translated.
     */
    private final boolean m_exceptionTranslationEnabled;

    /**
     * Constructor.
     * 
     * @param service
     *            Is the real service which does the work.
     * @param serviceInterface
     *            Is the interface which the service does implement.
     * @param exceptionManager
     *            Is the exception manager to translate exceptions.
     * @param exceptionTranslationEnabled
     *            Is the flag to indicate if exception translation should be
     *            done or not.
     */
    public ServerSoapInvocationHandler(Object service, Class serviceInterface,
        SoapExceptionManager exceptionManager, 
        boolean exceptionTranslationEnabled) {
        m_service = service;
        m_serviceInterface = serviceInterface;
        m_exceptionManager = exceptionManager;
        m_exceptionTranslationEnabled = exceptionTranslationEnabled;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        Method m = m_serviceInterface.getMethod(method.getName(), 
            method.getParameterTypes());
        
        try {
            return m.invoke(m_service, args);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (m_exceptionTranslationEnabled && t instanceof Exception) {
                Exception e = (Exception) t;
                throw m_exceptionManager.translateToSoapFaultException(e);
            } else {
                throw t;
            }
        }
    }
}