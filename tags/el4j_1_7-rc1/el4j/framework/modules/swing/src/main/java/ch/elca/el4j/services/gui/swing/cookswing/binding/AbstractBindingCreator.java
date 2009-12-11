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
package ch.elca.el4j.services.gui.swing.cookswing.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Property;

import org.w3c.dom.Element;

import com.silvermindsoftware.hitch.binding.PropertyUtil;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CookXmlException;
import cookxml.core.interfaces.Creator;

/**
 * This class provides basic functionality for cookXML binding creators.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public abstract class AbstractBindingCreator implements Creator {
	// some common attributes
	private static final String SOURCE = "src";
	protected static final String PROPERTY = "property";
	protected static final String VALIDATION = "validation";
	protected static final String UPDATE_STRATEGY = "updateStrategy";
	
	/**
	 * The logger.
	 */
	private static final Logger s_logger = LoggerFactory
		.getLogger(AbstractBindingCreator.class);
	
	/**
	 * @param elm    the current XML element
	 * @return       the updateStrategy attribute
	 */
	protected UpdateStrategy getUpdateStrategy(Element elm) {
		UpdateStrategy updateStrategy = UpdateStrategy.READ;
		String updateStrategyString = elm.getAttribute(UPDATE_STRATEGY);
		if (updateStrategyString.equals("read once")) {
			updateStrategy = UpdateStrategy.READ_ONCE;
		} else if (updateStrategyString.equals("read write")) {
			updateStrategy = UpdateStrategy.READ_WRITE;
		}
		return updateStrategy;
	}
	
	/**
	 * @param decodeEngine    the cookXML decodeEngine
	 * @param elm             the current XML element
	 * @return                the source object to bind
	 */
	@SuppressWarnings("unchecked")
	protected Object getSource(DecodeEngine decodeEngine, Element elm) {
		Object obj = null;
		String src = elm.getAttribute(SOURCE);
		if (!src.equals("")) {
			if (src.contains(".")) {
				Property prop = PropertyUtil.create(
					src.substring(src.indexOf(".") + 1));
				obj = prop.getValue(decodeEngine.getVariable(
					src.substring(0, src.indexOf("."))));
			} else {
				obj = decodeEngine.getVariable(elm.getAttribute(SOURCE));
			}
		} else {
			s_logger.error("Error processing XML element " + elm.getNodeName()
				+ ". Mandatory attribute '" + SOURCE + "' not found.");
		}
		return obj;
	}
	
	/**
	 * @param elm    the current XML element
	 * @return       <code>true</code> if values should be validated
	 */
	protected boolean getValidate(Element elm) {
		boolean validate = Boolean.parseBoolean(elm.getAttribute(VALIDATION));
		if (!elm.getAttribute(VALIDATION).equals("")) {
			validate = true;
		}
		return validate;
	}
	
	/**
	 * Adds binding to the class associated with this XML GUI description.
	 * @param decodeEngine    the cookXML decodeEngine
	 * @param binding         the beans binding to add
	 */
	@SuppressWarnings("unchecked")
	protected void addBinding(DecodeEngine decodeEngine, AutoBinding binding) {
		Object form = decodeEngine.getVariable("this");
		if (Bindable.class.isAssignableFrom(form.getClass())) {
			Bindable bindableForm = (Bindable) form;
			
			bindableForm.getBinder().addManualBinding(binding);
		} else {
			s_logger.warn("No class to bind found. " + form
				+ " does not implement interface Bindable");
		}
	}
	
	/** {@inheritDoc} */
	public Object editFinished(String parentNS, String parentTag, Element elm,
		Object parentObj, Object obj, DecodeEngine decodeEngine)
		throws CookXmlException {

		return obj;
	}
}
