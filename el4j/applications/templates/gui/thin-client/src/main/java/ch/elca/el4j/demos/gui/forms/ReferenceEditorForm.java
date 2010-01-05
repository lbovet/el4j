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
package ch.elca.el4j.demos.gui.forms;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;
import org.springframework.stereotype.Component;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;

import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.model.mixin.SaveRestoreCapability;
import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrameAware;
import ch.elca.el4j.util.codingsupport.annotations.FindBugsSuppressWarnings;

import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * This GUI can be used to edit a reference from the refDB.
 *
 * This form is used by {@link RefDBDemoForm}. It extends the features shown in
 * {@link CancelableDemoForm}. It is shown how to close the window containing
 * this panel (<code>AbstractWrapperFactory.getWrapper(this).dispose()</code>).
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Component("referenceEditorForm")
@Form(autoBind = true)
@FindBugsSuppressWarnings(value = {"NP_UNWRITTEN_FIELD", "UWF_NULL_FIELD"}, 
							justification = "Bug warnings due usage of form binding")
public class ReferenceEditorForm extends JPanel implements ApplicationFrameAware {
	/**
	 * The textfield for a reference name. Bound to reference.name (prefix "" gets removed).
	 */
	private JTextField name;
	
	/**
	 * The textfield for a reference description. Bound to reference.description (prefix "" gets removed).
	 */
	private JTextField description;
	
	private JButton okButton;
	private JButton cancelButton;
	
	@ModelObject(isDefault = true)
	private Reference mixinReference = null;
	
	
	/**
	 * The key of the current reference. This property cannot be stored by
	 * SaveRestoreCapability because Cglib2AopProxy is unable to proxy
	 * final methods.
	 */
	private int key;
	
	/**
	 * The binder instance variable.
	 */
	private final Binder binder = BinderManager.getBinder(this);
	
	/**
	 * The application frame this form is embedded.
	 */
	private ApplicationFrame applicationFrame;
	
	public ReferenceEditorForm() {
		GUIApplication application = GUIApplication.getInstance();
		createComponents();
		createLayout();
		
		// assign actions
		okButton.setAction(application.getAction(this, "applyChanges"));
		cancelButton.setAction(application.getAction(this, "discardChanges"));
	}
	
	/**
	 * @param reference    the reference to be edited on the GUI
	 */
	public void setReference(Reference reference) {
		mixinReference = PropertyChangeListenerMixin.addPropertyChangeMixin(reference);
		key = mixinReference.getKey();
		
		// save properties
		((SaveRestoreCapability) mixinReference).save();
		
		// bind the variable "reference" to "this"
		// this interprets the @ModelObject annotation (see above)
		binder.addAutoBinding(this);
		binder.bindAll();
	}
	
	
	/**
	 * Apply changes to the model.
	 */
	@Action
	public void applyChanges() {
		((SaveRestoreCapability) mixinReference).save();
		mixinReference.setKey(key);
		binder.removeAll();
		
		EventBus.publish(new ReferenceUpdateEvent(mixinReference.getKey()));
		applicationFrame.close();
		mixinReference = null;
	}
	
	/**
	 * Discard changes.
	 */
	@Action
	public void discardChanges() {
		((SaveRestoreCapability) mixinReference).restore();
		mixinReference.setKey(key);
		binder.removeAll();
		
		applicationFrame.close();
		mixinReference = null;
	}
	
	/**
	 * Create the form components.
	 */
	private void createComponents() {
		name = new JTextField();
		description = new JTextField();
		okButton = new JButton();
		cancelButton = new JButton();
	}
	
	/**
	 * Layout the form components.
	 */
	private void createLayout() {
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(this);
		setLayout(layout);

		// the first two rows contains a label and a text field each
		layout.row().grid(new JLabel("Name")).add(name);
		layout.row().grid(new JLabel("Description")).add(description);
		layout.row().grid().add(okButton).add(cancelButton);
	}
	
	/** {@inheritDoc} */
	public void setApplicationFrame(ApplicationFrame applicationFrame) {
		this.applicationFrame = applicationFrame;
	}
}
