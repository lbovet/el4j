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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.validation.ValidatedProperty;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.binding.components.TableBinding;

import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.demos.gui.events.SearchRefDBEvent;
import ch.elca.el4j.demos.model.ServiceBroker;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.events.OpenCloseEventHandler;
import ch.elca.el4j.gui.swing.wrapper.AbstractWrapperFactory;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

import zappini.designgridlayout.DesignGridLayout;

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
public class RefDBDemoForm extends JPanel implements OpenCloseEventHandler {
    
    private JTable references;
    
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
    
    public RefDBDemoForm() {
        ServiceBroker.setApplicationContext(
            GUIApplication.getInstance().getSpringContext());
        m_service = ServiceBroker.getReferenceService();
        
        createComponents();
        createLayout();
        
        m_editor = new ReferenceEditorForm();
        
        createDataBinding();
    }

    /**
     * Create the form components.
     */
    private void createComponents() {
        references = new JTable();
    }
    
    /**
     * Layout the form components.
     */
    private void createLayout() {
        JPanel formPanel = new JPanel();

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        
        // add the table to the center
        //children.setColumnControlVisible(true);
        JScrollPane scrollPane = new JScrollPane(references);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(formPanel);
        formPanel.setLayout(layout);
    }

    /**
     * Bind the model to the table.
     */
    @SuppressWarnings("unchecked")
    private void createDataBinding() {
        // It would be possible to add the property change mixin directly
        // to the service, which guarantees that all lists coming from the
        // service automatically are observable. The problem is that this
        // currently doesn't work if service is remote.
        //m_service = PropertyChangeListenerMixin
        //        .addPropertyChangeMixin(m_service);
        // So we need to add this mixin to all lists received from the service.
        
        m_refList = PropertyChangeListenerMixin
            .addPropertyChangeMixin(m_service.getAllReferences());

        updateBinding();
        
        references.setRowSelectionAllowed(true);
        references.setColumnSelectionAllowed(true);
        // select first entry
        if (m_refList.size() > 0) {
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
     * Update the reference binding.
     */
    @SuppressWarnings("unchecked")
    private void updateBinding() {
        // prepare table bindings
        String[] propertyNames = new String[] {
            "name", "description", "incomplete",
            "version", "date", "whenInserted"};
        String[] columnLabels = new String[] {
            "Name", "Description", "Incomplete?",
            "Version", "Date", "Insertion Timestamp"};
        Class[] columnClasses = new Class[] {
            String.class, String.class, Boolean.class,
            String.class, Date.class, Timestamp.class};
        
        TableBinding tb = new TableBinding(propertyNames,
            columnLabels, columnClasses);
        
        // table is not directly editable
        tb.setUpdateStrategy(UpdateStrategy.READ);
        
        // bind the table manually
        if (m_listBinding != null && m_listBinding.isBound()) {
            m_listBinding.unbind();
        }
        m_listBinding = m_binder.getSpecialBinding(
            m_refList, references, tb, true);
        m_listBinding.bind();
    }
    
    /**
     * Called when reference got updated.
     * 
     * @param event    the ReferenceUpdateEvent
     */
    @EventSubscriber(eventClass = ReferenceUpdateEvent.class)
    public void onEvent(ReferenceUpdateEvent event) {
        for (int i = 0; i < m_refList.size(); i++) {
            if (m_refList.get(i).getKey() == event.getKey()) {
                Reference ref = m_refList.get(i);
                try {
                    m_service.saveReference(ref);
                } catch (Throwable t) {
                    // reload value on optimistic locking exception
                    m_refList.remove(i);
                    m_refList.add(i, m_service.getReferenceByKey(ref.getKey()));
                    break;
                }
                break;
            }
        }
        // update table
        m_listBinding.unbind();
        m_listBinding.bind();
        
    }
    
    /**
     * Called when a reference is searched.
     * 
     * @param event    the SearchRefDBEvent
     */
    @EventSubscriber(eventClass = SearchRefDBEvent.class)
    public void onEvent(SearchRefDBEvent event) {
        QueryObject query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive(
            event.getField(), event.getValue()));
        m_refList = PropertyChangeListenerMixin
            .addPropertyChangeMixin(m_service.searchReferences(query));
        
        updateBinding();
        
        // select first entry
        if (m_refList.size() > 0) {
            references.setRowSelectionInterval(0, 0);
            references.setColumnSelectionInterval(0,
                references.getColumnCount() - 1);
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
