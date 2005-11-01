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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.soap.SOAPFaultException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class manages the soap exception/business exception handling.
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
public class SoapExceptionManager implements InitializingBean {
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(SoapExceptionManager.class);

    /**
     * All registered soap exception handlers.
     */
    protected Map m_soapExceptionHandlers = null;
    
    /**
     * The default soap exception handler if no specific handler 
     * could be found. 
     */
    protected SoapExceptionHandler m_defaultHandler = null;
    
    /**
     * Allowed translations. Contains all exception classes which are allowed to
     * be translated. This list contains only strings which are fully qualified 
     * names of exception classes.
     */
    protected Set m_allowedTranslations = null;

    /**
     * Is the same as field <code>m_allowedTranslations</code> but with 
     * <code>java.lang.Class</code> instead of strings.
     */
    private Set m_allowedTranslationClasses = null;

    /**
     * Allowed translations. Contains exception classes which are or are not
     * allowed to be translated. This map contains <code>Boolean.TRUE</code> and
     * <code>Boolean.FALSE</code> as value and the <code>java.lang.Class</code>
     * as key.
     */
    private Map m_exceptionTranslationsCache = null;
    
    
    /**
     * Default constructor. 
     */
    public SoapExceptionManager() {
        m_allowedTranslations = new HashSet();
        m_allowedTranslations.add(BaseException.class.getName());
        m_allowedTranslations.add(BaseRTException.class.getName());
        m_defaultHandler = new DefaultHandler();
        try {
            afterPropertiesSet();
        } catch (Exception e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "Default constructor could not be "
                    + "initialized properly. This should never occur! Please "
                    + "check the code.");
        }
    }
    
    /**
     * Method to translate a business exception to a soap fault exception. If
     * translation fails the given business exception will be returned.
     * 
     * @param businessException
     *            Is the business exception which should be translated.
     * @return Returns the translated exception if success, otherwise the given
     *         business exception.
     */
    public Exception translateToSoapFaultException(
        Exception businessException) {
        assert businessException != null;
        if (isTranslationAllowed(businessException)) {
            String key = businessException.getClass().getName();
            SoapExceptionHandler seh = getHandler(key);
            SOAPFaultException sfe = null;
            if (seh != null) {
                sfe = seh.translateToSoapFaultException(businessException);
            }
            if (sfe != null) {
                s_logger.info("Exception '" + key + "' could be successfully " 
                    + "converted to a 'SOAPFaultException'.");
                return sfe;
            } else {
                s_logger.info("Exception '" + key + "' could not be converted "
                    + "to a 'SOAPFaultException'.");
            }
        }
        return businessException;
    }

    /**
     * Method to check if it is allowed to translate the given exception. 
     * 
     * @param e Is the exception to check.
     * @return Return true if it is allowed to translate the given exception.
     */
    private boolean isTranslationAllowed(Exception e) {
        Class exceptionClass = e.getClass();
        if (m_exceptionTranslationsCache.containsKey(exceptionClass)) {
            Boolean b 
                = (Boolean) m_exceptionTranslationsCache.get(exceptionClass);
            return b.booleanValue();
        } else {
            boolean translationAllowed = false;
            Iterator it = m_allowedTranslationClasses.iterator();
            while (!translationAllowed && it.hasNext()) {
                Class c = (Class) it.next();
                translationAllowed = c.isAssignableFrom(exceptionClass);
            }
            m_exceptionTranslationsCache.put(exceptionClass, 
                Boolean.valueOf(translationAllowed));
            return translationAllowed;
        }
    }
    

    /**
     * Method to translate a given axis fault into the real business exception.
     * If translation fails the given axis fault will be returned.
     * 
     * @param axisFault
     *            Is the axis fault which should be translated.
     * @return Returns the extracted business exception if success, otherwise
     *         the given axis fault.
     */
    public Exception translateToBusinessException(AxisFault axisFault) {
        String key = axisFault.getFaultCode().getLocalPart();
        SoapExceptionHandler seh = getHandler(key);
        Exception e = null;
        if (seh != null) {
            e = seh.translateToBusinessException(axisFault);
        }
        if (e != null) {
            s_logger.info("'AxisFault' could be successfully " 
                + "converted to a '" + e.getClass().getName() + "'.");
            return e;
        } else {
            s_logger.info("'AxisFault' could not be converted to " 
                + "a business exception.");
            return axisFault;
        }
    }
    
    /**
     * Method to get the best matching soap exception handler. If no specific
     * handler could be found the default handler will be returned.
     * 
     * @param key
     *            Is the key to lookup for a usable soap exception handler.
     * @return Returns the best matching exception handler.
     */
    protected SoapExceptionHandler getHandler(String key) {
        SoapExceptionHandler seh 
            = (SoapExceptionHandler) m_soapExceptionHandlers.get(key);
        return seh == null ? m_defaultHandler : seh;
    }

    /**
     * @return Returns the defaultHandler.
     */
    public SoapExceptionHandler getDefaultHandler() {
        return m_defaultHandler;
    }

    /**
     * @param defaultHandler
     *            The defaultHandler to set.
     */
    public void setDefaultHandler(SoapExceptionHandler defaultHandler) {
        m_defaultHandler = defaultHandler;
    }

    /**
     * @return Returns the soapExceptionHandlers.
     */
    public Map getSoapExceptionHandlers() {
        return m_soapExceptionHandlers;
    }

    /**
     * @param soapExceptionHandlers
     *            The soapExceptionHandlers to set.
     */
    public void setSoapExceptionHandlers(Map soapExceptionHandlers) {
        m_soapExceptionHandlers = soapExceptionHandlers;
    }

    /**
     * @return Returns the allowedTranslations.
     */
    public Set getAllowedTranslations() {
        return m_allowedTranslations;
    }

    /**
     * @param allowedTranslations
     *            The allowedTranslations to set.
     */
    public void setAllowedTranslations(Set allowedTranslations) {
        m_allowedTranslations = allowedTranslations;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_defaultHandler == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                "defaultHandler", this);
        }
        
        if (m_soapExceptionHandlers == null) {
            m_soapExceptionHandlers = new HashMap();
        }
        
        m_allowedTranslationClasses = new HashSet();
        m_exceptionTranslationsCache = new HashMap();
        if (m_allowedTranslations != null || m_allowedTranslations.size() > 0) {
            Iterator it = m_allowedTranslations.iterator();
            while (it.hasNext()) {
                String s = (String) it.next();
                Class c = Class.forName(s);
                m_allowedTranslationClasses.add(c);
                m_exceptionTranslationsCache.put(c, Boolean.TRUE);
            }
        } else {
            m_allowedTranslations = new HashSet();
        }
    }
}
