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

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.w3c.dom.Element;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CreatorException;

/**
 * The cookSwing creator for general purpose &lt;columnbinding&gt;s inside 
 * &lt;tablebinding&gt;s.
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
public class ColumnBindingCreator extends AbstractBindingCreator {
    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(ColumnBindingCreator.class);
    
    // <column> specific attributes
    protected static final String LABEL = "label";
    protected static final String EDITABLE = "editable";
    protected static final String CLASS = "class";
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Object create(String parentNS, String parentTag, Element elm,
        Object parentObj, DecodeEngine decodeEngine) throws CreatorException {
        
        // read attributes
        JTableBinding tb = ((NoAddValueHolder<JTableBinding>)
            parentObj).getObject();
        Property prop = BeanProperty.create(elm.getAttribute(PROPERTY));
        
        // create binding
        ColumnBinding cb = tb.addColumnBinding(prop);
        cb.setColumnName(elm.getAttribute(LABEL));
        
        // is editable?
        cb.setEditable(elm.getAttribute(EDITABLE).equalsIgnoreCase("true"));
        
        // set class
        try {
            if (!elm.getAttribute(CLASS).equals("")) {
                cb.setColumnClass(Class.forName(elm.getAttribute(CLASS)));
            }
        } catch (ClassNotFoundException e) {
            s_logger.warn("Attribute '" + CLASS + "' of tag '"
                + elm.getNodeName() + "' contains invalid class.");
        }
        
        return new NoAddValueHolder<ColumnBinding>(cb);
    }
}
