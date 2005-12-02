/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.refdb.gui.forms;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Form for keyword properties.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class KeywordPropertiesForm extends AbstractForm {
    /**
     * Key used in property files for message lookup.
     */
    public static final String KEYWORD_PROPERTIES = "keywordProperties";

    /**
     * Is the first focused component.
     */
    private JComponent m_firstFocusedComponent = null;

    /**
     * Constructor.
     * 
     * @param formModel Is the model for the form.
     */
    public KeywordPropertiesForm(FormModel formModel) {
        super(formModel, KEYWORD_PROPERTIES);
    }

    /**
     * {@inheritDoc}
     * 
     * Creates form with name and description field.
     */
    protected JComponent createFormControl() {
        TableFormBuilder formBuilder 
            = new TableFormBuilder(getBindingFactory());
        m_firstFocusedComponent = formBuilder.add("name")[1];
        formBuilder.row();
        formBuilder.add("description");
        return formBuilder.getForm();
    }
    
    /**
     * Sets focus to first component.
     */
    public void focusFirstComponent() {
        if (m_firstFocusedComponent != null) {
            m_firstFocusedComponent.requestFocusInWindow();
        }
    }

}
