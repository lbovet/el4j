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
import javax.swing.table.TableModel;

import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.application.Action;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.validation.ValidatedProperty;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;

import ch.elca.el4j.apps.refdb.dom.Book;
import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.demos.gui.events.SearchRefDBEvent;
import ch.elca.el4j.demos.model.ServiceBroker;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.gui.swing.wrapper.AbstractWrapperFactory;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

import net.java.dev.designgridlayout.DesignGridLayout;

import cookxml.cookswing.CookSwing;


/**
 * This class demonstrates how to connect to the refDB.
 *
 * A single click on a table entry highlight the whole row. A double click opens
 * a simple editor ({@link ReferenceEditorForm}) that allows editing the
 * selected entry.
 *
 * Binding is done manually (see m_listBinding.getSpecialBinding).
 * This form listens to two events:
 * <ul>
 *   <li>ReferenceUpdateEvent: The editor commits the changes: we need to
 *       update the table.</li>
 *   <li>SearchRefDBEvent: A search on the refDB is requested: query the
 *       database and show the result</li>
 * </ul>
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
public class RefDBDemoForm extends JPanel implements Bindable {
	protected JTextField m_name;
	protected JTextField m_description;
	protected JCheckBox m_incomplete;
	
	protected JButton m_createButton;
	protected JButton m_deleteButton;
	
	protected JTable m_references;
	
	/**
	 * The list of references.
	 */
	protected List<Reference> m_refList;
	
	/**
	 * The manually created list binding.
	 */
	@SuppressWarnings("unchecked")
	protected AutoBinding m_listBinding;
	
	/**
	 * The model to bind to this form.
	 */
	protected ReferenceService m_service;
	
	/**
	 * The binder instance variable.
	 */
	protected final Binder m_binder = BinderManager.getBinder(this);
	
	public RefDBDemoForm() {
		loadModel();

		createUI();
		
		m_binder.bindAll();
		
		createDataBinding();
	}

	protected void createUI() {
		setLayout(new BorderLayout());
		
		CookSwing cookSwing = new CookSwing(this);
		add(cookSwing.render("gui/refDBDemoForm.xml"));
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		return m_binder;
	}
	
	/**
	 * Create a new table entry.
	 */
	@Action
	public void create() {
		Reference newRef = new Book();
		newRef.setName(m_name.getText());
		newRef.setDescription(m_description.getText());
		newRef.setIncomplete(m_incomplete.isSelected());
		
		m_refList.add(m_service.saveReference(newRef));
		
		m_name.setText("");
		m_description.setText("");
		m_incomplete.setSelected(false);
	}
	
	/**
	 * Delete selected table entry.
	 */
	@Action
	public void delete() {
		if (m_references.getSelectedRow() >= 0) {
			int selectedRow = m_references.getSelectedRow();
			
			Object o = m_references.getValueAt(selectedRow, 0);
			if (o != null) {
				Reference selectedReference
					= (Reference) ((ValidatedProperty) o).getParent();
				m_refList.remove(selectedReference);
				m_service.deleteReference(selectedReference.getKey());
			}
		}
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
		
		layout.row().add(new JLabel("Name")).add(m_name);
		layout.row().add(new JLabel("Description")).add(m_description);
		layout.row().add(new JLabel("Incomplete")).add(m_incomplete);
		layout.row().add(m_createButton).add(m_deleteButton);

		// Checkstyle: MagicNumber off
		layout.emptyRow(10);
		// Checkstyle: MagicNumber on
	}

	protected void loadModel() {
		GUIApplication app = GUIApplication.getInstance();
		ServiceBroker.setApplicationContext(
			app.getSpringContext());
		m_service = ServiceBroker.getReferenceService();
		
		// It would be possible to add the property change mixin directly
		// to the service, which guarantees that all lists coming from the
		// service automatically are observable. The problem is that this
		// currently doesn't work if service is remote.
		//m_service = PropertyChangeListenerMixin
		//        .addPropertyChangeMixin(m_service);
		// So we need to add this mixin to all lists received from the service.
		
		m_refList = PropertyChangeListenerMixin
			.addPropertyChangeMixin(m_service.getAllReferences());
	}
	/**
	 * Bind the model to the table.
	 */
	@SuppressWarnings("unchecked")
	protected void createDataBinding() {
		//updateBinding();
		
		m_references.setRowSelectionAllowed(true);
		m_references.setColumnSelectionAllowed(true);
		// select first entry
		if (m_refList.size() > 0) {
			m_references.setRowSelectionInterval(0, 0);
			m_references.setColumnSelectionInterval(0,
				m_references.getColumnCount() - 1);
		}
		
		m_references.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// make the whole row selected.
				int index = m_references.rowAtPoint(e.getPoint());
				
				if (index >= 0) {
					m_references.setRowSelectionInterval(index, index);
					m_references.setColumnSelectionInterval(0,
						m_references.getColumnCount() - 1);
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = m_references.rowAtPoint(e.getPoint());
				if (index >= 0 && e.getClickCount() == 2) {
					TableModel dlm = m_references.getModel();
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
		
		m_listBinding.addBindingListener(new AbstractBindingListener() {
			@Override
			public void synced(Binding binding) {
				if (binding.getSourceObject() instanceof Reference) {
					Reference r = (Reference) binding.getSourceObject();
					try {
						m_service.saveReference(r);
					} catch (Throwable t) {
						// reload value on optimistic locking exception
						for (int i = 0; i < m_refList.size(); i++) {
							if (m_refList.get(i).equals(r)) {
								m_refList.remove(i);
								m_refList.add(i,
									m_service.getReferenceByKey(r.getKey()));
								break;
							}
						}
					}
				}
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
		for (int i = 0; i < m_refList.size(); i++) {
			if (m_refList.get(i).getKey() == event.getKey()) {
				Reference ref = m_refList.get(i);
				try {
					Reference r = m_service.saveReference(ref);
					// update entry (version!)
					m_refList.remove(i);
					m_refList.add(i, r);
				} catch (Throwable t) {
					// reload value on optimistic locking exception
					m_refList.remove(i);
					m_refList.add(i, m_service.getReferenceByKey(ref.getKey()));
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
		
		// do not reassign m_refList, otherwise you need to setup the whole
		// property change mechanism and the binding!
		m_binder.unbindAll();
		m_refList.clear();
		m_refList.addAll(m_service.searchReferences(query));
		m_binder.bindAll();
		
		// select first entry
		if (m_refList.size() > 0) {
			m_references.setRowSelectionInterval(0, 0);
			m_references.setColumnSelectionInterval(0,
				m_references.getColumnCount() - 1);
		}
	}
}
