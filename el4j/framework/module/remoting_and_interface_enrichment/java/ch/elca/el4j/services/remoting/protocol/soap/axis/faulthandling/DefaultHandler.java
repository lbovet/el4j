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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.message.PrefixedQName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.remoting.protocol.soap.SoapHelper;

/**
 * This class is the default soap exception handler.
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
public class DefaultHandler implements SoapExceptionHandler {
    /**
     * Namespace for this class.
     */
    protected static final String DEFAULT_HANDLER_NAMESPACE 
        = "http://" + DefaultHandler.class.getName();
    
    /**
     * Is the qualified name for the "server side stack trace" detail element.
     */
    protected static final QName STACK_TRACE_QNAME 
        = new QName(DEFAULT_HANDLER_NAMESPACE, "serverSideStackTrace");
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(DefaultHandler.class);
    
    /**
     * Flag to indicate if the server side stack trace should be available
     * at client side.
     */
    protected boolean m_sendServerSideStackTraceActive = true;
    
    /**
     * {@inheritDoc}
     */
    public SOAPFaultException translateToSoapFaultException(
        Exception businessException) {
        
        QName faultCode = getFaultCode(businessException);
        String faultString = getFaultString(businessException);
        String faultActor = getFaultActor(businessException);
        Detail faultDetail = new org.apache.axis.message.Detail(); 
        fillFaultDetail(faultDetail, businessException);
        
        SOAPFaultException sfe = new SOAPFaultException(faultCode, 
            faultString, faultActor, faultDetail);
        
        if (isSendServerSideStackTraceActive()) {
            addServerSideStackTrace(faultDetail, businessException);
        }
        
        return sfe;
    }
    
    /**
     * Method to add the server side stack trace.
     * 
     * @param faultDetail
     *            Is the detail part of the soap fault exception where the stack
     *            trace has to be added.
     * @param businessException
     *            Is the business exception where the stack trace is comming
     *            from.
     */
    protected void addServerSideStackTrace(
        Detail faultDetail, Exception businessException) {
        StringWriter sw = new StringWriter();
        businessException.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        if (StringUtils.hasText(stackTrace)) {
            try {
                DetailEntry stackTraceElement 
                    = faultDetail.addDetailEntry(
                        new PrefixedQName(STACK_TRACE_QNAME));
                stackTraceElement.setValue(stackTrace);
            } catch (SOAPException e) {
                CoreNotificationHelper.notifyMisconfiguration(
                    "Stack trace detail entry could not be created.");
            }
        } else {
            CoreNotificationHelper.notifyMisconfiguration(
                "Stack trace is empty! Can not add an empty stack trace.");
        }
    }

    /**
     * Extracts the fault code out of the given business exception.
     * 
     * @param businessException
     *            Is the given business exception.
     * @return Returns the extracted fault code.
     */
    protected QName getFaultCode(Exception businessException) {
        String name = businessException.getClass().getName();
        return new QName(DEFAULT_HANDLER_NAMESPACE, name);
    }
    
    /**
     * Extracts the fault string out of the given business exception.
     * 
     * @param businessException
     *            Is the given business exception.
     * @return Returns the extracted fault string.
     */
    protected String getFaultString(Exception businessException) {
        return businessException.getMessage();
    }
    
    /**
     * Extracts the fault actor out of the given business exception.
     * 
     * @param businessException
     *            Is the given business exception.
     * @return Returns the extracted fault actor.
     */
    protected String getFaultActor(Exception businessException) {
        return null;
    }
    
    /**
     * Fill the fault detail with information from the given business exception.
     * 
     * @param faultDetail
     *            Is the object of the soap fault exception which has to be
     *            filled.
     * @param businessException
     *            Is the given business exception.
     */
    protected void fillFaultDetail(
        Detail faultDetail, Exception businessException) {
        // Per default add nothing.
    }
    

    /**
     * {@inheritDoc}
     */
    public Exception translateToBusinessException(AxisFault axisFault) {
        Class exceptionClass = getExceptionClass(axisFault);
        if (exceptionClass == null) {
            return null;
        }
        Element[] faultDetailElements = axisFault.getFaultDetails();
        Exception e 
            = getExceptionInstance(exceptionClass, faultDetailElements);
        if (e != null && isSendServerSideStackTraceActive()) {
            e = makeServerSideStackTraceAvailable(e, faultDetailElements);
        }
        return e;
    }
    
    /**
     * Method to extract the exception class out of the given axis fault. 
     * 
     * @param axisFault Is the given axis fault.
     * @return Returns the found exception class.
     */
    protected Class getExceptionClass(AxisFault axisFault) {
        QName faultCode = axisFault.getFaultCode();
        String namespace = faultCode.getNamespaceURI();
        String className = faultCode.getLocalPart();
        if (DEFAULT_HANDLER_NAMESPACE.equals(namespace) 
            && className != null && className.indexOf(' ') == -1) {
            try {
                Class exceptionClass  = Class.forName(className);
                return exceptionClass;
            } catch (ClassNotFoundException e) {
                s_logger.info("The exception class with name '" 
                    + className + "' could not be loaded.", e);
            }
        }
        return null;
    }
    
    /**
     * Method to create an instance of the given exception class and use the
     * fault detail elements to fill it.
     * 
     * @param exceptionClass
     *            Is the given exception class.
     * @param faultDetailElements
     *            Are the detail elements. They can be used to fill a
     *            constructor or setter methods of the exception.
     * @return Returns the created exception.
     */
    protected Exception getExceptionInstance(Class exceptionClass, 
        Element[] faultDetailElements) {
        assert exceptionClass != null;
        try {
            return (Exception) exceptionClass.newInstance();
        } catch (Exception e) {
            String message = "Exception class '" + exceptionClass.getName() 
                + "' could not be instantiated. Perhaps it has no " 
                + "default constructor.";
            s_logger.error(message, e);
            throw new BaseRTException(message, e);
        }
    }
    
    /**
     * Makes the server side stack trace available. By default the 
     * implementation will set the <code>ThreadLocal</code> of the 
     * <code>SoapHelper</code> to provide the last server side stack trace.
     * 
     * @param e
     *            Is the exception to which the stack trace belongs to.
     * @param faultDetailElements
     *            Are the detail elements. There should be an element which
     *            contains the server side stack trace.
     * @return Returns the exception.
     */
    protected Exception makeServerSideStackTraceAvailable(Exception e, 
        Element[] faultDetailElements) {
        String stackTrace = null;
        for (int i = 0; stackTrace == null 
            && i < faultDetailElements.length; i++) {
            Element element = faultDetailElements[i];
            if (STACK_TRACE_QNAME.getNamespaceURI()
                .equals(element.getNamespaceURI()) 
                && STACK_TRACE_QNAME.getLocalPart()
                    .equals(element.getLocalName())) {
                Node node = element.getFirstChild();
                stackTrace = node.getNodeValue();
            }
        }
        if (stackTrace != null && stackTrace.trim().length() > 0) {
            SoapHelper.setLastServerSideStackTrace(stackTrace);
            return e;
        } else {
            SoapHelper.setLastServerSideStackTrace(null);
            String errorMessage = "The server side stack trace could not "
                + "be found. Please check the server configuration.";
            s_logger.error(errorMessage);
            throw new BaseRTException(errorMessage);
        }
    }
    
    /**
     * @return Returns the sendServerSideStackTrace.
     */
    public boolean isSendServerSideStackTraceActive() {
        return m_sendServerSideStackTraceActive;
    }

    /**
     * @param sendServerSideStackTraceActive
     *            The sendServerSideStackTrace to set.
     */
    public void setSendServerSideStackTraceActive(
        boolean sendServerSideStackTraceActive) {
        this.m_sendServerSideStackTraceActive = sendServerSideStackTraceActive;
    }
}
