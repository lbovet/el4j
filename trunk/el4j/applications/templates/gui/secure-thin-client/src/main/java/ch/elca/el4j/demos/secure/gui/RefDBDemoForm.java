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
package ch.elca.el4j.demos.secure.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.bushe.swing.event.annotation.AnnotationProcessor;
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
import ch.elca.el4j.demos.gui.ReferenceEditorForm;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.demos.gui.events.SearchRefDBEvent;
import ch.elca.el4j.demos.model.ServiceBroker;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.exceptions.Handler;
import ch.elca.el4j.gui.swing.wrapper.AbstractWrapperFactory;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

import net.java.dev.designgridlayout.DesignGridLayout;

import cookxml.cookswing.CookSwing;


/**
 * This class demonstrates how to securely connect to the refDB.
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
	
	public RefDBDemoForm() {
		// set security token
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("el4normal", "el4j"));
		//SecurityContextHolder.getContext().setAuthentication(
		//    new UsernamePasswordAuthenticationToken("el4super", "secret"));
		
		Exceptions.getInstance().addHandler(new Handler() {
			public boolean recognize(Exception e) {
				if (e instanceof InvocationTargetException) {
					InvocationTargetException ite
						= (InvocationTargetException) e;
					if (ite.getTargetException()
						instanceof AccessDeniedException) {
						return true;
					}
				}
				return false;
			}
			public void handle(Exception e) {
				JOptionPane.showMessageDialog(RefDBDemoForm.this,
					"Access denied.", "RefDBDemoForm",
					JOptionPane.ERROR_MESSAGE);
			}
		});
		init();
	}
	
	private JTextField m_name;
	private JTextField m_description;
	private JCheckBox m_incomplete;
	
	private JButton m_createButton;
	private JButton m_deleteButton;
	
	private JTable m_references;
	
	/**
	 * The list of references.
	 */
	private List<Reference> m_refList;
	
	/**
	 * The manually created list binding.
	 */
	@SuppressWarnings("unchecked")
	private AutoBinding m_listBinding;
	
	/**
	 * The editor GUI for a reference.
	 */
	private ReferenceEditorForm m_editor;
	
	/**
	 * The model to bind to this form.
	 */
	private ReferenceService m_service;
	
	/**
	 * The binder instance variable.
	 */
	private final Binder m_binder = BinderManager.getBinder(this);
	
	/**
	 * Initialize the form.
	 */
	protected void init() {
		loadModel();
		m_editor = new ReferenceEditorForm();

		setLayout(new BorderLayout());
		
		CookSwing cookSwing = new CookSwing(this);
		add(cookSwing.render("gui/refDBDemoForm.xml"));
		
		m_binder.bindAll();
		
		createDataBinding();
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
				
				m_service.deleteReference(selectedReference.getKey());
				m_refList.remove(selectedReference);
			}
			m_listBinding.unbind();
			m_listBinding.bind();
		}
	}

	/**
	 * Layout the form components.
	 *
	 * @param formPanel    the panel to layout
	 */
	@SuppressWarnings("unused")
	private void setGridPanelLayout(JPanel formPanel) {
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(formPanel);
		formPanel.setLayout(layout);

		// the first two rows contains a label and a text field each
		
		layout.row().add(new JLabel("Name")).add(m_name);
		layout.row().add(new JLabel("Description")).add(m_description);
		layout.row().add(new JLabel("Incomplete")).add(m_incomplete);
		layout.row().add(m_createButton).add(m_deleteButton);
		// Hint: spacers can be inserted using add(Row.EMPTY)

		// Checkstyle: MagicNumber off
		layout.emptyRow(10);
		// Checkstyle: MagicNumber on
	}

	private void loadModel() {
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
	private void createDataBinding() {


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
						m_editor.setReference((Reference) p.getParent());
						if (AbstractWrapperFactory
							.getWrapper(m_editor) == null) {
							// open the editor for this reference
							GUIApplication.getInstance().show(m_editor);
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
	
	/** {@inheritDoc} */
	public void onOpen() {
		// register all event subscribers
		AnnotationProcessor.process(this);
	}
	
	/** {@inheritDoc} */
	public void onClose() {
		// unregister all event subscribers
		AnnotationProcessor.unsubscribe(this);
	}
}
