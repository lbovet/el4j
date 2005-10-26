/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.springframework.remoting.RemoteAccessException;

/**
 * Invocation handler to translate <code>AxisFault</code>s to their business
 * exceptions.
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
public class ClientSoapInvocationHandler implements InvocationHandler {
    /**
     * Is the implementation of the service which has to be called.
     */
    private final Object m_service;

    /**
     * The soap exception manager which handles exceptions.
     */
    private final SoapExceptionManager m_exceptionManager;
    
    /**
     * Flag to indicate if exceptions should be translated.
     */
    private final boolean m_exceptionTranslationEnabled;

    /**
     * Map which contains all business method exceptions.
     * This is only used if exception translation is turned off.
     */
    private final Map m_businessMethodExceptions = new HashMap();
    
    /**
     * Constructor.
     * 
     * @param service
     *            Is the real service which does the work.
     * @param businessInterface
     *            Is the interface which was actually written by user.
     * @param exceptionManager
     *            Is the exception manager to translate exceptions.
     * @param exceptionTranslationEnabled
     *            Is the flag to indicate if exception translation should be
     *            done or not.
     */
    public ClientSoapInvocationHandler(Object service,
        Class businessInterface,
        SoapExceptionManager exceptionManager, 
        boolean exceptionTranslationEnabled) {
        m_service = service;
        m_exceptionManager = exceptionManager;
        m_exceptionTranslationEnabled = exceptionTranslationEnabled;
        
        if (!exceptionTranslationEnabled) {
            Method[] methods = businessInterface.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                Class[] thrownExceptions = method.getExceptionTypes();
                if (thrownExceptions.length > 0) {
                    String methodName = method.getName();
                    m_businessMethodExceptions.put(
                        methodName, thrownExceptions);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        try {
            return method.invoke(m_service, args);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (m_exceptionTranslationEnabled) {
                findAndTranslateAxisFault(t);
            } else {
                if (t != null 
                    && t.getCause() != null
                    && t instanceof RemoteAccessException 
                    && t.getCause() instanceof RemoteException) {
                    RemoteException re = (RemoteException) t.getCause();
                    Class throwedClass = re.getClass();
                    if (isThrowedClassDeclaredInInterface(
                        throwedClass, method)) {
                        t = re;
                    }
                }
            }
            throw t;
        }
    }
    
    /**
     * Method to check if the given exception class is declared on business
     * interface. This method can only be used if the exception translation is
     * turned off.
     * 
     * @param throwedClass
     *            Is the thrown exception class.
     * @param method
     *            Is the method where the exception is comming from.
     * @return Returns true if the given exception class is declared on the
     *         business interface.
     */
    private boolean isThrowedClassDeclaredInInterface(
        Class throwedClass, Method method) {
        String methodName = method.getName();
        Class[] exceptionClasses 
            = (Class[]) m_businessMethodExceptions.get(methodName);
        if (exceptionClasses != null) {
            for (int i = 0; i < exceptionClasses.length; i++) {
                Class exceptionClass = exceptionClasses[i];
                if (exceptionClass.isAssignableFrom(throwedClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method finds an encapsulated <code>AxisFault</code>, translates
     * it if possible and throws the translated exception. This method will
     * return normally if no <code>AxisFault</code> could be found.
     * 
     * @param t
     *            Is the throwable to analyze.
     * @throws Throwable
     *             If an <code>AxisFault</code> could be found.
     */
    private void findAndTranslateAxisFault(Throwable t) throws Throwable {
        if (t == null || t.getCause() == t) {
            return;
        }
        if (t instanceof AxisFault) {
            AxisFault af = (AxisFault) t;
            throw m_exceptionManager.translateToBusinessException(af);
        } else {
            findAndTranslateAxisFault(t.getCause());
        }
    }
}