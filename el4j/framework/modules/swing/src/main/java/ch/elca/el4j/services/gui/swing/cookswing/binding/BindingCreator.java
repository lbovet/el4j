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

import javax.swing.JComponent;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.w3c.dom.Element;

import cookxml.common.helper.NoAddHelper;
import cookxml.core.DecodeEngine;
import cookxml.core.exception.CreatorException;

/**
 * The cookSwing creator for general purpose &lt;binding&gt;s.
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
public class BindingCreator extends AbstractBindingCreator {
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Object create(String parentNS, String parentTag, Element elm,
		Object parentObj, DecodeEngine decodeEngine) throws CreatorException {

		// read attributes
		UpdateStrategy updateStrategy = getUpdateStrategy(elm);
		Object src = getSource(decodeEngine, elm);
		String prop = elm.getAttribute(PROPERTY);
		JComponent component = (JComponent) parentObj;
		boolean validate = getValidate(elm);
		
		// create binding
		AutoBinding binding = null;
		Object form = decodeEngine.getVariable("this");
		if (Bindable.class.isAssignableFrom(form.getClass())) {
			Bindable bindableForm = (Bindable) form;
			
			binding = bindableForm.getBinder().addManualBinding(
				updateStrategy, src, prop, component, validate);
		}
		return new NoAddValueHolder<AutoBinding>(binding);
	}
}
