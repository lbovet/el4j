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
package ch.elca.el4j.demos.gui.validation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.InvalidValue;

import com.silvermindsoftware.hitch.validation.HibernateValidationCapability;
import com.silvermindsoftware.hitch.validation.response.DefaultValidationResponder;

/**
 * This validation responder sets the validation message on a text component.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class CustomValidationResponder extends DefaultValidationResponder {
    /**
     * The logger.
     */
    private static final Log s_logger = LogFactory.getLog(
        DefaultValidationResponder.class);
    
    /**
     * The text component for the validation messages.
     */
    private JComponent m_messageComponent;
    
    /**
     * The current validation message for each component.
     */
    private Map<JComponent, String> m_currentMessages;
    
    /**
     * @param messageComponent    the text component for the validation messages
     */
    public CustomValidationResponder(JComponent messageComponent) {
        m_messageComponent = messageComponent;
        m_currentMessages = new HashMap<JComponent, String>();
    }
    
    /** {@inheritDoc} */
    @Override
    public void setValid(Object object, JComponent component) {
        super.setValid(object, component);
        
        m_currentMessages.put(component, null);
        updateMessageText();
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void setInvalid(Object object, JComponent component,
        String message) {
        
        super.setInvalid(object, component, message);
        
        if (!(message != null && message.length() > 0) && object != null
            && object instanceof HibernateValidationCapability) {
            
            // validation using hibernate is possible
            HibernateValidationCapability source
                = (HibernateValidationCapability) object;
            
            InvalidValue[] validationMessages = source.getClassValidator()
                 .getInvalidValues(object);
            
            // construct the message
            StringBuilder sb = new StringBuilder();
            for (InvalidValue invalidValue : validationMessages) {
                sb.append(invalidValue.toString() + "<br>");
            }
            if (sb.length() > "<br>".length()) {
                // get rid of last "<br>"
                sb.setLength(sb.length() - "<br>".length());
            }
            m_currentMessages.put(component, sb.toString());
        } else {
            m_currentMessages.put(component, message);
        }
        
        updateMessageText();
    }
    
    /**
     * Update the text component for the validation messages.
     */
    protected void updateMessageText() {
        Method setText;
        try {
            setText = m_messageComponent.getClass().getMethod("setText",
                new Class[] {String.class});
            
            // collect all validation error messages
            StringBuilder sb = new StringBuilder("<html>");
            for (String msg : m_currentMessages.values()) {
                if (msg != null) {
                    sb.append(msg).append("<br>");
                }
            }
            if (sb.length() > "<html><br>".length()) {
                // get rid of last "<br>"
                sb.setLength(sb.length() - "<br>".length());
            }
            sb.append("</html>");
            
            // set the message to the text component
            setText.invoke(m_messageComponent, sb.toString());
        } catch (Exception e) {
            s_logger.warn(m_messageComponent.toString()
                + " has no setText method.");
        }
    }
}
