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
package ch.elca.el4j.services.richclient.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.richclient.application.ViewDescriptor;

import ch.elca.el4j.apps.lightrefdb.dom.Keyword;
import ch.elca.el4j.apps.refdb.gui.brokers.ServiceBroker;
import ch.elca.el4j.apps.refdb.gui.executors.KeywordDeleteExecutor;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.SelectAllBeanExecutor;
import ch.elca.el4j.services.gui.richclient.models.BeanTableModel;
import ch.elca.el4j.services.gui.richclient.views.AbstractBeanTableView;
import ch.elca.el4j.services.richclient.naming.Naming;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.collections.ExtendedList;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.helpers.Function;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;
import ch.elca.el4j.util.observer.ObservableValue;
import ch.elca.el4j.util.observer.ValueObserver;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;




/**
 * A read-only tabular overview over a set of entities of the same type. 
 * 
 * The set is defined by the filter query in {@link #filter}. 
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

// TODO: Have repository notify us about changed beans and refresh the control
// on update.
// TODO: fuse this.selection with swings selection model
public class Table extends AbstractGenericView {
    /** specifies which properties are shown and which are changeable. */
    public DisplayablePropertyList properties;
    
    /** the executors attached to this table. */
    public ExtendedWritableList<AbstractBeanExecutor> executors 
        = new ExtendedArrayList<AbstractBeanExecutor>();
    
    /** the delete executor. */
    public AbstractBeanExecutor delete;
    
    /** the select all executor. */
    public SelectAllBeanExecutor selectAll;
    
    //public PropertiesExecutor inspect;
    
    /** holds the filter selecting the entities to be displayed. */
    public SettableObservableValue<QueryObject> filter 
        = new SettableObservableValue<QueryObject>(new QueryObject());
    
    /** holds the currently selected entities. */
    // permit an application defined selection?
    public final ObservableValue<Collection<Object>> selection 
        = new SettableObservableValue<Collection<Object>>(java.util.Collections.emptyList());
    
    /***/
    private Model m_model;

    /** see source. @param c . */
    public Table(Class<?> c) {
        super(c);
        properties = new EditablePropertyList(m_type);

        // Kludge to account for non-generic persistence
        delete = c.equals(Keyword.class) ? new KeywordDeleteExecutor() : null;
        selectAll = new SelectAllBeanExecutor();
        //inspect = new PropertiesExecutor(t);
        //inspect.properties = new EditablePropertyList(t);
        executors.add(delete, selectAll);        
    }

    // Vision: merge the lookup code into BeanTableModel, 
    // replacing the inlined one
    /** A BeanTableModel with column names localized using Naming. */
    private class Model extends BeanTableModel {
        /** the precomputed column names. */
        String[] m_columnNames;
        
        /** @param properties .*/
        Model(DisplayablePropertyList properties) {
            ExtendedList<? extends DisplayableProperty> visible 
                = properties.m_eprops.filtered(
                    DisplayablePropertyList.s_visibles
                );
            setColumnPropertyNames(
                visible.mapped(DisplayablePropertyList.s_toName)
                       .toArray(String.class)
            );
            m_columnNames = visible.mapped(
                new Function<DisplayableProperty, String>() {
                    public String apply(DisplayableProperty d) {
                        return Naming.instance().getFieldFaceProperty(
                            schema,
                            d.prop,
                            "displayName"
                        );
                    }
                }
            ).toArray(String.class);            
        }
        
        /** {@inheritDoc} */
        @Override
        public String[] createColumnNames() {
            return m_columnNames;
        }
    }
    
    /***/
    class GenericComponent extends AbstractBeanTableView 
        implements ValueObserver<QueryObject> {
        
        /** updates the set of displayed entities.
         * @param q the new filter query */
        public void changed(QueryObject q) {
            Reject.ifNull(q);
            if (isControlCreated()) {
                // Kludge to account for non-generic persistence
                ReferenceService referenceService = ServiceBroker
                    .getReferenceService();
                if (m_type.clazz.equals(Keyword.class)) {
                    setBeans(referenceService.searchKeywords(q));
                } else {
                    setBeans(referenceService.searchReferences(q));
                }
            }
        }

        /***/
        @Override
        public void componentOpened() {
            super.componentOpened();
            filter.subscribe(this);
        }

        /***/
        @Override
        public void componentClosed() {
            filter.unsubscribe(this);
            super.componentClosed();
        }
        
        // invoked by selection listener
        /** updates selection. */
        // tentative support for immutable selections
        public void updateCommands() {
            if (selection instanceof SettableObservableValue) {                
                Object[] selected = getSelectedBeans();
                if (selected == null) {
                    selected = new Object[0];
                }
                ((SettableObservableValue<Collection<Object>>) selection).set(
                    Arrays.asList(selected)
                );
            }
            super.updateCommands();
        }
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDescriptor createDescriptor() {
        for (AbstractBeanExecutor e : executors) {
            m_awaker.awaken(e);
        }

        m_model = new Model(properties);

        // Kludge to account for the fact that dn!=n in this prototype.
        String dn;
        if (m_type.clazz.equals(Keyword.class)) {
            dn = "ch.elca.el4j.apps.keyword.dto." + m_type.name + "Dto";
        } else {
            dn = "ch.elca.el4j.apps.refdb.dto." + m_type.name + "Dto";
        }
        try {
            m_model.setBeanClass(Class.forName(dn));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        m_awaker.awaken(m_model);
        
        GenericComponent gc = new GenericComponent();
        gc.setBeanTableModel(m_model);
        gc.setBeanExecutors(executors.toArray(AbstractBeanExecutor.class));
        return configure(new Descriptor(gc));
    }
}