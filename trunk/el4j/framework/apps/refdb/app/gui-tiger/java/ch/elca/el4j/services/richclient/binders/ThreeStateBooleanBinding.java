/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.richclient.binders;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.springframework.binding.form.FormModel;

import ch.elca.el4j.services.dom.info.EntityType;
import ch.elca.el4j.services.dom.info.Property;
import ch.elca.el4j.services.gui.richclient.forms.binding.swing.AbstractSwingBinding;
import ch.elca.el4j.services.gui.richclient.forms.binding.swing.ThreeStateBooleanJPanel;
import ch.elca.el4j.services.gui.search.AbstractSearchItem;
import ch.elca.el4j.services.richclient.naming.Naming;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.registy.impl.StringMapBackedRegistry;

/**
 * Prototype for new ThreeStateBinding. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class ThreeStateBooleanBinding extends AbstractSwingBinding {
    /** 
     * The abstract search item represented by this binding, or null if
     * there is no such search item.
     */
    AbstractSearchItem m_searchItem;
    
    /**
     * Constructor. See 
     * {@link AbstractSwingBinding#AbstractSwingBinding(JComponent, FormModel,
     * String, Class)}.
     * @param control see super
     * @param formModel see super
     * @param formPropertyPath see super
     */
    @SuppressWarnings("unchecked")
    public ThreeStateBooleanBinding(ThreeStateBooleanJPanel control, 
                                    FormModel formModel,
                                    String formPropertyPath) {
        super(control, formModel, formPropertyPath, Boolean.class);
        
        m_searchItem = new StringMapBackedRegistry(
            formModel.getFieldMetadata(formPropertyPath).getAllUserMetadata()
        ).get(AbstractSearchItem.class);
        Reject.ifNull(
            m_searchItem, getClass().getName()
            + " requires model information to look up messages"
        );
    }

    /** {@inheritDoc} */
    @Override
    protected JComponent doBindControl() {
        ThreeStateBooleanJPanel control 
            = (ThreeStateBooleanJPanel) getJComponent();

        control.setValueModel(getValueModel());        
        
        
        Property property = EntityType.get(
            m_searchItem.getTargetBeanClass()
        ).find(
            m_searchItem.getTargetProperty()
        );
                    
        Naming msgs = Naming.instance();
        configure(
            control.getTrueButton(),
            msgs.forConstantValue(property, "true")
        );
        configure(
            control.getFalseButton(),
            msgs.forConstantValue(property, "false")
        );
        configure(
            control.getUnknownButton(),
            msgs.forConstantValue(property, "any")
        );
        
        return control;
    }
    
    /**
     * Configures a value button.
     * @param button the button to configure
     * @param msgs the message source to use
     */
    protected void configure(JToggleButton button, Naming.Fetcher msgs) {
        button.setText(msgs.get("displayName"));
        button.setToolTipText(msgs.get("description"));
    }
}
