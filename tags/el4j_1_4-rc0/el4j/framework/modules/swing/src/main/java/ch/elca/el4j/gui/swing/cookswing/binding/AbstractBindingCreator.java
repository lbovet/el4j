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
package ch.elca.el4j.gui.swing.cookswing.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;

import org.w3c.dom.Element;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CookXmlException;
import cookxml.core.interfaces.Creator;

/**
 * This class provides basic functionality for cookXML binding creators.
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
public abstract class AbstractBindingCreator implements Creator {
    // some common attributes
    private static final String SOURCE = "src";
    protected static final String PROPERTY = "property";
    protected static final String VALIDATION = "validation";
    protected static final String UPDATE_STRATEGY = "updateStrategy";
    
    /**
     * The logger.
     */
    private static final Log s_logger = LogFactory
        .getLog(AbstractBindingCreator.class);
    
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
                Property prop = BeanProperty.create(
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
