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

import java.util.List;

import javax.swing.JComboBox;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

import com.silvermindsoftware.hitch.validation.response.ComboBoxRenderer;
import com.silvermindsoftware.hitch.validation.response.DefaultValidatingComboBoxRenderer;

import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.util.config.GenericConfig;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CreatorException;

/**
 * The cookSwing creator for general purpose &lt;comboboxbinding&gt;s.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class ComboBoxBindingCreator extends AbstractBindingCreator {
	// <comboboxbinding> specific attributes
	protected static final String RENDERER = "rendererBean";

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Object create(String parentNS, String parentTag, Element elm,
		Object parentObj, DecodeEngine decodeEngine) throws CreatorException {
		
		// read attributes
		UpdateStrategy updateStrategy = getUpdateStrategy(elm);
		List listSource = (List) getSource(decodeEngine, elm);
		if (listSource == null) {
			return null;
		}
		JComboBox comboBox = (JComboBox) parentObj;
		
		// renderer and validation
		String renderer = elm.getAttribute(RENDERER);
		ComboBoxRenderer cr = null;
		if (renderer.equals("")) {
			GenericConfig config = GUIApplication.getInstance().getConfig();
			DefaultValidatingComboBoxRenderer validatingRenderer
				= (DefaultValidatingComboBoxRenderer)
				config.get("comboBoxRenderer");
			validatingRenderer.setValidate(getValidate(elm));
			cr = validatingRenderer;
		} else {
			ApplicationContext ctx
				= GUIApplication.getInstance().getSpringContext();
			cr = (ComboBoxRenderer) ctx.getBean(renderer);
		}
		
		cr.setProperty(elm.getAttribute(PROPERTY));
		comboBox.setRenderer(cr);
		
		// create binding
		JComboBoxBinding cb = SwingBindings.createJComboBoxBinding(
			updateStrategy, listSource, comboBox);
		
		addBinding(decodeEngine, cb);
		
		return new NoAddValueHolder<JComboBoxBinding>(cb);
	}
}
