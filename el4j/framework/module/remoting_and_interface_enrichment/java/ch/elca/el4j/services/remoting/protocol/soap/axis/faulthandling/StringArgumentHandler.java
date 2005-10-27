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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;

import org.apache.axis.message.PrefixedQName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.elca.el4j.core.exceptions.BaseException;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This handler is usable for exceptions with constructor which has one to many 
 * string constructor argument(s).
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
public class StringArgumentHandler extends DefaultHandler {
    /**
     * Namespace for this class.
     */
    protected static final String STRING_ARGUMENT_HANDLER_NAMESPACE 
        = "http://" + StringArgumentHandler.class.getName();
    
    /**
     * This is the base name of the string arguments.
     */
    private static final String STRING_ARGUMENT_NAME_BASE = "stringArgument";

    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(StringArgumentHandler.class);

    /**
     * {@inheritDoc}
     */
    protected void fillFaultDetail(Detail faultDetail,
        Exception businessException) {
        Object[] arguments = null;
        if (businessException instanceof BaseException) {
            BaseException e = (BaseException) businessException;
            arguments = e.getFormatParameters();
        } else if (businessException instanceof BaseRTException) {
            BaseRTException e = (BaseRTException) businessException;
            arguments = e.getFormatParameters();
        } else {
            String message = "This handler can only be used for "
                + "exceptions of type '" + BaseException.class.getName() 
                + "' and '" + BaseRTException.class.getName() + "'.";
            CoreNotificationHelper.notifyMisconfiguration(message);
        }
        
        if (arguments == null || arguments.length == 0) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "There are no arguments.");
        }
        
        for (int i = 0; i < arguments.length; i++) {
            String stringValue 
                = (arguments[i] == null ? "" : arguments[i].toString());
            try {
                PrefixedQName argumentQName 
                    = new PrefixedQName(STRING_ARGUMENT_HANDLER_NAMESPACE, 
                        STRING_ARGUMENT_NAME_BASE + i, null);
                DetailEntry entry = faultDetail.addDetailEntry(argumentQName);
                entry.setValue(stringValue);
            } catch (Exception e) {
                String message = "Could not create a fault detail entry for " 
                    + "string argument '" + stringValue + "' on position '" 
                    + i + "'.";
                CoreNotificationHelper.notifyMisconfiguration(message, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Exception getExceptionInstance(Class exceptionClass,
        Element[] faultDetailElements) {
        assert exceptionClass != null;
        
        int numberOfStringArguments = 0;
        List stringArguments = new ArrayList();
        for (int i = 0; i < faultDetailElements.length; i++) {
            Element element = faultDetailElements[i];
            String argumentName 
                = STRING_ARGUMENT_NAME_BASE + numberOfStringArguments;
            if (STRING_ARGUMENT_HANDLER_NAMESPACE
                    .equals(element.getNamespaceURI()) 
                        && argumentName.equals(element.getLocalName())) {
                Node node = element.getFirstChild();
                String argumentValue = node.getNodeValue();
                stringArguments.add(argumentValue);
                numberOfStringArguments++;
            }
        }
        
        if (numberOfStringArguments == 0) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "No string arguments could be found.");
        }
        
        try {
            Class[] constructorArgumentTypes 
                = new Class[numberOfStringArguments];
            String[] constructorArgumentValues 
                = new String[numberOfStringArguments];
            for (int i = 0; i < numberOfStringArguments; i++) {
                constructorArgumentTypes[i] = String.class;
                constructorArgumentValues[i] 
                    = (String) stringArguments.get(i);
            }
            
            Constructor c 
                = exceptionClass.getConstructor(constructorArgumentTypes);
            return (Exception) c.newInstance(constructorArgumentValues);
        } catch (Exception e) {
            String message = "Exception class '" + exceptionClass.getName() 
                + "' could not be instantiated. It was expected to have a "
                + "constructor with " + numberOfStringArguments 
                + " string argument(s).";
            s_logger.error(message, e);
            throw new BaseRTException(message, e);
        }
    }    
}
