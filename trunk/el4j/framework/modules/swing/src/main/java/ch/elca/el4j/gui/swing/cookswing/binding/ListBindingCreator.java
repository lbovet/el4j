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

import java.util.List;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.util.config.GenericConfig;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CreatorException;


/**
 * The cookSwing creator for general purpose &lt;listbinding&gt;s.
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
public class ListBindingCreator extends AbstractBindingCreator {
    // <listbinding> specific attributes
    protected static final String RENDERER = "rendererBean";
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Object create(String parentNS, String parentTag, Element elm,
        Object parentObj, DecodeEngine decodeEngine) throws CreatorException {
        
        UpdateStrategy updateStrategy = getUpdateStrategy(elm);
        Property prop = BeanProperty.create(elm.getAttribute(PROPERTY));
        
        List listSource = (List) getSource(decodeEngine, elm);
        if (listSource == null) {
            return null;
        }
        JList list = (JList) parentObj;
        
        // renderer and validation
        String renderer = elm.getAttribute(RENDERER);
        if (renderer.equals("")) {
            if (getValidate(elm)) {
                GenericConfig config = GUIApplication.getInstance().getConfig();
                list.setCellRenderer((ListCellRenderer) config
                    .get("cellRenderer"));
            }
        } else {
            ApplicationContext ctx
                = GUIApplication.getInstance().getSpringContext();
            list.setCellRenderer((ListCellRenderer) ctx.getBean(renderer));
        }
        
        // create binding
        JListBinding lb = SwingBindings.createJListBinding(
            updateStrategy, listSource, list);
        
        // show specified property
        lb.setDetailBinding(prop);

        addBinding(decodeEngine, lb);
        
        return new NoAddValueHolder<JListBinding>(lb);
    }
}
