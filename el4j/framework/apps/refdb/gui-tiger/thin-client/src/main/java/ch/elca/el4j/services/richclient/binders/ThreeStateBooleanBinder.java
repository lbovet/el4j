/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.richclient.binders;

import java.util.Map;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;

import ch.elca.el4j.services.gui.richclient.forms.binding.swing.ThreeStateBooleanJPanel;
import ch.elca.el4j.util.codingsupport.Reject;

public class ThreeStateBooleanBinder extends
    ch.elca.el4j.services.gui.richclient.forms.binding.ThreeStateBooleanBinder {

    /**
     * {@inheritDoc}
     * 
     * Binds the given <code>ThreeStateBooleanJPanel</code> with the given 
     * form model.
     */
    protected Binding doBind(JComponent control, FormModel formModel,
        String formPropertyPath, Map context) {
        Reject.ifFalse(control instanceof ThreeStateBooleanJPanel, 
            "Given control must of type " 
            + ThreeStateBooleanJPanel.class.getName() + ".");
        return new ThreeStateBooleanBinding((ThreeStateBooleanJPanel) control, 
            formModel, formPropertyPath);
    } 
}
