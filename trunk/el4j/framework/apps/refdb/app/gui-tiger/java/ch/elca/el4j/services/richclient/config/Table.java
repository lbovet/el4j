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

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.SelectAllBeanExecutor;
import ch.elca.el4j.services.gui.richclient.executors.convenience.GenericBeanDeleteExecutor;
import ch.elca.el4j.services.gui.richclient.models.BeanTableModel;
import ch.elca.el4j.services.gui.richclient.utils.Services;
import ch.elca.el4j.services.gui.richclient.views.AbstractBeanTableView;
import ch.elca.el4j.services.i18n.MessageProvider;
import ch.elca.el4j.services.persistence.generic.RepositoryAgency;
import ch.elca.el4j.services.persistence.generic.RepositoryAgent;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeListener;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier;
import ch.elca.el4j.services.persistence.generic.repo.RepositoryChangeNotifier.EntityDeleted;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;
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
    public <T> Table(Class<T> c) {
        super(c);
        properties = new EditablePropertyList(m_type);

        RepositoryAgent<T> agent = Services.get(RepositoryAgency.class)
                                           .getFor(c);
        
        delete = new GenericBeanDeleteExecutor<T>(agent);
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
                        return MessageProvider.instance().getFieldFaceProperty(
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
    class GenericComponent<T> extends AbstractBeanTableView 
            implements ValueObserver<QueryObject>,
                       RepositoryChangeListener {
        
        /** The agent for accessing the persistence layer. */
        private RepositoryAgent<T> m_repositoryAgent;
        
        /** Whether this component is currently refreshing. */ 
        private boolean m_refreshing;
        
        /**
         * Constructor.
         * @param cls the representation class for the entities 
         * shown in this view.
         */
        public GenericComponent(Class<T> cls) {
            m_repositoryAgent = Services.get(RepositoryAgency.class)
                                        .getFor(cls);
        }
            
        
        /**
         * Updates the set of displayed entities.
         * @param q the new filter query
         */
        public void changed(QueryObject q) {
            Reject.ifNull(q);
            if (isControlCreated()) {
                m_refreshing = true;
                setBeans(
                    m_repositoryAgent.findByQuery(q)
                );
                m_refreshing = false;
            }
        }

        /** Updates the set of displayed entities. */
        @ImplementationAssumption(
            "at most one thread is accessing the repository at any given time.")
        public void changed(RepositoryChangeNotifier.Change change) {
            if (change instanceof EntityDeleted) {
                Object deletee = ((EntityDeleted) change).changee;
                removeBean(deletee);
            } else {
                if (!m_refreshing) {
                    changed(filter.get());
                }
            }
        }

        /***/
        @Override
        public void componentOpened() {
            super.componentOpened();
            filter.subscribe(this);
            m_repositoryAgent.subscribe(this);
        }

        /***/
        @Override
        public void componentClosed() {
            m_repositoryAgent.unsubscribe(this);
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
    protected <T> ViewDescriptor createDescriptor(Class<T> clazz) {
        for (AbstractBeanExecutor e : executors) {
            m_awaker.awaken(e);
        }

        m_model = new Model(properties);
        m_model.setBeanClass(m_type.clazz);
        m_awaker.awaken(m_model);
        
        GenericComponent gc = new GenericComponent<T>(clazz);
        gc.setBeanTableModel(m_model);
        gc.setBeanExecutors(executors.toArray(AbstractBeanExecutor.class));
        return configure(new Descriptor(gc));
    }
}