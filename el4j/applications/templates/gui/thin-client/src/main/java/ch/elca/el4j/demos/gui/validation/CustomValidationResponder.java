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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.validator.InvalidValue;

import com.silvermindsoftware.hitch.validation.HibernateValidationCapability;
import com.silvermindsoftware.hitch.validation.response.DefaultValidationResponder;

/**
 * This validation responder sets the validation message on a text component.
 *
 * @see BindingDemoForm
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class CustomValidationResponder extends DefaultValidationResponder {
	/**
	 * The logger.
	 */
	private static final Logger s_logger = LoggerFactory.getLogger(
		DefaultValidationResponder.class);
	
	/**
	 * The text component for the validation messages.
	 */
	private JComponent messageComponent;
	
	/**
	 * The current validation message for each component.
	 */
	private Map<JComponent, String> currentMessages;
	
	/**
	 * @param messageComponent    the text component for the validation messages
	 */
	public CustomValidationResponder(JComponent messageComponent) {
		this.messageComponent = messageComponent;
		currentMessages = new HashMap<JComponent, String>();
	}
	
	/** {@inheritDoc} */
	@Override
	public void setValid(Object object, JComponent component) {
		super.setValid(object, component);
		
		currentMessages.put(component, null);
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
			currentMessages.put(component, sb.toString());
		} else {
			currentMessages.put(component, message);
		}
		
		updateMessageText();
	}
	
	/**
	 * Update the text component for the validation messages.
	 */
	protected void updateMessageText() {
		Method setText = null;
		try {
			setText = messageComponent.getClass().getMethod("setText", new Class[] {String.class});
		} catch (NoSuchMethodException e) {
			s_logger.warn(messageComponent.toString() + " has no setText method.");
		}
		// collect all validation error messages
		StringBuilder sb = new StringBuilder("<html>");
		for (String msg : currentMessages.values()) {
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
		try {
			if (setText != null) {
				setText.invoke(messageComponent, sb.toString());
			}
		} catch (IllegalArgumentException e) {
			s_logger.warn(messageComponent.toString() + " was called with the wrong number of arguments.");
		} catch (IllegalAccessException e) {
			s_logger.warn("Invocation of method setText on " + messageComponent.toString()
				+ "caused a IllegalAccessException");
		} catch (InvocationTargetException e) {
			s_logger.warn("Invocation of method setText on " + messageComponent.toString()
				+ "caused a InvocationTargetException");
		}

	}
}
