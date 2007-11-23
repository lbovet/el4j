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
import javax.swing.table.TableModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.swingbinding.validation.ValidatedProperty;
import org.jdesktop.swingx.JXTable;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;
import com.silvermindsoftware.hitch.annotations.Form;
import com.silvermindsoftware.hitch.annotations.ModelObject;
import com.silvermindsoftware.hitch.binding.components.TableBinding;

import ch.elca.el4j.apps.refdb.dom.Reference;
import ch.elca.el4j.demos.gui.events.ReferenceUpdateEvent;
import ch.elca.el4j.demos.model.ReferenceModel;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.events.OpenCloseEventHandler;
import ch.elca.el4j.gui.swing.wrapper.AbstractWrapperFactory;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;

import zappini.designgridlayout.DesignGridLayout;

/**
 * This class demonstrates how to connect to the refDB.
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
@Form(autoBind = false)
public class RefDBDemoForm extends JPanel implements OpenCloseEventHandler {
    
    private JXTable references;
    
    /**
     * The list of references.
     */
    private List<Reference> m_refList;
    
    /**
     * The manually created list binding.
     */
    @SuppressWarnings("unchecked")
    private AutoBinding m_listBinding;
    
    private ReferenceEditorForm m_editor;
    
    /**
     * The model to bind to this form.
     */
    @ModelObject(isDefault = true)
    private ReferenceModel model;
    
    /**
     * The binder instance variable.
     */
    private final Binder m_binder = BinderManager.getBinder(this);
    
    public RefDBDemoForm() {
        model = new ReferenceModel(
            GUIApplication.getInstance().getSpringContext());
        
        createComponents();
        createLayout();
        
        m_editor = new ReferenceEditorForm();
        
        createDataBinding();
    }

    /**
     * Create the form components.
     */
    private void createComponents() {
        references = new JXTable();
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
        model = PropertyChangeListenerMixin
                .addPropertyChangeMixin(model);
        
        m_refList = model.getReferences();

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
        m_listBinding = m_binder.getSpecialBinding(
            m_refList, references, tb, true);
        m_listBinding.bind();
        
        references.setRowSelectionAllowed(true);
        references.setColumnSelectionAllowed(true);
        references.setRowSelectionInterval(0, 0);
        references.setColumnSelectionInterval(0,
            references.getColumnCount() - 1);
        
        references.setEnabled(false);
        
        references.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = references.rowAtPoint(e.getPoint());
                
                references.setRowSelectionInterval(index, index);
                references.setColumnSelectionInterval(0,
                    references.getColumnCount() - 1);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = references.rowAtPoint(e.getPoint());
                if (e.getClickCount() == 2) {
                    TableModel dlm = references.getModel();
                    Object item = dlm.getValueAt(index, 0);
                    if (item instanceof ValidatedProperty) {
                        ValidatedProperty p = (ValidatedProperty) item;
                        m_editor.setReference((Reference) p.getParent());
                        if (AbstractWrapperFactory
                            .getWrapper(m_editor) == null) {
                            
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
                        model.saveReference(r);
                    } catch (Throwable t) {
                        // reload value on optimistic locking exception
                        for (int i = 0; i < m_refList.size(); i++) {
                            if (m_refList.get(i).equals(r)) {
                                m_refList.remove(i);
                                m_refList.add(i, model.getRefByKey(r.getKey()));
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
    @EventSubscriber(eventClass = ReferenceUpdateEvent.class)
    public void onEvent(ReferenceUpdateEvent event) {
        for (int i = 0; i < m_refList.size(); i++) {
            if (m_refList.get(i).getKey() == event.getKey()) {
                Reference ref = m_refList.get(i);
                try {
                    model.saveReference(ref);
                } catch (Throwable t) {
                    // reload value on optimistic locking exception
                    m_refList.remove(i);
                    m_refList.add(i, model.getRefByKey(ref.getKey()));
                    break;
                }
                break;
            }
        }
        // update table
        m_listBinding.unbind();
        m_listBinding.bind();
        
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
