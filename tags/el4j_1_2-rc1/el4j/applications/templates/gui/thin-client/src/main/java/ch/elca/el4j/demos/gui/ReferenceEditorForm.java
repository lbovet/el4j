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
package ch.elca.el4j.demos.gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.BindingGroup;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;

import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.wrapper.AbstractWrapperFactory;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.model.mixin.SaveRestoreCapability;

import zappini.designgridlayout.DesignGridLayout;

/**
 * This GUI can be used to edit a reference from the refDB.
 * 
 * This form is used by {@link RefDBDemoForm}. It extends the features shown in
 * {@link CancelableDemoForm}. It is shown how to close the window containing
 * this panel (<code>AbstractWrapperFactory.getWrapper(this).dispose()</code>).
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
@Form(autoBind = true)
public class ReferenceEditorForm extends JPanel {
    private JTextField name;
    private JTextField description;
    
    private JButton m_okButton;
    private JButton m_cancelButton;
    
    @ModelObject(isDefault = true)
    private Reference m_reference = null;
    
    /**
     * The current binding.
     */
    private BindingGroup m_binding;
    
    
    /**
     * The key of the current reference. This property cannot be stored by
     * SaveRestoreCapability because Cglib2AopProxy is unable to proxy 
     * final methods.
     */
    private int m_key;
    
    /**
     * The binder instance variable.
     */
    private final Binder m_binder = BinderManager.getBinder(this);
    
    public ReferenceEditorForm() {
        GUIApplication app = GUIApplication.getInstance();
        createComponents();
        createLayout();
        
        // assign actions
        m_okButton.setAction(app.getAction(this, "applyChanges"));
        m_cancelButton.setAction(app.getAction(this, "discardChanges"));
    }
    
    /**
     * @param reference    the refernce to be edited on the GUI
     */
    public void setReference(Reference reference) {
        m_reference = PropertyChangeListenerMixin
            .addPropertyChangeMixin(reference);
        m_key = reference.getKey();
        
        // save properties
        ((SaveRestoreCapability) m_reference).save();
        
        // bind the variable "m_reference" to "this"
        // this interprets the @ModelObject annotation (see above)
        m_binding = m_binder.getAutoBinding(this);
        m_binding.bind();
    }
    
    
    /**
     * Apply changes to the model.
     */
    @Action
    public void applyChanges() {
        ((SaveRestoreCapability) m_reference).save();
        m_reference.setKey(m_key);
        m_binding.unbind();
        
        EventBus.publish(new ReferenceUpdateEvent(m_reference.getKey()));
        AbstractWrapperFactory.getWrapper(this).dispose();
    }
    
    /**
     * Discard changes.
     */
    @Action
    public void discardChanges() {
        ((SaveRestoreCapability) m_reference).restore();
        m_reference.setKey(m_key);
        m_binding.unbind();
        
        AbstractWrapperFactory.getWrapper(this).dispose();
    }
    
    /**
     * Create the form components.
     */
    private void createComponents() {
        name = new JTextField();
        description = new JTextField();
        m_okButton = new JButton();
        m_cancelButton = new JButton();
    }
    
    /**
     * Layout the form components.
     */
    private void createLayout() {
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(this);
        setLayout(layout);

        // the first two rows contains a label and a text field each
        layout.row().label("Name").add(name);
        layout.row().label("Description").add(description);
        layout.row().add(m_okButton).add(m_cancelButton);
    }
}
