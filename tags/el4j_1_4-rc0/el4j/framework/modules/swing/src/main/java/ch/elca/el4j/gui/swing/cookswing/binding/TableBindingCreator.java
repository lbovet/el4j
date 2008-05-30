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

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.util.config.GenericConfig;

import cookxml.core.DecodeEngine;
import cookxml.core.exception.CreatorException;

/**
 * The cookSwing creator for general purpose &lt;tablebinding&gt;s.
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
public class TableBindingCreator extends AbstractBindingCreator {
    // <tablebinding> specific attributes
    protected static final String RENDERER = "rendererBean";
    protected static final String EDITOR = "editorBean";
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Object create(String parentNS, String parentTag, Element elm,
        Object parentObj, DecodeEngine decodeEngine) throws CreatorException {

        // read properties
        UpdateStrategy updateStrategy = getUpdateStrategy(elm);
        List listSource = (List) getSource(decodeEngine, elm);
        if (listSource == null) {
            return null;
        }
        JTable table = (JTable) parentObj;

        // renderer and validation
        String renderer = elm.getAttribute(RENDERER);
        if (renderer.equals("")) {
            if (getValidate(elm)) {
                GenericConfig config = GUIApplication.getInstance().getConfig();
                table.setDefaultRenderer(ValidatedProperty.class,
                    (TableCellRenderer) config.get("tableCellRenderer"));
            }
        } else {
            ApplicationContext ctx
                = GUIApplication.getInstance().getSpringContext();
            table.setDefaultRenderer(ValidatedProperty.class,
                (TableCellRenderer) ctx.getBean(renderer));
        }
        
        String editor = elm.getAttribute(EDITOR);
        if (editor.equals("")) {
            if (getValidate(elm)) {
                GenericConfig config = GUIApplication.getInstance().getConfig();
                table.setDefaultEditor(ValidatedProperty.class,
                    (TableCellEditor) config.get("tableCellEditor"));
            }
        } else {
            ApplicationContext ctx
                = GUIApplication.getInstance().getSpringContext();
            table.setDefaultEditor(ValidatedProperty.class,
                (TableCellEditor) ctx.getBean(editor));
        }

        // create binding
        JTableBinding tb = SwingBindings.createJTableBinding(
            updateStrategy, listSource, table);
        addBinding(decodeEngine, tb);

        return new NoAddValueHolder<JTableBinding>(tb);
    }
}
