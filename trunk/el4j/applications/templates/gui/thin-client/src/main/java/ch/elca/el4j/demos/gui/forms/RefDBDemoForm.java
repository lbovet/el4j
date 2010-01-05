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

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;

import net.java.dev.designgridlayout.DesignGridLayout;

import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.springframework.stereotype.Component;

import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.demos.gui.events.SearchRefDBEvent;
import ch.elca.el4j.demos.model.ServiceBroker;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.services.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.services.gui.swing.wrapper.AbstractWrapperFactory;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;

import cookxml.cookswing.CookSwing;


/**
 * This class demonstrates how to connect to the refDB.
 *
 * A single click on a table entry highlight the whole row. A double click opens
 * a simple editor ({@link ReferenceEditorForm}) that allows editing the
 * selected entry.
 *
 * Binding is done manually (see listBinding.getSpecialBinding).
 * This form listens to two events:
 * <ul>
 *   <li>ReferenceUpdateEvent: The editor commits the changes: we need to
 *       update the table.</li>
 *   <li>SearchRefDBEvent: A search on the refDB is requested: query the
 *       database and show the result</li>
 * </ul>
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Component("refDBDemoForm")
public class RefDBDemoForm extends JPanel implements Bindable {
	protected JTextField name;
	protected JTextField authorName;
	protected JTextField description;
	protected JCheckBox incomplete;
	
	protected JButton createButton;
	protected JButton deleteButton;
	
	protected boolean create;
	
	protected JTable references;
	
	/**
	 * The list of references.
	 */
	protected List<Reference> refList;
	
	/**
	 * The manually created list binding.
	 */
	@SuppressWarnings("unchecked")
	protected transient AutoBinding listBinding;
	
	/**
	 * The model to bind to this form.
	 */
	protected ReferenceService service;
	
	/**
	 * The binder instance variable.
	 */
	protected final Binder binder = BinderManager.getBinder(this);
	
	public RefDBDemoForm() {
		loadModel();

		createUI();
		
		binder.bindAll();
		
		createDataBinding();
	}

	protected void createUI() {
		setLayout(new BorderLayout());
		
		CookSwing cookSwing = new CookSwing(this);
		add(cookSwing.render("gui/refDBDemoForm.xml"));
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		return binder;
	}
	
	/**
	 * Create a new table entry.
	 */
	@Action(enabledProperty = "create")
	public void create() {
		
		Book newRef = new Book();
		newRef.setName(name.getText());
		newRef.setDescription(description.getText());
		newRef.setAuthorName(authorName.getText());
		newRef.setIncomplete(incomplete.isSelected());
		
		refList.add(service.saveReference(newRef));
		
		name.setText("");
		authorName.setText("");
		description.setText("");
		incomplete.setSelected(false);
	}
	
	/**
	 * Delete selected table entry.
	 */
	@Action
	public void delete() {
		if (references.getSelectedRow() >= 0) {
			int selectedRow = references.getSelectedRow();
			
			Object o = references.getValueAt(selectedRow, 0);
			if (o != null) {
				Reference selectedReference
					= (Reference) ((ValidatedProperty) o).getParent();
				service.deleteReference(selectedReference.getKey());
				refList.remove(selectedReference);
			}
		}
	}
	
	/**
	 * Is the user allowed to create a new Book.
	 * @return if the user is allowed to create.
	 */
	public boolean isCreate() {
		return create;
	}

	/**
	 * Check the condition if the user is allowed to create.
	 * Fires a property change.
	 */
	public void updateCreate() {
		boolean oldCreate = create;
		create = (name != null && name.getText().length() > 0 
			&& authorName != null && authorName.getText().length() > 2);
		firePropertyChange("create", oldCreate, create);
	}
	
	/**
	 * Layout the form components.
	 *
	 * @param formPanel    the panel to layout
	 */
	protected void setGridPanelLayout(JPanel formPanel) {
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(formPanel);
		formPanel.setLayout(layout);

		// the first two rows contains a label and a text field each
		
		layout.row().grid().add(new JLabel("Name")).add(name);
		layout.row().grid().add(new JLabel("Author")).add(authorName);
		layout.row().grid().add(new JLabel("Description")).add(description);
		layout.row().grid().add(new JLabel("Incomplete")).add(incomplete);
		layout.row().grid().add(createButton).add(deleteButton);
		createButton.setEnabled(false);
	}

	protected void loadModel() {
		GUIApplication app = GUIApplication.getInstance();
		ServiceBroker.setApplicationContext(
			app.getSpringContext());
		service = ServiceBroker.getReferenceService();
		
		// It would be possible to add the property change mixin directly
		// to the service, which guarantees that all lists coming from the
		// service automatically are observable. The problem is that this
		// currently doesn't work if service is remote.
		//service = PropertyChangeListenerMixin
		//        .addPropertyChangeMixin(service);
		// So we need to add this mixin to all lists received from the service.
		
		refList = PropertyChangeListenerMixin
			.addPropertyChangeMixin(service.getAllReferences());
	}
	/**
	 * Bind the model to the table.
	 */
	@SuppressWarnings("unchecked")
	protected void createDataBinding() {
		//updateBinding();
		
		references.setRowSelectionAllowed(true);
		references.setColumnSelectionAllowed(true);
		// select first entry
		if (refList.size() > 0) {
			references.setRowSelectionInterval(0, 0);
			references.setColumnSelectionInterval(0,
				references.getColumnCount() - 1);
		}
		
		references.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// make the whole row selected.
				int index = references.rowAtPoint(e.getPoint());
				
				if (index >= 0) {
					references.setRowSelectionInterval(index, index);
					references.setColumnSelectionInterval(0,
						references.getColumnCount() - 1);
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = references.rowAtPoint(e.getPoint());
				if (index >= 0 && e.getClickCount() == 2) {
					TableModel dlm = references.getModel();
					Object item = dlm.getValueAt(index, 0);
					if (item instanceof ValidatedProperty) {
						ValidatedProperty p = (ValidatedProperty) item;
						ReferenceEditorForm editor = new ReferenceEditorForm();
						editor.setReference((Reference) p.getParent());
						if (AbstractWrapperFactory
							.getFrame(editor) == null) {
							// open the editor for this reference
							GUIApplication.getInstance().show(editor);
						}
					}
				}
			}
		});
		
		listBinding.addBindingListener(new AbstractBindingListener() {
			@Override
			public void synced(Binding binding) {
				if (binding.getSourceObject() instanceof Reference) {
					Reference r = (Reference) binding.getSourceObject();
					try {
						service.saveReference(r);
					} catch (Throwable t) {
						// reload value on optimistic locking exception
						for (int i = 0; i < refList.size(); i++) {
							if (refList.get(i).equals(r)) {
								refList.remove(i);
								refList.add(i,
									service.getReferenceByKey(r.getKey()));
								break;
							}
						}
					}
				}
			}
		});
		
		name.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateCreate();
			}

			public void insertUpdate(DocumentEvent e) {
				updateCreate();
			}

			public void removeUpdate(DocumentEvent e) {
				updateCreate();
			}
		});
		
		authorName.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateCreate();
			}

			public void insertUpdate(DocumentEvent e) {
				updateCreate();
			}

			public void removeUpdate(DocumentEvent e) {
				updateCreate();
			}
		});
	}
	
	/**
	 * Called when reference got updated.
	 *
	 * @param event    the ReferenceUpdateEvent
	 */
	@EventSubscriber
	public void onEvent(ReferenceUpdateEvent event) {
		for (int i = 0; i < refList.size(); i++) {
			if (refList.get(i).getKey() == event.getKey()) {
				Reference ref = refList.get(i);
				try {
					Reference r = service.saveReference(ref);
					// update entry (version!)
					refList.remove(i);
					refList.add(i, r);
				} catch (Throwable t) {
					// reload value on optimistic locking exception
					refList.remove(i);
					refList.add(i, service.getReferenceByKey(ref.getKey()));
					break;
				}
				break;
			}
		}
	}
	
	/**
	 * Called when a reference is searched.
	 *
	 * @param event    the SearchRefDBEvent
	 */
	@EventSubscriber
	public void onEvent(SearchRefDBEvent event) {
		QueryObject query = new QueryObject();
		query.addCriteria(LikeCriteria.caseInsensitive(
			event.getFields()[0], event.getValue()));
		
		// do not reassign refList, otherwise you need to setup the whole
		// property change mechanism and the binding!
		binder.unbindAll();
		refList.clear();
		refList.addAll(service.searchReferences(query));
		binder.bindAll();
		
		// select first entry
		if (refList.size() > 0) {
			references.setRowSelectionInterval(0, 0);
			references.setColumnSelectionInterval(0,
				references.getColumnCount() - 1);
		}
	}
}
